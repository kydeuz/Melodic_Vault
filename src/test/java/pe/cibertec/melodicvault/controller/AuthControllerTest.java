package pe.cibertec.melodicvault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pe.cibertec.melodicvault.config.JwtAuthFilter;
import pe.cibertec.melodicvault.dto.LoginRequest;
import pe.cibertec.melodicvault.excepcion.GlobalExceptionHandler;
import pe.cibertec.melodicvault.interfaces.IArtista;
import pe.cibertec.melodicvault.interfaces.IUsuario;
import pe.cibertec.melodicvault.modelo.Usuario;
import pe.cibertec.melodicvault.config.SecurityConfig;
import pe.cibertec.melodicvault.service.JwtService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtService.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        // Generado una vez con: openssl rand -base64 48. Es solo para pruebas, no el real.
        "melodicvault.jwt.secreto=k7Qv3mZ9pL2xR8tN5wB1yJ6hF4cA0sD7eG3uI9oK2nM=",
        "melodicvault.jwt.expiracion-horas=24"
})
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder; // bean real, viene de SecurityConfig

    @MockBean private IUsuario usuarioRepo;
    @MockBean private IArtista artistaRepo;

    @Test
    void login_conCamposVacios_devuelve400() throws Exception {
        var request = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.email").exists())
                .andExpect(jsonPath("$.campos.password").exists());
    }

    @Test
    void login_conCorreoInexistente_devuelve401() throws Exception {
        when(usuarioRepo.findByEmail("noexiste@correo.com")).thenReturn(Optional.empty());

        var request = new LoginRequest("noexiste@correo.com", "123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    void login_conClaveIncorrecta_devuelve401() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("fan@correo.com");
        usuario.setPassword(passwordEncoder.encode("claveCorrecta"));
        usuario.setRol("FAN");
        usuario.setCiudad("Lima");

        when(usuarioRepo.findByEmail("fan@correo.com")).thenReturn(Optional.of(usuario));

        var request = new LoginRequest("fan@correo.com", "claveIncorrecta");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    void login_exitoso_devuelve200ConToken() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("fan@correo.com");
        usuario.setPassword(passwordEncoder.encode("claveCorrecta"));
        usuario.setRol("FAN");
        usuario.setCiudad("Lima");

        when(usuarioRepo.findByEmail("fan@correo.com")).thenReturn(Optional.of(usuario));
        when(artistaRepo.findByUsuarioIdUsuario(1)).thenReturn(Optional.empty());

        var request = new LoginRequest("fan@correo.com", "claveCorrecta");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("fan@correo.com"))
                .andExpect(jsonPath("$.rol").value("FAN"));
    }

    @Test
    void endpointProtegido_sinToken_devuelve401() throws Exception {
        // No hace falta que exista un controller en esta ruta: el filtro de seguridad corta la petición antes de llegar al DispatcherServlet,
        // así que valida el SecurityConfig, no un endpoint de nuestro negocio.
        mockMvc.perform(get("/api/ruta-protegida-de-prueba"))
                .andExpect(status().isUnauthorized());
    }
}