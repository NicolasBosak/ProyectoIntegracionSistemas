package com.campusconnect.gateway.security;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Filtro global: valida el JWT en toda peticion (salvo /auth y /actuator) y aplica
 * autorizacion por rol segun el prefijo de la ruta. Punto unico de seguridad del ecosistema.
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    // Rutas publicas (no requieren token)
    private static final List<String> PUBLIC_PATHS = List.of("/auth", "/actuator");

    // Autorizacion por rol segun prefijo de ruta
    private static final Map<String, String> PREFIX_ROLE = Map.of(
            "/academic", "SECRETARIA",
            "/payments", "FINANZAS",
            "/attendance", "DOCENTE",
            "/analytics", "DIRECCION",
            "/notifications", "DIRECCION"
    );

    private final JwtService jwtService;

    public AuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return deny(exchange, HttpStatus.UNAUTHORIZED, "Falta el token JWT");
        }

        final Claims claims;
        try {
            claims = jwtService.parse(authHeader.substring(7));
        } catch (Exception e) {
            return deny(exchange, HttpStatus.UNAUTHORIZED, "Token invalido o expirado");
        }

        String role = String.valueOf(claims.get("role"));
        String requiredRole = requiredRoleFor(path);
        if (requiredRole != null && !requiredRole.equals(role)) {
            log.warn("Acceso denegado path={} role={} requiere={}", path, role, requiredRole);
            return deny(exchange, HttpStatus.FORBIDDEN, "El rol " + role + " no puede acceder a " + path);
        }

        // Propaga identidad a los servicios downstream
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-User", claims.getSubject())
                .header("X-Role", role)
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private String requiredRoleFor(String path) {
        return PREFIX_ROLE.entrySet().stream()
                .filter(e -> path.startsWith(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private Mono<Void> deny(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        byte[] body = ("{\"status\":" + status.value() + ",\"message\":\"" + message + "\"}")
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() {
        return -1; // se ejecuta antes del enrutamiento
    }
}
