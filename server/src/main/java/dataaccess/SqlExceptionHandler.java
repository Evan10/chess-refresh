package dataaccess;

import java.sql.SQLException;

public class SqlExceptionHandler {

    public static void translateException(Exception ex) throws DataAccessException{
        if(ex instanceof SQLException sqlex){
            if(sqlex.getSQLState().startsWith("23")) {
                throw new InUseException("Error: name in use");
            }
        }
        throw new DataAccessException("Error: internal server error",ex);
    }
}
