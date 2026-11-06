package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.EmpresaRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import com.example.tasksync.domain.valueobject.CNPJ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Confere a situação atual do administrador no banco.
    // Assim, um token antigo não autoriza um usuário já desativado.
    private void verificarAdministradorAtivo() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null ||
                !(autenticacao.getPrincipal() instanceof UsuarioLogado)) {
            throw new AccessDeniedException("Usuário não autenticado!");
        }

        UsuarioLogado usuarioLogado = (UsuarioLogado) autenticacao.getPrincipal();
        Usuario usuarioBanco = usuarioRepository.findById(usuarioLogado.id())
                .orElse(null);

        if (usuarioBanco == null ||
                usuarioBanco.getStatus() != EnumStatusUsuario.ATIVO ||
                usuarioBanco.getRole() != EnumRole.ROLE_ADMIN) {
            throw new AccessDeniedException("Somente administradores ativos podem gerenciar empresas!");
        }
    }

    // Lista somente empresas que não foram excluídas logicamente.
    @Transactional(readOnly = true)
    public List<EmpresaResponse> listarTodasEmpresas() {
        verificarAdministradorAtivo();

        return empresaRepository.findAll()
                .stream()
                .filter(empresa -> empresa.getStatus() != EnumStatusEmpresa.EXCLUIDO)
                .map(EmpresaResponse::new)
                .toList();
    }

    // Cadastra uma empresa ou atualiza uma empresa existente.
    @Transactional
    public EmpresaResponse salvarEmpresa(EmpresaRequest empresa, Long id) {
        verificarAdministradorAtivo();

        if (empresa == null || id == null) {
            throw new IllegalArgumentException("Informe os dados da empresa!");
        }

        // NOVA EMPRESA
        if (id == 0L) {
            // O construtor utiliza o Value Object para validar o CNPJ.
            Empresa empresaBanco = new Empresa(empresa);

            // O CNPJ é consultado sem formatação para evitar duplicidade.
            if (empresaRepository.existsByCnpj_Cnpj(empresaBanco.getCnpj().getValor())) {
                throw new IllegalArgumentException("Já existe uma empresa cadastrada com este CNPJ!");
            }

            empresaRepository.save(empresaBanco);
            return new EmpresaResponse(empresaBanco);

        } else {
            // EMPRESA EXISTENTE
            Empresa empresaBanco = empresaRepository.findById(id).orElse(null);

            // Empresas excluídas não podem ser editadas nem recuperadas por PUT.
            if (empresaBanco == null ||
                    empresaBanco.getStatus() == EnumStatusEmpresa.EXCLUIDO) {
                return null;
            }

            CNPJ novoCnpj = new CNPJ(empresa.cnpj());
            String cnpjAnterior = empresaBanco.getCnpj().getValor();

            // Só procura duplicidade se o CNPJ realmente mudou.
            if (!novoCnpj.getValor().equals(cnpjAnterior) &&
                    empresaRepository.existsByCnpj_Cnpj(novoCnpj.getValor())) {
                throw new IllegalArgumentException("Já existe uma empresa cadastrada com este CNPJ!");
            }

            empresaBanco.setRazaoSocial(empresa.razaoSocial());
            empresaBanco.setCnpj(novoCnpj);
            empresaBanco.setNomeFantasia(empresa.nomeFantasia());

            empresaRepository.save(empresaBanco);
            return new EmpresaResponse(empresaBanco);
        }
    }

    // Busca a empresa, sem disponibilizar empresas já excluídas.
    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorId(Long id) {
        verificarAdministradorAtivo();

        Empresa empresaBanco = empresaRepository.findById(id).orElse(null);

        if (empresaBanco == null ||
                empresaBanco.getStatus() == EnumStatusEmpresa.EXCLUIDO) {
            return null;
        }

        return new EmpresaResponse(empresaBanco);
    }

    // Altera o status ou realiza a exclusão lógica da empresa.
    @Transactional
    public boolean atualizarStatus(EnumStatusEmpresa status, Long id) {
        verificarAdministradorAtivo();

        // Não aceita status nulo.
        if (status == null) {
            throw new IllegalArgumentException("Informe o status da empresa!");
        }

        Empresa empresaBanco = empresaRepository.findById(id).orElse(null);

        // Não permite alterar empresas inexistentes ou já excluídas.
        if (empresaBanco == null ||
                empresaBanco.getStatus() == EnumStatusEmpresa.EXCLUIDO) {
            return false;
        }

        if (status == EnumStatusEmpresa.EXCLUIDO) {
            // Antes de excluir a empresa, todos os projetos devem estar excluídos.
            boolean possuiProjetos = empresaBanco.getProjetos().stream()
                    .anyMatch(projeto -> projeto.getStatus() != EnumStatusProjeto.EXCLUIDO);

            if (possuiProjetos) {
                throw new IllegalArgumentException(
                        "Exclua os projetos da empresa antes de excluir a empresa!"
                );
            }

            // Também não pode haver usuários ainda ativos ou bloqueados.
            boolean possuiUsuarios = empresaBanco.getUsuarios().stream()
                    .anyMatch(usuario -> usuario.getStatus() != EnumStatusUsuario.EXCLUIDO);

            if (possuiUsuarios) {
                throw new IllegalArgumentException(
                        "Exclua os usuários da empresa antes de excluir a empresa!"
                );
            }
        }

        // BLOQUEADO pode voltar para ATIVO; EXCLUIDO não pode ser revertido.
        empresaBanco.setStatus(status);
        empresaRepository.save(empresaBanco);
        return true;
    }
}
