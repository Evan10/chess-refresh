package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    @Override
    public void clearGames() {

    }

    @Override
    public String addGame(String gameName) throws InUseException {
        return "";
    }

    @Override
    public void joinGame(ChessGame.TeamColor playerColor, String gameID) throws InUseException, DataNotFoundException {

    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
