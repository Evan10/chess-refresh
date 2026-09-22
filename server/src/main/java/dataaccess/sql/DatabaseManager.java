package dataaccess.sql;

import dataaccess.DataAccessException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        String setupScript = loadSQLScriptFromResources("setup.sql");
        setupScript = setupScript.replaceAll("\\$\\{database-name}",databaseName);
        String[] commands = setupScript.split(";");

        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword)) {
            for (String c : commands){
                try(Statement statement = conn.createStatement()) {
                    statement.execute(c);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }

    public static String loadSQLScriptFromResources(String sqlName){
        String path = "sql/"+sqlName;
        try(InputStream fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)){
            if(fileStream == null){
                throw new FileNotFoundException();
            }
            return new String(fileStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch (IOException e){
            throw new RuntimeException("Unable to process file "+sqlName, e);
        }
    }


     /*package-private*/ static boolean isEmpty(String table) throws DataAccessException{
        String sql = """
            SELECT CASE
                WHEN EXISTS(SELECT 1 FROM %s) THEN 0
                ELSE 1
            END AS IsEmpty;""".formatted(table);
        try(Connection conn = getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.execute();
                ResultSet rs = ps.getResultSet();
                rs.next();
                return rs.getBoolean("IsEmpty");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error:internal server error",e);
        }
    }
}
