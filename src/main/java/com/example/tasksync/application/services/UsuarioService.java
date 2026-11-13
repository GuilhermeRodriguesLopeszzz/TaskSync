package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.EmpresaRepository;
import com.example.tasksync.domain.repository.TarefaRepository;
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

    @Autowired
    private TarefaRepository tarefaRepository;

    @Value("${spring.secret}")
    private String secret;

    // Mantém o login atual, mas impede novos tokens para usuários inativos.
    // A troca da senha em texto puro por hash ficará para a revisão de segurança.
    public LoginResponse validarUsuarioAutenticadoERetornaToken(LoginRequest loginRequest) {

        if (loginRequest == null ||
                loginRequest.email() == null ||
                loginRequest.senha() == null) {
            return null;
        }

        // Não gera token se o e-mail estiver repetido no banco de dados.
        List<Usuario> usuariosComEmail = usuarioRepository.findAll().stream()
                .filter(usuario -> loginRequest.email().equals(usuario.getEmail()))
                .toList();

        if (usuariosComEmail.size() != 1) {
            return null;
        }

        Usuario usuarioBanco = usuariosComEmail.get(0);

        // Confere a senha e rejeita usuários bloqueados ou excluídos.
        if (!loginRequest.senha().equals(usuarioBanco.getSenha()) ||
                usuarioBanco.getStatus() != EnumStatusUsuario.ATIVO) {
            return null;
        }

        // Gerentes e desenvolvedores precisam de uma empresa ativa.
        if (usuarioBanco.getRole() != EnumRole.ROLE_ADMIN &&
                (usuarioBanco.getEmpresa() == null ||
                        usuarioBanco.getEmpresa().getStatus() != EnumStatusEmpresa.ATIVO)) {
            return null;
        }

        String token = tokenService.gerarToken(loginRequest);
        return new LoginResponse(token);
    }

    // Obtém o usuário do banco para consultar seu status e perfil atuais.
    public UsuarioLogado getUsuarioLogado() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null ||
                !(autenticacao.getPrincipal() instanceof UsuarioLogado)) {
            throw new AccessDeniedException("Usuário não autenticado!");
        }

        UsuarioLogado dadosToken = (UsuarioLogado) autenticacao.getPrincipal();
        Usuario usuarioBanco = usuarioRepository.findById(dadosToken.id()).orElse(null);

        if (usuarioBanco == null ||
                usuarioBanco.getStatus() != EnumStatusUsuario.ATIVO) {
            throw new AccessDeniedException("Usuário inativo ou excluído!");
        }

        if (usuarioBanco.getRole() != EnumRole.ROLE_ADMIN &&
                (usuarioBanco.getEmpresa() == null ||
                        usuarioBanco.getEmpresa().getStatus() != EnumStatusEmpresa.ATIVO)) {
            throw new AccessDeniedException("A empresa do usuário está inativa!");
        }

        return new UsuarioLogado(usuarioBanco);
    }

    // Evita e-mails duplicados, inclusive de usuários já excluídos.
    // Isso também impede ambiguidades na busca de usuário para o token JWT.
    private void verificarEmailDisponivel(String email, Long idUsuario) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Informe o e-mail do usuário!");
        }

        boolean emailDuplicado = usuarioRepository.findAll().stream()
                .anyMatch(usuario -> email.equalsIgnoreCase(usuario.getEmail()) &&
                        (idUsuario == null || !idUsuario.equals(usuario.getId())));

        if (emailDuplicado) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail!");
        }
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

    // Lista somente usuários não excluídos, conforme o perfil.
    @Transactional(readOnly = true)
    public List<UsuarioResponse> ListarTodosUsuariosTable() {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // ADMIN visualiza todos os usuários
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {

            return usuarioRepository.findAll()
                    .stream()
                    .filter(usuario -> usuario.getStatus() != EnumStatusUsuario.EXCLUIDO)
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
                    .filter(usuario -> usuario.getRole() == EnumRole.ROLE_DEV &&
                            usuario.getStatus() != EnumStatusUsuario.EXCLUIDO)
                    .map(UsuarioResponse::new)
                    .toList();

        } else {

            throw new AccessDeniedException(
                    "Usuário sem permissão para listar usuários!"
            );
        }
    }

    @Transactional
    public CriarAdminResponse criarAdmin(CriarAdminRequest criarAdminRequest) {

        // Verifica se a chave secreta está correta
        if (criarAdminRequest == null ||
                criarAdminRequest.secretKey() == null ||
                !criarAdminRequest.secretKey().equals(secret)) {

            return new CriarAdminResponse(0L, "Usuário salvo com sucesso!");
        }

        // Verifica se o e-mail já está cadastrado
        verificarEmailDisponivel(criarAdminRequest.email(), null);

        // Cria o administrador
        Usuario usuarioAdminSalvar = new Usuario(criarAdminRequest);

        // Salva no banco
        usuarioRepository.save(usuarioAdminSalvar);

        return new CriarAdminResponse(0L, "Usuário salvo com sucesso!");
    }

    // Cria ou atualiza usuários.
    @Transactional
    public UsuarioResponse salvarUsuario(UsuarioRequest usuario, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Apenas ADMIN e GERENTE podem cadastrar ou editar usuários
        if (usuarioLogado.role() != EnumRole.ROLE_ADMIN &&
                usuarioLogado.role() != EnumRole.ROLE_GERENTE) {

            throw new AccessDeniedException(
                    "Usuário sem permissão para cadastrar ou editar usuários!"
            );
        }

        if (usuario == null || id == null) {
            throw new IllegalArgumentException("Informe os dados do usuário!");
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
                throw new IllegalArgumentException("Empresa não encontrada!");
            }

            // Só cadastra usuários em uma empresa ativa.
            if (empresa.getStatus() != EnumStatusEmpresa.ATIVO) {
                throw new IllegalArgumentException("A empresa precisa estar ativa!");
            }

            verificarEmailDisponivel(usuario.email(), null);

            if (usuario.senha() == null || usuario.senha().isBlank()) {
                throw new IllegalArgumentException("Informe a senha do usuário!");
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

            // Não permite editar usuários excluídos.
            if (usuarioBanco == null ||
                    usuarioBanco.getStatus() == EnumStatusUsuario.EXCLUIDO) {
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
                    throw new IllegalArgumentException("Empresa não encontrada!");
                }

                if (empresaBanco.getStatus() != EnumStatusEmpresa.ATIVO) {
                    throw new IllegalArgumentException(
                            "A empresa precisa estar ativa!"
                    );
                }

            // A entidade verifica se o usuário pode mudar de empresa
                usuarioBanco.alterarEmpresa(empresaBanco);

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

            // Garante que o novo e-mail não esteja em uso por outro usuário.
            verificarEmailDisponivel(usuario.email(), id);

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

    // Busca usuário por ID respeitando as permissões.
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        Usuario usuarioBanco = usuarioRepository.findById(id)
                .orElse(null);

        if (usuarioBanco == null ||
                usuarioBanco.getStatus() == EnumStatusUsuario.EXCLUIDO) {
            return null;
        }

        verificarPermissaoUsuario(usuarioBanco, usuarioLogado);

        return new UsuarioResponse(usuarioBanco);
    }

    // Atualiza o status de um usuário ou realiza sua exclusão lógica.
    @Transactional
    public boolean atualizarStatus(EnumStatusUsuario status, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        if (status == null) {
            throw new IllegalArgumentException(
                    "Informe o status do usuário!"
            );
        }

        Usuario usuarioBanco = usuarioRepository.findById(id)
                .orElse(null);

        // Não é possível recuperar um usuário que já foi excluído.
        if (usuarioBanco == null ||
                usuarioBanco.getStatus() == EnumStatusUsuario.EXCLUIDO) {
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

        if (status == EnumStatusUsuario.EXCLUIDO &&
                usuarioBanco.getRole() == EnumRole.ROLE_DEV) {

            // Não exclui um DEV que ainda possui tarefas não excluídas
            // em projetos que continuam ativos.
            boolean possuiTarefas = tarefaRepository.findAll().stream()
                    .anyMatch(tarefa ->
                            tarefa.getDesenvolvedor() != null &&
                                    usuarioBanco.getId().equals(tarefa.getDesenvolvedor().getId()) &&
                                    tarefa.getStatus() != EnumStatusTarefa.EXCLUIDO &&
                                    tarefa.getProjeto() != null &&
                                    tarefa.getProjeto().getStatus() != EnumStatusProjeto.EXCLUIDO);

            if (possuiTarefas) {
                throw new IllegalArgumentException(
                        "Reatribua ou exclua as tarefas do desenvolvedor antes de excluí-lo!"
                );
            }

            // Remove os vínculos ativos no ManyToMany.
            // As tarefas históricas continuam no banco de dados.
            usuarioBanco.getProjetos().clear();
        }

        usuarioBanco.setStatus(status);
        usuarioRepository.save(usuarioBanco);
        return true;
    }

    // Busca os dados do próprio usuário autenticado.
    @Transactional(readOnly = true)
    public UsuarioResponse BuscarUsuarioLogado() {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        return usuarioRepository.findById(usuarioLogado.id())
                .stream()
                .map(UsuarioResponse::new)
                .findFirst()
                .orElse(null);
    }
}
