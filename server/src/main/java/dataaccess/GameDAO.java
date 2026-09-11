package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void clearGames();

    void addGame(GameData gameData);
    void joinGame(ChessGame.TeamColor playerColor, String username, int gameID) throws InUseException,DataNotFoundException;
    Collection<GameData> listGames();

    boolean isEmpty();

    int nextID();
}
