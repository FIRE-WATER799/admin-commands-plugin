package salatosik.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabasePlayersSystem {
    private static Connection connection = null;
    private static boolean databaseStatus = false;

    public static boolean init(String database) {
        if(connection != null) return false;
        else if(databaseStatus) return false;

        try {
            
            File file = new File(database);

            if(!file.exists()) {
                file.createNewFile();

                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + database);

                try(Statement statement = connection.createStatement()) {
                    statement.execute("CREATE TABLE player (id TEXT PRIMARY KEY NOT NULL, " +
                        "bantime INTEGER NOT NULL, mutetime INTEGER NOT NULL);");

                    databaseStatus = true;
                    return true;
                }
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + database);
            databaseStatus = true;
            return true;

        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
            return false;

        } catch(IOException ioException) {
            ioException.printStackTrace();
            return false;

        } catch(ClassNotFoundException exception) {
            exception.printStackTrace();
            return false;
        } 
    }

    public static boolean getDatabaseStatus() { return databaseStatus; }

    public static long getByPlayerId(String playerId, String key) {
        try {
            
            try(Statement statement = connection.createStatement()) {
                
                ResultSet resultSet = statement.executeQuery("SELECT * FROM player WHERE id = '[playerid]';"
                    .replace("[playerid]", playerId));
                
                if(resultSet.next()) {
                    return resultSet.getLong(key);
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return 0;
    }

    public static synchronized boolean createNewPlayer(String playerId, long banTime, long muteTime) {
        try {

            try(Statement statement = connection.createStatement()) {
                String sql = "INSERT INTO player (id, bantime, mutetime)\n" +
                    "VALUES('[playerid]', [bantime], [mutetime]);".replace("[playerid]", playerId)
                    .replace("[bantime]", Long.toString(banTime))
                    .replace("[mutetime]", Long.toString(muteTime));

                statement.execute(sql);
                return true;
            }

        } catch(SQLException sqlException) {
            return false;
        }
    }

    public static synchronized boolean replaceWherePlayerId(String playerid, String key, long keyValue) {
        try {

            try(Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT * FROM player");

                while (rs.next()) {
                    if(rs.getString("id").equals(playerid)) {
                        String sql = "UPDATE player SET [key] = [keyvalue] WHERE id = '[playerid]';"
                            .replace("[key]", key)
                            .replace("[keyvalue]", Long.toString(keyValue))
                            .replace("[playerid]", playerid);

                        statement.execute(sql);
                        return true;
                    }
                }
            }

        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }

        return false;
    }

    public static boolean searchId(String playerId) {
        try {

            try(Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT * FROM player");

                while (rs.next()) {
                    if(rs.getString("id").equals(playerId)) {
                        return true;
                    }
                }
            }

        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }

        return false;
    }

    public static List<String> getListId() {
        List<String> playerList = new ArrayList<>();

        try {

            try(Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT * FROM player");

                while(rs.next()) {
                    playerList.add(rs.getString("id"));
                }

                return playerList;
            }

        } catch(SQLException sqlException) {
            return null;
        }
    }
}
