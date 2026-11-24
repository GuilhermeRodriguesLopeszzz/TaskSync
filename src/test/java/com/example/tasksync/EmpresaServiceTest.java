package com.example.tasksync;

import com.example.tasksync.application.dto.*;
import com.example.tasksync.application.services.EmpresaService;
import com.example.tasksync.application.services.EnderecoService;
import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.repository.EmpresaRepository;
import com.example.tasksync.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private EmpresaService empresaService;

    @BeforeEach
    void autenticarAdmin() {
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setRole(EnumRole.ROLE_ADMIN);
        var principal = new UsuarioLogado(admin);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
    }

    @AfterEach
    void limparAutenticacao() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void salvaEnderecoConsultadoNoCadastro() {
        var request = new EmpresaRequest("Empresa Teste", "11222333000181", "Teste", "01001-000");
        when(enderecoService.BuscarEndereco("01001-000")).thenReturn(
                new EnderecoResponse("01001-000", "Praça da Sé", "Sé", "São Paulo", "SP"));

        EmpresaResponse resposta = empresaService.salvarEmpresa(request, 0L);

        assertEquals("01001-000", resposta.cep());
        assertEquals("Praça da Sé", resposta.logradouro());
        assertEquals("Sé", resposta.bairro());
        assertEquals("São Paulo", resposta.cidade());
        assertEquals("SP", resposta.uf());
        verify(empresaRepository).save(any(Empresa.class));
    }

    @Test
    void naoSalvaEmpresaComCepInvalido() {
        var request = new EmpresaRequest("Empresa Teste", "11222333000181", "Teste", "123");
        when(enderecoService.BuscarEndereco("123"))
                .thenThrow(new IllegalArgumentException("CEP inválido!"));

        assertThrows(IllegalArgumentException.class, () -> empresaService.salvarEmpresa(request, 0L));
        verify(empresaRepository, never()).save(any(Empresa.class));
    }

    @Test
    void mantemEnderecoQuandoCepNaoMudou() {
        var request = new EmpresaRequest("Empresa Teste", "11222333000181", "Teste", "01001-000");
        Empresa empresa = new Empresa(request);
        empresa.setId(2L);
        empresa.setCep("01001-000");
        empresa.setCidade("São Paulo");
        when(empresaRepository.findById(2L)).thenReturn(Optional.of(empresa));

        EmpresaResponse resposta = empresaService.salvarEmpresa(request, 2L);

        assertEquals("São Paulo", resposta.cidade());
        verifyNoInteractions(enderecoService);
        verify(empresaRepository).save(empresa);
    }
}
