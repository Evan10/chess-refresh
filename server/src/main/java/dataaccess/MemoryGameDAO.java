package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer,GameData> games;
    private int nextID;
    public MemoryGameDAO(){
        games = new HashMap<>();
        nextID = 0;
    }

    @Override
    public void clearGames() {
        games.clear();
    }

    @Override
    public void addGame(GameData gameData) {
        games.put(gameData.gameID(),gameData);
    }

    @Override
    public void joinGame(ChessGame.TeamColor playerColor,String username,int gameID) throws InUseException, DataNotFoundException {
        if(!games.containsKey(gameID)){
            throw new DataNotFoundException("game not found");
        }
        GameData gameData = games.get(gameID);

        String white = gameData.whiteUsername(), black = gameData.blackUsername();
        if(playerColor == ChessGame.TeamColor.WHITE && white == null) {
            gameData = new GameData(gameID,username,black, gameData.gameName(), gameData.game());
        }else if (playerColor == ChessGame.TeamColor.BLACK && black == null){
            gameData = new GameData(gameID,white,username, gameData.gameName(), gameData.game());
        }else{
            throw new InUseException("player color in use");
        }
        games.put(gameID,gameData);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public boolean isEmpty() {
        return games.isEmpty();
    }

    @Override
    public int nextID() {
        return ++nextID;
    }
}
