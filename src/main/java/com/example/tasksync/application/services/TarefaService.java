package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Projeto;
import com.example.tasksync.domain.entities.Tarefa;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.ProjetoRepository;
import com.example.tasksync.domain.repository.TarefaRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {
    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    // Retorna os dados do usuário autenticado pelo JWT
    public UsuarioLogado getUsuarioLogado() {
        return (UsuarioLogado) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public List<TarefaResponse> listarTodasTarefas() {

        return tarefaRepository.findAll()
                .stream()
                .map(TarefaResponse::new)
                .toList();
    }


    public TarefaResponse salvarTarefa(TarefaRequest tarefa, Long id) {

        // Recupera os dados do usuário autenticado
        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Busca o projeto pelo ID informado no Request
        Projeto projetoBanco = projetoRepository.findById(tarefa.projetoId()).orElse(null);

        // Verifica se o projeto existe
        if (projetoBanco == null) {
            throw new IllegalArgumentException("Projeto não encontrado!");
        }

        // PASSO 4: Gerente só pode gerenciar tarefas da própria empresa
        if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            if (usuarioLogado.empresaId() == null ||
                    projetoBanco.getEmpresa() == null ||
                    !usuarioLogado.empresaId().equals(projetoBanco.getEmpresa().getId())) {

                throw new IllegalArgumentException(
                        "Gerente não pode gerenciar tarefas de outra empresa!"
                );
            }
        }

        // Busca o desenvolvedor informado no Request
        Usuario desenvolvedorBanco = usuarioRepository.findById(
                tarefa.desenvolvedorId()
        ).orElse(null);

        // Verifica se o desenvolvedor existe
        if (desenvolvedorBanco == null) {
            throw new IllegalArgumentException("Desenvolvedor não encontrado!");
        }

        // PASSO 3: Verifica se o usuário possui ROLE_DEV
        if (desenvolvedorBanco.getRole() != EnumRole.ROLE_DEV) {
            throw new IllegalArgumentException(
                    "Somente desenvolvedores podem receber tarefas!"
            );
        }

        // PASSO 3: Verifica se o desenvolvedor participa do projeto
        if (!desenvolvedorBanco.getProjetos().contains(projetoBanco)) {
            throw new IllegalArgumentException(
                    "Desenvolvedor não está vinculado a este projeto!"
            );
        }

        // Nova tarefa
        if (id == 0L) {

            Tarefa tarefaBanco = new Tarefa(tarefa);

            // Vincula a tarefa ao projeto
            tarefaBanco.setProjeto(projetoBanco);

            // Define o desenvolvedor responsável
            tarefaBanco.setDesenvolvedor(desenvolvedorBanco);

            tarefaRepository.save(tarefaBanco);

            return new TarefaResponse(tarefaBanco);

        } else {

            // Tarefa existente
            Tarefa tarefaBanco = tarefaRepository.findById(id).orElse(null);

            if (tarefaBanco != null) {

                // Verifica se a tarefa pertence ao projeto informado
                if (tarefaBanco.getProjeto() == null ||
                        !tarefaBanco.getProjeto().getId().equals(projetoBanco.getId())) {
                    throw new IllegalArgumentException(
                            "Tarefa não pertence ao projeto informado!"
                    );
                }

                // Atualiza os dados da tarefa
                tarefaBanco.setTitulo(tarefa.titulo());
                tarefaBanco.setDescricao(tarefa.descricao());
                tarefaBanco.setDataPrazo(tarefa.dataPrazo());

                // Atualiza o desenvolvedor responsável
                tarefaBanco.setDesenvolvedor(desenvolvedorBanco);

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

