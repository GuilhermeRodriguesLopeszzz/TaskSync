package com.example.tasksync.controllers;

import com.example.tasksync.entities.Desenvolvedor;
import com.example.tasksync.repository.DesenvolvedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/desenvolvedor")
public class DesenvolvedorController {
    @Autowired
    private DesenvolvedorRepository desenvolvedorRepository;

    @GetMapping
    public ResponseEntity<?> ListarTodos(){

        return ResponseEntity.ok(desenvolvedorRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Desenvolvedor> criar(@RequestBody Desenvolvedor desenvolvedor){

        var desenvolvedorBanco = desenvolvedorRepository.save(desenvolvedor);
        return ResponseEntity.ok(desenvolvedorBanco);
    }
}
