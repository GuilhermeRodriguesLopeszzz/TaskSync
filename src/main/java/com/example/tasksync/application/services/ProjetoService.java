
package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.entities.Projeto;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.EmpresaRepository;
import com.example.tasksync.domain.repository.ProjetoRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    // Retorna os dados do usuário autenticado pelo JWT
    public UsuarioLogado getUsuarioLogado() {
        return (UsuarioLogado) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }


    // Lista os projetos conforme o perfil do usuário
    @Transactional(readOnly = true)
    public List<ProjetoResponse> listarTodosProjetos() {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Busca todos os projetos do banco
        List<Projeto> projetosBanco = projetoRepository.findAll();

        // ADMIN pode visualizar todos os projetos
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {

            return projetosBanco.stream()
                    .map(ProjetoResponse::new)
                    .toList();

        } else if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            // GERENTE visualiza somente projetos da própria empresa
            return projetosBanco.stream()
                    .filter(projeto ->
                            usuarioLogado.empresaId() != null &&
                                    projeto.getEmpresa() != null &&
                                    usuarioLogado.empresaId().equals(
                                            projeto.getEmpresa().getId()
                                    ))
                    .map(ProjetoResponse::new)
                    .toList();

        } else if (usuarioLogado.role() == EnumRole.ROLE_DEV) {

            // DEV visualiza somente projetos aos quais está vinculado
            return projetosBanco.stream()
                    .filter(projeto -> projeto.getDesenvolvedores()
                            .stream()
                            .anyMatch(dev ->
                                    dev.getId().equals(usuarioLogado.id())
                            ))
                    .map(ProjetoResponse::new)
                    .toList();

        } else {
            throw new IllegalArgumentException(
                    "Usuário sem permissão para listar projetos!"
            );
        }
    }


    // Vincula um desenvolvedor a um projeto existente
    @Transactional
    public ProjetoResponse vincularDesenvolvedor(
            Long projetoId,
            VincularDesenvolvedorRequest request) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Somente ADMIN e GERENTE podem vincular desenvolvedores
        if (usuarioLogado.role() != EnumRole.ROLE_ADMIN &&
                usuarioLogado.role() != EnumRole.ROLE_GERENTE) {

            throw new IllegalArgumentException(
                    "Usuário sem permissão para vincular desenvolvedores!"
            );
        }

        // Verifica se o ID do desenvolvedor foi informado
        if (request == null || request.desenvolvedorId() == null) {
            throw new IllegalArgumentException(
                    "Informe o ID do desenvolvedor!"
            );
        }

        // Busca o projeto pelo ID
        Projeto projetoBanco = projetoRepository.findById(projetoId)
                .orElse(null);

        if (projetoBanco == null) {
            throw new IllegalArgumentException(
                    "Projeto não encontrado!"
            );
        }

        // GERENTE só pode vincular desenvolvedores
        // em projetos da própria empresa
        if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            if (usuarioLogado.empresaId() == null ||
                    projetoBanco.getEmpresa() == null ||
                    !usuarioLogado.empresaId().equals(
                            projetoBanco.getEmpresa().getId())) {

                throw new IllegalArgumentException(
                        "Gerente não pode alterar projetos de outra empresa!"
                );
            }
        }

        // Busca o usuário que será vinculado ao projeto
        Usuario desenvolvedorBanco = usuarioRepository.findById(
                request.desenvolvedorId()
        ).orElse(null);

        if (desenvolvedorBanco == null) {
            throw new IllegalArgumentException(
                    "Desenvolvedor não encontrado!"
            );
        }

        // Somente usuários com ROLE_DEV podem ser vinculados
        if (desenvolvedorBanco.getRole() != EnumRole.ROLE_DEV) {
            throw new IllegalArgumentException(
                    "Somente desenvolvedores podem participar dos projetos!"
            );
        }

        // Verifica se o desenvolvedor pertence
        // à mesma empresa do projeto
        if (desenvolvedorBanco.getEmpresa() == null ||
                projetoBanco.getEmpresa() == null ||
                !desenvolvedorBanco.getEmpresa().getId().equals(
                        projetoBanco.getEmpresa().getId())) {

            throw new IllegalArgumentException(
                    "Desenvolvedor não pertence à empresa do projeto!"
            );
        }

        // Impede vincular o mesmo desenvolvedor duas vezes
        if (desenvolvedorBanco.getProjetos().contains(projetoBanco)) {
            throw new IllegalArgumentException(
                    "Desenvolvedor já está vinculado a este projeto!"
            );
        }

        // Adiciona o projeto à lista do desenvolvedor
        desenvolvedorBanco.adicionarProjeto(projetoBanco);

        // Salva pelo Usuario, pois ele controla o ManyToMany
        usuarioRepository.save(desenvolvedorBanco);

        // Atualiza o outro lado do relacionamento em memória
        projetoBanco.getDesenvolvedores().add(desenvolvedorBanco);

        return new ProjetoResponse(projetoBanco);
    }


    // Cria um novo projeto ou atualiza um projeto existente
    @Transactional
    public ProjetoResponse salvarProjeto(ProjetoRequest projeto, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Desenvolvedores não podem criar ou alterar projetos
        if (usuarioLogado.role() != EnumRole.ROLE_ADMIN &&
                usuarioLogado.role() != EnumRole.ROLE_GERENTE) {

            throw new IllegalArgumentException(
                    "Usuário sem permissão para criar ou alterar projetos!"
            );
        }


// NOVO PROJETO
        if (id == 0L) {

            Empresa empresaBanco = null;

            // ADMIN pode escolher em qual empresa criar o projeto
            if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {

                if (projeto.empresaId() == null) {
                    throw new IllegalArgumentException(
                            "Informe a empresa do projeto!"
                    );
                }

                empresaBanco = empresaRepository.findById(
                        projeto.empresaId()
                ).orElse(null);

            } else if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

                // GERENTE só pode criar projetos na própria empresa
                if (usuarioLogado.empresaId() == null) {
                    throw new IllegalArgumentException(
                            "Gerente não possui empresa vinculada!"
                    );
                }

                empresaBanco = empresaRepository.findById(
                        usuarioLogado.empresaId()
                ).orElse(null);
            }

            // Verifica se a empresa existe
            if (empresaBanco == null) {
                throw new IllegalArgumentException(
                        "Empresa não encontrada!"
                );
            }

            // Cria o projeto
            Projeto projetoBanco = new Projeto(projeto);

            // Vincula o projeto à empresa validada
            projetoBanco.setEmpresa(empresaBanco);

            // Salva no banco
            projetoRepository.save(projetoBanco);

            return new ProjetoResponse(projetoBanco);

        } else {

            // PROJETO EXISTENTE

            Projeto projetoBanco = projetoRepository.findById(id)
                    .orElse(null);

            if (projetoBanco == null) {
                return null;
            }

            // GERENTE só pode alterar projetos da própria empresa
            if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

                if (usuarioLogado.empresaId() == null ||
                        projetoBanco.getEmpresa() == null ||
                        !usuarioLogado.empresaId().equals(
                                projetoBanco.getEmpresa().getId())) {

                    throw new IllegalArgumentException(
                            "Gerente não pode alterar projetos de outras empresas!"
                    );
                }
            }

            // Atualiza os dados do projeto
            projetoBanco.setNome(projeto.nome());
            projetoBanco.setDescricao(projeto.descricao());
            projetoBanco.setDataInicio(projeto.dataInicio());
            projetoBanco.setDataPrevistaFim(projeto.dataPrevistaFim());

            projetoRepository.save(projetoBanco);

            return new ProjetoResponse(projetoBanco);
        }
    }


    // Busca um projeto pelo ID, respeitando as permissões
    @Transactional(readOnly = true)
    public ProjetoResponse buscarPorId(Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        Projeto projetoBanco = projetoRepository.findById(id)
                .orElse(null);

        if (projetoBanco == null) {
            return null;
        }

        // ADMIN pode consultar qualquer projeto
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {
            return new ProjetoResponse(projetoBanco);

        } else if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            // GERENTE somente pode consultar projetos da própria empresa
            if (usuarioLogado.empresaId() == null ||
                    projetoBanco.getEmpresa() == null ||
                    !usuarioLogado.empresaId().equals(
                            projetoBanco.getEmpresa().getId())) {

                throw new IllegalArgumentException(
                        "Gerente não pode acessar projetos de outra empresa!"
                );
            }

            return new ProjetoResponse(projetoBanco);

        } else if (usuarioLogado.role() == EnumRole.ROLE_DEV) {

            // Verifica se o desenvolvedor participa do projeto
            boolean participaProjeto = projetoBanco.getDesenvolvedores()
                    .stream()
                    .anyMatch(dev ->
                            dev.getId().equals(usuarioLogado.id())
                    );

            if (!participaProjeto) {
                throw new IllegalArgumentException(
                        "Desenvolvedor não está vinculado a este projeto!"
                );
            }

            return new ProjetoResponse(projetoBanco);

        } else {
            throw new IllegalArgumentException(
                    "Usuário sem permissão para consultar projetos!"
            );
        }
    }


    // Atualiza o status do projeto, incluindo exclusão lógica
    @Transactional
    public boolean atualizarStatus(EnumStatusProjeto status, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Somente ADMIN e GERENTE podem alterar o status
        if (usuarioLogado.role() != EnumRole.ROLE_ADMIN &&
                usuarioLogado.role() != EnumRole.ROLE_GERENTE) {

            throw new IllegalArgumentException(
                    "Usuário sem permissão para alterar projetos!"
            );
        }

        // Busca o projeto pelo ID
        Projeto projetoBanco = projetoRepository.findById(id)
                .orElse(null);

        // Verifica se o projeto existe
        if (projetoBanco == null) {
            return false;
        }

        // GERENTE só pode alterar projetos da própria empresa
        if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            if (usuarioLogado.empresaId() == null ||
                    projetoBanco.getEmpresa() == null ||
                    !usuarioLogado.empresaId().equals(
                            projetoBanco.getEmpresa().getId())) {

                throw new IllegalArgumentException(
                        "Gerente não pode alterar projetos de outra empresa!"
                );
            }
        }

        // Verifica se o projeto será excluído logicamente
        if (status == EnumStatusProjeto.EXCLUIDO) {

            // Procura alguma tarefa com status ANDAMENTO
            boolean tarefaEmAndamento = projetoBanco.getTarefas()
                    .stream()
                    .anyMatch(tarefa ->
                            tarefa.getStatus() == EnumStatusTarefa.ANDAMENTO
                    );

            // Impede a exclusão caso exista tarefa em andamento
            if (tarefaEmAndamento) {
                throw new IllegalArgumentException(
                        "Não é possível excluir um projeto com tarefas em andamento!"
                );
            }
        }

        // Atualiza o status do projeto
        projetoBanco.setStatus(status);

        // Salva a alteração no banco
        projetoRepository.save(projetoBanco);

        return true;
    }
}
