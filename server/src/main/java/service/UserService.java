package service;

import dataaccess.*;
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
            AuthData authData = addAuth(req.username());
            return new FailureOrResult<>(new RegisterResult(authData.username(),authData.authToken()));
        } catch (InUseException e) {
            return new FailureOrResult<>(new FailureResult(403, "Error: already taken"));
        } catch (DataAccessException e) {
            return new FailureOrResult<>(new FailureResult(500, "Error: internal server error"));
        }
    }

    public FailureOrResult<LoginResult> login(LoginRequest req){
        try {
            UserData userData = userDAO.getUser(req.username());
            if(!userData.password().equals(req.password())){
                return new FailureOrResult<>(new FailureResult(401,"Error: unauthorized"));
            }
            AuthData authData = addAuth(req.username());
            return new FailureOrResult<>(new LoginResult(authData.username(),authData.authToken()));
        } catch (InUseException e) {
            return new FailureOrResult<>(new FailureResult(401, "Error: unauthorized"));
        } catch (DataAccessException e){
            return new FailureOrResult<>(new FailureResult(500, "Error: internal server error"));
        }
    }

    public FailureOrResult<EmptyResult> logout(String authToken){
        try {
            authDAO.removeAuth(authToken);
            return new FailureOrResult<>(new EmptyResult());
        } catch (DataNotFoundException e) {
            return new FailureOrResult<>(new FailureResult(401, "Error: unauthorized"));
        } catch (DataAccessException e) {
            return new FailureOrResult<>(new FailureResult(500, "Error: internal server error"));
        }
    }

    private AuthData addAuth(String username) throws DataAccessException {
        String authToken = UUIDGenerator.generateUUID();
        AuthData authData = new AuthData(authToken,username);
        authDAO.addAuth(authData);
        return authData;
    }
}
