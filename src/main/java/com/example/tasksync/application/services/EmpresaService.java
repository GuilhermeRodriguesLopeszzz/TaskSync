package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.EmpresaRequest;
import com.example.tasksync.application.dto.EmpresaResponse;
import com.example.tasksync.application.dto.EnumStatusEmpresa;
import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.repository.EmpresaRepository;
import com.example.tasksync.domain.valueobject.CNPJ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public List<EmpresaResponse> listarTodasEmpresas() {

        return empresaRepository.findAll()
                .stream()
                .map(EmpresaResponse::new)
                .toList();
    }


    public EmpresaResponse salvarEmpresa(EmpresaRequest empresa, Long id) {

        // NOVA EMPRESA
        if (id == 0L) {

            // Cria a empresa e valida o CNPJ pelo Value Object
            Empresa empresaBanco = new Empresa(empresa);

            // Verifica se o CNPJ já existe no banco
            if (empresaRepository.existsByCnpj_Cnpj(
                    empresaBanco.getCnpj().getValor())) {

                throw new IllegalArgumentException(
                        "Já existe uma empresa cadastrada com este CNPJ!"
                );
            }

            empresaRepository.save(empresaBanco);

            return new EmpresaResponse(empresaBanco);

        } else {

            // EMPRESA EXISTENTE
            Empresa empresaBanco = empresaRepository.findById(id)
                    .orElse(null);

            if (empresaBanco == null) {
                return null;
            }

            // Cria e valida o novo CNPJ
            CNPJ novoCnpj = new CNPJ(empresa.cnpj());

            // Guarda o valor anterior para comparar
            String cnpjAnterior = empresaBanco.getCnpj().getValor();

            // Se o CNPJ mudou, verifica se já pertence a outra empresa
            if (!novoCnpj.getValor().equals(cnpjAnterior)) {

                if (empresaRepository.existsByCnpj_Cnpj(
                        novoCnpj.getValor())) {

                    throw new IllegalArgumentException(
                            "Já existe uma empresa cadastrada com este CNPJ!"
                    );
                }
            }

            // Atualiza os dados da empresa
            empresaBanco.setRazaoSocial(empresa.razaoSocial());
            empresaBanco.setCnpj(novoCnpj);
            empresaBanco.setNomeFantasia(empresa.nomeFantasia());

            empresaRepository.save(empresaBanco);

            return new EmpresaResponse(empresaBanco);
        }
    }


    public EmpresaResponse buscarPorId(Long id) {

        var empresa = empresaRepository.findById(id).orElse(null);
        if (empresa == null) {
            return null;
        }

        return new EmpresaResponse(empresa);
    }

    public boolean atualizarStatus(EnumStatusEmpresa status, Long id) {

        Empresa empresaBanco = empresaRepository.findById(id).orElse(null);
        if (empresaBanco != null) {
            empresaBanco.setStatus(status);
            empresaRepository.save(empresaBanco);
            return true;
        }
        return false;
    }
}