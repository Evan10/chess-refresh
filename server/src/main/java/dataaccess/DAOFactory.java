package dataaccess;

public class DAOFactory {

    boolean inMemory;
    public DAOFactory(boolean inMemory){
        this.inMemory = inMemory;
    }

    public UserDAO buildUserDAO(){
        return inMemory ? new MemoryUserDAO() : null;
    }

    public GameDAO buildGameDAO(){
        return inMemory ? new MemoryGameDAO() : null;
    }

    public AuthDAO buildAuthDAO(){
        return inMemory ? new MemoryAuthDAO() : null;
    }
}
