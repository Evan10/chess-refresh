package dataaccess;

import java.sql.SQLDataException;

public class SqlExceptionHandler {

    public static void translateException(Exception ex) throws InUseException{
        if(ex instanceof SQLDataException sqlex){
            if(sqlex.getSQLState().startsWith("23")) {
                throw new InUseException("Error: name in use");
            }
        }
        throw new RuntimeException("Error: internal server error");
    }
}
