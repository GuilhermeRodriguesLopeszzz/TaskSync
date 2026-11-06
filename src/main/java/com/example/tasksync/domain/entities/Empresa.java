
package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.EmpresaRequest;
import com.example.tasksync.application.dto.EnumStatusEmpresa;
import com.example.tasksync.domain.valueobject.CNPJ;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// Faz com que somente os atributos marcados com @EqualsAndHashCode.Include
// sejam utilizados para comparar objetos, evitando problemas com relacionamentos.
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco gera o ID
    // Define que o ID será utilizado para comparar os objetos.
    @EqualsAndHashCode.Include
    private Long id;

    private String razaoSocial;

    // Incorpora o Value Object CNPJ na entidade Empresa.
    // A validação acontece no construtor da classe CNPJ.
    @Embedded
    private CNPJ cnpj;

    private String nomeFantasia;

    // Uma empresa pode possuir vários usuários.
    // mappedBy indica que o relacionamento é controlado por Usuario.empresa.
    @OneToMany(mappedBy = "empresa")
    @ToString.Exclude
    private List<Usuario> usuarios = new ArrayList<>();

    private EnumStatusEmpresa status = EnumStatusEmpresa.ATIVO;

    // Uma empresa pode possuir vários projetos.
    // mappedBy indica que o relacionamento é controlado por Projeto.empresa.
    @OneToMany(mappedBy = "empresa")
    @ToString.Exclude
    private List<Projeto> projetos = new ArrayList<>();

    public Empresa(EmpresaRequest empresa) {
        this.setRazaoSocial(empresa.razaoSocial());
        this.setCnpj(new CNPJ(empresa.cnpj()));
        this.setNomeFantasia(empresa.nomeFantasia());
    }
}
