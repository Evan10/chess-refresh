package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .before((context )->{})
                .post("/user",(context)->{})
                .post("/session",(context)->{})
                .delete("/session",(context) -> {})
                .get("/game",(context) -> {})
                .post("game",(context)->{})
                .put("/game",(context)->{})
                .delete("/db",(context)->{})
                .error(400,context->{})
                .error(401,(context)->{})
                .error(500,(context)->{});
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
