package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void clearGames() throws DataAccessException;

    void addGame(GameData gameData) throws DataAccessException;
    void joinGame(ChessGame.TeamColor playerColor, String username, int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;

    boolean isEmpty() throws DataAccessException;

    int nextID();
}
