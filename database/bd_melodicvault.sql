-- ============================================================
-- Melodic Vault - script de base de datos (rama feature/modelo)
-- Cambios respecto a main:
--   * banda  ->  artista (nuevos campos del documento Entidades y ER)
--   * nueva tabla genero (catalogo; artista.id_genero reemplaza el texto libre)
--   * nueva tabla firma (voto unico por usuario y artista)
--   * nueva tabla sala (GIRA / LIVE) para completar las 7 entidades
--   * album.id_banda  ->  album.id_artista
-- Sin tocar en este paso: usuario (correo y ciudad llegan con el login JWT),
-- columnas de album y cancion (se simplifican despues).
-- ============================================================
DROP DATABASE IF EXISTS bd_melodicvault;
CREATE DATABASE bd_melodicvault;
USE bd_melodicvault;

-- ============ TABLAS ============

CREATE TABLE usuario (
    id_usuario  INT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    rol         VARCHAR(20)  NOT NULL,
    ciudad      VARCHAR(30)  NOT NULL
);

CREATE TABLE genero (
    id_genero  INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE artista (
    id_artista        INT AUTO_INCREMENT PRIMARY KEY,
    slug              VARCHAR(30)  NOT NULL UNIQUE,
    nombre            VARCHAR(80)  NOT NULL,
    id_genero         INT          NOT NULL,
    firmas            INT          NOT NULL DEFAULT 0,
    canal_habilitado  BOOLEAN      NOT NULL DEFAULT FALSE,
    clip_url          VARCHAR(255),
    clip_titulo       VARCHAR(100),
    clip_inicio_seg   INT          NOT NULL DEFAULT 0,
    spotify_url       VARCHAR(255),
    youtube_url       VARCHAR(255),
    instagram_url     VARCHAR(255),
    tiktok_url        VARCHAR(255),
    version           INT          NOT NULL DEFAULT 0,   -- @Version
    id_usuario        INT          UNIQUE,               -- dueno del perfil (opcional)
    CONSTRAINT fk_artista_genero  FOREIGN KEY (id_genero)  REFERENCES genero(id_genero),
    CONSTRAINT fk_artista_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON DELETE SET NULL
);

CREATE TABLE sala (
    id_sala     INT AUTO_INCREMENT PRIMARY KEY,
    tipo        VARCHAR(10)  NOT NULL,                       -- GIRA o LIVE
    titulo      VARCHAR(100) NOT NULL,
    fecha       DATETIME     NOT NULL,                       -- futura (lo valida el servicio)
    lugar       VARCHAR(120) NOT NULL DEFAULT 'Por confirmar', -- GIRA: sitio; LIVE: enlace de la videollamada
    id_artista  INT          NOT NULL,
    CONSTRAINT fk_sala_artista FOREIGN KEY (id_artista) REFERENCES artista(id_artista)
        ON DELETE CASCADE
);

CREATE TABLE album (
    id_album     INT AUTO_INCREMENT PRIMARY KEY,
    titulo       VARCHAR(100) NOT NULL,
    anio         INT          NOT NULL,
    id_artista   INT          NOT NULL,
    CONSTRAINT fk_album_artista FOREIGN KEY (id_artista) REFERENCES artista(id_artista)
        ON DELETE CASCADE
);

CREATE TABLE cancion (
    id_cancion         INT AUTO_INCREMENT PRIMARY KEY,
    titulo             VARCHAR(100) NOT NULL,
    numero_pista       INT          NOT NULL,
    id_album           INT          NOT NULL,
    CONSTRAINT fk_cancion_album FOREIGN KEY (id_album) REFERENCES album(id_album)
        ON DELETE CASCADE,
    CONSTRAINT uq_cancion_album_pista UNIQUE (id_album, numero_pista)
);

CREATE TABLE firma (
    id_firma    INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario  INT         NOT NULL,
    id_artista  INT         NOT NULL,
    numero      INT         NOT NULL,      -- el puesto de la firma = la medalla
    ciudad      VARCHAR(30) NOT NULL,      -- ciudad del usuario al firmar
    fecha       DATETIME    NOT NULL,      -- sirve para contar el tope diario
    CONSTRAINT fk_firma_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
        ON DELETE CASCADE,
    CONSTRAINT fk_firma_artista FOREIGN KEY (id_artista) REFERENCES artista(id_artista)
        ON DELETE CASCADE,
    CONSTRAINT uq_firma_usuario_artista UNIQUE (id_usuario, id_artista),
    INDEX idx_firma_artista_ciudad (id_artista, ciudad)   -- "donde esta tu gente"
);

-- ============ DATOS ============

-- Catalogo inicial de generos
INSERT INTO genero (nombre) VALUES
('Indie folk'), ('Rock'), ('Pop'), ('Cumbia'), ('Hip hop / Rap'), ('Urbano'),
('Electrónica'), ('Jazz'), ('Fusión'), ('Folklore'), ('Otro');

-- Los 3 artistas de prueba heredados de las bandas (se reemplazan por los 5 del prototipo mas adelante)
INSERT INTO artista (slug, nombre, id_genero)
VALUES
('dokken',  'Dokken',  (SELECT id_genero FROM genero WHERE nombre = 'Rock')),
('europe',  'Europe',  (SELECT id_genero FROM genero WHERE nombre = 'Rock')),
('journey', 'Journey', (SELECT id_genero FROM genero WHERE nombre = 'Rock'));

INSERT INTO album (titulo, anio, id_artista)
VALUES
('Tooth And Nail',      1984, (SELECT id_artista FROM artista WHERE slug = 'dokken')),
('Back for Attack',     1987, (SELECT id_artista FROM artista WHERE slug = 'dokken')),
('The Final Countdown', 1986, (SELECT id_artista FROM artista WHERE slug = 'europe')),
('Escape',              1981, (SELECT id_artista FROM artista WHERE slug = 'journey'));

INSERT INTO usuario (email, password, rol, ciudad) VALUES
('admin@melodicvault.com', '$2b$10$aGsUNkvtNDwhTgm29zPs8eqWjD9I8rYghRx0p4ntzot/T.iCesOAq', 'ADMIN', 'Lima'),
-- clave del admin : admin123
('fan@melodicvault.com',   '$2b$10$DQuGrGBMFRwENahhD4sQ.uEe4hfvlGi/RHImkPataPw.MNhm6iFBm', 'FAN',   'Lima');
-- clave del fan : lector123

INSERT INTO cancion (titulo, numero_pista, id_album) VALUES
('Into The Fire',       1, (SELECT id_album FROM album WHERE titulo = 'Tooth And Nail')),
('Just Got Lucky',      2, (SELECT id_album FROM album WHERE titulo = 'Tooth And Nail')),
('The Final Countdown', 1, (SELECT id_album FROM album WHERE titulo = 'The Final Countdown'));

SELECT * FROM genero;
SELECT * FROM artista;
SELECT * FROM album;
SELECT * FROM usuario;
SELECT * FROM cancion;
SELECT * FROM firma;
SELECT * FROM sala;