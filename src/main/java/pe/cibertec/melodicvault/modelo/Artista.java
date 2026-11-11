package pe.cibertec.melodicvault.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "artista")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_artista")
    private Integer idArtista;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])[a-z0-9-]{3,30}$",
            message = "Entre 3 y 30 caracteres: minúsculas, números y guiones, con al menos una letra")
    @Column(nullable = false, unique = true, length = 30, updatable = false)
    private String slug;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String nombre;

    @NotNull(message = "Debe seleccionar un género")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_genero", nullable = false)
    private Genero genero;

    @PositiveOrZero(message = "Las firmas no pueden ser negativas")
    @Column(nullable = false)
    private Integer firmas = 0;

    @Column(name = "canal_habilitado", nullable = false)
    private Boolean canalHabilitado = false;

    @Size(max = 255, message = "La URL del clip no puede superar 255 caracteres")
    @Column(name = "clip_url", length = 255)
    private String clipUrl;

    @Size(max = 100, message = "El título del clip no puede superar 100 caracteres")
    @Column(name = "clip_titulo", length = 100)
    private String clipTitulo;              // opcional; lo pone el servidor al subir el clip

    @NotNull
    @Min(value = 0, message = "El segundo de inicio no puede ser negativo")
    @Column(name = "clip_inicio_seg", nullable = false)
    private Integer clipInicioSeg = 0;      // opcional en el POST; editable en el PUT (mín. 0)

    @Size(max = 255)
    @Column(name = "spotify_url", length = 255)
    private String spotifyUrl;              // opcional

    @Size(max = 255)
    @Column(name = "youtube_url", length = 255)
    private String youtubeUrl;              // opcional

    @Size(max = 255)
    @Column(name = "instagram_url", length = 255)
    private String instagramUrl;            // opcional

    @Size(max = 255)
    @Column(name = "tiktok_url", length = 255)
    private String tiktokUrl;               // opcional

    @Version
    private Integer version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", unique = true)   // opcional: los artistas de demo no tienen dueño
    private Usuario usuario;

    public Integer getIdArtista() {
        return idArtista;
    }

    public void setIdArtista(Integer idArtista) {
        this.idArtista = idArtista;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Integer getFirmas() {
        return firmas;
    }

    public void setFirmas(Integer firmas) {
        this.firmas = firmas;
    }

    public Boolean getCanalHabilitado() {
        return canalHabilitado;
    }

    public void setCanalHabilitado(Boolean canalHabilitado) {
        this.canalHabilitado = canalHabilitado;
    }

    public String getClipUrl() {
        return clipUrl;
    }

    public void setClipUrl(String clipUrl) {
        this.clipUrl = clipUrl;
    }

    public String getClipTitulo() {
        return clipTitulo;
    }

    public void setClipTitulo(String clipTitulo) {
        this.clipTitulo = clipTitulo;
    }

    public Integer getClipInicioSeg() {
        return clipInicioSeg;
    }

    public void setClipInicioSeg(Integer clipInicioSeg) {
        this.clipInicioSeg = clipInicioSeg;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getTiktokUrl() {
        return tiktokUrl;
    }

    public void setTiktokUrl(String tiktokUrl) {
        this.tiktokUrl = tiktokUrl;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}