package com.example.tasksync.presentation;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.application.services.UsuarioService;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name="Usuarios", description = "Grupo de APIs responsavel por controlar a estrutura de crição e consulta de usuario do sistema!")
public class UsuarioController {


    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos(){

        return  ResponseEntity.ok(usuarioService.ListarTodosUsuariosTable());
    }


    @PostMapping("/admin")
    public ResponseEntity<CriarAdminResponse> criarAdmin(@RequestBody CriarAdminRequest criarAdminRequest){

        try{
            CriarAdminResponse respostaSalvar = usuarioService.criarAdmin(criarAdminRequest);
            return ResponseEntity.ok(respostaSalvar);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }

    }

    //Apartir de aqui

    @GetMapping("/{id}")
    @Operation(summary = "Metodo de consulta de usuario por id", description = "Metodo responsavel em efetuar a busca de um usuario especifico atraves do seu id")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id){
        UsuarioResponse usuarioBanco = usuarioService.buscarPorId(id);
        if (usuarioBanco != null){
            return ResponseEntity.ok(usuarioBanco);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> criar(@RequestBody UsuarioRequest usuario){

        try {
            UsuarioResponse usuarioBanco = usuarioService.salvarUsuario(usuario,0L);
            return ResponseEntity.ok(usuarioBanco);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Metodo de atualização de status do usuario", description = "Metodo responsavel em atualizar apenas o status de um usuario especifico")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRquest statusRequest){
        var atualizarUsuario = usuarioService.atualizarStatus(statusRequest.status(),id);

        if(atualizarUsuario){
            return ResponseEntity.ok().build();
        }

        return  ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Metodo de atualização de usuario", description = "Metodo responsavel em atualizar todos os dados de um usuario existente")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuario){
        try {
            UsuarioResponse usuarioBanco = usuarioService.salvarUsuario(usuario,id);
            return ResponseEntity.ok(usuarioBanco);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
}

    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Metodo de exclusão de usuario", description = "Metodo responsavel em efetuar a exclusão logica de um usuario, alterando seu status para EXCLUIDO")
    public ResponseEntity<Void> excluir(@PathVariable Long id){
            var atualizarUsuario = usuarioService.atualizarStatus(EnumStatusUsuario.EXCLUIDO,id);

            if(atualizarUsuario) {
                return ResponseEntity.ok().build();
            }
            return  ResponseEntity.notFound().build();
    }

}
