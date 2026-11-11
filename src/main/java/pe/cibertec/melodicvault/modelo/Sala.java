package pe.cibertec.melodicvault.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "sala")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Integer idSala;

    @NotBlank
    @Pattern(regexp = "GIRA|LIVE")
    @Column(nullable = false, length = 10)
    private String tipo;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String titulo;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fecha;            // que sea futura lo valida el servicio o el DTO

    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String lugar;                   // GIRA: sitio; LIVE: enlace de la videollamada

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_artista", nullable = false)
    private Artista artista;

    @PrePersist
    void valoresPorDefecto() {
        if (lugar == null || lugar.isBlank()) lugar = "Por confirmar";
    }

    public Integer getIdSala() {
        return idSala;
    }

    public void setIdSala(Integer idSala) {
        this.idSala = idSala;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }
}