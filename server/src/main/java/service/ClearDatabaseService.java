package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import result.EmptyResult;
import result.FailureOrResult;

public class ClearDatabaseService {

    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    public ClearDatabaseService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public FailureOrResult<EmptyResult> clearDatabase(){
        authDAO.clearAuth();
        gameDAO.clearGames();
        userDAO.clearUsers();
        return new FailureOrResult<>(new EmptyResult());
    }
}
