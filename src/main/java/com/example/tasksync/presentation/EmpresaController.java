package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.AtualizarStatusRequestEmpresa;
import com.example.tasksync.application.dto.EmpresaRequest;
import com.example.tasksync.application.dto.EmpresaResponse;
import com.example.tasksync.application.dto.EnumStatusEmpresa;
import com.example.tasksync.application.services.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresa")
@Tag(name = "Empresa", description = "Grupo de APIs responsavel por controlar a estrutura de criação e consulta de empresas do sistema!")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    @Operation(summary = "Metodo de consulta de lista de empresas", description = "Metodo responsavel em efetuar a consulta de todas as empresas sem filtro")
    public ResponseEntity<List<EmpresaResponse>> listarTodas() {

        return ResponseEntity.ok(empresaService.listarTodasEmpresas());
    }

    @PostMapping
    @Operation(summary = "Metodo de criação de empresas", description = "Metodo responsavel em efetuar a criação de novas empresas")
    public ResponseEntity<?> criar(@RequestBody EmpresaRequest empresa) {

        try {
            EmpresaResponse empresaBanco = empresaService.salvarEmpresa(empresa, 0L);
            return ResponseEntity.status(HttpStatus.CREATED).body(empresaBanco);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Metodo de consulta de empresa por id", description = "Metodo responsavel em efetuar a busca de uma empresa especifica atraves do seu id")
    public ResponseEntity<EmpresaResponse> buscarPorId(@PathVariable Long id) {
        EmpresaResponse empresaBanco = empresaService.buscarPorId(id);
        if (empresaBanco != null) {
            return ResponseEntity.ok(empresaBanco);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Metodo de atualização de status da empresa", description = "Metodo responsavel em atualizar apenas o status de uma empresa especifica")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRequestEmpresa statusRequest) {
        var atualizou = empresaService.atualizarStatus(statusRequest.status(), id);

        if (atualizou) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Metodo de atualização de empresa", description = "Metodo responsavel em atualizar todos os dados de uma empresa existente")
    public ResponseEntity<?> atualizarEmpresa(@PathVariable Long id, @RequestBody EmpresaRequest empresa) {
        try {
            EmpresaResponse empresaBanco = empresaService.salvarEmpresa(empresa, id);
            if (empresaBanco != null) {
                return ResponseEntity.ok(empresaBanco);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Metodo de exclusão de empresa", description = "Metodo responsavel em efetuar a exclusão logica de uma empresa, alterando seu status para EXCLUIDO")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        var atualizou = empresaService.atualizarStatus(EnumStatusEmpresa.EXCLUIDO, id);

        if (atualizou) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}