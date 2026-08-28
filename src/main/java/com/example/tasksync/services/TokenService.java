package com.example.tasksync.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
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
    @Value("${spring.emissor}")
    private String emissor;

    public String gerarToken(String subject){

        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withIssuer(emissor)
                    .withSubject(subject)
                    .withExpiresAt(getDataExpiracao())
                    .sign(algorithm);

            return token;

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public DecodedJWT verificarToken(String token)throws JWTVerificationException {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verificador = JWT.require(algorithm).withIssuer(emissor).build();
        return verificador.verify(token);
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
