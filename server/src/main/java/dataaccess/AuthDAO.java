package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {

    void clearAuth() throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;
    void addAuth(AuthData authData) throws DataAccessException;
    void removeAuth(String authToken) throws DataAccessException;


    boolean isEmpty() throws DataAccessException;
}
