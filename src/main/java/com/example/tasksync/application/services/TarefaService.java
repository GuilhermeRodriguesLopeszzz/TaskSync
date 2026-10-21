package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.EnumStatusTarefa;
import com.example.tasksync.application.dto.TarefaRequest;
import com.example.tasksync.application.dto.TarefaResponse;
import com.example.tasksync.domain.entities.Tarefa;
import com.example.tasksync.domain.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {
    @Autowired
    private TarefaRepository tarefaRepository;

    public List<TarefaResponse> listarTodasTarefas() {

        return tarefaRepository.findAll()
                .stream()
                .map(TarefaResponse::new)
                .toList();
    }

    public TarefaResponse salvarTarefa(TarefaRequest tarefa, Long id) {

        //Nova Tarefa
        if (id == 0L) {
            Tarefa tarefaBanco = new Tarefa(tarefa);
            tarefaRepository.save(tarefaBanco);

            return new TarefaResponse(tarefaBanco);
        } else {
            //Tarefa existente
            Tarefa tarefaBanco = tarefaRepository.findById(id).orElse(null);
            if (tarefaBanco != null) {
                tarefaBanco.setTitulo(tarefa.titulo());
                tarefaBanco.setDescricao(tarefa.descricao());
                tarefaBanco.setDataPrazo(tarefa.dataPrazo());
                tarefaRepository.save(tarefaBanco);
                return new TarefaResponse(tarefaBanco);
            }
        }

        return null;
    }

    public TarefaResponse buscarPorId(Long id) {

        var tarefa = tarefaRepository.findById(id).orElse(null);
        if (tarefa == null) {
            return null;
        }

        return new TarefaResponse(tarefa);
    }

    public boolean atualizarStatus(EnumStatusTarefa status, Long id) {

        Tarefa tarefaBanco = tarefaRepository.findById(id).orElse(null);
        if (tarefaBanco != null) {
            tarefaBanco.setStatus(status);
            tarefaRepository.save(tarefaBanco);
            return true;
        }
        return false;
    }
}

