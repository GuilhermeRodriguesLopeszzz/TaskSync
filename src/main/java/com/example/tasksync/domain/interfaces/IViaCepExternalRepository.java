package com.example.tasksync.domain.interfaces;

import com.example.tasksync.infra.dto.ViaCepResponse;

public interface IViaCepExternalRepository {

    ViaCepResponse buscarEnderecoViaCepPorCep(String cep);


}
