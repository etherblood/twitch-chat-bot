package com.etherblood.twitch.chat.bot.data.commands.alias;

import com.etherblood.twitch.chat.bot.data.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("insert into commandalias (alias, command_id) values (?, ?);");
        prepareStatement.setString(1, alias.alias);
        prepareStatement.setLong(2, alias.commandId);
        return prepareStatement.executeUpdate();
    }

    private int update(CommandAlias alias) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("update commandalias set alias=?, command_id=? where lower(alias)=lower(?);");
        prepareStatement.setString(1, alias.alias);
        prepareStatement.setLong(2, alias.commandId);
        prepareStatement.setString(3, alias.alias);
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

    public List<String> getCommands(int page, int pageSize) throws SQLException {
        List<String> result = new ArrayList<>();
        //sorted by use frequency
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select alias from commandalias, command where command_id=id order by (1.0 / (usecount + 1)) * (current_timestamp - lastmodified) asc offset ? limit ?;");
        prepareStatement.setInt(1, page * pageSize);
        prepareStatement.setInt(2, pageSize);
        ResultSet resultSet = prepareStatement.executeQuery();
        while (resultSet.next()) {
            result.add(resultSet.getString(1));
        }
        return result;
    }
}
