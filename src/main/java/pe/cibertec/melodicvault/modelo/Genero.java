package pe.cibertec.melodicvault.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
@Table(name = "genero")

public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genero")
    private Integer idGenero;

    @NotBlank(message = "El nombre del género es obligatorio")
    @Size(max = 40, message = "El nombre del género no puede tener más de 40 caracteres")
    @Column(name = "nombre", length = 40, nullable = false, unique = true)
    private String nombre;

    public Integer getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(Integer idGenero) {
        this.idGenero = idGenero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}