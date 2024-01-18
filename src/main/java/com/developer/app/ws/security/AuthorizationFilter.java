package com.developer.app.ws.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);

    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(SecurityConstants.HEADER_STRING);

        if (authorizationHeader == null) {
            return null;
        }

        String token = authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
        byte[] secretKeyBytes = Base64.getEncoder().encode(SecurityConstants.getTokenSecret().getBytes());
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

//        Instant now = Instant.now();
//
//        String token = Jwts.builder()
//                //.subject(userName)
//                //.expiration(Date.from(now.plusMillis(Long.parseLong(environment.getProperty("token.expiration_time")))))
//                .issuedAt(Date.from(now))
//                .signWith(secretKey)
//                .compact();

        JwtParser parser = Jwts.parser()
                .verifyWith(secretKey)
                .build();

        Claims claims = parser.parseSignedClaims(token).getPayload();
        String subject = claims.getSubject();

        if (subject == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());

    }

}