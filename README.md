# Melodic Vault — Registro de cambios

Los fans descubren artistas emergentes en un feed, les dan su **pepa** (su firma) y, cuando un artista llega a la meta de firmas, se abre su canal para hacer lives con su gente.

---

# Comit 1.1.1

## Resumen de este commit

Primer commit de la rama `feature/modelo`: **migra la base de datos del proyecto anterior (bandas y álbumes) al nuevo alcance, con 7 tablas.**

Incluye solo el script SQL (`bd_melodicvault`) con las 7 tablas, sus relaciones y datos iniciales. Las entidades y repositorios en Java van en el siguiente commit.

## Por qué

El alcance nuevo (documento *Entidades y ER*) pide cosas que el modelo anterior no tenía:

- **Artistas con firmas y canal**, en lugar de bandas con seguidores.
- **Login con correo y ciudad**, porque la ciudad se usa para mostrar dónde está la gente de cada artista.
- **Fechas del artista** (giras y lives).
- **Mínimo de 7 entidades**, confirmado por el profesor.

Así el equipo puede trabajar en paralelo contra un modelo estable: Harold sigue con Álbum y Canción, y Celeste conecta Angular.

## Qué cambió

| Antes | Ahora | Motivo |
|---|---|---|
| `banda` | `artista` (con `slug`, `firmas`, `canal_habilitado`, clip, redes y `version`) | Es el eje del sistema; el `slug` es su enlace público `/@slug` |
| `usuario.username` | `usuario.email` | El login es por correo |
| — | `usuario.ciudad` | Se copia a cada firma para contar dónde está la gente |
| Género como texto libre | Tabla `genero` (catálogo) y `artista.id_genero` | El artista elige de una lista; es solo lectura |
| `album.id_banda` | `album.id_artista` | Sigue el renombre de banda a artista |
| `album` con tipo, rating, formato, portada y descripción | `album` solo con `titulo`, `anio` e `id_artista` | El contrato de API solo maneja esos campos |
| `cancion.duracion_segundos` | Eliminado | El contrato no lo usa |
| Rol `LECTOR` | Rol `FAN` | Los roles son `ADMIN` y `FAN` |
| — | Tabla nueva `firma` | Voto único por usuario y artista; guarda el número (la medalla) y la ciudad |
| — | Tabla nueva `sala` | Eventos `GIRA` y `LIVE` del artista |

## Las 7 entidades

| Entidad | Qué guarda | Relaciones |
|---|---|---|
| Usuario | Correo, contraseña (BCrypt), rol y ciudad | 1:N con Firma; 0..1 con Artista (dueño del perfil) |
| Artista | Perfil público, firmas, canal, clip y redes | N:1 con Género; 1:N con Álbum, Firma y Sala |
| Género | Catálogo de géneros | 1:N con Artista |
| Álbum | Discografía del artista | N:1 con Artista; 1:N con Canción |
| Canción | Pista de un álbum | N:1 con Álbum |
| Firma | Voto único con su número y la ciudad del usuario | N:1 con Usuario y Artista |
| Sala | Evento con fecha y lugar (`GIRA` o `LIVE`) | N:1 con Artista |

## Reglas de integridad (en la base de datos)

- **Únicos:** `usuario.email`, `artista.slug`, `artista.id_usuario`, `genero.nombre`, `firma (id_usuario, id_artista)` y `cancion (id_album, numero_pista)`.
- **Cascadas:** al borrar un artista se borran sus álbumes, salas y firmas; al borrar un álbum se borran sus canciones.
- **Índice** `firma (id_artista, ciudad)`: permite sacar "dónde está tu gente" con un solo `GROUP BY`.
- **`version`** en `artista` para `@Version`: evita que dos ediciones simultáneas se pisen.
- Las firmas suben el contador con un `UPDATE` atómico y no tocan `version`.

Las reglas de negocio (meta de firmas, tope diario, canal LIVE solo si está habilitado) viven en el servicio, no en la base.

## Datos de prueba

- 11 géneros (Indie folk, Rock, Pop, Cumbia, Hip hop / Rap, Urbano, Electrónica, Jazz, Fusión, Folklore y Otro).
- 3 artistas heredados del proyecto anterior (Dokken, Europe, Journey) con sus álbumes y algunas canciones. Se reemplazarán por los 5 artistas de demo.
- 2 usuarios:

| Correo | Clave | Rol |
|---|---|---|
| admin@melodicvault.com | admin123 | ADMIN |
| fan@melodicvault.com | lector123 | FAN |

## Siguientes pasos (Sprint 1)

- Segundo commit: entidades JPA y repositorios de las 7 tablas.
- Login, registro y filtro JWT (punto de control: 14 de noviembre).
- REST de Artista y Género.
- Harold: ramas de Álbum y Canción.
- Celeste: conectar Angular con los servicios reales.


---

# Commit 1.1.2

## Resumen de este commit

Segundo commit de la rama `feature/modelo`: **pasa el código Java al nuevo modelo de 7 entidades y elimina lo de la versión anterior (bandas y vistas).**

Incluye las entidades JPA, los repositorios y los servicios, y la limpieza de controllers y servicios viejos.

## Por qué

El commit 1.1.1 dejó la base de datos con 7 tablas, pero las clases Java seguían hablando de `Banda`, `rating` y `tipo`, así que la app no podía arrancar contra el nuevo esquema. Además, el proyecto pasa a ser una API REST: los controllers viejos devolvían vistas y dependían de `Banda`.

## Qué cambió

| Antes | Ahora | Motivo |
|---|---|---|
| `Banda` | `Artista` (con `slug`, `firmas`, `canalHabilitado`, clip, redes, `@Version`, `genero` y `usuario`) | Es el eje del sistema |
| `Usuario` con `username` | `Usuario` con `email` y `ciudad` | Login por correo; la ciudad se copia a cada firma |
| `Album` con tipo, rating, formato, portada y descripción | `Album` solo con `titulo`, `anio` y `artista` | El contrato de API solo maneja esos campos |
| `Cancion` con duración | `Cancion` sin duración, con relación `LAZY` a álbum | El contrato no la usa |
| — | Entidades nuevas: `Genero`, `Firma` y `Sala` | Completan las 7 entidades |
| `IBanda` (búsqueda por nombre, país y género) | `IArtista` (búsqueda por slug, por dueño y contador atómico de firmas) | El feed es paginado y las firmas no deben tocar `@Version` |
| `IAlbum` e `ICancion` con búsquedas y estadísticas | Solo CRUD; `ICancion` con comprobación de pista repetida | Lo demás no está en el contrato |
| — | Repositorios nuevos: `IGenero`, `IUsuario`, `IFirma` e `ISala` | Un repositorio por entidad |
| `BandaService` | `ArtistaService` paginado; `save` devuelve la entidad guardada | El POST responde con el artista creado |
| `UsuarioDetailsService` por `username` | Carga por `email` | Base del login con JWT |
| Servicios nuevos | `UsuarioService` (registro con BCrypt) y `GeneroService` | Registro y `GET /api/generos` |

## Eliminado

- Controllers de la versión anterior (`HomeController`, `AlbumController`, `AlbumCompletoController`, `BandaController`, `CancionController`, `LoginController` y `RegistroController`).
- `ReporteService` y `AlbumTransaccionalService`.
- Búsquedas por país, tipo y rating, y las estadísticas por banda.

## Convención de carpetas

- `modelo`: entidades.
- `interfaces`: repositorios, con nombre `IEntidad`.
- `interfacesService`: contratos de servicio, con nombre `IEntidadService`.
- `service`: implementaciones, con nombre `EntidadService`.

## Verificación

La app arranca con `spring.jpa.hibernate.ddl-auto=validate`, así que las entidades coinciden con las tablas del script.

## Siguientes pasos (Sprint 1)

- Login, registro y filtro JWT (punto de control: 14 de noviembre)..
- REST de Artista y Género: controllers nuevos, DTOs y respuestas 403, 404 y 409.
- Harold: REST de Álbum y Canción.
- Celeste: conectar entrar, registro y GET /api/artistas a servicios reales cuando el login esté arriba.

---

# Commit 1.1.3

## Resumen de este commit

Login, registro y filtro JWT funcionando de punta a punta: la API ya no depende
de Spring Security por formularios, sino de JSON Web Tokens. Incluye el
backend completo (DTOs, JwtService, JwtAuthFilter, SecurityConfig stateless,
AuthController, manejo de errores) y 5 pruebas automatizadas. Cierra el punto
de control del 14 de noviembre.

## Por qué

El commit 1.1.2 dejó las entidades y repositorios listos, pero la API no
tenía forma de autenticar a nadie: no había login, ni token, ni reglas de
quién puede entrar a qué ruta. Sin esto, Celeste no puede conectar Angular
contra servicios reales y Harold no puede probar Álbum ni Canción con un
usuario de verdad.

## Qué cambió

| Antes | Ahora | Motivo |
|---|---|---|
| `thymeleaf` + `thymeleaf-extras-springsecurity6` + JasperReports en el `pom.xml` | Eliminados; se agregan `jjwt-api`, `jjwt-impl`, `jjwt-jackson` y `spring-security-test` | El proyecto es una API REST, no sirve vistas; se necesita JWT y pruebas de endpoints protegidos |
| `application.properties` sin prefijo (`expiracion-horas`, `firmas.meta`, `tope-diario`) | Todo bajo `melodicvault.` (`melodicvault.jwt.expiracion-horas`, `melodicvault.firmas.meta`, `melodicvault.firmas.tope-diario`) | Coincide con las claves que leen los `@Value` del código |
| `RegistroRequest` como clase con getters/setters | DTOs como `record` (`LoginRequest`, `LoginResponse`, `RegistroRequest`, `RegistroResponse`, `ErrorResponse`) | Son datos inmutables sin lógica; menos código repetido |
| Sin JWT | `JwtService`: genera y lee el token (HS256, secreto en variable de entorno, vencimiento configurable) | Base de la autenticación *stateless* |
| Sin filtro de seguridad propio | `JwtAuthFilter`: lee `Authorization: Bearer`, autentica si el token es válido, ignora si no (nunca lanza error) | Deja que las rutas públicas sigan como anónimas con token vencido, tal como pide el contrato |
| `SecurityConfig` de formularios (`formLogin`, `UsuarioDetailsService`) | `SecurityConfig` stateless: CSRF off, CORS para `localhost:4200`, rutas públicas (`/api/auth/**`, `GET /api/artistas`, `GET /api/generos`, `/clips/**`) vs protegidas, 401/403 en el formato del contrato | El login ya no pasa por el flujo estándar de Spring Security, lo valida `AuthController` a mano |
| Sin `AuthController` | `AuthController`: `POST /api/auth/login` (BCrypt, mismo mensaje para correo inexistente y clave incorrecta) y `POST /api/auth/registro` (rol siempre `FAN`, 409 si el correo existe) | Son los dos únicos endpoints públicos sin token |
| Sin manejo central de errores | `GlobalExceptionHandler` (`@RestControllerAdvice`) con el formato `{error, campos}`; nuevas excepciones `CredencialesInvalidasException` y `CorreoDuplicadoException` | Se reutiliza en todos los REST que vienen después (Artista, Álbum, Canción, Sala) |
| Sin pruebas del login | `AuthControllerTest` con `@WebMvcTest`: campos vacíos (400), correo inexistente (401), clave incorrecta (401), login exitoso (200 con token), endpoint protegido sin token (401) | Pide la rúbrica; corre sin depender de la base de datos ni de la variable de entorno real |

## Verificación

Las 5 pruebas de `AuthControllerTest` pasan en verde con `mvn test`, sin necesitar
MySQL levantado ni la variable `JWT_SECRETO` del sistema (el test trae su propia
clave de prueba).

## Siguientes pasos (Sprint 1, entrega 18 de noviembre)

- `GET /api/generos`.
- REST de Artista: `GET /api/artistas` (paginado), `GET /api/artistas/{id o slug}`,
  `POST`, `PUT` (con `version`), `DELETE`.
- Harold: revisión de avance del SEPTE y pruebas de datos.
- Celeste: conectar entrar, registro y `GET /api/artistas` a los servicios reales.