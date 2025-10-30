package net.ab79.juntos.juntosapp.auth.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final String SECRET = "supersecretkeysupersecretkeysupersecretkey";
  private static final long EXPIRATION = 1000 * 60 * 60;

  private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

  public String generateToken(String email, String role) {
    return Jwts.builder()
        .setSubject(email)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> validateToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }

  public String extractEmail(String token) {
    return validateToken(token).getBody().getSubject();
  }
}
