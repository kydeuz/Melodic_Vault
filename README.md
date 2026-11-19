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


---
# Commit 1.1.4
Resumen de este commit

La API ya tiene vida propia: Género y Artista quedan expuestos de punta a punta. El feed se puede paginar, el perfil público se abre por id o por @slug, y un fan puede crear, editar y eliminar su perfil de artista, con permisos de dueño o ADMIN y control de versiones. Cierra el carril de API de César para el Sprint 1.

Incluye los controllers, los DTOs, el mapeo al JSON del contrato y las excepciones nuevas con su respuesta de error.
Por qué

Hasta el commit 1.1.3 la API sabía quién eras, pero no tenía nada que mostrarte. Faltaba todo lo de negocio:

    Celeste no podía armar el selector de género sin GET /api/generos.
    Tampoco podía conectar el feed ni el perfil público sin el listado y el detalle de artistas.
    Nadie podía crear, editar ni borrar un perfil desde Mi perfil sin POST, PUT y DELETE.

Ahora cada pantalla del Sprint 1 tiene un servicio real al que apuntar.
Los 6 servicios nuevos
Método 	Ruta 	Quién 	Respuestas
GET 	/api/generos 	Público 	200
GET 	/api/artistas?page=0&size=10 	Público (con token rellena miNumero) 	200
GET 	/api/artistas/{id o slug} 	Público 	200, 404
POST 	/api/artistas 	Con sesión y sin perfil 	201, 400, 401, 409
PUT 	/api/artistas/{id} 	Dueño o ADMIN 	200, 400, 401, 403, 404, 409
DELETE 	/api/artistas/{id} 	Dueño o ADMIN 	204, 401, 403, 404
Así se ve una tarjeta del feed

Cada artista del listado trae todo lo que la tarjeta necesita en una sola llamada. La meta sale de la configuración del servidor, nunca de un 50 escrito a mano:

{
"id": 1,
"slug": "luna",
"nombre": "Luna Chaska",
"genero": "Indie folk",
"firmas": 12,
"meta": 50,
"miNumero": null,
"proximaSala": null,
"ciudades": [ { "ciudad": "Lima", "firmas": 8 }, { "ciudad": "Arequipa", "firmas": 3 } ]
}

Con token válido, miNumero trae la medalla del usuario. Con token vencido o sin token, la respuesta sale como anónima y no falla.
Cómo funciona

Crear un perfil. El usuario con sesión manda slug, nombre y generoId. El servidor responde 400 si el género no existe, y 409 si el usuario ya tiene perfil o si el enlace ya está tomado. Quien lo crea queda como dueño.

Editar sin pisarse. El PUT lleva la version que Angular recibió en el detalle:

    El artista está en versión 0 y se edita con version 0. Responde 200 y la versión sube a 1.
    Si alguien más lo editó mientras tanto, la versión ya es otra. Responde 409 y pide recargar, en vez de borrar lo que el otro cambió.

Quién puede tocar qué. PermisosArtista aplica la regla en un solo lugar: el dueño del artista o un ADMIN pasan; cualquier otro recibe 403, aunque escriba la URL a mano.

Eliminar de verdad. El DELETE responde 204, borra en cascada álbumes, canciones, firmas y salas, elimina el archivo de clip y deja al usuario libre para crear otro perfil.
Qué cambió
Antes 	Ahora 	Motivo
Un solo controller (AuthController) 	GeneroController y ArtistaController 	Exponen los servicios del contrato
Sin DTOs de artista 	Records: ArtistaResumen, ArtistaDetalle, ArtistaCrearRequest, ArtistaEditarRequest, GeneroResponse, PaginaResponse, SalaResumen, CiudadFirmas, AlbumResumen, CancionResumen 	Nombres de campo exactos del contrato
Sin mapeo entidad a JSON 	ArtistaMapper 	Arma el JSON y toma la meta de melodicvault.firmas.meta
Sin lógica de gestión 	ArtistaGestionService (crear, editar, eliminar) y PermisosArtista 	Controllers delgados y la regla de dueño o ADMIN en un solo lugar
Excepciones solo de login 	Nuevas: RecursoNoEncontradoException (404), ConflictoException (409), ProhibidoException (403) y DatosInvalidosException (400 con campos) 	Cubren los errores del contrato
GlobalExceptionHandler sin 404, 403 ni conflicto de versión 	Handlers nuevos, incluido ObjectOptimisticLockingFailureException como 409 	Mismo formato {error, campos}
Repositorios sin consultas para el detalle 	IArtista con @EntityGraph en findById y findBySlug; IFirma con búsqueda por usuario y artista; IAlbum e ICancion con listados ordenados; IGeneroService.buscarPorId 	Evitan errores de carga diferida y alimentan miNumero y los álbumes anidados
Verificación

Probado a mano en Postman contra la API en local. Todo salió como se esperaba:

    ✅ GET /api/generos sin token: 200, géneros ordenados por nombre.
    ✅ GET /api/artistas?page=0&size=10 sin token: 200, con miNumero en null.
    ✅ GET /api/artistas/{id} y /{slug}: 200. Con un artista que no existe: 404 con {"error":"Artista no encontrado"}.
    ✅ POST sin token: 401. Con sesión: 201. Repetido: 409. Enlace tomado: 409. generoId inexistente: 400 con el campo generoId.
    ✅ PUT con la versión vigente: 200 y la versión sube. Repetido con la versión vieja: 409.
    ✅ PUT y DELETE de otro usuario: 403. Con ADMIN: permitido.
    ✅ DELETE: 204, y el GET siguiente da 404.

Para el equipo

    Harold: PermisosArtista (usuarioActual y exigirDuenoOAdmin) sirve tal cual para el 403 de Álbum, Canción, Sala y clip. Para los clips, usen la misma clave melodicvault.clips.carpeta.
    Celeste: ya puedes conectar el feed (GET /api/artistas), el perfil público (/api/artistas/{slug}), el selector de género y el alta, la edición y la baja del perfil. Guarda la version del detalle y mándala en el PUT.

Siguientes pasos (Sprint 2, entrega 28 de noviembre)

    Firma: POST /api/artistas/{id}/firmar, GET /api/firmas/mias, meta configurable, tope diario, medallas y demanda por ciudad.
    LIVE inicial "Primer live con su gente" al llegar a la meta.
    Feed de descubrimiento (scroll, clip de 10 s tras 0,7 s y "Es una pepa" desde la tarjeta).
    Harold: REST de Álbum, Canción, Sala y subida de clip.
    Celeste: pasar Mi perfil y el feed a los servicios reales.