package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.Projeto;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.ProjetoRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TipoProjetoService {
    @Autowired
    private ProjetoRepository projetoRepository;


    @Value("${spring.secret}")
    private String secret;


    public CriarAdminResponse criarAdmin(CriarAdminRequest criarAdminRequest) {

        if(!criarAdminRequest.secretKey().equals(secret)){
            return new CriarAdminResponse(0L,"Usuario Salvo com sucesso!");

        }

        Projeto tipoProjetoSalvar = new Projeto(criarAdminRequest);
        projetoRepository.save(tipoProjetoSalvar);

        return new CriarAdminResponse(tipoProjetoSalvar.getId(),"Tipo projeto salvo com sucesso!");
    }
}
