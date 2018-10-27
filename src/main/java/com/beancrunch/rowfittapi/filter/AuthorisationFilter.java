package com.beancrunch.rowfittapi.filter;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Optional;

@Component
public class AuthorisationFilter implements WebFilter {

    private JwtParser jwtParser;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        return Optional
                .ofNullable(serverWebExchange.getRequest().getHeaders().get("Authorization"))
                .flatMap(headers -> headers.stream().findFirst())
                .filter(authHeader -> !authHeader.isEmpty() && authHeader.startsWith("Bearer"))
                .map(authHeader -> authHeader.replaceAll("Bearer ", ""))
                .map(accessToken -> {
                    try {
                        validateAccessTokenAndSetEmailAttribute(accessToken, serverWebExchange);
                    } catch (CertificateException e) {
                        throw new NotAuthorisedException("access token couldn't be verified", e);
                    }
                    return webFilterChain.filter(serverWebExchange);
                })
                .orElseThrow(() -> new NotAuthorisedException("access token invalid"));
    }

    private void validateAccessTokenAndSetEmailAttribute(String accessToken, ServerWebExchange serverWebExchange) throws CertificateException {
        jwtParser = Optional.ofNullable(jwtParser).orElse(Jwts.parser());
        Jws<Claims> claimsJws = jwtParser
                .setSigningKey(getPublicKey())
                .parseClaimsJws(accessToken);

        String email = Optional
                .ofNullable(claimsJws)
                .map(Jwt::getBody)
                .map(claim -> claim.get("https://beancrunch.com/email", String.class))
                .orElseThrow(() -> new NotAuthorisedException("access token invalid"));
        serverWebExchange.getAttributes().put("userId", email);
    }

    private PublicKey getPublicKey() throws CertificateException {
        String certificate =
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDCTCCAfGgAwIBAgIJAO+L33WbKBihMA0GCSqGSIb3DQEBCwUAMCIxIDAeBgNV\n" +
                "BAMTF2JlYW5jcnVuY2guZXUuYXV0aDAuY29tMB4XDTE3MDUwMTExMTIxNVoXDTMx\n" +
                "MDEwODExMTIxNVowIjEgMB4GA1UEAxMXYmVhbmNydW5jaC5ldS5hdXRoMC5jb20w\n" +
                "ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC8oo8reDWDQ4nc4zSkT9gS\n" +
                "L7jABvgGGxgMiYPQB31PkiE5YgimNJtD4AJXMI1ebht+MhooXtjJnS8p8isX8qyl\n" +
                "+oDupeJHQ1lTcBbW4tQ11ympSi8A0jIFmoodFXKVE6kN5YRlb1fRP5nNOYwxBX2L\n" +
                "bD2ppEHiiITaqMx+8FhWr08DPjttgLwDTZompV9KDhhz0n0kUBbORX/ZRA0lams2\n" +
                "pPIz5S5yIkcNpp7y0UrYERitdIwmZvQwTol3+8RKsQAXclGZwnwE8T6y+pafchNG\n" +
                "obkD7aSOgXsD4mPYuUAl2v7PUvOYs821V3yA8es/oD8Rt7dMDpqN8fU9r1JgFqcB\n" +
                "AgMBAAGjQjBAMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFAXWaecLNyWGMnIB\n" +
                "rWlSeLCbtDVjMA4GA1UdDwEB/wQEAwIChDANBgkqhkiG9w0BAQsFAAOCAQEAmUbX\n" +
                "f0tVYIU45kUZifOBg0ZKb2mtSjSjji3tIhmysUchHmRgVwbGysO6nwZQQVgBHmY4\n" +
                "YEPMT0HovmrEZcZyn3ernQqUmEk5Nwc258asBUnirFWlzL2y3xhPh5admlpAtTuA\n" +
                "QgUEMvxHLKRJ9rv4WnIyiX6gAbvJC4Nu3N2gMarhJINW5wai1Hdx4hbxf+yTG0dY\n" +
                "xO3WX3j+WekFx3iUWzykMBiDreYxJr+nGwnWi4aVTmVk21CVlUelzKSO7pSsVwo+\n" +
                "fCh1mhz4R9aTsRLi6lQQEEK/C2f+dDmGC24QvwUYoL106YT81nIEV36d10YeZj7w\n" +
                "rHgy0YXJK3f3U/dADw==\n" +
                "-----END CERTIFICATE-----\n";
        InputStream inputStream = new ByteArrayInputStream(certificate.getBytes());
        return CertificateFactory.getInstance("X.509").generateCertificate(inputStream).getPublicKey();
    }

    public void setJwtParser(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }
}
