package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DataNotFoundException;
import model.AuthData;

import java.sql.*;

import static dataaccess.sql.DatabaseManager.getConnection;


public class SqlAuthDAO implements AuthDAO {

    public SqlAuthDAO(){}

    @Override
    public void clearAuth() throws DataAccessException {
        String sql = """
            DELETE FROM authentication""";
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.execute();
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error:internal server error",e);
        }
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        String sql = """
                SELECT username FROM authentication
                WHERE auth_token = ?
                """;
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,authToken);
                ps.execute();
                ResultSet rs = ps.getResultSet();
                if(!rs.next()){
                    throw new DataNotFoundException("Error: session not found");
                }
                return rs.getString("username");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal server error",e);
        }
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException{
        String sql = """
                INSERT INTO authentication (username,auth_token)
                VALUES (?,?)
                """;
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,authData.username());
                ps.setString(2,authData.authToken());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error:internal server error",e);
        }
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        String sql = """
                DELETE FROM authentication
                WHERE auth_token = ?
                """;
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,authToken);
                if(ps.executeUpdate()==0){
                    throw new DataNotFoundException("Error: session not found");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal server error",e);
        }
    }

    @Override
    public boolean isEmpty() throws DataAccessException{
        return DatabaseManager.isEmpty("authentication");
    }
}
