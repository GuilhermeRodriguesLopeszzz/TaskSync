package com.example.tasksync.controllers;

import com.example.tasksync.DTOs.AtualizarStatusRequestDesenvolvedor;
import com.example.tasksync.DTOs.AtualizarStatusRequestProjeto;
import com.example.tasksync.entities.Desenvolvedor;
import com.example.tasksync.entities.EnumStatusDesenvolvedor;
import com.example.tasksync.entities.EnumStatusProjeto;
import com.example.tasksync.entities.Projeto;
import com.example.tasksync.repository.DesenvolvedorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/desenvolvedor")
@Tag(name="Desenvolvedor", description = "Controller de desenvolvedor")
public class DesenvolvedorController {
    @Autowired
    private DesenvolvedorRepository desenvolvedorRepository;

    @GetMapping
    @Operation(summary = "Metodo de consulta de lista de desenvolvedores", description = "Metodo responsavel em efetuar a consulta de todas os desenvolvedores sem filtro")
    public ResponseEntity<?> ListarTodos(){

        return ResponseEntity.ok(desenvolvedorRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Metodo de criação de desenvolvedores", description = "Metodo responsavel em efetuar a criação de novos desenvolvedores")
    public ResponseEntity<Desenvolvedor> criar(@RequestBody Desenvolvedor desenvolvedor){

        var desenvolvedorBanco = desenvolvedorRepository.save(desenvolvedor);
        return ResponseEntity.ok(desenvolvedorBanco);
    }
    //
    @GetMapping("/{id}")
    @Operation(summary = "Metodo de consulta de desenvolvedor por id", description = "Metodo responsavel em efetuar a busca de um desenvolvedor especifica atraves do seu id")
    public ResponseEntity<Desenvolvedor> buscarPorId(@PathVariable Long id){
        Desenvolvedor desenvolvedorBanco = desenvolvedorRepository.findById(id).orElse(null);
        if (desenvolvedorBanco != null){
            return ResponseEntity.ok(desenvolvedorBanco);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Metodo de atualização de status do desenvolvedor", description = "Metodo responsavel em atualizar apenas o status de um desenvolvedor especifico")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRequestDesenvolvedor statusRequest){

        Desenvolvedor desenvolvedorBanco = desenvolvedorRepository.findById(id).orElse(null);
        if (desenvolvedorBanco != null){
            desenvolvedorBanco.setStatus(statusRequest.status());
            desenvolvedorRepository.save(desenvolvedorBanco);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Metodo de atualização de desenvolvedor", description = "Metodo responsavel em atualizar todos os dados de uma desenvolvedor existente")
    public ResponseEntity<Desenvolvedor> atualizarDesenvolvedor(@PathVariable Long id, @RequestBody Desenvolvedor desenvolvedor){

        try {
            Desenvolvedor desenvolvedorBanco = desenvolvedorRepository.findById(id).orElse(null);
            if (desenvolvedorBanco != null){
                desenvolvedorBanco.setStatus(desenvolvedor.getStatus());
                desenvolvedorBanco.setNome(desenvolvedor.getNome());
                desenvolvedorBanco.setCpf(desenvolvedor.getCpf());
                desenvolvedorBanco.setEmail(desenvolvedor.getEmail());
                desenvolvedorBanco.setSenha(desenvolvedor.getSenha());
                desenvolvedorRepository.save(desenvolvedorBanco);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Metodo de exclusão de desenvolvedor", description = "Metodo responsavel em efetuar a exclusão logica de um desenvolvedor, alterando seu status para EXCLUIDO")
    public ResponseEntity<Void> excluir(@PathVariable Long id){
        Desenvolvedor desenvolvedorBanco = desenvolvedorRepository.findById(id).orElse(null);
        if (desenvolvedorBanco != null){
            desenvolvedorBanco.setStatus(EnumStatusDesenvolvedor.EXCLUIDO);
            desenvolvedorRepository.save(desenvolvedorBanco);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
