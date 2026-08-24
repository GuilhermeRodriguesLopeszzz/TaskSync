package com.example.tasksync.controllers;

import com.example.tasksync.entities.Projeto;
import com.example.tasksync.repository.ProjetoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projetos")
@Tag(name="Projeto", description = "Grupo de APIs responsavel por controlar a estrutura de crição e consulta de projetos do sistema!")
public class ProjetoController {

    @Autowired
    private ProjetoRepository projetoRepository;

    @GetMapping
    @Operation(summary = "Metodo de consulta de lista de projeto", description = "Metodo responsavel em efetuar a consulta de todas os projetos sem filtro")
    public ResponseEntity<?> ListarTodos(){

        return ResponseEntity.ok(projetoRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation (summary = "Metodo de criação de projeto", description = "Metodo responsavel em efetuar a criação de novos projetos")
    public ResponseEntity<Projeto> criar(@RequestBody Projeto projeto){

        var projetoBanco = projetoRepository.save(projeto);
        return ResponseEntity.ok(projetoBanco);
    }
}
