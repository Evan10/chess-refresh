package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import static dataaccess.DatabaseManager.getConnection;

public class SqlGameDAO implements GameDAO{
    @Override
    public void clearGames() {
        String sql = """
            DELETE FROM games""";
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.execute();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addGame(GameData gameData) {

    }

    @Override
    public void joinGame(ChessGame.TeamColor playerColor, String username, int gameID) throws InUseException, DataNotFoundException {

    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public boolean isEmpty() {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int nextID() {
        return 0;
    }
}
