package pe.cibertec.melodicvault.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.cibertec.melodicvault.interfaces.IUsuario;
import pe.cibertec.melodicvault.interfacesService.IUsuarioService;
import pe.cibertec.melodicvault.modelo.Usuario;

import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private IUsuario data;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario registrar(String email, String password, String ciudad) {
        if (data.existsByEmail(email)) {
            throw new IllegalStateException("El correo ya está registrado");   // luego se traduce a 409
        }
        Usuario u = new Usuario();
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setRol("FAN");                    // el registro siempre crea FAN
        u.setCiudad(ciudad);
        return data.save(u);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return data.findByEmail(email);
    }
}