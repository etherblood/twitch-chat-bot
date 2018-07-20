package com.etherblood.twitch.chat.bot.data.commands.alias;

import com.etherblood.twitch.chat.bot.data.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Philipp
 */
public class CommandAliasRepository {

    private final Connection psqlConnection;

    public CommandAliasRepository(Connection psqlConnection) {
        this.psqlConnection = psqlConnection;
    }

    public int save(CommandAlias alias) throws SQLException {
        if (load(alias.alias) == null) {
            return insert(alias);
        }
        return update(alias);
    }

    private int insert(CommandAlias alias) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("insert into commandalias (alias, command_id, author, usecount, lastused, lastmodified) values (?, ?, ?, ?, ?, ?);");
        prepareStatement.setString(1, alias.alias);
        prepareStatement.setLong(2, alias.commandId);
        prepareStatement.setString(3, alias.author);
        prepareStatement.setLong(4, alias.useCount);
        prepareStatement.setTimestamp(5, Util.toTimestamp(alias.lastUsed));
        prepareStatement.setTimestamp(6, Util.toTimestamp(alias.lastModified));
        return prepareStatement.executeUpdate();
    }

    private int update(CommandAlias alias) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("update commandalias set alias=?, command_id=?, author=?, usecount=?, lastused=?, lastmodified=? where lower(alias)=lower(?);");
        prepareStatement.setString(1, alias.alias);
        prepareStatement.setLong(2, alias.commandId);
        prepareStatement.setString(3, alias.author);
        prepareStatement.setLong(4, alias.useCount);
        prepareStatement.setTimestamp(5, Util.toTimestamp(alias.lastUsed));
        prepareStatement.setTimestamp(6, Util.toTimestamp(alias.lastModified));
        prepareStatement.setString(7, alias.alias);
        return prepareStatement.executeUpdate();
    }

    public int delete(String alias) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("delete from commandalias where lower(alias)=lower(?);");
        prepareStatement.setString(1, alias);
        return prepareStatement.executeUpdate();
    }

    public CommandAlias load(String alias) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select * from commandalias where lower(alias)=lower(?);");
        prepareStatement.setString(1, alias);
        ResultSet commandsResult = prepareStatement.executeQuery();
        if (commandsResult.next()) {
            return new CommandAlias(
                    commandsResult.getString("alias"),
                    commandsResult.getLong("command_id"),
                    commandsResult.getString("author"),
                    commandsResult.getLong("usecount"),
                    Util.toInstant(commandsResult.getTimestamp("lastused")),
                    Util.toInstant(commandsResult.getTimestamp("lastmodified")));
        }
        return null;
    }
}
