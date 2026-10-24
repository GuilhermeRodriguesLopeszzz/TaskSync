package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.EnumStatusTarefa;
import com.example.tasksync.application.dto.TarefaRequest;
import com.example.tasksync.application.dto.UsuarioRequest;
import com.example.tasksync.domain.valueobject.CPF;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
//Get and Setters
@Data
//Construtor sem argumentos
@NoArgsConstructor
//Construtor com argumentos
@AllArgsConstructor
public class Tarefa {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String titulo;

        private String descricao;

        private LocalDate dataPrazo;

        private EnumStatusTarefa status = EnumStatusTarefa.PENDENTE;

        @ManyToOne
        @JoinColumn(name = "projeto_id")
        private Projeto projeto;

        public Tarefa(TarefaRequest tarefa) {
                this.setTitulo(tarefa.titulo());
                this.setDescricao(tarefa.descricao());
                this.setDataPrazo(tarefa.dataPrazo());
        }

    }

