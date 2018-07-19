package com.etherblood.twitch.chat.bot.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Philipp
 */
public class WhitelistRepository {

    private final Connection psqlConnection;

    public WhitelistRepository(Connection psqlConnection) {
        this.psqlConnection = psqlConnection;
    }

    public void setWhitelisted(String username, boolean value) throws SQLException {
        if (value) {
            PreparedStatement prepareStatement = psqlConnection.prepareStatement("insert into whitelist values (?);");
            prepareStatement.setString(1, username.toLowerCase());
            prepareStatement.executeUpdate();
        } else {
            PreparedStatement prepareStatement = psqlConnection.prepareStatement("delete from whitelist where sender=?;");
            prepareStatement.setString(1, username.toLowerCase());
            prepareStatement.executeUpdate();
        }
    }

    public boolean isWhitelisted(String username) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select count(*) from whitelist where sender=?;");
        prepareStatement.setString(1, username.toLowerCase());
        ResultSet result = prepareStatement.executeQuery();
        return result.next() && result.getInt(1) == 1;
    }
}
