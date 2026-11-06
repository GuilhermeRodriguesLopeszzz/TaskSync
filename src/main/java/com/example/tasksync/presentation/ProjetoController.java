package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.application.services.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
@Tag(name = "Projeto", description = "APIs responsáveis pelo gerenciamento de projetos"
)
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;


    // Lista os projetos conforme as permissões do usuário logado
    @GetMapping
    @Operation(summary = "Listar projetos", description = "Lista projetos respeitando as permissões de ADMIN, GERENTE e DEV"
    )
    public ResponseEntity<List<ProjetoResponse>> listarTodos() {

        return ResponseEntity.ok(
                projetoService.listarTodosProjetos()
        );
    }


    // Cadastra um novo projeto
    @PostMapping
    @Operation(summary = "Cadastrar projeto", description = "Cria um projeto vinculado a uma empresa"
    )
    public ResponseEntity<ProjetoResponse> criar(
            @RequestBody ProjetoRequest projeto) {

        // O ID 0 indica que será criado um novo projeto
        ProjetoResponse projetoBanco =
                projetoService.salvarProjeto(projeto, 0L);

        // Retorna 201 Created quando o projeto é cadastrado
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projetoBanco);
    }


    // Busca um projeto específico pelo ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto por ID", description = "Consulta um projeto respeitando as permissões do usuário"
    )
    public ResponseEntity<ProjetoResponse> buscarPorId(
            @PathVariable Long id) {

        ProjetoResponse projetoBanco = projetoService.buscarPorId(id);

        // Retorna 200 quando encontrar o projeto
        if (projetoBanco != null) {
            return ResponseEntity.ok(projetoBanco);
        }

        // Retorna 404 se o projeto não existir
        return ResponseEntity.notFound().build();
    }


    // Atualiza somente o status do projeto
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do projeto", description = "Altera o status do projeto respeitando as regras de negócio"
    )
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequestProjeto statusRequest) {

        // A Service verifica as permissões e as regras de negócio
        boolean atualizou = projetoService.atualizarStatus(
                statusRequest.status(), id
        );

        // Retorna 200 se o status foi atualizado
        if (atualizou) {
            return ResponseEntity.ok().build();
        }

        // Retorna 404 se o projeto não existir
        return ResponseEntity.notFound().build();
    }

    // Atualiza os dados de um projeto existente
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto", description = "Atualiza os dados de um projeto existente"
    )
    public ResponseEntity<ProjetoResponse> atualizarProjeto(
            @PathVariable Long id,
            @RequestBody ProjetoRequest projeto) {

        ProjetoResponse projetoBanco =
                projetoService.salvarProjeto(projeto, id);

        // Retorna 200 com os dados atualizados
        if (projetoBanco != null) {
            return ResponseEntity.ok(projetoBanco);
        }
        // Retorna 404 se o projeto não existir
        return ResponseEntity.notFound().build();
    }

    // Realiza a exclusão lógica do projeto
    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Excluir projeto", description = "Realiza a exclusão lógica, alterando o status para EXCLUIDO"
    )
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        // Não apaga o projeto do banco de dados.
        // Apenas altera seu status para EXCLUIDO.
        boolean atualizou = projetoService.atualizarStatus(
                EnumStatusProjeto.EXCLUIDO, id
        );

        // Retorna 200 quando o projeto for excluído logicamente
        if (atualizou) {
            return ResponseEntity.ok().build();
        }

        // Retorna 404 se o projeto não existir
        return ResponseEntity.notFound().build();
    }


    // Vincula um desenvolvedor a um projeto existente
    @PostMapping("/{id}/desenvolvedores")
    @Operation(summary = "Vincular desenvolvedor ao projeto", description = "Vincula um usuário ROLE_DEV a um projeto da mesma empresa"
    )
    public ResponseEntity<ProjetoResponse> vincularDesenvolvedor(
            @PathVariable Long id,
            @RequestBody VincularDesenvolvedorRequest request) {

        // A Service verifica a empresa e o perfil do desenvolvedor
        ProjetoResponse projetoBanco =
                projetoService.vincularDesenvolvedor(id, request);

        // Retorna 404 se o projeto ou desenvolvedor não existir
        if (projetoBanco == null) {
            return ResponseEntity.notFound().build();
        }

        // Retorna 200 com os desenvolvedores vinculados
        return ResponseEntity.ok(projetoBanco);
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
