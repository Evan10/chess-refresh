package service;

import dataaccess.*;
import io.javalin.http.Context;
import model.AuthData;

import java.util.HashMap;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public AuthData authenticate(String authToken){
        try {
            String username = authDAO.getUsername(authToken);
            return new AuthData(authToken,username);
        } catch (DataAccessException e) {
            return null;
        }
    }
    
}
