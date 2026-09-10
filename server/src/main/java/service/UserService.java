package service;

import dataaccess.AuthDAO;
import dataaccess.InUseException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;

public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public FailureOrResult<RegisterResult> registerUser(RegisterRequest req){
        UserData userData = new UserData(req.username(),req.password(),req.email());
        try {
            userDAO.addUser(userData);
        } catch (InUseException e) {
            return new FailureOrResult<>(new FailureResult(403, "Error: already taken"));
        }
        AuthData authData = addAuth(req.username());


        return new FailureOrResult<>(new FailureResult(500, "Error: not implemented"));
    }

    public FailureOrResult<LoginResult> login(LoginRequest req){

        return new FailureOrResult<>(new FailureResult(500, "Error: not implemented"));
    }

    public FailureOrResult<EmptyResult> logout(){

        return new FailureOrResult<>(new FailureResult(500, "Error: not implemented"));
    }

    private AuthData addAuth(String username){
        String authToken = UUIDGenerator.generateUUID();
        AuthData authData = new AuthData(username,authToken);
        authDAO.addAuth(authData);
        return authData;
    }
}
