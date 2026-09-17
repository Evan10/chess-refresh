package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static dataaccess.DatabaseManager.getConnection;


public class SqlAuthDAO implements AuthDAO{
    @Override
    public void clearAuth() {
        String sql = """
            DELETE FROM authentication""";
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.execute();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUsername(String authToken) throws DataNotFoundException {
        String sql = """
                SELECT username FROM authentication
                WHERE auth_token = ?
                """;
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,authToken);
                if(!ps.execute()){
                    throw new DataNotFoundException("Error: session not found");
                }
                ResultSet rs = ps.getResultSet();
                rs.next();
                return rs.getString("username");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAuth(AuthData authData) {
        String sql = """
                INSERT INTO authentication (username,auth_token)
                VALUES (?,?)
                """;
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,authData.);
                if(!ps.execute()){
                    throw new DataNotFoundException("Error: session not found");
                }
                ResultSet rs = ps.getResultSet();
                rs.next();
                return rs.getString("username");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAuth(String authToken) throws DataNotFoundException {

    }

    @Override
    public boolean isEmpty() {
        String sql = """
            SELECT CASE
                WHEN EXISTS(SELECT 1 FROM authentication) THEN 0
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
}
