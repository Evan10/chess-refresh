package dataaccess;

import model.UserData;

public interface UserDAO {

    void clearUsers() throws DataAccessException;

    String addUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    boolean isEmpty() throws DataAccessException;
}
