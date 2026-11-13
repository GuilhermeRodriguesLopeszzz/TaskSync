
package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.EmpresaRequest;
import com.example.tasksync.application.dto.EnumStatusEmpresa;
import com.example.tasksync.application.dto.EnumStatusProjeto;
import com.example.tasksync.application.dto.EnumStatusUsuario;
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


    // Atualiza o status da empresa
    public void setStatus(EnumStatusEmpresa status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Informe o status da empresa!"
            );
        }

        // Não permite recuperar uma empresa já excluída
        if (this.status == EnumStatusEmpresa.EXCLUIDO) {
            throw new IllegalArgumentException(
                    "Uma empresa excluída não pode ser reativada!"
            );
        }

        if (status == EnumStatusEmpresa.EXCLUIDO) {

            // Não permite excluir empresas com projetos não excluídos
            boolean possuiProjetos = this.projetos.stream()
                    .anyMatch(projeto ->
                            projeto.getStatus() != EnumStatusProjeto.EXCLUIDO
                    );

            if (possuiProjetos) {
                throw new IllegalArgumentException(
                        "Exclua os projetos da empresa antes de excluir a empresa!"
                );
            }

            // Não permite excluir empresas com usuários não excluídos
            boolean possuiUsuarios = this.usuarios.stream()
                    .anyMatch(usuario ->
                            usuario.getStatus() != EnumStatusUsuario.EXCLUIDO
                    );

            if (possuiUsuarios) {
                throw new IllegalArgumentException(
                        "Exclua os usuários da empresa antes de excluir a empresa!"
                );
            }
        }

        this.status = status;
    }

}
