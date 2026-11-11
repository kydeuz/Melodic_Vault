package pe.cibertec.melodicvault.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "firma")
public class Firma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_firma")
    private Integer idFirma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_artista", nullable = false)
    private Artista artista;

    @NotNull
    @Column(nullable = false)
    private Integer numero;                 // el puesto = la medalla

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String ciudad;                  // copia de la ciudad del usuario al firmar

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fecha;

    public Integer getIdFirma() {
        return idFirma;
    }

    public void setIdFirma(Integer idFirma) {
        this.idFirma = idFirma;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}