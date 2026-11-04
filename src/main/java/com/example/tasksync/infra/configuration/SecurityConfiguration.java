
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

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

                        // Cadastro inicial de administrador
                        .requestMatchers(HttpMethod.POST, "/usuarios/admin").permitAll()

                        // Cadastro de gerente: somente ADMIN
                        .requestMatchers(HttpMethod.POST, "/usuarios")
                        .hasRole("ADMIN")

                        // Cadastro de desenvolvedor: ADMIN e GERENTE
                        .requestMatchers(HttpMethod.POST, "/usuarios/dev")
                        .hasAnyRole("ADMIN", "GERENTE")

                        // Qualquer usuário logado pode buscar os próprios dados
                        .requestMatchers(HttpMethod.GET, "/usuarios/buscarusuariologado")
                        .hasAnyRole("ADMIN", "GERENTE", "DEV")

                        // Empresa: somente ADMIN
                        .requestMatchers("/empresa/**")
                        .hasRole("ADMIN")

                        // Consulta de projetos e tarefas
                        .requestMatchers(HttpMethod.GET,
                                "/projetos/**",
                                "/tarefa/**"
                        ).hasAnyRole("ADMIN", "GERENTE", "DEV")

                        // Atualização de tarefas
                        .requestMatchers(HttpMethod.PUT, "/tarefa/**")
                        .hasAnyRole("ADMIN", "GERENTE", "DEV")

                        .requestMatchers(HttpMethod.PATCH, "/tarefa/**")
                        .hasAnyRole("ADMIN", "GERENTE", "DEV")

                        //Todoo restante dessas áreas
                        //somente ADMIN e GERENTE
                        .requestMatchers(
                                "/usuarios/**",
                                "/projetos/**",
                                "/tarefa/**",
                                "/desenvolvedor/**",
                                "/tipoprojeto/**"
                        ).hasAnyRole("ADMIN", "GERENTE")

                        // Qualquer outra rota exige autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
