package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{

    private final Map<String, AuthData> authMap;

    public MemoryAuthDAO(){
        authMap = new HashMap<>();
        authMap.put("Hello", new AuthData("Hello", "bob"));
    }

    @Override
    public void clearAuth() {
        authMap.clear();
    }

    @Override
    public String getUsername(String authToken) throws DataNotFoundException{
        if (!authMap.containsKey(authToken)){
            throw new DataNotFoundException("auth token not found");
        }
        return authMap.get(authToken).username();
    }

    @Override
    public void addAuth(AuthData authData) {
        authMap.put(authData.authToken(), authData);
    }

    @Override
    public void removeAuth(String authToken) throws DataNotFoundException{
        if (!authMap.containsKey(authToken)){
            throw new DataNotFoundException("auth token not found");
        }
        authMap.remove(authToken);
    }

    @Override
    public boolean isEmpty() {
        return authMap.isEmpty();
    }

}
