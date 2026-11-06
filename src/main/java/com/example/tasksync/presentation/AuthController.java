
package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.LoginRequest;
import com.example.tasksync.application.dto.LoginResponse;
import com.example.tasksync.application.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "APIs responsáveis pela autenticação dos usuários")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;


    // Realiza a autenticação do usuário
    @PostMapping("/login")
    @Operation(
            summary = "Login do usuário",
            description = "Valida as credenciais e retorna um token JWT"
    )
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        // Envia os dados para a Service validar o login
        LoginResponse respostaLogin =
                usuarioService.validarUsuarioAutenticadoERetornaToken(loginRequest);

        // Se a autenticação for válida, retorna o token JWT
        if (respostaLogin != null) {
            return ResponseEntity.ok(respostaLogin);
        }

        // Se o email ou senha estiver incorreto, retorna 401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Email ou senha inválidos!");
    }
}
