package com.rinca.erisserver.exceptions.constraint;

public interface ConstraintParserStrategy {
    String parse(String errorMessage);

    default String getUserMessage(String table, String field) {
        return switch (table + "." + field) {
            case "user.email" -> "Cet email est déjà utilisé";
            case "user.name" -> "Ce nom d'utilisateur existe déjà";
            default -> String.format("Le champ %s est déjà utilisé", field);
        };
    }
}
