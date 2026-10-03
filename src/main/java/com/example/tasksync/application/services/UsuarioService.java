package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.LoginRequest;
import com.example.tasksync.application.dto.LoginResponse;
import com.example.tasksync.application.dto.UsuarioResponse;
import com.example.tasksync.domain.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenService tokenService;

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

}
