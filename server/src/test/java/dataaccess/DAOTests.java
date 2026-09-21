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

    final static UserData user = new UserData("Bob","B0b","bob@gmail.com");
    final static AuthData auth = new AuthData("AuthToken","Username");
    static GameData game = new GameData(1,null,null,"Game",new ChessGame());

    @BeforeAll
    static void init() throws DataAccessException {
        DatabaseManager.createDatabase();
        DAOFactory factory = new DAOFactory(DAOType.Database);
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
    void SqlCreateUserSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->userDAO.addUser(user));
        Assertions.assertFalse(userDAO.isEmpty());
    }

    @Test
    void SqlCreateUserUsernameInUse() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->userDAO.addUser(user));
        Assertions.assertThrows(InUseException.class,()->userDAO.addUser(user));
        Assertions.assertFalse(userDAO.isEmpty());
    }

    @Test
    void SqlLoginSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(auth));
        Assertions.assertDoesNotThrow(()->{
            String retrieved = authDAO.getUsername(auth.authToken());
            Assertions.assertEquals(auth.username(),retrieved);
        });
        Assertions.assertFalse(authDAO.isEmpty());
    }

    @Test
    void SqlLogoutSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->authDAO.addAuth(auth));
        Assertions.assertDoesNotThrow(()->authDAO.removeAuth(auth.authToken()));
        Assertions.assertThrows(DataAccessException.class,()->authDAO.getUsername(auth.authToken()));
        Assertions.assertTrue(authDAO.isEmpty());
    }

    @Test
    void SqlLogoutNotLoggedIn() throws DataAccessException {
        Assertions.assertThrows(DataNotFoundException.class,()->authDAO.removeAuth(auth.authToken()));
        Assertions.assertTrue(authDAO.isEmpty());
    }

    @Test
    void SqlCreateGameSuccess() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->gameDAO.addGame(game));
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void SqlCreateGameIDInUse() throws DataAccessException {
        gameDAO.addGame(game);
        Assertions.assertThrows(DataAccessException.class,()->gameDAO.addGame(game));
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void SqlListGames() throws DataAccessException {
        gameDAO.addGame(game);
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.contains(game));
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void SqlJoinGameSuccess() throws DataAccessException {
        userDAO.addUser(user);
        gameDAO.addGame(game);
        gameDAO.joinGame(ChessGame.TeamColor.BLACK,user.username(),game.gameID());
        Collection<GameData> games = gameDAO.listGames();
        games.stream().filter((g)->g.gameID()==game.gameID()).findFirst().ifPresent((gameData)->{
            Assertions.assertEquals(user.username(),gameData.blackUsername());
        });
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void SqlJoinGameFail() throws DataAccessException {
        userDAO.addUser(user);
        gameDAO.addGame(game);
        gameDAO.joinGame(ChessGame.TeamColor.BLACK,user.username(),game.gameID());
        Assertions.assertThrows(InUseException.class,
                ()->gameDAO.joinGame(ChessGame.TeamColor.BLACK,user.username(),game.gameID()));
        Assertions.assertFalse(gameDAO.isEmpty());
    }

    @Test
    void SqlGameStateMaintained() throws DataAccessException {
        userDAO.addUser(user);
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
