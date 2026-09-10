package service;

import chess.ChessGame;
import dataaccess.DataNotFoundException;
import dataaccess.GameDAO;
import dataaccess.InUseException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.*;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public FailureOrResult<ListGamesResult> getGames(){
        Collection<GameData> games = gameDAO.listGames();
        return new FailureOrResult<>(new ListGamesResult(games));
    }

    public FailureOrResult<CreateGameResult> createGame(CreateGameRequest req){
        int id = gameDAO.nextID();
        GameData newGame = new GameData(id,null,null,req.gameName(),new ChessGame());
        gameDAO.addGame(newGame);
        return new FailureOrResult<>(new CreateGameResult(id));
    }

    public FailureOrResult<EmptyResult> joinGame(JoinGameRequest req, String username){

        try {
            gameDAO.joinGame(req.playerColor(),username, req.gameID());
            return new FailureOrResult<>(new EmptyResult());
        } catch (InUseException e) {
            return new FailureOrResult<>(new FailureResult(403, "Error: player position already in use"));
        } catch (DataNotFoundException e) {
            return new FailureOrResult<>(new FailureResult(400, "Error: bad request"));
        }

    }

}
