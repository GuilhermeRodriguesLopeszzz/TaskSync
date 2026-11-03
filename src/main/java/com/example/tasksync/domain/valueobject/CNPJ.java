
package com.example.tasksync.domain.valueobject;

public class CNPJ {

    private String cnpj;

    public CNPJ() {
        this.cnpj = "";
    }

    public CNPJ(String cnpj) {

        if (!isValidCnpj(cnpj)) {
            throw new IllegalArgumentException("CNPJ Inválido!");
        }

        this.cnpj = cnpj.replaceAll("\\D", "");
    }

    public static boolean isValidCnpj(String cnpj) {

        if (cnpj == null) {
            return false;
        }

        // Remove tudo o que não for dígito
        cnpj = cnpj.replaceAll("\\D", "");

        // Verifica se possui 14 dígitos
        if (cnpj.length() != 14) {
            return false;
        }

        // Descarta CNPJs com todos os dígitos iguais
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {

            // Pesos utilizados no cálculo dos dígitos verificadores
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            // Cálculo do primeiro dígito verificador
            int soma = 0;

            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
            }

            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;

            if (digito1 != Character.getNumericValue(cnpj.charAt(12))) {
                return false;
            }

            // Pesos utilizados no segundo cálculo
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            // Cálculo do segundo dígito verificador
            soma = 0;

            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
            }

            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;

            return digito2 == Character.getNumericValue(cnpj.charAt(13));

        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return cnpj.replaceAll("\\D", "")
                .replaceAll(
                        "(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})",
                        "$1.$2.$3/$4-$5"
                );
    }
}
