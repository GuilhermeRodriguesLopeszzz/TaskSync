package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.EmpresaRequest;
import com.example.tasksync.application.dto.EnumStatusEmpresa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // o banco gera o id
    private Long id;

    private String razaoSocial;

    // unique: não permite duas empresas com o mesmo CNPJ
    @Column(unique = true)
    private String cnpj;

    private String nomeFantasia;

    // 1 empresa tem N usuários. "mappedBy" diz que a FK está em Usuario.empresa.
    // Os dois Exclude evitam o loop infinito do @Data (Empresa -> Usuario -> Empresa...).
    @OneToMany(mappedBy = "empresa")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Usuario> usuarios = new ArrayList<>();

    private EnumStatusEmpresa status = EnumStatusEmpresa.ATIVO;

    // 1 empresa tem N projetos. A FK está em Projeto.empresa.
    @OneToMany(mappedBy = "empresa")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Projeto> projetos = new ArrayList<>();

    public Empresa(EmpresaRequest empresa) {
        this.setRazaoSocial(empresa.razaoSocial());
        this.setCnpj(empresa.cnpj());
        this.setNomeFantasia(empresa.nomeFantasia());
    }
}