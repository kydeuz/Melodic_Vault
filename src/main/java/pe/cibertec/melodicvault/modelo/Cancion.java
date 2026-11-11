package pe.cibertec.melodicvault.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "cancion")

public class Cancion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column( name = "id_cancion")
    private Integer idCancion;
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100 , message = "El titulo no puede tener mas de 100 letras")
    private String titulo;

	@Column(name = "numero_pista", nullable = false)
    @NotNull(message = "El número de pista es obligatorio")
    @Min(value = 1, message = "La pista debe ser 1 o mayor")
    private Integer numeroPista;


    @NotNull(message = "Debe seleccionar un álbum")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_album", nullable = false)
    private Album album;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Integer getNumeroPista() {
        return numeroPista;
    }

    public void setNumeroPista(Integer numeroPista) {
        this.numeroPista = numeroPista;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(Integer idCancion) {
        this.idCancion = idCancion;
    }
}
