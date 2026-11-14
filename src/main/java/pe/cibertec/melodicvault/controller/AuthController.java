package pe.cibertec.melodicvault.controller;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.melodicvault.dto.LoginRequest;
import pe.cibertec.melodicvault.dto.LoginResponse;
import pe.cibertec.melodicvault.dto.RegistroRequest;
import pe.cibertec.melodicvault.dto.RegistroResponse;
import pe.cibertec.melodicvault.excepcion.CorreoDuplicadoException;
import pe.cibertec.melodicvault.excepcion.CredencialesInvalidasException;
import pe.cibertec.melodicvault.interfaces.IArtista;
import pe.cibertec.melodicvault.interfaces.IUsuario;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Usuario;
import pe.cibertec.melodicvault.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IUsuario usuarioRepo;
    private final IArtista artistaRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(IUsuario usuarioRepo, IArtista artistaRepo,
                          PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepo = usuarioRepo;
        this.artistaRepo = artistaRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepo.findByEmail(request.email())
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario.getEmail(), usuario.getRol());

        Integer artistaId = artistaRepo.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .map(Artista::getIdArtista)
                .orElse(null);

        return ResponseEntity.ok(new LoginResponse(
                token, usuario.getEmail(), usuario.getRol(), usuario.getCiudad(), artistaId));
    }

    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registro(@Valid @RequestBody RegistroRequest request) {
        if (usuarioRepo.existsByEmail(request.email())) {
            throw new CorreoDuplicadoException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setCiudad(request.ciudad());
        usuario.setRol("FAN");

        Usuario guardado = usuarioRepo.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistroResponse(
                        guardado.getIdUsuario(), guardado.getEmail(), guardado.getRol(), guardado.getCiudad()));
    }
}