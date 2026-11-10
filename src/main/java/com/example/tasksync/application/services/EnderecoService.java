package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.EnderecoResponse;
import com.example.tasksync.domain.interfaces.IViaCepExternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

    @Autowired
    private IViaCepExternalRepository viaCepExternalRepository;


    public EnderecoResponse BuscarEndereco(String cep) {

        var enderecoCompleto =  viaCepExternalRepository.buscarEnderecoViaCepPorCep(cep);

        return new EnderecoResponse( enderecoCompleto.getCep(),
                enderecoCompleto.getLogradouro(),
                enderecoCompleto.getBairro(),
                enderecoCompleto.getLocalidade(),
                enderecoCompleto.getUf());


    }
}
