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

    public LoginResponse validarUsuarioAutenticadoERetornaToken( LoginRequest loginRequest ){


        if(usuarioRepository.existsUsuarioByEmailAndSenha(loginRequest.email(), loginRequest.senha())){

            var token = tokenService.gerarToken(loginRequest);
            return new LoginResponse(token);
        }
        return null;
    }

    public UsuarioLogado getUsuarioLogado() {
        return (UsuarioLogado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    public List<UsuarioResponse> ListarTodosUsuariosTable(){

        var usuarioLogado = getUsuarioLogado();

        if(usuarioLogado.empresaId() ==null){
            return  usuarioRepository.findAll()
                    .stream()
                    .map(UsuarioResponse::new)
                    .toList();
        }

        return  usuarioRepository.getUsuariosByEmpresa_Id(usuarioLogado.empresaId())
                .stream()
                .map(UsuarioResponse::new)
                .toList();
    }

    public CriarAdminResponse criarAdmin(CriarAdminRequest criarAdminRequest) {

        if(!criarAdminRequest.secretKey().equals(secret)){
            return new CriarAdminResponse(0L,"Usuario Salvo com sucesso!");

        }

        Usuario usuarioAdminSalvar = new Usuario(criarAdminRequest);
        usuarioRepository.save(usuarioAdminSalvar);

        return new CriarAdminResponse(usuarioAdminSalvar.getId(),"Usuario Salvo com sucesso!");
    }



    public UsuarioResponse salvarUsuario(UsuarioRequest usuario, Long id) {

        // Recupera os dados do usuário que está autenticado no sistema
        var usuarioLogado = getUsuarioLogado();

        // Variáveis que receberão a empresa e a role do novo usuário
        Empresa empresa = null;
        EnumRole role = null;

        // Verifica se quem está realizando a operação é ADMIN
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {

            // ADMIN pode escolher a empresa pelo ID enviado no cadastro
            if (usuario.empresaId() == null) {
                throw new RuntimeException("Informe uma empresa.");
            }

            empresa = empresaRepository.findById(usuario.empresaId()).orElse(null);

            // ADMIN pode criar GERENTE ou DEV
            if (usuario.role() == EnumRole.ROLE_GERENTE ||
                    usuario.role() == EnumRole.ROLE_DEV) {

                role = usuario.role();

            } else {
                throw new AccessDeniedException("Tipo de usuário inválido.");
            }

        } else if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            // GERENTE só pode criar desenvolvedores
            if (usuario.role() != EnumRole.ROLE_DEV) {
                throw new AccessDeniedException(
                        "Gerente só pode cadastrar desenvolvedores."
                );
            }

            // O desenvolvedor será vinculado à empresa do próprio gerente
            empresa = empresaRepository.findById(usuarioLogado.empresaId()).orElse(null);

            // Define a role como DEV, sem depender de outros valores
            // enviados pelo frontend
            role = EnumRole.ROLE_DEV;

        } else {

            // Outros perfis, como DEV, não podem cadastrar usuários
            throw new AccessDeniedException(
                    "Seu perfil não tem permissão para realizar esta operação."
            );
        }

        // Verifica se a empresa informada ou vinculada foi encontrada
        if (empresa == null) {
            throw new RuntimeException("Empresa não encontrada.");
        }

        // Verifica se a operação é uma criação de usuário
        if (id == 0L) {

            // Cria uma nova entidade de usuário
            Usuario usuarioBanco = new Usuario(usuario);

            // Define a empresa e o perfil que foram validados acima
            usuarioBanco.setEmpresa(empresa);
            usuarioBanco.setRole(role);

            // Salva o novo usuário no banco de dados
            usuarioRepository.save(usuarioBanco);

            // Retorna os dados do usuário cadastrado
            return new UsuarioResponse(usuarioBanco);

        } else {

            // Busca o usuário que será atualizado
            Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);

            // Verifica se o usuário existe
            if (usuarioBanco == null) {
                throw new RuntimeException("Usuário não encontrado.");
            }

            // GERENTE só pode alterar usuários pertencentes à própria empresa
            if (usuarioLogado.role() == EnumRole.ROLE_GERENTE &&
                    (usuarioBanco.getEmpresa() == null ||
                            !usuarioBanco.getEmpresa().getId().equals(usuarioLogado.empresaId()))) {

                throw new AccessDeniedException(
                        "Você não pode alterar usuários de outra empresa."
                );
            }

            // Atualiza os dados básicos do usuário
            usuarioBanco.setNome(usuario.nome());
            usuarioBanco.setCpf(new CPF(usuario.cpf()));
            usuarioBanco.setEmail(usuario.email());

            // Atualiza a senha somente se uma nova senha foi informada
            if (usuario.senha() != null && !usuario.senha().isBlank()) {
                usuarioBanco.setSenha(usuario.senha());
            }

            // Atualiza a empresa conforme as regras de permissão
            usuarioBanco.setEmpresa(empresa);

            // Não altera a role durante a edição
            // Assim, o usuário não consegue mudar o próprio perfil pelo request

            // Salva as alterações no banco
            usuarioRepository.save(usuarioBanco);

            // Retorna os dados atualizados
            return new UsuarioResponse(usuarioBanco);
        }
    }



    /* public CriarDevResponse criarDev(CriarDevRequest criarDevRequest) {

        Usuario usuarioDevSalvar = new Usuario(criarDevRequest);
        usuarioRepository.save(usuarioDevSalvar);

        return new CriarDevResponse(usuarioDevSalvar.getId(),"Usuario Salvo com sucesso!");
    } */

    public UsuarioResponse buscarPorId(Long id) {

        var usuario = usuarioRepository.findById(id).orElse(null);
        if(usuario == null){
            return null;
        }

        return new UsuarioResponse(usuario);


    }

    public boolean atualizarStatus(EnumStatusUsuario status,Long id) {

        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);
        if(usuarioBanco!= null ){
            usuarioBanco.setStatus(status);
            usuarioRepository.save(usuarioBanco);
            return  true;
        }
        return false;

    }

    public UsuarioResponse BuscarUsuarioLogado() {
        try {
            var usuarioLogado = getUsuarioLogado();
            return usuarioRepository.findById(usuarioLogado.id())
                    .stream()
                    //vai instanciar o contstrutor para construir o usuario response
                    .map(UsuarioResponse::new)
                    .findFirst()
                    .orElse(null);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuario logado");
        }

    }
}
