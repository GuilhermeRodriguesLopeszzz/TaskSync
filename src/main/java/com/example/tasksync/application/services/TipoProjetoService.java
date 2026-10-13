package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.entities.TipoProjeto;
import com.example.tasksync.domain.repository.ProjetoRepository;
import com.example.tasksync.domain.repository.TipoProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TipoProjetoService {
    @Autowired
    private ProjetoRepository projetoRepository;


    @Value("${spring.secret}")
    private String secret;
    @Autowired
    private TipoProjetoRepository tipoProjetoRepository;


    public CriarTipoProjetoResponse criarTipoProjeto(CriarTipoProjetoRequest criarTipoProjetoRequest) {

        if(!criarTipoProjetoRequest.secretKey().equals(secret)){
            return new CriarTipoProjetoResponse(0L,"Tipo projeto salvo com sucesso!");

        }

        TipoProjeto tipoProjetoSalvar = new TipoProjeto(criarTipoProjetoRequest);
        tipoProjetoRepository.save(tipoProjetoSalvar);

        return new CriarTipoProjetoResponse(tipoProjetoSalvar.getId(),"Tipo projeto salvo com sucesso!");
    }
}
