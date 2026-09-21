package dataaccess;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import dataaccess.sql.DatabaseManager;
import dataaccess.sql.SqlAuthDAO;
import dataaccess.sql.SqlGameDAO;
import dataaccess.sql.SqlUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Random;

public class DAOTests{

    private static SqlAuthDAO authDAO;
    private static SqlGameDAO gameDAO;
    private static SqlUserDAO userDAO;

    final static UserData USER = new UserData("Bob","B0b","bob@gmail.com");
    final static AuthData AUTH = new AuthData("AuthToken","Username");
    static GameData game = new GameData(1,null,null,"Game",new ChessGame());

    @BeforeAll
    static void init() throws DataAccessException {
        DatabaseManager.createDatabase();
        DAOFactory factory = new DAOFactory(DAOFactory.DAOType.Database);
        authDAO = (SqlAuthDAO) factory.buildAuthDAO();
        gameDAO = (SqlGameDAO) factory.buildGameDAO();
        userDAO = (SqlUserDAO)  factory.buildUserDAO();
    }

    @BeforeEach
    void reset() throws DataAccessException {
        authDAO.clearAuth();
        gameDAO.clearGames();
        userDAO.clearUsers();

        game = new GameData(gameDAO.nextID(),null,null,"Game",new ChessGame());
    }


    @Test
    void sqlCreateUserSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->userDAO.addUser(USER));
        Assertions.assertFalse(userDAO.isEmpty());
        Assertions.assertDoesNotThrow(()->{
            Assertions.assertEquals(USER,userDAO.getUser(USER.username()));
        });
    }

    @Test
    void sqlCreateUserUsernameInUse() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->userDAO.addUser(USER));
        Assertions.assertThrows(InUseException.class,()->userDAO.addUser(USER));
        Assertions.assertFalse(userDAO.isEmpty());
    }

    @Test
    void sqlLoginSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(AUTH));
        Assertions.assertDoesNotThrow(()->{
            String retrieved = authDAO.getUsername(AUTH.authToken());
            Assertions.assertEquals(AUTH.username(),retrieved);
        });
        Assertions.assertFalse(authDAO.isEmpty());
    }

    @Test
    void sqlLogoutSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(AUTH));
        Assertions.assertDoesNotThrow(()->authDAO.removeAuth(AUTH.authToken()));
        Assertions.assertThrows(DataAccessException.class,()->authDAO.getUsername(AUTH.authToken()));
        Assertions.assertTrue(authDAO.isEmpty());
    }

    @Test
    void sqlLogoutNotLoggedIn() throws DataAccessException {
        Assertions.assertThrows(DataNotFoundException.class,()->authDAO.removeAuth(AUTH.authToken()));
        Assertions.assertTrue(authDAO.isEmpty());
    }

    @Test
    void sqlCreateGameSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game));
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void sqlCreateGameIDInUse() throws DataAccessException {
        gameDAO.addGame(game);
        Assertions.assertThrows(DataAccessException.class,()->gameDAO.addGame(game));
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void sqlListGames() throws DataAccessException {
        gameDAO.addGame(game);
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.contains(game));
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void sqlJoinGameSuccess() throws DataAccessException {
        userDAO.addUser(USER);
        gameDAO.addGame(game);
        gameDAO.joinGame(ChessGame.TeamColor.BLACK, USER.username(),game.gameID());
        Collection<GameData> games = gameDAO.listGames();
        games.stream().filter((g)->g.gameID()==game.gameID()).findFirst().ifPresent((gameData)->{
            Assertions.assertEquals(USER.username(),gameData.blackUsername());
        });
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void sqlJoinGameFail() throws DataAccessException {
        userDAO.addUser(USER);
        gameDAO.addGame(game);
        gameDAO.joinGame(ChessGame.TeamColor.BLACK, USER.username(),game.gameID());
        Assertions.assertThrows(InUseException.class,
                ()->gameDAO.joinGame(ChessGame.TeamColor.BLACK, USER.username(),game.gameID()));
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void sqlGameStateMaintained() throws DataAccessException {
        userDAO.addUser(USER);
        ChessGame randomGame = getChessGameRandomBoard();
        GameData gameData = new GameData(5,"Bob","Bob","GameName123",randomGame);
        gameDAO.addGame(gameData);
        Assertions.assertDoesNotThrow(()->{
            GameData returnedGame = gameDAO.listGames().stream().findFirst().get();
            Assertions.assertEquals(gameData,returnedGame);
        });
        Assertions.assertFalse(gameDAO.isEmpty());
    }




    private static ChessGame getChessGameRandomBoard(){
        ChessGame cg = new ChessGame();
        Random r = new Random();
        for(int x = 1; x <= 8; x++){
            for(int y = 1; y <=8; y++){
                int ord= r.nextInt(0,5);
                ChessGame.TeamColor team = r.nextBoolean()?
                        ChessGame.TeamColor.BLACK:ChessGame.TeamColor.WHITE;
                ChessPiece p = new ChessPiece(team,ChessPiece.PieceType.values()[ord]);
                cg.getBoard().addPiece(new ChessPosition(y,x),p);
            }
        }
        return cg;
    }



}
