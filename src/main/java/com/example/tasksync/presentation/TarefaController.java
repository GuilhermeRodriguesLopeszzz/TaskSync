
package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.application.services.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefa")
@Tag(
        name = "Tarefa",
        description = "APIs responsáveis pelo gerenciamento de tarefas"
)
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;


    // Lista as tarefas conforme as permissões do usuário logado
    @GetMapping
    @Operation(
            summary = "Listar tarefas",
            description = "Lista as tarefas respeitando as permissões de ADMIN, GERENTE e DEV"
    )
    public ResponseEntity<List<TarefaResponse>> listarTodos() {

        return ResponseEntity.ok(
                tarefaService.listarTodasTarefas()
        );
    }


    // Cria uma nova tarefa e atribui a um desenvolvedor
    @PostMapping
    @Operation(
            summary = "Cadastrar tarefa",
            description = "Cria uma tarefa e atribui a um desenvolvedor vinculado ao projeto"
    )
    public ResponseEntity<TarefaResponse> criar(
            @RequestBody TarefaRequest tarefa) {

        // O ID 0 indica que uma nova tarefa será criada
        TarefaResponse tarefaBanco =
                tarefaService.salvarTarefa(tarefa, 0L);

        // Retorna 201 Created quando a tarefa é cadastrada
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tarefaBanco);
    }


    // Busca uma tarefa específica pelo ID
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar tarefa por ID",
            description = "Consulta uma tarefa específica respeitando as permissões"
    )
    public ResponseEntity<TarefaResponse> buscarPorId(
            @PathVariable Long id) {

        TarefaResponse tarefaBanco = tarefaService.buscarPorId(id);

        // Retorna 200 quando encontrar a tarefa
        if (tarefaBanco != null) {
            return ResponseEntity.ok(tarefaBanco);
        }

        // Retorna 404 se a tarefa não existir
        return ResponseEntity.notFound().build();
    }


    // Atualiza somente o status da tarefa
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status da tarefa",
            description = "Altera o status de uma tarefa respeitando as permissões"
    )
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequestTarefa statusRequest) {

        // A Service verifica quem pode alterar o status
        boolean atualizou = tarefaService.atualizarStatus(
                statusRequest.status(), id
        );

        // Retorna 200 se o status foi atualizado
        if (atualizou) {
            return ResponseEntity.ok().build();
        }

        // Retorna 404 se a tarefa não existir
        return ResponseEntity.notFound().build();
    }


    // Atualiza os dados de uma tarefa existente
    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar tarefa",
            description = "Atualiza os dados e o desenvolvedor responsável pela tarefa"
    )
    public ResponseEntity<TarefaResponse> atualizarTarefa(
            @PathVariable Long id,
            @RequestBody TarefaRequest tarefa) {

        TarefaResponse tarefaBanco =
                tarefaService.salvarTarefa(tarefa, id);

        // Retorna 200 com os dados atualizados
        if (tarefaBanco != null) {
            return ResponseEntity.ok(tarefaBanco);
        }

        // Retorna 404 se a tarefa não existir
        return ResponseEntity.notFound().build();
    }


    // Realiza a exclusão lógica da tarefa
    @DeleteMapping("/{id}/excluir")
    @Operation(
            summary = "Excluir tarefa",
            description = "Realiza a exclusão lógica alterando o status para EXCLUIDO"
    )
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        // A Service verifica as permissões e altera o status
        // O registro continua existindo no banco de dados
        boolean atualizou = tarefaService.excluirTarefa(id);

        // Retorna 200 quando a tarefa for excluída logicamente
        if (atualizou) {
            return ResponseEntity.ok().build();
        }

        // Retorna 404 se a tarefa não existir
        return ResponseEntity.notFound().build();
    }

    // Trata erros de permissão lançados pela Service
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> acessoNegado(
            AccessDeniedException e) {

        // 403: usuário autenticado, mas sem permissão
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }


    // Trata erros de validação e regras de negócio
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> dadosInvalidos(
            IllegalArgumentException e) {

        // 400: dados inválidos ou regra de negócio não atendida
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
