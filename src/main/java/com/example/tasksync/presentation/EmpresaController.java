
package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.application.services.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresa")
@Tag(
        name = "Empresa",
        description = "APIs responsáveis pelo gerenciamento de empresas"
)
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;


    // Lista todas as empresas cadastradas
    @GetMapping
    @Operation(
            summary = "Listar empresas",
            description = "Consulta todas as empresas cadastradas"
    )
    public ResponseEntity<List<EmpresaResponse>> listarTodas() {

        return ResponseEntity.ok(
                empresaService.listarTodasEmpresas()
        );
    }

    // TESTE TEMPORÁRIO PARA APRESENTAÇÃO
    @GetMapping("/teste-seguranca")
    public ResponseEntity<String> testarSeguranca() {
        return ResponseEntity.ok("Acesso permitido!");
    }


    // Cadastra uma nova empresa
    @PostMapping
    @Operation(
            summary = "Cadastrar empresa",
            description = "Cadastra uma nova empresa no sistema"
    )
    public ResponseEntity<EmpresaResponse> criar(
            @RequestBody EmpresaRequest empresa) {

        // O ID 0 indica que uma nova empresa será criada
        EmpresaResponse empresaBanco =
                empresaService.salvarEmpresa(empresa, 0L);

        // Retorna 201 Created quando a empresa é cadastrada
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(empresaBanco);
    }


    // Busca uma empresa pelo ID
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar empresa por ID",
            description = "Consulta uma empresa específica pelo seu ID"
    )
    public ResponseEntity<EmpresaResponse> buscarPorId(
            @PathVariable Long id) {

        EmpresaResponse empresaBanco = empresaService.buscarPorId(id);
        // Retorna 200 se a empresa for encontrada
        if (empresaBanco != null) {
            return ResponseEntity.ok(empresaBanco);
        }
        // Retorna 404 se a empresa não existir
        return ResponseEntity.notFound().build();
    }

    // Atualiza somente o status da empresa
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status da empresa",
            description = "Altera o status de uma empresa cadastrada"
    )
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequestEmpresa statusRequest) {

        // Envia o novo status para a Service
        boolean atualizou = empresaService.atualizarStatus(
                statusRequest.status(), id
        );
        // Retorna 200 quando o status é atualizado
        if (atualizou) {
            return ResponseEntity.ok().build();
        }
        // Retorna 404 quando a empresa não existe
        return ResponseEntity.notFound().build();
    }

    // Atualiza os dados de uma empresa existente
    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar empresa",
            description = "Atualiza os dados de uma empresa existente"
    )
    public ResponseEntity<EmpresaResponse> atualizarEmpresa(
            @PathVariable Long id,
            @RequestBody EmpresaRequest empresa) {

        EmpresaResponse empresaBanco =
                empresaService.salvarEmpresa(empresa, id);

        // Retorna 200 com os dados atualizados
        if (empresaBanco != null) {
            return ResponseEntity.ok(empresaBanco);
        }

        // Retorna 404 se a empresa não existir
        return ResponseEntity.notFound().build();
    }


    // Realiza a exclusão lógica da empresa
    @DeleteMapping("/{id}/excluir")
    @Operation(
            summary = "Excluir empresa",
            description = "Realiza a exclusão lógica alterando o status para EXCLUIDO"
    )
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        // Não remove a empresa do banco de dados.
        // Apenas altera seu status para EXCLUIDO.
        boolean atualizou = empresaService.atualizarStatus(
                EnumStatusEmpresa.EXCLUIDO, id
        );

        // Retorna 200 se a empresa foi excluída logicamente
        if (atualizou) {
            return ResponseEntity.ok().build();
        }

        // Retorna 404 se a empresa não existir
        return ResponseEntity.notFound().build();
    }


    // Trata erros de permissão lançados pela Service
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> acessoNegado(
            AccessDeniedException e) {

        // Retorna 403 quando o usuário não tem permissão
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }


    // Trata erros de validação e regras de negócio
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> dadosInvalidos(
            IllegalArgumentException e) {

        // Retorna 400 quando os dados são inválidos
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
