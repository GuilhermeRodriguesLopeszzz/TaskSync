package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.EmpresaRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import com.example.tasksync.domain.valueobject.CPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

        if(usuarioLogado.empresa_id() ==null){
            return  usuarioRepository.findAll()
                    .stream()
                    .map(UsuarioResponse::new)
                    .toList();
        }

        return  usuarioRepository.getUsuariosByEmpresa_Id(usuarioLogado.empresa_id())
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

        //Novo Usuario
        if(id == 0L) {
            //var usuarioLogado = getUsuarioLogado();
            Usuario usuarioBanco = new Usuario(usuario);
           // usuarioBanco.setEmpresa(usuarioLogado.empresa_id());
            usuarioRepository.save(usuarioBanco);

            return new UsuarioResponse(usuarioBanco);
        }else {
            //Usuario existente
            Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);
            if(usuarioBanco!= null ){
                usuarioBanco.setNome(usuario.nome());
                usuarioBanco.setCpf(new CPF(usuario.cpf()));
                usuarioBanco.setEmail(usuario.email());
                usuarioBanco.setSenha(usuario.senha());
                //usuarioBanco.setEmpresa(usuario.empresaId());
                usuarioRepository.save(usuarioBanco);
                return  new UsuarioResponse(usuarioBanco);
            }

        }

        return null;
    }

    public CriarDevResponse criarDev(CriarDevRequest criarDevRequest) {

        Usuario usuarioDevSalvar = new Usuario(criarDevRequest);
        usuarioRepository.save(usuarioDevSalvar);

        return new CriarDevResponse(usuarioDevSalvar.getId(),"Usuario Salvo com sucesso!");
    }

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
