package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.application.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "APIs responsáveis pelo cadastro e gerenciamento de usuários"
)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Lista os usuários conforme as permissões do perfil logado
    @GetMapping
    @Operation(summary = "Listar usuários", description = "Lista os usuários respeitando as permissões de ADMIN e GERENTE"
    )
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {

        return ResponseEntity.ok(
                usuarioService.ListarTodosUsuariosTable()
        );
    }


    // Cadastro inicial do administrador pelo aplicativo desktop
    @PostMapping("/admin")
    @Operation(summary = "Cadastrar administrador", description = "Realiza o cadastro inicial do administrador utilizando uma chave secreta"
    )
    public ResponseEntity<CriarAdminResponse> criarAdmin(
            @RequestBody CriarAdminRequest criarAdminRequest) {

        // A Service decide se o administrador será salvo.
        // Mantém a resposta de sucesso mesmo quando a chave está incorreta,
        // conforme a regra definida para o cadastro inicial.
        CriarAdminResponse respostaSalvar =
                usuarioService.criarAdmin(criarAdminRequest);

        return ResponseEntity.ok(respostaSalvar);
    }


    // Busca um usuário pelo ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Consulta um usuário específico respeitando as permissões"
    )
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id) {

        UsuarioResponse usuarioBanco = usuarioService.buscarPorId(id);

        // Se encontrar o usuário, retorna 200 OK
        if (usuarioBanco != null) {
            return ResponseEntity.ok(usuarioBanco);
        }

        // Se não encontrar, retorna 404 Not Found
        return ResponseEntity.notFound().build();
    }


    // Cadastra GERENTE ou DEV conforme as permissões
    @PostMapping
    @Operation(summary = "Cadastrar usuário", description = "ADMIN pode cadastrar GERENTE ou DEV. GERENTE pode cadastrar apenas DEV"
    )
    public ResponseEntity<UsuarioResponse> criar(
            @RequestBody UsuarioRequest usuario) {

        // O ID 0 indica que será criado um novo usuário
        UsuarioResponse usuarioBanco =
                usuarioService.salvarUsuario(usuario, 0L);

        // Retorna 201 Created quando o cadastro é realizado
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioBanco);
    }


    // Atualiza somente o status do usuário
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do usuário", description = "Altera o status respeitando as permissões do usuário logado"
    )
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequestUsuario statusRequest) {

        boolean atualizou = usuarioService.atualizarStatus(
                statusRequest.status(), id
        );

        // Retorna 200 se o status foi atualizado
        if (atualizou) {
            return ResponseEntity.ok().build();
        }

        // Retorna 404 se o usuário não existe
        return ResponseEntity.notFound().build();
    }


    // Atualiza os dados de um usuário existente
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário respeitando as permissões"
    )
    public ResponseEntity<UsuarioResponse> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioRequest usuario) {

        UsuarioResponse usuarioBanco =
                usuarioService.salvarUsuario(usuario, id);

        // Retorna 200 com os dados atualizados
        if (usuarioBanco != null) {
            return ResponseEntity.ok(usuarioBanco);
        }

        // Retorna 404 quando o usuário não existe
        return ResponseEntity.notFound().build();
    }


    // Realiza a exclusão lógica do usuário
    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Excluir usuário", description = "Realiza a exclusão lógica alterando o status para EXCLUIDO"
    )
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        // Não apaga o registro do banco.
        // Apenas altera seu status para EXCLUIDO.
        boolean atualizou = usuarioService.atualizarStatus(
                EnumStatusUsuario.EXCLUIDO, id
        );

        if (atualizou) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }


    // Retorna os dados do próprio usuário autenticado
    @GetMapping("/buscarusuariologado")
    @Operation(summary = "Buscar usuário logado", description = "Consulta os dados do usuário autenticado pelo JWT"
    )
    public ResponseEntity<UsuarioResponse> buscarUsuarioLogado() {

        UsuarioResponse usuarioBanco =
                usuarioService.BuscarUsuarioLogado();

        if (usuarioBanco != null) {
            return ResponseEntity.ok(usuarioBanco);
        }

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
