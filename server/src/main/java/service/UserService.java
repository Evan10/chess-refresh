package service;

import dataaccess.AuthDAO;
import dataaccess.DataNotFoundException;
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
        return new FailureOrResult<>(new RegisterResult(authData.username(),authData.authToken()));
    }

    public FailureOrResult<LoginResult> login(LoginRequest req){
        try {
            UserData userData = userDAO.getUser(req.username());
            if(!userData.password().equals(req.password())){
                return new FailureOrResult<>(new FailureResult(401,"Error: unauthorized"));
            }
            AuthData authData = addAuth(req.username());
            return new FailureOrResult<>(new LoginResult(authData.username(),authData.authToken()));
        } catch (DataNotFoundException e) {
            return new FailureOrResult<>(new FailureResult(401, "Error: unauthorized"));
        }
    }

    public FailureOrResult<EmptyResult> logout(String authToken){
        try {
            authDAO.removeAuth(authToken);
            return new FailureOrResult<>(new EmptyResult());
        } catch (DataNotFoundException e) {
            return new FailureOrResult<>(new FailureResult(401, "Error: unauthorized"));
        }
    }

    private AuthData addAuth(String username){
        String authToken = UUIDGenerator.generateUUID();
        AuthData authData = new AuthData(authToken,username);
        authDAO.addAuth(authData);
        return authData;
    }
}
