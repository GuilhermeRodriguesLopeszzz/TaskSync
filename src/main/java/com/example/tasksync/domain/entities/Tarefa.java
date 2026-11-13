package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.*;
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


        // Atribui um desenvolvedor à tarefa
        public void atribuirDesenvolvedor(Usuario desenvolvedor) {

                if (desenvolvedor == null ||
                        desenvolvedor.getRole() != EnumRole.ROLE_DEV) {
                        throw new IllegalArgumentException(
                                "Somente desenvolvedores podem receber tarefas!"
                        );
                }

                if (desenvolvedor.getStatus() != EnumStatusUsuario.ATIVO) {
                        throw new IllegalArgumentException(
                                "O desenvolvedor precisa estar ativo!"
                        );
                }

                // A tarefa precisa estar associada a um projeto
                if (this.projeto == null) {
                        throw new IllegalArgumentException(
                                "A tarefa precisa pertencer a um projeto!"
                        );
                }

                // Desenvolvedor e projeto precisam ser da mesma empresa
                if (desenvolvedor.getEmpresa() == null ||
                        this.projeto.getEmpresa() == null ||
                        !desenvolvedor.getEmpresa().getId().equals(
                                this.projeto.getEmpresa().getId())) {

                        throw new IllegalArgumentException(
                                "Desenvolvedor não pertence à empresa do projeto!"
                        );
                }

                // O desenvolvedor precisa participar do projeto
                if (!desenvolvedor.getProjetos().contains(this.projeto)) {
                        throw new IllegalArgumentException(
                                "Desenvolvedor não está vinculado a este projeto!"
                        );
                }

                this.desenvolvedor = desenvolvedor;
        }

        // Atualiza o status respeitando o responsável
        public void setStatus(EnumStatusTarefa status) {

                if (status == null) {
                        throw new IllegalArgumentException(
                                "Informe o status da tarefa!"
                        );
                }

                // A exclusão lógica não exige responsável ativo
                if (status != EnumStatusTarefa.EXCLUIDO) {

                        if (this.desenvolvedor == null ||
                                this.desenvolvedor.getStatus() != EnumStatusUsuario.ATIVO) {

                                throw new IllegalArgumentException(
                                        "O responsável pela tarefa precisa estar ativo!"
                                );
                        }
                }

                this.status = status;
        }


}

