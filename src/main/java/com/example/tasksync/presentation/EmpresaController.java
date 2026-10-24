package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.EmpresaRequest;
import com.example.tasksync.application.dto.EmpresaResponse;
import com.example.tasksync.application.dto.UsuarioRequest;
import com.example.tasksync.application.dto.UsuarioResponse;
import com.example.tasksync.application.services.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/empresas")
@Tag(name="Empresas", description = "Grupo de APIs responsavel por controlar a estrutura de crição e consulta de empresa do sistema!")
public class EmpresaController {


    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Metodo de criação de usuario", description = "Metodo responsavel em efetuar a criação de novas empresas")
    public ResponseEntity<?> criar(@RequestBody EmpresaRequest empresa) {

        try {
            EmpresaResponse empresaBanco = empresaService.salvarEmpresa(empresa, 0L);
            return ResponseEntity.ok(empresaBanco);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
