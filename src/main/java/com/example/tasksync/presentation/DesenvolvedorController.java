package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.AtualizarStatusRequestDesenvolvedor;
import com.example.tasksync.application.dto.DesenvolvedorRequest;
import com.example.tasksync.application.dto.DesenvolvedorResponse;
import com.example.tasksync.application.services.DesenvolvedorService;
import com.example.tasksync.application.dto.EnumStatusDesenvolvedor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/desenvolvedor")
@Tag(name = "Desenvolvedor", description = "Grupo de APIs responsavel por controlar a estrutura de criação e consulta de desenvolvedores do sistema!")
public class DesenvolvedorController {

    @Autowired
    private DesenvolvedorService desenvolvedorService;

    @GetMapping
    @Operation(summary = "Metodo de consulta de lista de desenvolvedores", description = "Metodo responsavel em efetuar a consulta de todos os desenvolvedores sem filtro")
    public ResponseEntity<List<DesenvolvedorResponse>> listarTodos() {

        return ResponseEntity.ok(desenvolvedorService.listarTodosDesenvolvedores());
    }

    @PostMapping
    @Operation(summary = "Metodo de criação de desenvolvedores", description = "Metodo responsavel em efetuar a criação de novos desenvolvedores")
    public ResponseEntity<?> criar(@RequestBody DesenvolvedorRequest desenvolvedor) {

        try {
            DesenvolvedorResponse desenvolvedorBanco = desenvolvedorService.salvarDesenvolvedor(desenvolvedor, 0L);
            return ResponseEntity.status(HttpStatus.CREATED).body(desenvolvedorBanco);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Metodo de consulta de desenvolvedor por id", description = "Metodo responsavel em efetuar a busca de um desenvolvedor especifico atraves do seu id")
    public ResponseEntity<DesenvolvedorResponse> buscarPorId(@PathVariable Long id) {
        DesenvolvedorResponse desenvolvedorBanco = desenvolvedorService.buscarPorId(id);
        if (desenvolvedorBanco != null) {
            return ResponseEntity.ok(desenvolvedorBanco);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Metodo de atualização de status do desenvolvedor", description = "Metodo responsavel em atualizar apenas o status de um desenvolvedor especifico")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRequestDesenvolvedor statusRequest) {
        var atualizou = desenvolvedorService.atualizarStatus(statusRequest.status(), id);

        if (atualizou) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Metodo de atualização de desenvolvedor", description = "Metodo responsavel em atualizar todos os dados de um desenvolvedor existente")
    public ResponseEntity<?> atualizarDesenvolvedor(@PathVariable Long id, @RequestBody DesenvolvedorRequest desenvolvedor) {
        try {
            DesenvolvedorResponse desenvolvedorBanco = desenvolvedorService.salvarDesenvolvedor(desenvolvedor, id);
            if (desenvolvedorBanco != null) {
                return ResponseEntity.ok(desenvolvedorBanco);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Metodo de exclusão de desenvolvedor", description = "Metodo responsavel em efetuar a exclusão logica de um desenvolvedor, alterando seu status para EXCLUIDO")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        var atualizou = desenvolvedorService.atualizarStatus(EnumStatusDesenvolvedor.EXCLUIDO, id);

        if (atualizou) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}