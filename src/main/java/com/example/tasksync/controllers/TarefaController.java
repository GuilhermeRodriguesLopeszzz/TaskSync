package com.example.tasksync.controllers;

import com.example.tasksync.entities.Tarefa;
import com.example.tasksync.repository.TarefaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tarefa")
@Tag(name="Tarefa", description = "Grupo de APIs responsavel por controlar a estrutura de crição e consulta de tarefas do sistema!")
public class TarefaController {
    @Autowired
    private TarefaRepository tarefaRepository;

    @GetMapping
    @Operation(summary = "Metodo de consulta de lista de tarefa", description = "Metodo responsavel em efetuar a consulta de todas as tarefas sem filtro")
    public ResponseEntity<?> ListarTodos(){

        return ResponseEntity.ok(tarefaRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation (summary = "Metodo de criação de tarefa", description = "Metodo responsavel em efetuar a criação de novas tarefas")
    public ResponseEntity<Tarefa> criar(@RequestBody Tarefa tarefa){

        var tarefaBanco = tarefaRepository.save(tarefa);
        return ResponseEntity.ok(tarefaBanco);
    }
}
