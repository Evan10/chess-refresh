package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO{
    @Override
    public void clearUsers() {

    }

    @Override
    public String addUser(UserData userData) throws InUseException {
        return "";
    }

    @Override
    public UserData getUser(String username) throws DataNotFoundException {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
