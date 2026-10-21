package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.AtualizarStatusRequestTarefa;
import com.example.tasksync.application.dto.EnumStatusTarefa;
import com.example.tasksync.application.dto.TarefaRequest;
import com.example.tasksync.application.dto.TarefaResponse;
import com.example.tasksync.application.services.TarefaService;
import com.example.tasksync.domain.entities.Tarefa;
import com.example.tasksync.domain.repository.TarefaRepository;
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
    @Operation(summary = "Metodo de consulta de lista de tarefa", description = "Metodo responsavel em efetuar a consulta de todas as tarefas sem filtro")
    public ResponseEntity<List<TarefaResponse>> listarTodos() {

        return ResponseEntity.ok(tarefaService.listarTodasTarefas());
    }

    @PostMapping
    @Operation(summary = "Metodo de criação de tarefa", description = "Metodo responsavel em efetuar a criação de novas tarefas")
    public ResponseEntity<?> criar(@RequestBody TarefaRequest tarefa) {

        try {
            TarefaResponse tarefaBanco = tarefaService.salvarTarefa(tarefa, 0L);
            return ResponseEntity.status(HttpStatus.CREATED).body(tarefaBanco);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Metodo de consulta de tarefa por id", description = "Metodo responsavel em efetuar a busca de uma tarefa especifica atraves do seu id")
    public ResponseEntity<TarefaResponse> buscarPorId(@PathVariable Long id) {
        TarefaResponse tarefaBanco = tarefaService.buscarPorId(id);
        if (tarefaBanco != null) {
            return ResponseEntity.ok(tarefaBanco);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Metodo de atualização de status da tarefa", description = "Metodo responsavel em atualizar apenas o status de uma tarefa especifica")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRequestTarefa statusRequest) {
        var atualizou = tarefaService.atualizarStatus(statusRequest.status(), id);

        if (atualizou) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Metodo de atualização de tarefa", description = "Metodo responsavel em atualizar todos os dados de uma tarefa existente")
    public ResponseEntity<?> atualizarTarefa(@PathVariable Long id, @RequestBody TarefaRequest tarefa) {
        try {
            TarefaResponse tarefaBanco = tarefaService.salvarTarefa(tarefa, id);
            if (tarefaBanco != null) {
                return ResponseEntity.ok(tarefaBanco);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Metodo de exclusão de tarefa", description = "Metodo responsavel em efetuar a exclusão logica de uma tarefa, alterando seu status para INTERROMPIDO")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        var atualizou = tarefaService.atualizarStatus(EnumStatusTarefa.INTERROMPIDO, id);

        if (atualizou) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}