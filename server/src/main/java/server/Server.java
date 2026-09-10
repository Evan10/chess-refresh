package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DAOFactory;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import model.AuthData;
import org.eclipse.jetty.http.HttpStatus;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.FailureOrResult;
import result.FailureResult;
import result.ListGamesResult;
import service.AuthService;
import service.ClearDatabaseService;
import service.GameService;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {

        DAOFactory factory = new DAOFactory(true);
        AuthDAO authDAO = factory.buildAuthDAO();
        GameDAO gameDAO = factory.buildGameDAO();
        UserDAO userDAO = factory.buildUserDAO();

        AuthService authService = new AuthService(authDAO);
        ClearDatabaseService clearDatabaseService = new ClearDatabaseService(authDAO,gameDAO,userDAO);
        GameService gameService = new GameService(gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .before((ctx )->{
                    AuthData authData = authService.authenticate(ctx.header("authorization"));
                    ctx.attribute("auth",authData);
                })
                .post("/user",(ctx)->{

                })
                .post("/session",(ctx)->{

                })
                .delete("/session",(ctx) -> {
                    if(!isAuthorized(ctx)){
                        unauthorized(ctx);
                        return;
                    }

                })
                .get("/game",(ctx) -> {
                    if(!isAuthorized(ctx)){
                        unauthorized(ctx);
                        return;
                    }
                    resolveServiceResult(ctx,gameService.getGames());
                })
                .post("/game",(ctx)->{
                    if(!isAuthorized(ctx)){
                        unauthorized(ctx);
                        return;
                    }
                    CreateGameRequest req = new Gson().fromJson(ctx.body(),CreateGameRequest.class);
                    resolveServiceResult(ctx,gameService.createGame(req));
                })
                .put("/game",(ctx)->{
                    if(!isAuthorized(ctx)){
                        unauthorized(ctx);
                        return;
                    }
                    JoinGameRequest req = new Gson().fromJson(ctx.body(),JoinGameRequest.class);
                    resolveServiceResult(ctx,gameService.joinGame(req));
                })
                .delete("/db",(ctx)->{
                    clearDatabaseService.clearDatabase();
                    ctx.status(HttpStatus.OK_200);
                    ctx.contentType(ContentType.APPLICATION_JSON);
                    ctx.result("{}");
                })
                .exception(RuntimeException.class,(e, ctx) -> {
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                })
                .error(HttpStatus.INTERNAL_SERVER_ERROR_500,(ctx)->{
                    ctx.contentType(ContentType.APPLICATION_JSON);
                    ctx.result(new Gson().toJson(Map.of("message","Error: internal server error")));
                });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public boolean isAuthorized(Context ctx){
        AuthData authData = ctx.attribute("auth");
        return authData != null && !authData.username().isEmpty() && !authData.authToken().isEmpty();
    }

    public void unauthorized(Context ctx){
        ctx.contentType(ContentType.APPLICATION_JSON);
        ctx.status(HttpStatus.UNAUTHORIZED_401);
        ctx.result(new Gson().toJson(Map.of("message","Error:unauthorized")));
    }

    public <T extends Record> void resolveServiceResult(Context ctx, FailureOrResult<T> result){
        ctx.contentType(ContentType.APPLICATION_JSON);
        if(result.wasSuccessful()){
            ctx.result(new Gson().toJson(result.getResult()));
        }else{
            ctx.status(result.getFailure().status());
            ctx.result(new Gson().toJson(result.getFailure()));
        }
    }
}
