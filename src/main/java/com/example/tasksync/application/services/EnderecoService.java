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

        // Aceita CEP com ou sem hífen, mas exige oito dígitos.
        if (cep == null || !cep.matches("[0-9]{5}-?[0-9]{3}")) {
            throw new IllegalArgumentException("Informe um CEP válido com 8 dígitos!");
        }

        var enderecoCompleto = viaCepExternalRepository.buscarEnderecoViaCepPorCep(cep.replace("-", ""));

        return new EnderecoResponse( enderecoCompleto.getCep(),
                enderecoCompleto.getLogradouro(),
                enderecoCompleto.getBairro(),
                enderecoCompleto.getLocalidade(),
                enderecoCompleto.getUf());


    }
}
