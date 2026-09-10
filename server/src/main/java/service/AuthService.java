package service;

import dataaccess.AuthDAO;
import dataaccess.DataNotFoundException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import model.AuthData;

import java.util.HashMap;

public class AuthService {

    private AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public AuthData authenticate(String authToken){
        try {
            String username = authDAO.getUsername(authToken);
            return new AuthData(authToken,username);
        } catch (DataNotFoundException e) {
            return null;
        }
        }
    
}
