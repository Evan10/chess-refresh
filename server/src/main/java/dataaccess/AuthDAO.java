package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {

    void clearAuth();

    String getUsername(String authToken) throws DataNotFoundException;
    void addAuth(AuthData authData);
    void removeAuth(String authToken) throws DataNotFoundException;


    boolean isEmpty();
}
