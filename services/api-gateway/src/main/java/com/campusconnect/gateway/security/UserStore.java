package com.campusconnect.gateway.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Usuarios de prueba en memoria (uno por rol). Para un entorno de demostracion.
 * En produccion se usaria un proveedor de identidad y contrasenas cifradas.
 */
@Component
public class UserStore {

    public record User(String username, String password, String role) {}

    private final Map<String, User> users = Map.of(
            "secretaria", new User("secretaria", "demo123", "SECRETARIA"),
            "finanzas", new User("finanzas", "demo123", "FINANZAS"),
            "docente", new User("docente", "demo123", "DOCENTE"),
            "direccion", new User("direccion", "demo123", "DIRECCION")
    );

    public Optional<User> authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.password().equals(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
