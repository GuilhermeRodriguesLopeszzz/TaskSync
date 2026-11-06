package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.EmpresaRequest;
import com.example.tasksync.application.dto.EmpresaResponse;
import com.example.tasksync.application.dto.EnumStatusEmpresa;
import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.repository.EmpresaRepository;
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

        // Nova Empresa
        if (id == 0L) {

            // Verifica se já existe uma empresa com o CNPJ informado
            if (empresaRepository.existsByCnpj(empresa.cnpj())) {
                throw new IllegalArgumentException(
                        "Já existe uma empresa cadastrada com este CNPJ!"
                );
            }

            Empresa empresaBanco = new Empresa(empresa);
            empresaRepository.save(empresaBanco);

            return new EmpresaResponse(empresaBanco);
        } else {
            //Empresa existente
            Empresa empresaBanco = empresaRepository.findById(id).orElse(null);
            if (empresaBanco != null) {
                empresaBanco.setRazaoSocial(empresa.razaoSocial());
                empresaBanco.setCnpj(empresa.cnpj());
                empresaBanco.setNomeFantasia(empresa.nomeFantasia());
                empresaRepository.save(empresaBanco);
                return new EmpresaResponse(empresaBanco);
            }
        }

        return null;
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