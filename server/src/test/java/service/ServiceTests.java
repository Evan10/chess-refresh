package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DAOFactory;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceTests {

    private static final GameData DUMMY_GAME = new GameData(-1," ", " "," game", new ChessGame());
    private static final UserData DUMMY_USER = new UserData("bob","b0b","bob@bob.com");
    private static final AuthData DUMMY_AUTH = new AuthData("dummyToken","bob");

    private GameDAO gameDAO;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    private GameService gameService;
    private UserService userService;
    private AuthService authService;
    private ClearDatabaseService clearDatabaseService;


    @BeforeEach
    public void init(){
        DAOFactory f = new DAOFactory(true);
        gameDAO = f.buildGameDAO();
        userDAO = f.buildUserDAO();
        authDAO = f.buildAuthDAO();

        gameService = new GameService(gameDAO);
        userService = new UserService(userDAO,authDAO);
        authService = new AuthService(authDAO);
        clearDatabaseService = new ClearDatabaseService(authDAO,gameDAO,userDAO);
    }

    @Test
    public void clearDatabaseSuccess(){
        Assertions.assertTrue(gameDAO.isEmpty()); // verify empty at start
        Assertions.assertTrue(userDAO.isEmpty());
        Assertions.assertTrue(authDAO.isEmpty());


        gameDAO.addGame(DUMMY_GAME);
        Assertions.assertDoesNotThrow(()->userDAO.addUser(DUMMY_USER));
        authDAO.addAuth(DUMMY_AUTH);

        Assertions.assertFalse(gameDAO.isEmpty());
        Assertions.assertFalse(userDAO.isEmpty());
        Assertions.assertFalse(authDAO.isEmpty());



    }

    @Test void authenticateSuccess(){

    }

    @Test void authenticateIsNullFail(){

    }

    @Test
    public void registerUserTestSuccess(){

    }

    @Test
    public void registerUserFail(){

    }

    @Test
    public void loginUserTestSuccess(){

    }

    @Test
    public void loginUserWrongPasswordFail(){

    }
    @Test
    public void loginNoSuchUserFail(){

    }

    @Test
    public void logoutSuccess(){

    }

    @Test
    public void logoutNoSuchUserFail(){

    }

    @Test
    public void getGamesSuccess(){

    }

    @Test
    public void createGameSuccess(){

    }

    @Test
    public void joinGameSuccess(){

    }

    @Test
    public void joinGameNoSuchGameFail(){

    }

    @Test
    public void joinGameSpotTakenFail(){

    }




}
