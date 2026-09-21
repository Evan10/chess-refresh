package dataaccess;

import dataaccess.sql.SqlAuthDAO;
import dataaccess.sql.SqlGameDAO;
import dataaccess.sql.SqlUserDAO;

public class DAOFactory {

    private final DAOType type;

    public DAOFactory(DAOType type) {
        this.type = type;
    }

    public UserDAO buildUserDAO() {
        return type == DAOType.Memory ? new MemoryUserDAO() : new SqlUserDAO();
    }

    public GameDAO buildGameDAO() {
        return type == DAOType.Memory ? new MemoryGameDAO() : new SqlGameDAO();
    }

    public AuthDAO buildAuthDAO() {
        return type == DAOType.Memory ? new MemoryAuthDAO() : new SqlAuthDAO();
    }
}
