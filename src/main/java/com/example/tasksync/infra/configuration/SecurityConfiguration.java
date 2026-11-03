package com.example.tasksync.infra.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception{

        return http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Rotas públicas (login e documentação)
                        .requestMatchers("/auth/login",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Cadastro de usuário público
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        // Cadastro de administrador
                        .requestMatchers(HttpMethod.POST, "/usuarios/admin").permitAll()

                        // Qualquer usuário logado pode buscar os próprios dados.
                        // Precisa vir ANTES da regra "/usuarios/**", senão ela barra DEV e GERENTE
                        .requestMatchers(HttpMethod.GET, "/usuarios/buscarusuariologado")
                        .hasAnyRole("ADMIN", "GERENTE", "DEV")

                        // Empresa: só ADMIN (todos os métodos).
                        // Vem antes das regras gerais para o GERENTE não entrar
                        .requestMatchers("/empresa/**")
                        .hasRole("ADMIN")

                        // Projetos e Tarefas (consulta): ADMIN, GERENTE e DEV
                        .requestMatchers(HttpMethod.GET,
                                "/projetos/**",
                                "/tarefa/**"
                        ).hasAnyRole("ADMIN", "GERENTE", "DEV")

                        // DEV também atualiza tarefas (PUT e PATCH /status)
                        .requestMatchers(HttpMethod.PUT, "/tarefa/**")
                        .hasAnyRole("ADMIN", "GERENTE", "DEV")
                        .requestMatchers(HttpMethod.PATCH, "/tarefa/**")
                        .hasAnyRole("ADMIN", "GERENTE", "DEV")

                        // Todo o resto dessas áreas (inclusive criar e excluir): ADMIN e GERENTE
                        .requestMatchers(
                                "/usuarios/**",
                                "/projetos/**",
                                "/tarefa/**",
                                "/desenvolvedor/**",
                                "/tipoprojeto/**"
                        ).hasAnyRole("ADMIN", "GERENTE")

                        .anyRequest().authenticated()
                )
                .addFilterBefore( jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }


}