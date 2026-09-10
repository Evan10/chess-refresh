package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;

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

        Assertions.assertFalse(gameDAO.isEmpty()); // verify that DAOs are not empty now
        Assertions.assertFalse(userDAO.isEmpty());
        Assertions.assertFalse(authDAO.isEmpty());

        FailureOrResult<EmptyResult> res = clearDatabaseService.clearDatabase();
        Assertions.assertTrue(res.wasSuccessful());

        Assertions.assertTrue(gameDAO.isEmpty()); // verify empty at after clear
        Assertions.assertTrue(userDAO.isEmpty());
        Assertions.assertTrue(authDAO.isEmpty());
    }

    @Test void authenticateSuccess(){
        authDAO.addAuth(DUMMY_AUTH);
        AuthData res = authService.authenticate(DUMMY_AUTH.authToken());
        Assertions.assertNotNull(res);
    }

    @Test void authenticateIsNullFail(){
        AuthData res = authService.authenticate(DUMMY_AUTH.authToken());
        Assertions.assertNull(res);
    }

    @Test
    public void registerUserTestSuccess(){
        Assertions.assertTrue(userDAO.isEmpty());
        RegisterRequest req = new RegisterRequest(DUMMY_USER.username(),DUMMY_USER.password(),DUMMY_USER.email());
        FailureOrResult<RegisterResult> res = userService.registerUser(req);
        Assertions.assertTrue(res.wasSuccessful());
        Assertions.assertFalse(userDAO.isEmpty());
    }

    @Test
    public void registerUserFail(){
        RegisterRequest req = new RegisterRequest(DUMMY_USER.username(),DUMMY_USER.password(),DUMMY_USER.email());
        FailureOrResult<RegisterResult> res = userService.registerUser(req);
        Assertions.assertTrue(res.wasSuccessful());
        FailureOrResult<RegisterResult> res2 = userService.registerUser(req);
        Assertions.assertFalse(res2.wasSuccessful());
    }

    @Test
    public void loginUserTestSuccess(){
        RegisterRequest req = new RegisterRequest(DUMMY_USER.username(),DUMMY_USER.password(),DUMMY_USER.email());
        userService.registerUser(req);
        authDAO.clearAuth();

        LoginRequest lreq = new LoginRequest(DUMMY_USER.username(),DUMMY_USER.password());
        FailureOrResult<LoginResult> lres = userService.login(lreq);
        Assertions.assertTrue(lres.wasSuccessful());
        LoginResult r = lres.getResult();
        Assertions.assertEquals(DUMMY_USER.username(),r.username());
    }

    @Test
    public void loginUserWrongPasswordFail(){
        RegisterRequest req = new RegisterRequest(DUMMY_USER.username(),DUMMY_USER.password(),DUMMY_USER.email());
        userService.registerUser(req);
        authDAO.clearAuth();

        LoginRequest lreq = new LoginRequest(DUMMY_USER.username(),"wrongPassword");
        FailureOrResult<LoginResult> lres = userService.login(lreq);
        Assertions.assertFalse(lres.wasSuccessful());
    }
    @Test
    public void loginNoSuchUserFail(){
        LoginRequest lreq = new LoginRequest(DUMMY_USER.username(),DUMMY_USER.username());
        FailureOrResult<LoginResult> lres = userService.login(lreq);
        Assertions.assertFalse(lres.wasSuccessful());
    }

    @Test
    public void logoutSuccess(){
        RegisterRequest req = new RegisterRequest(DUMMY_USER.username(),DUMMY_USER.password(),DUMMY_USER.email());
        FailureOrResult<RegisterResult> res = userService.registerUser(req);
        Assertions.assertTrue(res.wasSuccessful());
        RegisterResult rres = res.getResult();

        Assertions.assertDoesNotThrow(()->authDAO.getUsername(rres.authToken()));

        FailureOrResult<EmptyResult> lores =  userService.logout(rres.authToken());
        Assertions.assertTrue(lores.wasSuccessful());

        Assertions.assertThrows(DataNotFoundException.class, ()->authDAO.getUsername(rres.authToken()));
    }

    @Test
    public void logoutNoSuchUserFail(){

        FailureOrResult<EmptyResult> res =  userService.logout("invalidToken");
        Assertions.assertFalse(res.wasSuccessful());

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
