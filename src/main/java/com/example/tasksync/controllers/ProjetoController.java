package com.example.tasksync.controllers;

import com.example.tasksync.entities.Projeto;
import com.example.tasksync.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    @Autowired
    private ProjetoRepository projetoRepository;

    @GetMapping
    public ResponseEntity<?> ListarTodos(){

        return ResponseEntity.ok(projetoRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Projeto> criar(@RequestBody Projeto projeto){

        var projetoBanco = projetoRepository.save(projeto);
        return ResponseEntity.ok(projetoBanco);
    }
}
