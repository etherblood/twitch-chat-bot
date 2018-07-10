package com.etherblood.twitch.chat.bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class CommandRepository {

    private final Connection psqlConnection;

    public CommandRepository(Connection psqlConnection) {
        this.psqlConnection = psqlConnection;
    }

    public int save(Command command) throws SQLException {
        if (load(command.id) == null) {
            return insert(command);
        }
        return update(command);
    }
    
    private int insert(Command command) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("insert into command (id, code, author, usecount, lastused, lastmodified) values (?, ?, ?, ?, ?, ?);");
        prepareStatement.setString(1, command.id);
        prepareStatement.setString(2, command.code);
        prepareStatement.setString(3, command.author);
        prepareStatement.setLong(4, command.useCount);
        prepareStatement.setTimestamp(5, toTimestamp(command.lastUsed));
        prepareStatement.setTimestamp(6, toTimestamp(command.lastModified));
        return prepareStatement.executeUpdate();
    }
    
    private static Timestamp toTimestamp(Instant instant) {
        if(instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }
    
    private static Instant toInstant(Timestamp timestamp) {
        if(timestamp == null) {
            return null;
        }
        return timestamp.toInstant();
    }
    
    private int update(Command command) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("update command set code=?, author=?, usecount=?, lastused=?, lastmodified=? where lower(id)=lower(?);");
        prepareStatement.setString(1, command.code);
        prepareStatement.setString(2, command.author);
        prepareStatement.setLong(3, command.useCount);
        prepareStatement.setTimestamp(4, toTimestamp(command.lastUsed));
        prepareStatement.setTimestamp(5, toTimestamp(command.lastModified));
        prepareStatement.setString(6, command.id);
        return prepareStatement.executeUpdate();
    }

    public int delete(String commandId) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("delete from command where lower(id)=lower(?);");
        prepareStatement.setString(1, commandId);
        return prepareStatement.executeUpdate();
    }

    public Command load(String commandId) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select * from command where lower(id)=lower(?);");
        prepareStatement.setString(1, commandId);
        ResultSet commandsResult = prepareStatement.executeQuery();
        if (commandsResult.next()) {
            String code = commandsResult.getString("code");
            String author = commandsResult.getString("author");
            long useCount = commandsResult.getLong("usecount");
            Instant lastUsed = toInstant(commandsResult.getTimestamp("lastused"));
            Instant lastModified = toInstant(commandsResult.getTimestamp("lastmodified"));
            return new Command(commandId, code, author, useCount, lastUsed, lastModified);
        }
        return null;
    }

    public List<String> getCommands(int page, int pageSize) throws SQLException {
        List<String> result = new ArrayList<>();
        //sorted by use frequency
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select id from command order by (1.0 / (usecount + 1)) * (current_timestamp - lastmodified) asc offset ? limit ?;");
        prepareStatement.setInt(1, page * pageSize);
        prepareStatement.setInt(2, pageSize);
        ResultSet resultSet = prepareStatement.executeQuery();
        while (resultSet.next()) {
            result.add(resultSet.getString(1));
        }
        return result;
    }
}
