package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static dataaccess.DatabaseManager.getConnection;

public class SqlGameDAO implements GameDAO{

    private int id = 1;
    private static final Gson gameSerializer = new Gson();
    SqlGameDAO(){
    }

    @Override
    public void clearGames() throws DataAccessException{
        String sql = """
            DELETE FROM games""";
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to access database",e);
        }
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        String sql = """
                INSERT INTO games (game_id,game_name,white_username,black_username,game_data)
                VALUES (?,?,?,?,?)
                """;
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1,gameData.gameID());
                ps.setString(2,gameData.gameName());
                ps.setString(3,gameData.whiteUsername());
                ps.setString(4,gameData.blackUsername());
                String game = gameSerializer.toJson(gameData.game());
                ps.setString(5,game);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database exception");
        }

    }

    @Override
    public void joinGame(ChessGame.TeamColor playerColor, String username, int gameID) throws DataAccessException {

        GameData gameData = getGame(gameID);
        switch (playerColor){
            case WHITE -> {if(gameData.whiteUsername() != null) throw new InUseException("Error: player color in use");}
            case BLACK -> {if(gameData.blackUsername() != null) throw new InUseException("Error: player color in use");}
        }

        String sql = """
                UPDATE games
                SET white_username = ?, black_username = ?
                WHERE game_id = ?
                """;
        try(Connection conn = getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, playerColor == WHITE ? username : gameData.whiteUsername());
                ps.setString(2, playerColor == WHITE ? gameData.blackUsername() : username);
                ps.setInt(3, gameID);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: can't update game",e);
        }
    }

    private GameData getGame(int gameID) throws DataAccessException {
        String sql = """
                SELECT game_id,game_name,white_username,black_username,game_data
                FROM games
                WHERE game_id = ?
                """;
        try( Connection conn = getConnection()){
            try( PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setInt(1,gameID);
                ps.execute();
                ResultSet rs = ps.getResultSet();
                if(!rs.next()){
                    throw new DataNotFoundException("Error: unable to find game with given id");
                }
                ChessGame game = gameSerializer.fromJson(rs.getString("game_data"),ChessGame.class);
                return new GameData(gameID, rs.getString("white_username"),
                        rs.getString("black_username"), rs.getString("game_name"), game);
            }
        } catch (RuntimeException | SQLException e) {
            throw new DataAccessException("Error: unable to access database",e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException{
        String sql = """
                SELECT game_id,game_name,white_username,black_username,game_data
                FROM games
                """;
        try( Connection conn = getConnection()){
            try( PreparedStatement ps = conn.prepareStatement(sql)){
                if(!ps.execute()){
                    throw new DataNotFoundException("Error: unable to find game with given id");
                }
                ResultSet rs = ps.getResultSet();
                ArrayList<GameData> games = new ArrayList<>();
                while (rs.next()) {
                    ChessGame game = gameSerializer.fromJson(rs.getString("game_data"), ChessGame.class);
                    games.add(new GameData(rs.getInt("game_id"), rs.getString("white_username"),
                            rs.getString("black_username"), rs.getString("game_name"), game));
                }
                return games;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to access database",e);
        }
    }

    @Override
    public boolean isEmpty() throws DataAccessException{
        String sql = """
            SELECT CASE
                WHEN EXISTS(SELECT 1 FROM games) THEN 0
                ELSE 1
            END AS IsEmpty;""";
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.execute();
                ResultSet rs = ps.getResultSet();
                if (rs == null) {
                    throw new RuntimeException("Invalid SQL query response");
                }
                return rs.getBoolean("IsEmpty");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to access database",e);
        }
    }

    @Override
    public int nextID() {
        return ++id;
    }
}
