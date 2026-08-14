package com.example.tasksync.controllers;

import com.example.tasksync.entities.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping
    public ResponseEntity<?> ListarTodos(){

        List<Usuario> usuarios = List.of(
                new Usuario(1L, "Guilherme", "12345678910", "121233", "email")
        );

        return ResponseEntity.ok(usuarios);
    }

}
