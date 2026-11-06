
package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.domain.valueobject.CPF;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// Faz com que somente os atributos marcados com @EqualsAndHashCode.Include
// sejam utilizados para comparar objetos, evitando problemas com relacionamentos.
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Define que o ID será utilizado para comparar os objetos.
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;

    @Embedded
    private CPF cpf;

    private String senha;

    private String email;

    private EnumRole role = EnumRole.ROLE_GERENTE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id", referencedColumnName = "id")
    private Empresa empresa;

    // Um desenvolvedor pode participar de vários projetos
    @ManyToMany
    @JoinTable(
            name = "projeto_desenvolvedor",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "projeto_id")
    )
    @ToString.Exclude
    private Set<Projeto> projetos = new HashSet<>();


    private EnumStatusUsuario status = EnumStatusUsuario.ATIVO;

    public void setEmail(String email) {
        if (!email.isEmpty() && !email.contains("@")) {
            throw new IllegalArgumentException("Email Invalido!");
        }
        this.email = email;
    }

    // Vincula um desenvolvedor a um projeto
    public void adicionarProjeto(Projeto projeto) {

        // Somente desenvolvedores podem participar de projetos
        if (this.role != EnumRole.ROLE_DEV) {
            throw new IllegalArgumentException(
                    "Somente desenvolvedores podem participar dos projetos!"
            );
        }

        // Impede vincular o mesmo desenvolvedor duas vezes
        if (this.projetos.contains(projeto)) {
            throw new IllegalArgumentException(
                    "Desenvolvedor já está vinculado a este projeto!"
            );
        }

        // Adiciona o projeto ao desenvolvedor
        this.projetos.add(projeto);
    }

    public Usuario(CriarAdminRequest criarAdminRequest) {
        this.setCpf(new CPF(criarAdminRequest.cpf()));
        this.setNome(criarAdminRequest.nome());
        this.setSenha(criarAdminRequest.senha());
        this.setEmail(criarAdminRequest.email());
        this.setRole(EnumRole.ROLE_ADMIN);
    }

    public Usuario(UsuarioRequest usuario) {
        this.setCpf(new CPF(usuario.cpf()));
        this.setNome(usuario.nome());
        this.setSenha(usuario.senha());
        this.setEmail(usuario.email());
        this.setRole(EnumRole.ROLE_GERENTE);
    }

    public Usuario(CriarDevRequest usuarioDev) {
        this.setCpf(new CPF(usuarioDev.cpf()));
        this.setNome(usuarioDev.nome());
        this.setSenha(usuarioDev.senha());
        this.setEmail(usuarioDev.email());
        this.setRole(EnumRole.ROLE_DEV);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
