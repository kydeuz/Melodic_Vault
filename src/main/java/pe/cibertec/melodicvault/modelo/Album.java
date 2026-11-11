package pe.cibertec.melodicvault.modelo;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_album")
    private Integer idAlbum;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String titulo;

    @NotNull(message = "El año es obligatorio")
    @Column(nullable = false)
    private Integer anio;

    @NotNull(message = "Debe seleccionar un artista")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_artista", nullable = false)
    private Artista artista;

    public Integer getIdAlbum() { return idAlbum; }
    public void setIdAlbum(Integer idAlbum) { this.idAlbum = idAlbum; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }
}