package com.example.back;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TipoProjetoController {
    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtDesc;



    @FXML
    public void salvarTipoProjeto(ActionEvent event) throws Exception {

        //Validar Dados
        String jsonRequest = String.format("{\n" +
                        "  \"nome\": \"%s\",\n" +
                        "  \"descricao\": \"%s\",\n" +
                        "  \"secretKey\": \"%s\"\n" +
                        "}",
                txtNome.getText(),
                txtDesc.getText(),
                "lfdsjmaaokfjdfdsajndas");

        int respostaApi = executaMetodoAPI("http://localhost:8080/tipoprojeto/criartipoprojeto",jsonRequest,"POST");


        if(respostaApi ==200){
            showMessage(Alert.AlertType.INFORMATION,"Tipo projeto salvo com sucesso!");
            voltar(event);

        }else {
            showMessage(Alert.AlertType.ERROR,"Erro ao salvar tipo projeto !");
        }
    }

    @FXML
    public void voltar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/back/menu-view.fxml"));

        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    private int executaMetodoAPI(String url, String json, String protocolohttp) throws Exception{

        URL urlAPI = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlAPI.openConnection();
        connection.setRequestMethod(protocolohttp);
        connection.setDoOutput(true);

        if (!json.isEmpty()){
            connection.setRequestProperty("Content-Type", "application/json");
            try (OutputStream os = connection.getOutputStream()) {
                os.write(json.getBytes());
            }
        }

        return connection.getResponseCode();
    }

    private void showMessage(Alert.AlertType type, String mensagem){
        Alert alerta = new Alert(type);

        alerta.setTitle("Mensagem do sistema");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
