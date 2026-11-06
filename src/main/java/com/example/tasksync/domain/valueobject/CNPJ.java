
package com.example.tasksync.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Locale;

@Embeddable
public class CNPJ {

    // Armazena o CNPJ na tabela Empresa.
    // Impede valores nulos e duplicados no banco de dados.
    @Column(name = "cnpj", nullable = false, unique = true, length = 14)
    private String cnpj;

    public CNPJ() {
        this.cnpj = "";
    }

    public CNPJ(String cnpj) {

        // Verifica se o CNPJ informado é válido
        if (!isValidCnpj(cnpj)) {
            throw new IllegalArgumentException("CNPJ inválido!");
        }

        // Armazena o CNPJ sem pontuação e com letras maiúsculas
        this.cnpj = normalizar(cnpj);
    }

    // Remove a formatação para facilitar a validação e o armazenamento
    private static String normalizar(String cnpj) {
        return cnpj.trim()
                .toUpperCase(Locale.ROOT)
                .replace(".", "")
                .replace("/", "")
                .replace("-", "");
    }

    // Retorna o CNPJ sem formatação, para salvar no banco
    public String getValor() {
        return this.cnpj;
    }

    // Verifica se os dígitos verificadores do CNPJ estão corretos
    public static boolean isValidCnpj(String cnpj) {

        // Impede CNPJ nulo
        if (cnpj == null) {
            return false;
        }

        cnpj = normalizar(cnpj);

        // As primeiras 12 posições podem conter letras ou números.
        // As duas últimas precisam ser números.
        if (!cnpj.matches("[A-Z0-9]{12}[0-9]{2}")) {
            return false;
        }

        // Rejeita CNPJs numéricos com todos os dígitos iguais
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Pesos utilizados no primeiro dígito verificador
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;

        // Calcula o primeiro dígito verificador
        for (int i = 0; i < 12; i++) {

            // Subtrai 48 do código ASCII.
            // Para números, obtém seu valor numérico.
            // Para letras, obtém o valor usado pela Receita Federal.
            int valor = cnpj.charAt(i) - 48;

            soma += valor * pesos1[i];
        }

        int resto = soma % 11;
        int digito1 = (resto < 2) ? 0 : 11 - resto;

        // Compara o primeiro dígito calculado com o informado
        if (digito1 != Character.getNumericValue(cnpj.charAt(12))) {
            return false;
        }

        // Pesos utilizados no segundo dígito verificador
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        soma = 0;

        // Calcula o segundo dígito verificador
        for (int i = 0; i < 13; i++) {
            int valor = cnpj.charAt(i) - 48;
            soma += valor * pesos2[i];
        }

        resto = soma % 11;
        int digito2 = (resto < 2) ? 0 : 11 - resto;

        // Retorna true somente se o segundo dígito estiver correto
        return digito2 == Character.getNumericValue(cnpj.charAt(13));
    }

    @Override
    public String toString() {

        // Retorna o CNPJ formatado: XX.XXX.XXX/XXXX-XX
        // Aceita números e letras nas primeiras 12 posições
        return cnpj.replaceAll(
                "([A-Z0-9]{2})([A-Z0-9]{3})([A-Z0-9]{3})([A-Z0-9]{4})([0-9]{2})",
                "$1.$2.$3/$4-$5"
        );
    }
}
