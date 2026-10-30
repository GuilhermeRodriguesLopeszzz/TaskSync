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
                        .requestMatchers("/auth/login",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Cadastro de usuário público
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        // Usuários (só ADMIN)
                        .requestMatchers("/usuarios/**")
                        .hasRole("ADMIN")

                        // Consulta liberada para ADMIN e DEV
                        .requestMatchers(HttpMethod.GET,
                                "/projetos/**",
                                "/tarefa/**",
                                "/desenvolvedor/**",
                                "/empresas/**",
                                "/tipoprojeto/**"
                        ).hasAnyRole("ADMIN", "DEV")

                        // DEV também atualiza tarefas (PUT e PATCH /status)
                        .requestMatchers(HttpMethod.PUT, "/tarefa/**")
                        .hasAnyRole("ADMIN", "DEV")
                        .requestMatchers(HttpMethod.PATCH, "/tarefa/**")
                        .hasAnyRole("ADMIN", "DEV")

                        // Demais escritas (POST/PUT/PATCH/DELETE) só ADMIN
                        .requestMatchers(
                                "/projetos/**",
                                "/tarefa/**",
                                "/desenvolvedor/**",
                                "/empresas/**",
                                "/tipoprojeto/**"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore( jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }


}