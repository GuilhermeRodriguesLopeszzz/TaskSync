package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.EmpresaRequest;
import com.example.tasksync.application.dto.EmpresaResponse;
import com.example.tasksync.application.dto.UsuarioRequest;
import com.example.tasksync.application.dto.UsuarioResponse;
import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.EmpresaRepository;
import com.example.tasksync.domain.valueobject.CPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public EmpresaResponse salvarEmpresa(EmpresaRequest empresa, Long id) {

        //Novo Usuario
        if(id == 0L) {
            Empresa empresaBanco = new Empresa(empresa);
            empresaRepository.save(empresaBanco);

            return new EmpresaResponse(empresaBanco);
        }else {
            //Usuario existente
            Empresa empresaBanco = empresaRepository.findById(id).orElse(null);
            if(empresaBanco!= null ){
                empresaBanco.setRazaoSocial(empresa.razaoSocial());
                empresaBanco.setCnpj(empresa.cnpj());
                empresaBanco.setNomeFantasia(empresa.nomeFantasia());
                //usuarioBanco.setEmpresa_id(usuarioLogado.empresa_id());
                empresaRepository.save(empresaBanco);
                return  new EmpresaResponse(empresaBanco);
            }

        }

        return null;
    }
}
