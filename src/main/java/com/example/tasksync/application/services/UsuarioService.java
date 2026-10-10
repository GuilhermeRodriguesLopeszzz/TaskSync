package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenService tokenService;

    @Value("${spring.secret}")
    private String secret;

    public LoginResponse validarUsuarioAutenticadoERetornaToken( LoginRequest loginRequest ){


        if(usuarioRepository.existsUsuarioByEmailAndSenha(loginRequest.email(), loginRequest.senha())){

            //gerar token está diferente que o do professor
            var token = tokenService.gerarToken(loginRequest.email());
            return new LoginResponse(token);
        }
        return null;
    }


    public List<UsuarioResponse> ListarTodosUsuariosTable(){

        return usuarioRepository.findAll()
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
}
