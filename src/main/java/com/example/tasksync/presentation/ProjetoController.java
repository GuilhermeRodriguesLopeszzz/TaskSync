package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.AtualizarStatusRequestProjeto;
import com.example.tasksync.application.dto.EnumStatusProjeto;
import com.example.tasksync.application.dto.ProjetoRequest;
import com.example.tasksync.application.dto.ProjetoResponse;
import com.example.tasksync.application.services.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
@Tag(name = "Projeto", description = "Grupo de APIs responsavel por controlar a estrutura de criação e consulta de projetos do sistema!")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @GetMapping
    @Operation(summary = "Metodo de consulta de lista de projeto", description = "Metodo responsavel em efetuar a consulta de todos os projetos sem filtro")
    public ResponseEntity<List<ProjetoResponse>> listarTodos() {

        return ResponseEntity.ok(projetoService.listarTodosProjetos());
    }

    @PostMapping
    @Operation(summary = "Metodo de criação de projeto", description = "Metodo responsavel em efetuar a criação de novos projetos")
    public ResponseEntity<?> criar(@RequestBody ProjetoRequest projeto) {

        try {
            ProjetoResponse projetoBanco = projetoService.salvarProjeto(projeto, 0L);
            return ResponseEntity.status(HttpStatus.CREATED).body(projetoBanco);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Metodo de consulta de projeto por id", description = "Metodo responsavel em efetuar a busca de um projeto especifico atraves do seu id")
    public ResponseEntity<ProjetoResponse> buscarPorId(@PathVariable Long id) {
        ProjetoResponse projetoBanco = projetoService.buscarPorId(id);
        if (projetoBanco != null) {
            return ResponseEntity.ok(projetoBanco);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Metodo de atualização de status do projeto", description = "Metodo responsavel em atualizar apenas o status de um projeto especifico")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRequestProjeto statusRequest) {
        var atualizou = projetoService.atualizarStatus(statusRequest.status(), id);

        if (atualizou) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Metodo de atualização de projeto", description = "Metodo responsavel em atualizar todos os dados de um projeto existente")
    public ResponseEntity<?> atualizarProjeto(@PathVariable Long id, @RequestBody ProjetoRequest projeto) {
        try {
            ProjetoResponse projetoBanco = projetoService.salvarProjeto(projeto, id);
            if (projetoBanco != null) {
                return ResponseEntity.ok(projetoBanco);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Metodo de exclusão de projeto", description = "Metodo responsavel em efetuar a exclusão logica de um projeto, alterando seu status para EXCLUIDO")
    public ResponseEntity<Void> excluir(@PathVariable Long id) { // Recebe o id do projeto pela URL
        // Delega ao service a troca do status do projeto para EXCLUIDO (exclusão lógica)
        var atualizou = projetoService.atualizarStatus(EnumStatusProjeto.EXCLUIDO, id);

        // Se o status foi atualizado, o projeto existia: responde 200 OK
        if (atualizou) {
            return ResponseEntity.ok().build();
        }
        // Caso contrário, o projeto não foi encontrado: responde 404 Not Found
        return ResponseEntity.notFound().build();
    }
}