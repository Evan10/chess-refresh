package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import model.AuthData;
import org.eclipse.jetty.http.HttpStatus;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.FailureOrResult;
import service.AuthService;
import service.ClearDatabaseService;
import service.GameService;
import service.UserService;

import java.lang.reflect.RecordComponent;
import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {

        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error: unable to start server due to database connection error",e);
        }

        DAOFactory factory = new DAOFactory(true);
        AuthDAO authDAO = factory.buildAuthDAO();
        GameDAO gameDAO = factory.buildGameDAO();
        UserDAO userDAO = factory.buildUserDAO();

        AuthService authService = new AuthService(authDAO);
        ClearDatabaseService clearDatabaseService = new ClearDatabaseService(authDAO,gameDAO,userDAO);
        GameService gameService = new GameService(gameDAO);
        UserService userService = new UserService(userDAO,authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .before((ctx )->{
                    AuthData authData = authService.authenticate(ctx.header("authorization"));
                    ctx.attribute("auth",authData);
                })
                .post("/user",(ctx)->{
                    RegisterRequest req = new Gson().fromJson(ctx.body(),RegisterRequest.class);
                    if(!isValidRequest(req)){
                        ctx.status(400);
                        return;
                    }
                    resolveServiceResult(ctx,userService.registerUser(req));
                })
                .post("/session",(ctx)->{
                    LoginRequest req = new Gson().fromJson(ctx.body(),LoginRequest.class);
                    if(!isValidRequest(req)){
                        ctx.status(400);
                        return;
                    }
                    resolveServiceResult(ctx,userService.login(req));
                })
                .delete("/session",(ctx) -> {
                    if(!isAuthorized(ctx)){
                        unauthorized(ctx);
                        return;
                    }
                    AuthData authData = ctx.attribute("auth");
                    assert authData != null;
                    resolveServiceResult(ctx,userService.logout(authData.authToken()));
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
                    if(!isValidRequest(req)){
                        ctx.status(400);
                        return;
                    }
                    resolveServiceResult(ctx,gameService.createGame(req));
                })
                .put("/game",(ctx)->{
                    if(!isAuthorized(ctx)){
                        unauthorized(ctx);
                        return;
                    }
                    JoinGameRequest req = new Gson().fromJson(ctx.body(),JoinGameRequest.class);
                    if(!isValidRequest(req)){
                        ctx.status(400);
                        return;
                    }
                    AuthData authData = ctx.attribute("auth");
                    assert authData != null;
                    resolveServiceResult(ctx,gameService.joinGame(req,authData.username()));
                })
                .delete("/db",(ctx)->{
                    resolveServiceResult(ctx,clearDatabaseService.clearDatabase());
                })
                .exception(RuntimeException.class,(e, ctx) -> {
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                })
                .error(HttpStatus.BAD_REQUEST_400,(ctx)->{
                    ctx.contentType(ContentType.APPLICATION_JSON);
                    ctx.result(new Gson().toJson(Map.of("message","Error: bad request")));
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
            ctx.status(HttpStatus.OK_200);
            ctx.result(new Gson().toJson(result.getResult()));
        }else{
            ctx.status(result.getFailure().status());
            ctx.result(new Gson().toJson(result.getFailure()));
        }
    }

    /*
    *   returns true if every field in a Record object is not null
    *   and furthermore that all strings are not empty
    * */
    public <T extends Record> boolean isValidRequest(T t){
        for(RecordComponent rc: t.getClass().getRecordComponents()){
            try {
                Object o = rc.getAccessor().invoke(t);
                if(o == null){
                    return false;
                }else if(o instanceof String s && s.isEmpty()){
                    return false;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
        }
    }

