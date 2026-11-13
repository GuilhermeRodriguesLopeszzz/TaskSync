package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Projeto;
import com.example.tasksync.domain.entities.Tarefa;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.ProjetoRepository;
import com.example.tasksync.domain.repository.TarefaRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProjetoRepository projetoRepository;


    // Consulta o usuário no banco para verificar o status atual da conta.
    public UsuarioLogado getUsuarioLogado() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null ||
                !(autenticacao.getPrincipal() instanceof UsuarioLogado)) {
            throw new AccessDeniedException("Usuário não autenticado!");
        }

        UsuarioLogado dadosToken = (UsuarioLogado) autenticacao.getPrincipal();
        Usuario usuarioBanco = usuarioRepository.findById(dadosToken.id()).orElse(null);

        if (usuarioBanco == null ||
                usuarioBanco.getStatus() != EnumStatusUsuario.ATIVO) {
            throw new AccessDeniedException("Usuário inativo ou excluído!");
        }

        // ADMIN não precisa ter empresa; os outros perfis precisam de empresa ativa.
        if (usuarioBanco.getRole() != EnumRole.ROLE_ADMIN &&
                (usuarioBanco.getEmpresa() == null ||
                        usuarioBanco.getEmpresa().getStatus() != EnumStatusEmpresa.ATIVO)) {
            throw new AccessDeniedException("A empresa do usuário está inativa!");
        }

        return new UsuarioLogado(usuarioBanco);
    }


    // Tarefa excluída, ou pertencente a projeto/empresa excluídos, não é exibida.
    private boolean tarefaDisponivel(Tarefa tarefa) {
        return tarefa.getStatus() != EnumStatusTarefa.EXCLUIDO &&
                tarefa.getProjeto() != null &&
                tarefa.getProjeto().getStatus() != EnumStatusProjeto.EXCLUIDO &&
                tarefa.getProjeto().getEmpresa() != null &&
                tarefa.getProjeto().getEmpresa().getStatus() != EnumStatusEmpresa.EXCLUIDO;
    }


    // Verifica se o usuário pode acessar uma tarefa
    private void verificarPermissaoTarefa(
            Tarefa tarefaBanco,
            UsuarioLogado usuarioLogado) {

        // ADMIN pode acessar qualquer tarefa
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {
            return;
        }

        // GERENTE só pode acessar tarefas da própria empresa
        if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            if (usuarioLogado.empresaId() == null ||
                    tarefaBanco.getProjeto() == null ||
                    tarefaBanco.getProjeto().getEmpresa() == null ||
                    !usuarioLogado.empresaId().equals(
                            tarefaBanco.getProjeto().getEmpresa().getId())) {

                // Retorna erro de permissão (403)
                throw new AccessDeniedException(
                        "Gerente não pode acessar tarefas de outra empresa!"
                );
            }

            return;
        }

        // DEV só pode acessar tarefas atribuídas a ele
        if (usuarioLogado.role() == EnumRole.ROLE_DEV) {

            if (tarefaBanco.getDesenvolvedor() == null ||
                    !usuarioLogado.id().equals(
                            tarefaBanco.getDesenvolvedor().getId())) {

                // Retorna erro de permissão (403)
                throw new AccessDeniedException(
                        "Desenvolvedor não pode acessar esta tarefa!"
                );
            }

            return;
        }

        // Bloqueia qualquer outro perfil não autorizado
        throw new AccessDeniedException(
                "Usuário sem permissão para acessar tarefas!"
        );
    }


    // Lista as tarefas conforme o perfil do usuário
    @Transactional(readOnly = true)
    public List<TarefaResponse> listarTodasTarefas() {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Evita mostrar tarefas excluídas, inclusive as de projetos excluídos.
        List<Tarefa> tarefasBanco = tarefaRepository.findAll()
                .stream()
                .filter(this::tarefaDisponivel)
                .toList();

        // ADMIN visualiza todas as tarefas
        if (usuarioLogado.role() == EnumRole.ROLE_ADMIN) {

            return tarefasBanco.stream()
                    .map(TarefaResponse::new)
                    .toList();

        } else if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            // GERENTE visualiza tarefas da própria empresa
            return tarefasBanco.stream()
                    .filter(tarefa ->
                            usuarioLogado.empresaId() != null &&
                                    tarefa.getProjeto() != null &&
                                    tarefa.getProjeto().getEmpresa() != null &&
                                    usuarioLogado.empresaId().equals(
                                            tarefa.getProjeto().getEmpresa().getId()
                                    ))
                    .map(TarefaResponse::new)
                    .toList();

        } else if (usuarioLogado.role() == EnumRole.ROLE_DEV) {

            // DEV visualiza somente tarefas atribuídas a ele
            return tarefasBanco.stream()
                    .filter(tarefa ->
                            tarefa.getDesenvolvedor() != null &&
                                    usuarioLogado.id().equals(
                                            tarefa.getDesenvolvedor().getId()
                                    ))
                    .map(TarefaResponse::new)
                    .toList();

        } else {

            // Usuário sem permissão recebe 403
            throw new AccessDeniedException(
                    "Usuário sem permissão para listar tarefas!"
            );
        }
    }


    // Cria ou atualiza uma tarefa
    @Transactional
    public TarefaResponse salvarTarefa(TarefaRequest tarefa, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Somente ADMIN e GERENTE podem criar ou editar tarefas
        if (usuarioLogado.role() != EnumRole.ROLE_ADMIN &&
                usuarioLogado.role() != EnumRole.ROLE_GERENTE) {
            throw new AccessDeniedException(
                    "Usuário sem permissão para criar ou editar tarefas!"
            );
        }

        // Verifica se os IDs foram informados
        if (tarefa == null ||
                id == null ||
                tarefa.projetoId() == null ||
                tarefa.desenvolvedorId() == null) {
            throw new IllegalArgumentException(
                    "Informe o projeto e o desenvolvedor!"
            );
        }

        Tarefa tarefaBanco = null;

        // Se for edição, busca a tarefa existente
        if (id != 0L) {

            tarefaBanco = tarefaRepository.findById(id).orElse(null);

            if (tarefaBanco == null || !tarefaDisponivel(tarefaBanco)) {
                return null;
            }

            verificarPermissaoTarefa(tarefaBanco, usuarioLogado);
        }

        // Busca o projeto
        Projeto projetoBanco = projetoRepository.findById(
                tarefa.projetoId()
        ).orElse(null);

        if (projetoBanco == null) {
            throw new IllegalArgumentException(
                    "Projeto não encontrado!"
            );
        }

        // Não permite usar projetos excluídos
        if (projetoBanco.getStatus() == EnumStatusProjeto.EXCLUIDO) {
            throw new IllegalArgumentException(
                    "Não é possível usar um projeto excluído!"
            );
        }

        // GERENTE só pode gerenciar projetos da própria empresa
        if (usuarioLogado.role() == EnumRole.ROLE_GERENTE) {

            if (usuarioLogado.empresaId() == null ||
                    projetoBanco.getEmpresa() == null ||
                    !usuarioLogado.empresaId().equals(
                            projetoBanco.getEmpresa().getId())) {

                throw new AccessDeniedException(
                        "Gerente não pode gerenciar tarefas de outra empresa!"
                );
            }
        }

        // Empresa do projeto precisa estar ativa
        if (projetoBanco.getEmpresa() == null ||
                projetoBanco.getEmpresa().getStatus() != EnumStatusEmpresa.ATIVO) {
            throw new IllegalArgumentException(
                    "A empresa do projeto precisa estar ativa!"
            );
        }

        // Busca o desenvolvedor
        Usuario desenvolvedorBanco = usuarioRepository.findById(
                tarefa.desenvolvedorId()
        ).orElse(null);

        if (desenvolvedorBanco == null) {
            throw new IllegalArgumentException(
                    "Desenvolvedor não encontrado!"
            );
        }

        // NOVA TAREFA
        if (id == 0L) {

            tarefaBanco = new Tarefa(tarefa);

            // Define o projeto antes de atribuir o desenvolvedor
            tarefaBanco.setProjeto(projetoBanco);

            // A entidade Tarefa valida o responsável
            tarefaBanco.atribuirDesenvolvedor(desenvolvedorBanco);

        } else {

            // TAREFA EXISTENTE

            // Não permite mudar a tarefa para outro projeto
            if (tarefaBanco.getProjeto() == null ||
                    !tarefaBanco.getProjeto().getId().equals(
                            projetoBanco.getId())) {

                throw new IllegalArgumentException(
                        "Tarefa não pertence ao projeto informado!"
                );
            }

            // A entidade valida os dados necessários
            tarefaBanco.setTitulo(tarefa.titulo());
            tarefaBanco.setDescricao(tarefa.descricao());
            tarefaBanco.setDataPrazo(tarefa.dataPrazo());

            // A entidade verifica o vínculo do desenvolvedor
            tarefaBanco.atribuirDesenvolvedor(desenvolvedorBanco);
        }

        // Salva a tarefa
        tarefaRepository.save(tarefaBanco);

        return new TarefaResponse(tarefaBanco);
    }



    // Busca uma tarefa pelo ID, respeitando as permissões
    @Transactional(readOnly = true)
    public TarefaResponse buscarPorId(Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        Tarefa tarefaBanco = tarefaRepository.findById(id)
                .orElse(null);

        if (tarefaBanco == null || !tarefaDisponivel(tarefaBanco)) {
            return null;
        }

        // Verifica se o usuário pode consultar a tarefa
        verificarPermissaoTarefa(tarefaBanco, usuarioLogado);

        return new TarefaResponse(tarefaBanco);
    }


    // Atualiza o status de uma tarefa
    @Transactional
    public boolean atualizarStatus(EnumStatusTarefa status, Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Verifica se o status foi informado
        if (status == null) {
            throw new IllegalArgumentException(
                    "Informe o status da tarefa!"
            );
        }

        // Busca a tarefa
        Tarefa tarefaBanco = tarefaRepository.findById(id)
                .orElse(null);

        // Tarefas excluídas não podem ser alteradas
        if (tarefaBanco == null || !tarefaDisponivel(tarefaBanco)) {
            return false;
        }

        // Verifica as permissões do usuário
        verificarPermissaoTarefa(tarefaBanco, usuarioLogado);

        // DEV não pode excluir tarefas
        if (usuarioLogado.role() == EnumRole.ROLE_DEV &&
                status == EnumStatusTarefa.EXCLUIDO) {

            throw new AccessDeniedException(
                    "Desenvolvedor não possui permissão para excluir tarefas!"
            );
        }

        // Empresa bloqueada permite somente exclusão
        if (tarefaBanco.getProjeto().getEmpresa().getStatus()
                != EnumStatusEmpresa.ATIVO &&
                status != EnumStatusTarefa.EXCLUIDO) {
            throw new IllegalArgumentException(
                    "A empresa da tarefa está bloqueada!"
            );
        }

        // A entidade Tarefa valida o responsável e o status
        tarefaBanco.setStatus(status);

        tarefaRepository.save(tarefaBanco);

        return true;
    }



    // Realiza a exclusão lógica da tarefa
    @Transactional
    public boolean excluirTarefa(Long id) {

        UsuarioLogado usuarioLogado = getUsuarioLogado();

        // Somente ADMIN e GERENTE podem excluir tarefas
        if (usuarioLogado.role() != EnumRole.ROLE_ADMIN &&
                usuarioLogado.role() != EnumRole.ROLE_GERENTE) {

            throw new AccessDeniedException(
                    "Usuário sem permissão para excluir tarefas!"
            );
        }

        // Reutiliza o atualizarStatus para validar a empresa
        // e alterar o status da tarefa para EXCLUIDO
        return atualizarStatus(EnumStatusTarefa.EXCLUIDO, id);
    }
}
