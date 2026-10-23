package com.example.tasksync.application.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.tasksync.application.dto.LoginRequest;
import com.example.tasksync.application.dto.UsuarioLogado;
import com.example.tasksync.domain.entities.Token;
import com.example.tasksync.domain.repository.TokenRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${spring.secret}")
    private String secret;
    @Value("${spring.expiracao}")
    private Long expiracao;
    //@Value("${spring.emissor}")
    private String emissor = "DEVTEST";

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TokenRepository tokenRepository;

    public String gerarToken(LoginRequest loginRequestDto){

        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);

            var usuario = usuarioRepository.findAll()
                    .stream().filter(u->u.getEmail().equals(loginRequestDto.email())).findFirst().orElse(null);

            String token = JWT.create()
                    .withIssuer(emissor)
                    .withSubject(loginRequestDto.email())
                    .withExpiresAt(this.getDataExpiracao())
                    .sign(algorithm);

            tokenRepository.save(new Token(token, usuario));

            return token;

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public UsuarioLogado verificarToken(String token)throws JWTVerificationException {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verificador = JWT.require(algorithm).withIssuer(emissor).build();

        verificador.verify(token);

        var tokenBanco = tokenRepository.findTokenByToken(token).orElse(null);

        if (tokenBanco == null){
            throw new IllegalArgumentException("Token invalido");
        }
        return new UsuarioLogado(tokenBanco.getUsuario());
    }

    private Instant getDataExpiracao(){

        //pegar data atual
        var dataAtual = LocalDateTime.now();
        //adicionar ou diminuir tempo da data atual
        var dataFutura = dataAtual.plusMinutes(expiracao);
        //Converter fuso do Brasil
        return dataFutura.toInstant(ZoneOffset.of("-03:00"));
    }

}
