package com.example.tasksync.controllers;

import com.example.tasksync.DTOs.AtualizarStatusRequestTarefa;
import com.example.tasksync.DTOs.AtualizarStatusRquest;
import com.example.tasksync.entities.EnumStatusTarefa;
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

    @GetMapping("/{id}")
    @Operation(summary = "Metodo de consulta de tarefa por id", description = "Metodo responsavel em efetuar a busca de uma tarefa especifica atraves do seu id")
    public ResponseEntity<Tarefa> buscarPorId(@PathVariable Long id){
        Tarefa tarefaBanco = tarefaRepository.findById(id).orElse(null);
        if (tarefaBanco != null){
            return ResponseEntity.ok(tarefaBanco);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Metodo de atualização de status do tarefa", description = "Metodo responsavel em atualizar apenas o status de uma tarefa especifico")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRequestTarefa statusRequest){

        Tarefa tarefaBanco = tarefaRepository.findById(id).orElse(null);
        if (tarefaBanco != null){
            tarefaBanco.setStatus(statusRequest.status());
            tarefaRepository.save(tarefaBanco);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Metodo de atualização de tarefa", description = "Metodo responsavel em atualizar todos os dados de uma tarefa existente")
    public ResponseEntity<Tarefa> atualizarTarefa(@PathVariable Long id, @RequestBody Tarefa tarefa){

        try {
            Tarefa tarefaBanco = tarefaRepository.findById(id).orElse(null);
            if (tarefaBanco != null){
                tarefaBanco.setStatus(tarefa.getStatus());
                tarefaBanco.setTitulo(tarefa.getTitulo());
                tarefaBanco.setDescricao(tarefa.getDescricao());
                tarefaBanco.setDataPrazo(tarefa.getDataPrazo());
                tarefaRepository.save(tarefaBanco);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Metodo de exclusão de tarefa", description = "Metodo responsavel em efetuar a exclusão logica de uma tarefa, alterando seu status para EXCLUIDO")
    public ResponseEntity<Void> excluir(@PathVariable Long id){
        Tarefa tarefaBanco = tarefaRepository.findById(id).orElse(null);
        if (tarefaBanco != null){
            tarefaBanco.setStatus(EnumStatusTarefa.INTERROMPIDO);
            tarefaRepository.save(tarefaBanco);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
