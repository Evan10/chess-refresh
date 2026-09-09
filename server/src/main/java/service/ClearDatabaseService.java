package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearDatabaseService {

    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    ClearDatabaseService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    void clearDatabase(){
        authDAO.clearAuth();
        gameDAO.clearGames();
        userDAO.clearUsers();
    }
}
