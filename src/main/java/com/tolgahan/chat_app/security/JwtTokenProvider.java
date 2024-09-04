package com.tolgahan.chat_app.security;

import com.tolgahan.chat_app.controller.UserController;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${chat_app.jwtToken.secret}")
    private String APP_SECRET;
    @Value("${chat_app.jwtToken.expires_in_seconds}")
    private long EXPIRES_IN;

    public String generateToken(Authentication authentication) {
        JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
        UUID id = userDetails.getId();
        Date current = new Date();

        Date expiryDate = Date.from(Instant.now().plusSeconds(EXPIRES_IN));
        return Jwts.builder()
                .setSubject(id.toString())
                .setIssuedAt(current)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, APP_SECRET)
                .compact();
    }

    public String generateJwtTokenByUserId(UUID userId) {
        Date expireDate = Date.from(Instant.now().plusSeconds(EXPIRES_IN));
        return Jwts.builder().setSubject(userId.toString())
                .setIssuedAt(new Date()).setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, APP_SECRET).compact();
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token).getBody().getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }


}
