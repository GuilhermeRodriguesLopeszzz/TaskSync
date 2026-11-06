
package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.EnumStatusProjeto;
import com.example.tasksync.application.dto.EnumStatusTarefa;
import com.example.tasksync.application.dto.ProjetoRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
// Getters e Setters
@Data
// Construtor sem argumentos
@NoArgsConstructor
// Construtor com argumentos
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;

    private String descricao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id", referencedColumnName = "id")
    private Empresa empresa;

    // Um projeto pode possuir vários desenvolvedores
    // O relacionamento é controlado pela entidade Usuario
    @ManyToMany(mappedBy = "projetos")
    @ToString.Exclude
    private Set<Usuario> desenvolvedores = new HashSet<>();

    // Um projeto pode possuir várias tarefas
    @OneToMany(mappedBy = "projeto")
    // Evita que o Lombok inclua este relacionamento no metodo toString().
    // Isso impede um loop infinito entre entidades relacionadas,
    // como Projeto -> Tarefa -> Projeto, evitando StackOverflowError.
    @ToString.Exclude
    private List<Tarefa> tarefas = new ArrayList<>();

    /*O new ArrayList<>() inicializa a lista de tarefas vazia,
    evitando que ela fique nula quando um projeto é criado.
    Assim, posso adicionar tarefas sem precisar inicializar a lista depois.
     */

    private EnumStatusProjeto status = EnumStatusProjeto.MEDIA;

    // Impede que o Lombok crie setters individuais para as datas.
    // Assim, elas só podem ser alteradas pelo metodo atualizarDatas(),
    // que verifica se a data final é anterior à inicial.
    @Setter(AccessLevel.NONE)
    private LocalDate dataInicio;

    @Setter(AccessLevel.NONE)
    private LocalDate dataPrevistaFim;

    // Atualiza as duas datas do projeto de forma segura.
// Impede que a data final seja anterior à data inicial.
    public void atualizarDatas(LocalDate inicio, LocalDate fim) {

        // Só compara se as duas datas estiverem preenchidas
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            throw new IllegalArgumentException(
                    "A data final não pode ser anterior à data inicial!"
            );
        }

        // Atualiza as datas somente após passar pela validação
        this.dataInicio = inicio;
        this.dataPrevistaFim = fim;
    }

    public Projeto(ProjetoRequest projeto) {
        this.setNome(projeto.nome());
        this.setDescricao(projeto.descricao());
        // Valida as datas antes de criar o projeto
        this.atualizarDatas(projeto.dataInicio(), projeto.dataPrevistaFim());
    }

    // Atualiza o status do projeto
    public void setStatus(EnumStatusProjeto status) {

        // Se o projeto estiver sendo excluído
        if (status == EnumStatusProjeto.EXCLUIDO) {

            // Verifica se existe alguma tarefa em andamento
            boolean tarefaEmAndamento = this.tarefas.stream()
                    .anyMatch(tarefa ->
                            tarefa.getStatus() == EnumStatusTarefa.ANDAMENTO
                    );

            // Impede a exclusão se houver tarefa em andamento
            if (tarefaEmAndamento) {
                throw new IllegalArgumentException(
                        "Não é possível excluir um projeto com tarefas em andamento!"
                );
            }
        }

        // Atualiza o status caso a regra seja respeitada
        this.status = status;
    }
}
