package server;

import dataaccess.AuthDAO;
import dataaccess.DAOFactory;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import model.AuthData;
import service.AuthService;

public class Server {

    private final Javalin javalin;

    public Server() {

        DAOFactory factory = new DAOFactory(true);
        AuthDAO authDAO = factory.buildAuthDAO();
        GameDAO gameDAO = factory.buildGameDAO();
        UserDAO userDAO = factory.buildUserDAO();

        AuthService authService = new AuthService(authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .before((ctx )->{
                    AuthData authData = authService.authenticate(ctx.header("authorization"));
                    ctx.attribute("auth",authData);
                    System.out.println((AuthData)ctx.attribute("auth"));
                })
                .post("/user",(ctx)->{})
                .post("/session",(ctx)->{})
                .delete("/session",(ctx) -> {})
                .get("/game",(ctx) -> {})
                .post("game",(ctx)->{})
                .put("/game",(ctx)->{})
                .delete("/db",(ctx)->{ })
                .error(400,ctx->{})
                .error(401,(ctx)->{})
                .error(500,(ctx)->{});
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
