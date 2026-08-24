package com.example.tasksync.controllers;

import com.example.tasksync.DTOs.LoginRequest;
import com.example.tasksync.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;

@RestController
@RequestMapping("/auth")
@Tag(name="Autenticação", description = "Controller de autenticação")
public class AuthController {


    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    @Operation(description = "Método de login", summary = "Autenticação de usuarios")
    public ResponseEntity<?> login(@RequestParam LoginRequest loginRequest){

        if(loginRequest.email().equals("string")&& loginRequest.senha().equals("string")){
            //Gera Token
            var token = tokenService.gerarToken(loginRequest.email());

            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpURLConnection.HTTP_UNAUTHORIZED).build();
    }

}
