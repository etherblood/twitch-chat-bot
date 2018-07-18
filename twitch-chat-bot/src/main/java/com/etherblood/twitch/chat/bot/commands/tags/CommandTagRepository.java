package com.etherblood.twitch.chat.bot.commands.tags;

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
public class CommandTagRepository {

    private final Connection psqlConnection;

    public CommandTagRepository(Connection psqlConnection) {
        this.psqlConnection = psqlConnection;
    }

    public int save(CommandTag tag) throws SQLException {
        return insert(tag);
    }
    
    private int insert(CommandTag tag) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("insert into commandtag (tag, command_id) values (?, ?);");
        prepareStatement.setString(1, tag.tag);
        prepareStatement.setLong(2, tag.commandId);
        return prepareStatement.executeUpdate();
    }

    public int delete(long commandId, String tag) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("delete from commandtag where lower(tag)=lower(?) and command_id=?;");
        prepareStatement.setString(1, tag);
        prepareStatement.setLong(2, commandId);
        return prepareStatement.executeUpdate();
    }

    public List<String> getCommands(String tag) throws SQLException {
        List<String> result = new ArrayList<>();
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select alias from commandtag t, commandalias a where a.command_id=t.command_id and lower(t.tag)=lower(?) order by lower(alias) asc;");
        prepareStatement.setString(1, tag);
        ResultSet resultSet = prepareStatement.executeQuery();
        while (resultSet.next()) {
            result.add(resultSet.getString(1));
        }
        return result;
    }

    public List<String> getTags() throws SQLException {
        List<String> result = new ArrayList<>();
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select distinct tag from commandtag order by tag asc;");
        ResultSet resultSet = prepareStatement.executeQuery();
        while (resultSet.next()) {
            result.add(resultSet.getString(1));
        }
        return result;
    }
}
