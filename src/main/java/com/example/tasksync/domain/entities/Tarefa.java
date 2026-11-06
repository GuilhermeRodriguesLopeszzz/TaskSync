package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.EnumStatusTarefa;
import com.example.tasksync.application.dto.TarefaRequest;
import com.example.tasksync.application.dto.UsuarioRequest;
import com.example.tasksync.domain.valueobject.CPF;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
//Get and Setters
@Data
//Construtor sem argumentos
@NoArgsConstructor
//Construtor com argumentos
@AllArgsConstructor
// Faz com que somente os atributos marcados com @EqualsAndHashCode.Include
// sejam utilizados para comparar objetos, evitando problemas com relacionamentos.
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tarefa {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        // Define que o ID será utilizado para comparar os objetos.
        @EqualsAndHashCode.Include
        private Long id;

        private String titulo;

        private String descricao;

        private LocalDate dataPrazo;

        private EnumStatusTarefa status = EnumStatusTarefa.PENDENTE;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "projeto_id",referencedColumnName = "id")
        @ToString.Exclude
        private Projeto projeto;

        // Desenvolvedor responsável pela tarefa
        @ManyToOne
        @JoinColumn(name = "desenvolvedor_id", referencedColumnName = "id")
        @ToString.Exclude
        private Usuario desenvolvedor;

        public Tarefa(TarefaRequest tarefa) {
                this.setTitulo(tarefa.titulo());
                this.setDescricao(tarefa.descricao());
                this.setDataPrazo(tarefa.dataPrazo());
        }

        // Valida o título antes de atribuir o valor
        public void setTitulo(String titulo) {
                // Impede títulos nulos, vazios ou apenas com espaços
                if (titulo == null || titulo.isBlank()) {
                        throw new IllegalArgumentException(
                                "O título da tarefa é obrigatório!"
                        );
                }

                this.titulo = titulo;
        }

    }

