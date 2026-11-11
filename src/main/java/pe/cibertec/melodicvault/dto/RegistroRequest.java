package pe.cibertec.melodicvault.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistroRequest {

	@NotBlank(message = "El correo es obligatorio")
	@Email
	@Size(max = 100)
	private String email;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 6, max = 72, message = "Entre 6 y 72 caracteres")
	private String password;

	@NotBlank(message = "La ciudad es obligatoria")
	@Pattern(regexp = "Lima|Callao|Arequipa|Trujillo|Cusco|Piura|Chiclayo",
			message = "Ciudad no válida")
	private String ciudad;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}