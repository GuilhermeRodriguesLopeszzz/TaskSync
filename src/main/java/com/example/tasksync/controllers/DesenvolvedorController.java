package com.example.tasksync.controllers;

import com.example.tasksync.entities.Desenvolvedor;
import com.example.tasksync.repository.DesenvolvedorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/desenvolvedor")
@Tag(name="Desenvolvedor", description = "Controller de desenvolvedor")
public class DesenvolvedorController {
    @Autowired
    private DesenvolvedorRepository desenvolvedorRepository;

    @GetMapping
    @Operation(summary = "Metodo de consulta de lista de desenvolvedores", description = "Metodo responsavel em efetuar a consulta de todas os desenvolvedores sem filtro")
    public ResponseEntity<?> ListarTodos(){

        return ResponseEntity.ok(desenvolvedorRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Metodo de criação de desenvolvedores", description = "Metodo responsavel em efetuar a criação de novos desenvolvedores")
    public ResponseEntity<Desenvolvedor> criar(@RequestBody Desenvolvedor desenvolvedor){

        var desenvolvedorBanco = desenvolvedorRepository.save(desenvolvedor);
        return ResponseEntity.ok(desenvolvedorBanco);
    }
}
