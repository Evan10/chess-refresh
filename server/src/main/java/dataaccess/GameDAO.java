package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.ChessUserRole;

import java.util.Collection;

public interface GameDAO {

    void clearGames();

    String addGame(String gameName) throws InUseException;
    void joinGame(ChessGame.TeamColor playerColor, String gameID) throws InUseException,DataNotFoundException;
    Collection<GameData> listGames();

    boolean isEmpty();
}
