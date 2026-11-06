
package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.EmpresaRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import com.example.tasksync.domain.valueobject.CPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private TokenService tokenService;

    @Value("${spring.secret}")
    private String secret;

    public LoginResponse validarUsuarioAutenticadoERetornaToken(LoginRequest loginRequest) {

        if (usuarioRepository.existsUsuarioByEmailAndSenha(
                loginRequest.email(), loginRequest.senha())) {

            var token = tokenService.gerarToken(loginRequest);
            return new LoginResponse(token);
        }

        return null;
    }

    // Retorna o usuário autenticado pelo JWT
    public UsuarioLogado getUsuarioLogado() {
        return (UsuarioLogado) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // Verifica se o usuário logado pode acessar o usuário informado
    private void verificarPermissaoUsuario(
            Usuario usuarioBanco,
            UsuarioLogado usuarioLogado) {

        // ADMIN pode acessar usuários de qualquer empresa
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {
            return;
        }

        // GERENTE só pode acessar desenvolvedores da própria empresa
        if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            if (usuarioLogado.empresaId() == null ||
                    usuarioBanco.getEmpresa() == null ||
                    !usuarioLogado.empresaId().equals(
                            usuarioBanco.getEmpresa().getId())) {

                throw new AccessDeniedException(
                        "Gerente não pode acessar usuários de outra empresa!"
                );
            }

            if (usuarioBanco.getRole() != EnumRole.ROLE_DEV) {

                throw new AccessDeniedException(
                        "Gerente só pode gerenciar desenvolvedores!"
                );
            }

            return;
        }

        throw new AccessDeniedException(
                "Usuário sem permissão para acessar outros usuários!"
        );
    }

    // Lista os usuários conforme o perfil
    public List<UsuarioResponse> ListarTodosUsuariosTable() {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // ADMIN visualiza todos os usuários
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {

            return usuarioRepository.findAll()
                    .stream()
                    .map(UsuarioResponse::new)
                    .toList();

        } else if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            // GERENTE visualiza somente desenvolvedores da sua empresa
            if (usuarioLogado.empresaId() == null) {
                throw new AccessDeniedException(
                        "Gerente não possui empresa vinculada!"
                );
            }

            return usuarioRepository
                    .getUsuariosByEmpresa_Id(usuarioLogado.empresaId())
                    .stream()
                    .filter(usuario -> usuario.getRole() == EnumRole.ROLE_DEV)
                    .map(UsuarioResponse::new)
                    .toList();

        } else {

            throw new AccessDeniedException(
                    "Usuário sem permissão para listar usuários!"
            );
        }
    }

    // Cadastro inicial do administrador

    public CriarAdminResponse criarAdmin(CriarAdminRequest criarAdminRequest) {

        // Valida a chave secreta
        if (criarAdminRequest == null ||
                criarAdminRequest.secretKey() == null ||
                !criarAdminRequest.secretKey().equals(secret)) {

            throw new IllegalArgumentException(
                    "Chave secreta inválida!"
            );
        }

        // Permite o cadastro inicial somente quando não existe administrador
        boolean existeAdmin = usuarioRepository.findAll()
                .stream()
                .anyMatch(usuario ->
                        usuario.getRole() == EnumRole.ROLE_ADMIN
                );

        if (existeAdmin) {
            throw new IllegalArgumentException(
                    "Já existe um administrador cadastrado!"
            );
        }

        Usuario usuarioAdminSalvar = new Usuario(criarAdminRequest);

        usuarioRepository.save(usuarioAdminSalvar);

        return new CriarAdminResponse(
                usuarioAdminSalvar.getId(),
                "Usuário salvo com sucesso!"
        );
    }

    // Cria ou atualiza usuários
    public UsuarioResponse salvarUsuario(UsuarioRequest usuario, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Apenas ADMIN e GERENTE podem cadastrar ou editar usuários
        if (usuarioLogado.role() != EnumRole.ROLE_ADMIN &&
                usuarioLogado.role() != EnumRole.ROLE_GERENTE) {

            throw new AccessDeniedException(
                    "Usuário sem permissão para cadastrar ou editar usuários!"
            );
        }

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Informe os dados do usuário!"
            );
        }

        // NOVO USUÁRIO
        if (id == 0L) {

            Empresa empresa = null;
            EnumRole role = null;

            if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {

                // ADMIN escolhe a empresa
                if (usuario.empresaId() == null) {
                    throw new IllegalArgumentException(
                            "Informe uma empresa!"
                    );
                }

                empresa = empresaRepository.findById(
                        usuario.empresaId()
                ).orElse(null);

                // ADMIN pode criar GERENTE ou DEV
                if (usuario.role() == EnumRole.ROLE_GERENTE ||
                        usuario.role() == EnumRole.ROLE_DEV) {

                    role = usuario.role();

                } else {
                    throw new IllegalArgumentException(
                            "Tipo de usuário inválido!"
                    );
                }

            } else if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

                // GERENTE só pode cadastrar ROLE_DEV
                if (usuario.role() != EnumRole.ROLE_DEV) {
                    throw new AccessDeniedException(
                            "Gerente só pode cadastrar desenvolvedores!"
                    );
                }

                if (usuarioLogado.empresaId() == null) {
                    throw new IllegalArgumentException(
                            "Gerente não possui empresa vinculada!"
                    );
                }

                // Usa a empresa do gerente, não a enviada no Request
                empresa = empresaRepository.findById(
                        usuarioLogado.empresaId()
                ).orElse(null);

                role = EnumRole.ROLE_DEV;
            }

            if (empresa == null) {
                throw new IllegalArgumentException(
                        "Empresa não encontrada!"
                );
            }

            Usuario usuarioBanco = new Usuario(usuario);

            usuarioBanco.setEmpresa(empresa);
            usuarioBanco.setRole(role);

            usuarioRepository.save(usuarioBanco);

            return new UsuarioResponse(usuarioBanco);

        } else {

            // USUÁRIO EXISTENTE

            Usuario usuarioBanco = usuarioRepository.findById(id)
                    .orElse(null);

            if (usuarioBanco == null) {
                return null;
            }

            // Verifica a permissão sobre o usuário existente
            verificarPermissaoUsuario(usuarioBanco, usuarioLogado);

            // Não permite editar ADMIN por esta rota
            if (usuarioBanco.getRole() == EnumRole.ROLE_ADMIN) {
                throw new AccessDeniedException(
                        "Administrador não pode ser alterado por esta rota!"
                );
            }

            // Somente ADMIN pode transferir usuários de empresa
            if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {

                if (usuario.empresaId() == null) {
                    throw new IllegalArgumentException(
                            "Informe uma empresa!"
                    );
                }

                Empresa empresaBanco = empresaRepository.findById(
                        usuario.empresaId()
                ).orElse(null);

                if (empresaBanco == null) {
                    throw new IllegalArgumentException(
                            "Empresa não encontrada!"
                    );
                }

                // Não permite trocar a empresa de um DEV
                // que já participa de projetos
                if (usuarioBanco.getRole() == EnumRole.ROLE_DEV &&
                        usuarioBanco.getEmpresa() != null &&
                        !usuarioBanco.getEmpresa().getId().equals(
                                empresaBanco.getId()) &&
                        !usuarioBanco.getProjetos().isEmpty()) {

                    throw new IllegalArgumentException(
                            "Desvincule o desenvolvedor dos projetos antes de mudar sua empresa!"
                    );
                }

                usuarioBanco.setEmpresa(empresaBanco);

            } else if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

                // GERENTE não pode transferir desenvolvedores
                // para outra empresa
                if (usuario.empresaId() != null &&
                        !usuario.empresaId().equals(
                                usuarioLogado.empresaId())) {

                    throw new AccessDeniedException(
                            "Gerente não pode transferir usuários para outra empresa!"
                    );
                }

                // Mantém a empresa original do desenvolvedor
            }

            // Atualiza os dados básicos
            usuarioBanco.setNome(usuario.nome());
            usuarioBanco.setCpf(new CPF(usuario.cpf()));
            usuarioBanco.setEmail(usuario.email());

            // Só altera a senha quando uma nova senha é informada
            if (usuario.senha() != null &&
                    !usuario.senha().isBlank()) {

                usuarioBanco.setSenha(usuario.senha());
            }

            // A role permanece igual durante a edição
            usuarioRepository.save(usuarioBanco);

            return new UsuarioResponse(usuarioBanco);
        }
    }

    // Busca usuário por ID respeitando as permissões
    public UsuarioResponse buscarPorId(Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        Usuario usuarioBanco = usuarioRepository.findById(id)
                .orElse(null);

        if (usuarioBanco == null) {
            return null;
        }

        verificarPermissaoUsuario(usuarioBanco, usuarioLogado);

        return new UsuarioResponse(usuarioBanco);
    }

    // Atualiza o status de um usuário
    public boolean atualizarStatus(EnumStatusUsuario status, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        if (status == null) {
            throw new IllegalArgumentException(
                    "Informe o status do usuário!"
            );
        }

        Usuario usuarioBanco = usuarioRepository.findById(id)
                .orElse(null);

        if (usuarioBanco == null) {
            return false;
        }

        // Verifica a empresa e o perfil do usuário
        verificarPermissaoUsuario(usuarioBanco, usuarioLogado);

        // Impede a alteração de status do administrador
        if (usuarioBanco.getRole() == EnumRole.ROLE_ADMIN) {
            throw new AccessDeniedException(
                    "Não é permitido alterar o status do administrador!"
            );
        }

        usuarioBanco.setStatus(status);

        usuarioRepository.save(usuarioBanco);

        return true;
    }

    // Busca os dados do próprio usuário autenticado
    public UsuarioResponse BuscarUsuarioLogado() {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        return usuarioRepository.findById(usuarioLogado.id())
                .stream()
                .map(UsuarioResponse::new)
                .findFirst()
                .orElse(null);
    }
}
