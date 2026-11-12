package pe.cibertec.melodicvault.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiracionMs;

    public JwtService(
            @Value("${melodicvault.jwt.secreto}") String secreto,
            @Value("${melodicvault.jwt.expiracion-horas}") long horas) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
        this.expiracionMs = horas * 60 * 60 * 1000;
    }

    public String generarToken(String email, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(key)
                .compact();
    }

    public Claims leerClaims(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }

    public boolean esValido(String token) {
        try {
            leerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extraerEmail(String token) {
        return leerClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return leerClaims(token).get("rol", String.class);
    }
}