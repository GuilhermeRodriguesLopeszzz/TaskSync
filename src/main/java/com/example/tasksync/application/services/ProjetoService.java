package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.entities.Projeto;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.EmpresaRepository;
import com.example.tasksync.domain.repository.ProjetoRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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


    // Consulta o usuário autenticado e verifica seu status atual no banco.
    public UsuarioLogado getUsuarioLogado() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null ||
                !(autenticacao.getPrincipal() instanceof UsuarioLogado)) {
            throw new AccessDeniedException("Usuário não autenticado!");
        }

        UsuarioLogado dadosToken = (UsuarioLogado) autenticacao.getPrincipal();
        Usuario usuarioBanco = usuarioRepository.findById(dadosToken.id()).orElse(null);

        // Um usuário excluído ou bloqueado não pode operar com um token antigo.
        if (usuarioBanco == null ||
                usuarioBanco.getStatus() != EnumStatusUsuario.ATIVO) {
            throw new AccessDeniedException("Usuário inativo ou excluído!");
        }

        // ADMIN pode não possuir empresa; GERENTE e DEV precisam de empresa ativa.
        if (usuarioBanco.getRole() != EnumRole.ROLE_ADMIN &&
                (usuarioBanco.getEmpresa() == null ||
                        usuarioBanco.getEmpresa().getStatus() != EnumStatusEmpresa.ATIVO)) {
            throw new AccessDeniedException("A empresa do usuário está inativa!");
        }

        // Usa o perfil e a empresa atuais, em vez de dados antigos do token.
        return new UsuarioLogado(usuarioBanco);
    }


    // Verifica se o usuário pode acessar um projeto
    private void verificarPermissaoProjeto(
            Projeto projetoBanco,
            UsuarioLogado usuarioLogado) {

        // ADMIN pode acessar qualquer projeto
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {
            return;
        }

        // GERENTE só pode acessar projetos da própria empresa
        if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            if (usuarioLogado.empresaId() == null ||
                    projetoBanco.getEmpresa() == null ||
                    !usuarioLogado.empresaId().equals(
                            projetoBanco.getEmpresa().getId())) {

                throw new AccessDeniedException(
                        "Gerente não pode acessar projetos de outra empresa!"
                );
            }

            return;
        }

        // DEV só pode acessar projetos aos quais está vinculado
        if (usuarioLogado.role() == EnumRole.ROLE_DEV) {

            boolean participaProjeto = projetoBanco.getDesenvolvedores()
                    .stream()
                    .anyMatch(dev ->
                            dev.getId().equals(usuarioLogado.id())
                    );

            if (!participaProjeto) {
                throw new AccessDeniedException(
                        "Desenvolvedor não está vinculado a este projeto!"
                );
            }

            return;
        }

        // Bloqueia qualquer perfil não autorizado
        throw new AccessDeniedException(
                "Usuário sem permissão para acessar projetos!"
        );
    }


    // Lista os projetos conforme o perfil do usuário
    @Transactional(readOnly = true)
    public List<ProjetoResponse> listarTodosProjetos() {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Busca os projetos, ignorando os excluídos logicamente
        List<Projeto> projetosBanco = projetoRepository.findAll()
                .stream()
                .filter(projeto ->
                        projeto.getStatus() != EnumStatusProjeto.EXCLUIDO &&
                                projeto.getEmpresa() != null &&
                                projeto.getEmpresa().getStatus() != EnumStatusEmpresa.EXCLUIDO
                )
                .toList();

        // ADMIN visualiza todos os projetos não excluídos
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {

            return projetosBanco.stream()
                    .map(ProjetoResponse::new)
                    .toList();

        } else if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            // GERENTE visualiza projetos da própria empresa
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

            throw new AccessDeniedException(
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

            throw new AccessDeniedException(
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

        // Projeto inexistente ou excluído não pode receber desenvolvedores
        if (projetoBanco == null ||
                projetoBanco.getStatus() == EnumStatusProjeto.EXCLUIDO) {
            return null;
        }

        // Verifica se ADMIN ou GERENTE pode acessar esse projeto
        verificarPermissaoProjeto(projetoBanco, usuarioLogado);

        // Não permite novos vínculos em empresas bloqueadas ou excluídas.
        if (projetoBanco.getEmpresa() == null ||
                projetoBanco.getEmpresa().getStatus() != EnumStatusEmpresa.ATIVO) {
            throw new IllegalArgumentException("A empresa do projeto precisa estar ativa!");
        }

        // Busca o usuário que será vinculado
        Usuario desenvolvedorBanco = usuarioRepository.findById(
                request.desenvolvedorId()
        ).orElse(null);

        // Retorna 404 pelo Controller se o desenvolvedor não existir
        if (desenvolvedorBanco == null) {
            return null;
        }

    // A entidade Usuario valida:
    // - ROLE_DEV
    // - Desenvolvedor ativo
    // - Mesma empresa
    // - Vínculo não duplicado
        desenvolvedorBanco.adicionarProjeto(projetoBanco);

    // Salva pelo lado proprietário do relacionamento
        usuarioRepository.save(desenvolvedorBanco);

    // Atualiza o outro lado do relacionamento em memória
        projetoBanco.getDesenvolvedores().add(desenvolvedorBanco);

        return new ProjetoResponse(projetoBanco);
    }


    // Cria um novo projeto ou atualiza um existente
    @Transactional
    public ProjetoResponse salvarProjeto(ProjetoRequest projeto, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Somente ADMIN e GERENTE podem criar ou editar projetos
        if (usuarioLogado.role() != EnumRole.ROLE_ADMIN &&
                usuarioLogado.role() != EnumRole.ROLE_GERENTE) {

            throw new AccessDeniedException(
                    "Usuário sem permissão para criar ou editar projetos!"
            );
        }

        // Verifica os dados mínimos antes de criar ou editar o projeto.
        if (projeto == null || projeto.nome() == null || projeto.nome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do projeto!");
        }

        // NOVO PROJETO
        if (id == 0L) {

            Empresa empresaBanco = null;

            // ADMIN pode escolher a empresa do novo projeto
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
                    throw new AccessDeniedException(
                            "Gerente não possui empresa vinculada!"
                    );
                }

                // Ignora uma empresa diferente informada no Request
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

            // Impede criar projetos em empresas bloqueadas ou excluídas.
            if (empresaBanco.getStatus() != EnumStatusEmpresa.ATIVO) {
                throw new IllegalArgumentException(
                        "Não é possível criar projetos em uma empresa inativa!"
                );
            }

            // Cria o projeto
            Projeto projetoBanco = new Projeto(projeto);

            // Vincula o projeto à empresa validada
            projetoBanco.setEmpresa(empresaBanco);

            // Salva no banco de dados
            projetoRepository.save(projetoBanco);

            return new ProjetoResponse(projetoBanco);

        } else {

            // PROJETO EXISTENTE

            Projeto projetoBanco = projetoRepository.findById(id)
                    .orElse(null);

            // Não permite editar projetos inexistentes ou excluídos
            if (projetoBanco == null ||
                    projetoBanco.getStatus() == EnumStatusProjeto.EXCLUIDO) {
                return null;
            }

            // Verifica se o usuário pode editar este projeto
            verificarPermissaoProjeto(projetoBanco, usuarioLogado);

            // Não permite editar projetos de empresas bloqueadas ou excluídas.
            if (projetoBanco.getEmpresa() == null ||
                    projetoBanco.getEmpresa().getStatus() != EnumStatusEmpresa.ATIVO) {
                throw new IllegalArgumentException("A empresa do projeto precisa estar ativa!");
            }

            // Atualiza os dados básicos do projeto
            projetoBanco.setNome(projeto.nome());
            projetoBanco.setDescricao(projeto.descricao());

            // A entidade Projeto verifica se as datas são válidas
            projetoBanco.atualizarDatas(
                    projeto.dataInicio(),
                    projeto.dataPrevistaFim()
            );

            // Mantém a empresa original do projeto
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

        // Projetos inexistentes, excluídos ou de empresas excluídas retornam 404.
        if (projetoBanco == null ||
                projetoBanco.getStatus() == EnumStatusProjeto.EXCLUIDO ||
                projetoBanco.getEmpresa() == null ||
                projetoBanco.getEmpresa().getStatus() == EnumStatusEmpresa.EXCLUIDO) {
            return null;
        }

        // Verifica o acesso de ADMIN, GERENTE ou DEV
        verificarPermissaoProjeto(projetoBanco, usuarioLogado);

        return new ProjetoResponse(projetoBanco);
    }


    // Atualiza o status do projeto, incluindo exclusão lógica
    @Transactional
    public boolean atualizarStatus(EnumStatusProjeto status, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Somente ADMIN e GERENTE podem alterar o status
        if (usuarioLogado.role() != EnumRole.ROLE_ADMIN &&
                usuarioLogado.role() != EnumRole.ROLE_GERENTE) {

            throw new AccessDeniedException(
                    "Usuário sem permissão para alterar o status do projeto!"
            );
        }

        // Impede atualizar o projeto com status nulo
        if (status == null) {
            throw new IllegalArgumentException(
                    "Informe o status do projeto!"
            );
        }

        // Busca o projeto pelo ID
        Projeto projetoBanco = projetoRepository.findById(id)
                .orElse(null);

        // Não altera projetos inexistentes ou já excluídos
        if (projetoBanco == null ||
                projetoBanco.getStatus() == EnumStatusProjeto.EXCLUIDO) {
            return false;
        }

        // Verifica se o gerente pertence à empresa do projeto
        verificarPermissaoProjeto(projetoBanco, usuarioLogado);

        // Em empresa bloqueada, permite somente a exclusão para limpeza.
        // Uma empresa já excluída não deve possuir projetos modificáveis.
        if (projetoBanco.getEmpresa() == null ||
                projetoBanco.getEmpresa().getStatus() == EnumStatusEmpresa.EXCLUIDO) {
            return false;
        }

        if (projetoBanco.getEmpresa().getStatus() != EnumStatusEmpresa.ATIVO &&
                status != EnumStatusProjeto.EXCLUIDO) {
            throw new IllegalArgumentException("A empresa do projeto está bloqueada!");
        }

        // A entidade Projeto verifica se a exclusão é permitida.
        // Não permite excluir projetos com tarefas em ANDAMENTO.
        projetoBanco.setStatus(status);

        // Salva o novo status no banco
        projetoRepository.save(projetoBanco);

        return true;
    }
}
