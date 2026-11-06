package com.example.tasksync.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// Permite incorporar o CPF na entidade Usuario
@Embeddable
public class CPF {

    // Armazena o CPF sem pontuação e impede duplicidade no banco
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    // Construtor vazio necessário para o JPA
    public CPF() {
        this.cpf = "";
    }

    // Construtor que recebe e valida o CPF
    public CPF(String cpf) {

        // Remove a pontuação, caso o CPF esteja formatado
        String cpfLimpo = cpf == null ? null : cpf.replaceAll("\\D", "");

        // Impede a criação de um CPF inválido
        if (!isValidCpf(cpfLimpo)) {
            throw new IllegalArgumentException("CPF inválido!");
        }

        // Armazena somente os 11 números
        this.cpf = cpfLimpo;
    }

    // Verifica se o CPF informado é válido
    public static boolean isValidCpf(String cpf) {

        // Impede CPF nulo
        if (cpf == null) {
            return false;
        }

        // Remove tudo o que não for dígito
        cpf = cpf.replaceAll("\\D", "");

        // Verifica se possui exatamente 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Rejeita CPFs com todos os números iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int digito1 = 11 - (soma % 11);

        if (digito1 > 9) {
            digito1 = 0;
        }

        // Verifica se o primeiro dígito está correto
        if (digito1 != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        // Calcula o segundo dígito verificador
        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        int digito2 = 11 - (soma % 11);

        if (digito2 > 9) {
            digito2 = 0;
        }

        // Verifica se o segundo dígito está correto
        return digito2 == Character.getNumericValue(cpf.charAt(10));
    }

    @Override
    public String toString() {

        // Retorna o CPF formatado: XXX.XXX.XXX-XX
        return cpf.replaceAll(
                "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                "$1.$2.$3-$4"
        );
    }
}
