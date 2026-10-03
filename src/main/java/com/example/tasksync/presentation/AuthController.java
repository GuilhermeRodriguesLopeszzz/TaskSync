package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.LoginRequest;
import com.example.tasksync.application.dto.LoginResponse;
import com.example.tasksync.application.services.TokenService;
import com.example.tasksync.application.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name="Autenticação", description = "Controller de autenticação")
public class AuthController {


    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/login")
    @Operation(description = "Método de login", summary = "Autenticação de usuarios")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){

        var resultadoAutenticacaoRetornoToken = usuarioService.validarUsuarioAutenticadoERetornaToken(loginRequest);

        if(resultadoAutenticacaoRetornoToken != null){

            return ResponseEntity.ok(resultadoAutenticacaoRetornoToken);
        }
        return ResponseEntity.badRequest().body("Usuario ou senha invalido!");
    }

}
