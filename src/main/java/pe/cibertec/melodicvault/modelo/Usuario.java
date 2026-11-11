package pe.cibertec.melodicvault.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private Integer idUsuario;

	@NotBlank
	@Email
	@Size(max = 100 , message = "El email no puede superar 100 caracteres")
	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@NotBlank

	@Column(nullable = false, length = 100)
	private String password;   // hash BCrypt, nunca la clave en texto plano

	@NotBlank
	@Column(nullable = false, length = 20)
	private String rol;        // "ADMIN" o "FAN"

	@NotBlank
	@Size(max = 30)
	@Column(nullable = false, length = 30)
	private String ciudad;

	public Integer getIdUsuario() { return idUsuario; }
	public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String getRol() { return rol; }
	public void setRol(String rol) { this.rol = rol; }

	public String getCiudad() { return ciudad; }
	public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}