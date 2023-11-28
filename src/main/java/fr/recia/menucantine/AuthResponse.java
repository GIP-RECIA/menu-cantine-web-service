package fr.recia.menucantine;

import lombok.Data;

@Data
public class AuthResponse {

    private String message;
    private String token;
    private String error;

}