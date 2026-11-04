
package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.EnumStatusProjeto;
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

    private LocalDate dataInicio;

    private LocalDate dataPrevistaFim;

    public Projeto(ProjetoRequest projeto) {
        this.setNome(projeto.nome());
        this.setDescricao(projeto.descricao());
        this.setDataInicio(projeto.dataInicio());
        this.setDataPrevistaFim(projeto.dataPrevistaFim());
    }
}
