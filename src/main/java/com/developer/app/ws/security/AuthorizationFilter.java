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

    /**
     * Processes an HTTP request to perform additional filtering based on security headers.
     * This method intercepts the request to check for the presence of a specific security header.
     * If the header is missing or does not start with a predefined token prefix, the filter chain is
     * allowed to proceed without interruption. If the header is present and correctly formatted,
     * the method attempts to authenticate the user based on the header's content.
     *
     * @param request  The {@link HttpServletRequest} object that contains the request the client made to the servlet.
     * @param response The {@link HttpServletResponse} object that contains the response the servlet returns to the client.
     * @param chain    The {@link FilterChain} for invoking the next filter or the resource at the end of the chain.
     * @throws IOException      If an input or output exception occurs while the servlet is handling the HTTP request.
     * @throws ServletException If the request could not be handled.
     *
     *                          <p>Firstly, the method retrieves the security header from the request. If this header is either absent
     *                          or does not begin with the expected token prefix (as defined in {@code SecurityConstants.HEADER_STRING}
     *                          and {@code SecurityConstants.TOKEN_PREFIX}), the filter chain is allowed to continue to the next element
     *                          in the chain or the requested resource, effectively bypassing any specific security checks implemented
     *                          in this method.</p>
     *
     *                          <p>If the security header is present and correctly prefixed, the method proceeds to authenticate the user.
     *                          This is done by calling {@code getAuthentication(HttpServletRequest)}, a method presumably designed to parse
     *                          the header and validate the credentials contained within. Upon successful authentication, the authenticated
     *                          {@link UsernamePasswordAuthenticationToken} is set in the {@link SecurityContextHolder}, ensuring that the
     *                          user's security context is correctly established for subsequent security checks in the application.</p>
     *
     *                          <p>Finally, regardless of the authentication outcome, the filter chain is allowed to proceed, ensuring that
     *                          the request continues through any additional filters and ultimately to the requested resource, albeit with
     *                          the user's authentication status appropriately updated based on the presence and validity of the security header.</p>
     */
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

    /**
     * Attempts to authenticate a user based on the JWT token found in the HTTP request's authorization header.
     * This method extracts the token from the authorization header, decodes and verifies it to extract the subject
     * (typically the username), and then constructs an {@link UsernamePasswordAuthenticationToken} with the subject
     * and an empty list of authorities, indicating that the user is authenticated but without any specific roles.
     *
     * @param request The {@link HttpServletRequest} from which the authorization header is extracted.
     * @return An {@link UsernamePasswordAuthenticationToken} representing the authenticated user, or {@code null}
     * if the token is invalid, expired, or not present in the request.
     *
     * <p>The method first attempts to retrieve the authorization header from the incoming request using the
     * header name specified by {@code SecurityConstants.HEADER_STRING}. If this header is not found, the method
     * immediately returns {@code null}, indicating that authentication could not be performed due to the absence
     * of the token.</p>
     *
     * <p>If the authorization header is present, the method then strips the prefix defined by
     * {@code SecurityConstants.TOKEN_PREFIX} from the token, assuming the token is prefixed as such to denote its type.
     * Following this, the method decodes the secret key, specified by {@code SecurityConstants.getTokenSecret()},
     * using Base64 encoding and uses it to create a {@link SecretKey} instance compatible with the HS512 signature
     * algorithm.</p>
     *
     * <p>With the secret key, the method creates a {@link JwtParser} to verify and parse the token. If the token is
     * successfully parsed, it extracts the claims payload to retrieve the subject of the token, which represents the
     * username of the authenticated user. If the subject is not present in the claims, indicating an invalid or malformed
     * token, the method returns {@code null}.</p>
     *
     * <p>Assuming a valid subject is found, the method constructs and returns a new
     * {@link UsernamePasswordAuthenticationToken} with the subject as the principal, {@code null} credentials, and an
     * empty list of granted authorities. This token can then be used by the security context to represent the current
     * user's authentication state.</p>
     */

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(SecurityConstants.HEADER_STRING);

        if (authorizationHeader == null) {
            return null;
        }

        String token = authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
        byte[] secretKeyBytes = Base64.getEncoder().encode(SecurityConstants.getTokenSecret().getBytes());
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

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