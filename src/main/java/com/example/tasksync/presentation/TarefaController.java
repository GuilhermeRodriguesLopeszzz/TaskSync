package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.application.services.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefa")
@Tag(name = "Tarefa", description = "Grupo de APIs responsavel por controlar a estrutura de criação e consulta de tarefas do sistema!")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @GetMapping
    @Operation(summary = "Metodo de consulta de lista de tarefas", description = "Lista as tarefas conforme as permissoes do usuario")
    public ResponseEntity<?> listarTodos() {

        try {
            return ResponseEntity.ok(tarefaService.listarTodasTarefas());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Metodo de criação de tarefa", description = "Cria uma tarefa e atribui a um desenvolvedor vinculado ao projeto")
    public ResponseEntity<?> criar(@RequestBody TarefaRequest tarefa) {

        try {
            // Envia os dados para o Service criar uma nova tarefa
            TarefaResponse tarefaBanco = tarefaService.salvarTarefa(tarefa, 0L);

            return ResponseEntity.status(HttpStatus.CREATED).body(tarefaBanco);

        } catch (IllegalArgumentException e) {

            // Retorna a mensagem caso alguma regra seja violada
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Metodo de consulta de tarefa por id", description = "Busca uma tarefa especifica pelo seu id")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {

        try {
            TarefaResponse tarefaBanco = tarefaService.buscarPorId(id);

            if (tarefaBanco != null) {
                return ResponseEntity.ok(tarefaBanco);
            }

            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Metodo de atualização de status da tarefa", description = "Atualiza apenas o status de uma tarefa")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequestTarefa statusRequest) {

        try {
            boolean atualizou = tarefaService.atualizarStatus(
                    statusRequest.status(), id
            );

            if (atualizou) {
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Metodo de atualização de tarefa", description = "Atualiza os dados e o desenvolvedor responsavel pela tarefa")
    public ResponseEntity<?> atualizarTarefa(
            @PathVariable Long id,
            @RequestBody TarefaRequest tarefa) {

        try {
            TarefaResponse tarefaBanco = tarefaService.salvarTarefa(tarefa, id);

            if (tarefaBanco != null) {
                return ResponseEntity.ok(tarefaBanco);
            }

            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Metodo de exclusão de tarefa", description = "Efetua exclusão logica alterando o status para INTERROMPIDO")
    public ResponseEntity<?> excluir(@PathVariable Long id) {

        try {
            // Exclusão lógica: altera o status para EXCLUIDO
            boolean atualizou = tarefaService.excluirTarefa(id);

            if (atualizou) {
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
