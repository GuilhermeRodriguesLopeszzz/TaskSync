package com.example.tasksync.controllers;

import com.example.tasksync.entities.Tarefa;
import com.example.tasksync.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tarefa")
public class TarefaController {
    @Autowired
    private TarefaRepository tarefaRepository;

    @GetMapping
    public ResponseEntity<?> ListarTodos(){

        return ResponseEntity.ok(tarefaRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Tarefa> criar(@RequestBody Tarefa tarefa){

        var tarefaBanco = tarefaRepository.save(tarefa);
        return ResponseEntity.ok(tarefaBanco);
    }
}
