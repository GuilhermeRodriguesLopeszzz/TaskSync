package com.example.tasksync.infra.external;

import com.example.tasksync.domain.interfaces.IViaCepExternalRepository;
import com.example.tasksync.infra.dto.ViaCepResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class ViaCepExternalRepository implements IViaCepExternalRepository {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ViaCepResponse buscarEnderecoViaCepPorCep(String cep) {
        var url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
            // A consulta precisa terminar antes de preencher o endereço.
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 400) {
                throw new IllegalArgumentException("CEP inválido!");
            }

            if (response.statusCode() != 200) {
                throw new IllegalStateException("ViaCEP indisponível. Tente novamente!");
            }

            ViaCepResponse viaCepResponse = objectMapper.readValue(response.body(), ViaCepResponse.class);

            // O ViaCEP retorna erro=true quando o CEP não existe.
            if (viaCepResponse != null && Boolean.TRUE.equals(viaCepResponse.getErro())) {
                throw new IllegalArgumentException("CEP não encontrado!");
            }

            if (viaCepResponse == null || viaCepResponse.getCep() == null ||
                    viaCepResponse.getLocalidade() == null || viaCepResponse.getUf() == null) {
                throw new IllegalStateException("ViaCEP retornou um endereço inválido!");
            }

            return viaCepResponse;

        } catch (IOException e) {
            throw new IllegalStateException("ViaCEP indisponível. Tente novamente!", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Consulta do CEP interrompida. Tente novamente!", e);
        }
    }
}
