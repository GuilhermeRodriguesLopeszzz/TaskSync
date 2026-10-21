package com.example.tasksync.application.services;

import com.example.tasksync.application.dto.DesenvolvedorRequest;
import com.example.tasksync.application.dto.DesenvolvedorResponse;
import com.example.tasksync.application.dto.EnumStatusDesenvolvedor;
import com.example.tasksync.domain.entities.Desenvolvedor;
import com.example.tasksync.domain.repository.DesenvolvedorRepository;
import com.example.tasksync.domain.valueobject.CPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesenvolvedorService {

    @Autowired
    private DesenvolvedorRepository desenvolvedorRepository;

    public List<DesenvolvedorResponse> listarTodosDesenvolvedores() {

        return desenvolvedorRepository.findAll()
                .stream()
                .map(DesenvolvedorResponse::new)
                .toList();
    }

    public DesenvolvedorResponse salvarDesenvolvedor(DesenvolvedorRequest desenvolvedor, Long id) {

        //Novo Desenvolvedor
        if (id == 0L) {
            Desenvolvedor desenvolvedorBanco = new Desenvolvedor(desenvolvedor);
            desenvolvedorRepository.save(desenvolvedorBanco);

            return new DesenvolvedorResponse(desenvolvedorBanco);
        } else {
            //Desenvolvedor existente
            Desenvolvedor desenvolvedorBanco = desenvolvedorRepository.findById(id).orElse(null);
            if (desenvolvedorBanco != null) {
                desenvolvedorBanco.setNome(desenvolvedor.nome());
                desenvolvedorBanco.setCpf(new CPF(desenvolvedor.cpf()));
                desenvolvedorBanco.setEmail(desenvolvedor.email());
                desenvolvedorBanco.setSenha(desenvolvedor.senha());
                desenvolvedorRepository.save(desenvolvedorBanco);
                return new DesenvolvedorResponse(desenvolvedorBanco);
            }
        }

        return null;
    }

    public DesenvolvedorResponse buscarPorId(Long id) {

        var desenvolvedor = desenvolvedorRepository.findById(id).orElse(null);
        if (desenvolvedor == null) {
            return null;
        }

        return new DesenvolvedorResponse(desenvolvedor);
    }

    public boolean atualizarStatus(EnumStatusDesenvolvedor status, Long id) {

        Desenvolvedor desenvolvedorBanco = desenvolvedorRepository.findById(id).orElse(null);
        if (desenvolvedorBanco != null) {
            desenvolvedorBanco.setStatus(status);
            desenvolvedorRepository.save(desenvolvedorBanco);
            return true;
        }
        return false;
    }
}