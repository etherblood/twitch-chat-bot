package com.etherblood.twitch.chat.bot.data.commands;

import com.etherblood.twitch.chat.bot.data.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        Command loaded = load(command.alias);
        if (loaded != null) {
            command.id = loaded.id;
            return update(command);
        }
        return insert(command);
    }

    private long insert(Command command) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("insert into command (code, author, usecount, lastused, lastmodified, alias) values (?, ?, ?, ?, ?, ?) returning id;");
        prepareStatement.setString(1, command.code);
        prepareStatement.setString(2, command.author);
        prepareStatement.setLong(3, command.useCount);
        prepareStatement.setTimestamp(4, Util.toTimestamp(command.lastUsed));
        prepareStatement.setTimestamp(5, Util.toTimestamp(command.lastModified));
        prepareStatement.setString(6, command.alias);
        ResultSet result = prepareStatement.executeQuery();
        result.next();
        return result.getLong(1);
    }

    private long update(Command command) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("update command set code=?, author=?, usecount=?, lastused=?, lastmodified=?, alias=? where id=? returning id;");
        prepareStatement.setString(1, command.code);
        prepareStatement.setString(2, command.author);
        prepareStatement.setLong(3, command.useCount);
        prepareStatement.setTimestamp(4, Util.toTimestamp(command.lastUsed));
        prepareStatement.setTimestamp(5, Util.toTimestamp(command.lastModified));
        prepareStatement.setString(6, command.alias);
        prepareStatement.setLong(7, command.id);
        ResultSet result = prepareStatement.executeQuery();
        result.next();
        return result.getLong(1);
    }

    public int delete(String alias) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("delete from command where lower(alias)=lower(?);");
        prepareStatement.setString(1, alias);
        return prepareStatement.executeUpdate();
    }

    public Command load(String alias) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select * from command where lower(alias)=lower(?);");
        prepareStatement.setString(1, alias);
        ResultSet commandsResult = prepareStatement.executeQuery();
        if (commandsResult.next()) {
            long id = commandsResult.getLong("id");
            String code = commandsResult.getString("code");
            String author = commandsResult.getString("author");
            long useCount = commandsResult.getLong("usecount");
            Instant lastUsed = Util.toInstant(commandsResult.getTimestamp("lastused"));
            Instant lastModified = Util.toInstant(commandsResult.getTimestamp("lastmodified"));
            String commandAlias = commandsResult.getString("alias");
            return new Command(id, commandAlias, code, author, useCount, lastUsed, lastModified);
        }
        return null;
    }

}
