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

import java.io.IOException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.*;

public class Server {

    private final Javalin javalin;

    private Logger logger;

    public Server() {

        configureLogger();

        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error: unable to start server due to database connection error",e);
        }
        logger.info("Database connection setup");

        DAOFactory factory = new DAOFactory(DAOType.Database);
        AuthDAO authDAO = factory.buildAuthDAO();
        GameDAO gameDAO = factory.buildGameDAO();
        UserDAO userDAO = factory.buildUserDAO();
        logger.info("DAOs setup");

        AuthService authService = new AuthService(authDAO);
        ClearDatabaseService clearDatabaseService = new ClearDatabaseService(authDAO,gameDAO,userDAO);
        GameService gameService = new GameService(gameDAO);
        UserService userService = new UserService(userDAO,authDAO);
        logger.info("services setup");

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
                    logServerError(e);
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
        logger.info("API endpoints setup");

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





        private void configureLogger(){
            logger = Logger.getGlobal();
            try {
                FileHandler h = new FileHandler("log.log",true);
                h.setFormatter(new SimpleFormatter());
                logger.addHandler(h);
                logger.setLevel(Level.INFO);
                Runtime.getRuntime().addShutdownHook(new Thread(h::close));
            } catch (IOException e) {
                throw new RuntimeException("Error: unable to setup file handler for logging");
            }
        }

        private void logServerError(Exception e){
            StringBuilder sb = new StringBuilder(e.toString());
            sb.append("\n");
            Arrays.stream(e.getStackTrace())
                    .limit(6)
                    .forEach((t)-> sb.append(t.toString().indent(4)));
            logger.warning(sb.toString());
        }
    }

