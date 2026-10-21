package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.EnumStatusProjeto;
import com.example.tasksync.application.dto.ProjetoRequest;
import com.example.tasksync.application.dto.ProjetoResponse;
import com.example.tasksync.domain.entities.Projeto;
import com.example.tasksync.domain.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    public List<ProjetoResponse> listarTodosProjetos() {

        return projetoRepository.findAll()
                .stream()
                .map(ProjetoResponse::new)
                .toList();
    }

    public ProjetoResponse salvarProjeto(ProjetoRequest projeto, Long id) {

        //Novo Projeto
        if (id == 0L) {
            Projeto projetoBanco = new Projeto(projeto);
            projetoRepository.save(projetoBanco);

            return new ProjetoResponse(projetoBanco);
        } else {
            //Projeto existente
            Projeto projetoBanco = projetoRepository.findById(id).orElse(null);
            if (projetoBanco != null) {
                projetoBanco.setNome(projeto.nome());
                projetoBanco.setDescricao(projeto.descricao());
                projetoBanco.setDataInicio(projeto.dataInicio());
                projetoBanco.setDataPrevistaFim(projeto.dataPrevistaFim());
                projetoRepository.save(projetoBanco);
                return new ProjetoResponse(projetoBanco);
            }
        }

        return null;
    }

    public ProjetoResponse buscarPorId(Long id) {

        var projeto = projetoRepository.findById(id).orElse(null);
        if (projeto == null) {
            return null;
        }

        return new ProjetoResponse(projeto);
    }

    public boolean atualizarStatus(EnumStatusProjeto status, Long id) {

        Projeto projetoBanco = projetoRepository.findById(id).orElse(null);
        if (projetoBanco != null) {
            projetoBanco.setStatus(status);
            projetoRepository.save(projetoBanco);
            return true;
        }
        return false;
    }
}