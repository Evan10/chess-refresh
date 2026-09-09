package service;

import dataaccess.GameDAO;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.*;

public class GameService {

    GameDAO gameDAO;

    public GameService(GameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public FailureOrResult<ListGamesResult> getGames(){


        return new FailureOrResult<>(new FailureResult(500, "Error: not implemented"));
    }

    public FailureOrResult<CreateGameResult> createGame(CreateGameRequest req){


        return new FailureOrResult<>(new FailureResult(500, "Error: not implemented"));
    }

    public FailureOrResult<JoinGameResult> joinGameResult(JoinGameRequest req){


        return new FailureOrResult<>(new FailureResult(500, "Error: not implemented"));
    }

}
