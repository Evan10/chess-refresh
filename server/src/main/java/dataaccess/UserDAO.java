package dataaccess;

import model.UserData;

public interface UserDAO {

    void clearUsers();

    String addUser(UserData userData) throws InUseException;
    UserData getUser(String username) throws DataNotFoundException;
    boolean usernameInUse(String username);

    boolean isEmpty();
}
