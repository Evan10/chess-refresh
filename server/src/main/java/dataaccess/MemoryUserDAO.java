package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{

    private final Map<String, UserData> users;
    public MemoryUserDAO(){
        users = new HashMap<>();
    }

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public String addUser(UserData userData) throws InUseException {
        if(users.containsKey(userData.username())){
            throw new InUseException("Username in use");
        }
        users.put(userData.username(),userData);
        return userData.username();
    }

    @Override
    public UserData getUser(String username) throws DataNotFoundException {
        if(!users.containsKey(username)){
            throw new DataNotFoundException("User not found");
        }
        return users.get(username);
    }

    @Override
    public boolean isEmpty() {
        return users.isEmpty();
    }
}
