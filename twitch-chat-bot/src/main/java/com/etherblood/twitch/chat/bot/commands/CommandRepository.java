package com.etherblood.twitch.chat.bot.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class CommandRepository {

    private final Connection psqlConnection;

    public CommandRepository(Connection psqlConnection) {
        this.psqlConnection = psqlConnection;
    }

    public long save(Command command) throws SQLException {
        if (command.id == null || load(command.id) == null) {
            return insert(command);
        }
        return update(command);
    }

    private long insert(Command command) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("insert into command (code, author, usecount, lastused, lastmodified) values (?, ?, ?, ?, ?) returning id;");
        prepareStatement.setString(1, command.code);
        prepareStatement.setString(2, command.author);
        prepareStatement.setLong(3, command.useCount);
        prepareStatement.setTimestamp(4, toTimestamp(command.lastUsed));
        prepareStatement.setTimestamp(5, toTimestamp(command.lastModified));
        ResultSet result = prepareStatement.executeQuery();
        result.next();
        return result.getLong(1);
    }

    private static Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }

    private static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toInstant();
    }

    private long update(Command command) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("update command set code=?, author=?, usecount=?, lastused=?, lastmodified=? where id=? returning id;");
        prepareStatement.setString(1, command.code);
        prepareStatement.setString(2, command.author);
        prepareStatement.setLong(3, command.useCount);
        prepareStatement.setTimestamp(4, toTimestamp(command.lastUsed));
        prepareStatement.setTimestamp(5, toTimestamp(command.lastModified));
        prepareStatement.setLong(6, command.id);
        ResultSet result = prepareStatement.executeQuery();
        result.next();
        return result.getLong(1);
    }

    public int delete(long commandId) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("delete from command where id=?;");
        prepareStatement.setLong(1, commandId);
        return prepareStatement.executeUpdate();
    }

    public Command load(long commandId) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select * from command where id=?;");
        prepareStatement.setLong(1, commandId);
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

//    public List<String> getCommands(int page, int pageSize) throws SQLException {
//        List<String> result = new ArrayList<>();
//        //sorted by use frequency
//        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select id from command order by (1.0 / (usecount + 1)) * (current_timestamp - lastmodified) asc offset ? limit ?;");
//        prepareStatement.setInt(1, page * pageSize);
//        prepareStatement.setInt(2, pageSize);
//        ResultSet resultSet = prepareStatement.executeQuery();
//        while (resultSet.next()) {
//            result.add(resultSet.getString(1));
//        }
//        return result;
//    }
}
