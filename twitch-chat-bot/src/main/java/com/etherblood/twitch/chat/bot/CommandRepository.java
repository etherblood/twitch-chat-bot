package com.etherblood.twitch.chat.bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class CommandRepository implements AutoCloseable {

    private final Connection psqlConnection;

    public CommandRepository(String url, String user, String password) throws SQLException {
        psqlConnection = DriverManager.getConnection(url, user, password);
    }

    public int save(String command, String text) throws SQLException {
        String oldValue = load(command);
        PreparedStatement prepareStatement;
        if (oldValue == null) {
            prepareStatement = psqlConnection.prepareStatement("insert into command values (?, ?);");
            prepareStatement.setString(1, command);
            prepareStatement.setString(2, text);
        } else if (!oldValue.equals(text)) {
            prepareStatement = psqlConnection.prepareStatement("update command set code=? where id=?;");
            prepareStatement.setString(1, text);
            prepareStatement.setString(2, command);
        } else {
            return 0;
        }
        return prepareStatement.executeUpdate();
    }

    public int delete(String command) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("delete from command where id=?;");
        prepareStatement.setString(1, command);
        return prepareStatement.executeUpdate();
    }

    public String load(String command) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select code from command where id=?;");
        prepareStatement.setString(1, command);
        ResultSet commandsResult = prepareStatement.executeQuery();
        if (commandsResult.next()) {
            return commandsResult.getString(1);
        }
        return null;
    }

    public List<String> getCommands(int page, int pageSize) throws SQLException {
        List<String> result = new ArrayList<>();
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select id from command order by id asc offset ? limit ?;");
        prepareStatement.setInt(1, page * pageSize);
        prepareStatement.setInt(2, pageSize);
        ResultSet resultSet = prepareStatement.executeQuery();
        while (resultSet.next()) {
            result.add(resultSet.getString(1));
        }
        return result;
    }

    @Override
    public void close() throws SQLException {
        psqlConnection.close();
    }
}
