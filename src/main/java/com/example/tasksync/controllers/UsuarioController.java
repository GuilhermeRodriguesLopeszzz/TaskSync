package com.example.tasksync.controllers;

import com.example.tasksync.entities.Usuario;
import com.example.tasksync.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name="Usuarios", description = "Grupo de APIs responsavel por controlar a estrutura de crição e consulta de usuario do sistema!")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    @Operation (summary = "Metodo de consulta de lista de usuario", description = "Metodo responsavel em efetuar a consulta de todos os usuarios sem filtro")
    public ResponseEntity<?> ListarTodos(){

        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation (summary = "Metodo de criação de usuario", description = "Metodo responsavel em efetuar a criação de novos usuarios")
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario){

        var usuarioBanco = usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuarioBanco);
    }
}
