package dataaccess;

import model.UserData;

import java.sql.*;

import static dataaccess.DatabaseManager.getConnection;

public class SqlUserDAO implements UserDAO{
    @Override
    public void clearUsers() {
        String sql = """
            DELETE FROM users""";
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.execute();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String addUser(UserData userData) throws InUseException {
        String sql = """
            INSERT INTO users(username, password_h,email)
            VALUES (?,?,?)""";
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,userData.username());
                ps.setString(2,userData.password());
                ps.setString(3,userData.email());
                ps.execute();
            }
        } catch (Exception e) {
            SqlExceptionHandler.translateException(e);
        }
        return userData.username();
    }

    @Override
    public UserData getUser(String username) throws DataNotFoundException {
        String sql = """
                SELECT username, password_h, email FROM users
                WHERE username = ?
                """;
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,username);
                if(!ps.execute()){
                    throw new DataNotFoundException("Error: user with username not found");
                }
                ResultSet rs = ps.getResultSet();
                rs.next();
                String usernm = rs.getString("username");
                String psh = rs.getString("password_h");
                String email = rs.getString("email");
                return new UserData(usernm,psh,email);
            }
        }catch (DataNotFoundException e){
            throw(e);
        }
        catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        String sql = """
            SELECT CASE
                WHEN EXISTS(SELECT 1 FROM users) THEN 0
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
