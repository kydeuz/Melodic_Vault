package pe.cibertec.melodicvault.interfacesService;

import pe.cibertec.melodicvault.modelo.Usuario;

import java.util.Optional;

public interface IUsuarioService
{
    Usuario registrar(String email, String password, String ciudad);
    Optional<Usuario> buscarPorEmail(String email);

}
