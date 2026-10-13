package com.example.tasksync.presentation;


import com.example.tasksync.application.dto.CriarTipoProjetoRequest;
import com.example.tasksync.application.dto.CriarTipoProjetoResponse;
import com.example.tasksync.application.dto.UsuarioResponse;
import com.example.tasksync.application.services.TipoProjetoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipoprojeto")
@Tag(name="Tipo projeto", description = "Grupo de APIs responsavel por controlar a estrutura de crição e consulta de tipo de projeto do sistema!")
public class TipoProjetoController {



    @Autowired
    private TipoProjetoService tipoProjetoService;

    @PostMapping("/criartipoprojeto")
    public ResponseEntity<CriarTipoProjetoResponse> criarTipoProjeto(@RequestBody CriarTipoProjetoRequest criarTipoProjetoRequest){

        try {
            CriarTipoProjetoResponse responseSalvar = tipoProjetoService.criarTipoProjeto(criarTipoProjetoRequest);
            return ResponseEntity.ok(responseSalvar);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
