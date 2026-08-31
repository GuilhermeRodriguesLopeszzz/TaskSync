package com.example.tasksync.controllers;

import com.example.tasksync.DTOs.AtualizarStatusRequestProjeto;
import com.example.tasksync.DTOs.AtualizarStatusRequestTarefa;
import com.example.tasksync.entities.EnumStatusProjeto;
import com.example.tasksync.entities.EnumStatusTarefa;
import com.example.tasksync.entities.Projeto;
import com.example.tasksync.entities.Tarefa;
import com.example.tasksync.repository.ProjetoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projetos")
@Tag(name="Projeto", description = "Grupo de APIs responsavel por controlar a estrutura de crição e consulta de projetos do sistema!")
public class ProjetoController {

    @Autowired
    private ProjetoRepository projetoRepository;

    @GetMapping
    @Operation(summary = "Metodo de consulta de lista de projeto", description = "Metodo responsavel em efetuar a consulta de todas os projetos sem filtro")
    public ResponseEntity<?> ListarTodos(){

        return ResponseEntity.ok(projetoRepository.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation (summary = "Metodo de criação de projeto", description = "Metodo responsavel em efetuar a criação de novos projetos")
    public ResponseEntity<Projeto> criar(@RequestBody Projeto projeto){

        var projetoBanco = projetoRepository.save(projeto);
        return ResponseEntity.ok(projetoBanco);
    }
//
    @GetMapping("/{id}")
    @Operation(summary = "Metodo de consulta de projeto por id", description = "Metodo responsavel em efetuar a busca de um projeto especifica atraves do seu id")
    public ResponseEntity<Projeto> buscarPorId(@PathVariable Long id){
        Projeto projetoBanco = projetoRepository.findById(id).orElse(null);
        if (projetoBanco != null){
            return ResponseEntity.ok(projetoBanco);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Metodo de atualização de status do projeto", description = "Metodo responsavel em atualizar apenas o status de uma desenvolvedor especifico")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusRequestProjeto statusRequest){

        Projeto projetoBanco = projetoRepository.findById(id).orElse(null);
        if (projetoBanco != null){
            projetoBanco.setStatus(statusRequest.status());
            projetoRepository.save(projetoBanco);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Metodo de atualização de Projeto", description = "Metodo responsavel em atualizar todos os dados de uma Projeto existente")
    public ResponseEntity<Projeto> atualizarProjeto(@PathVariable Long id, @RequestBody Projeto projeto){

        try {
            Projeto projetoBanco = projetoRepository.findById(id).orElse(null);
            if (projetoBanco != null){
                projetoBanco.setStatus(projeto.getStatus());
                projetoBanco.setNome(projeto.getNome());
                projetoBanco.setDescricao(projeto.getDescricao());
                projetoBanco.setDataInicio(projeto.getDataInicio());
                projetoBanco.setDataPrevistaFim(projeto.getDataPrevistaFim());
                projetoRepository.save(projetoBanco);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}/excluir")
    @Operation(summary = "Metodo de exclusão de projeto", description = "Metodo responsavel em efetuar a exclusão logica de um projeto, alterando seu status para EXCLUIDO")
    public ResponseEntity<Void> excluir(@PathVariable Long id){
        Projeto projetoBanco = projetoRepository.findById(id).orElse(null);
        if (projetoBanco != null){
            projetoBanco.setStatus(EnumStatusProjeto.EXCLUIDO);
            projetoRepository.save(projetoBanco);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
