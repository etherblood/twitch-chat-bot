package com.etherblood.twitch.chat.bot.clips;

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
public class ClipRepository {

    private final Connection psqlConnection;

    public ClipRepository(Connection psqlConnection) {
        this.psqlConnection = psqlConnection;
    }

    public int save(Clip clip) throws SQLException {
        if (load(clip.id) == null) {
            return insert(clip);
        }
        return update(clip);
    }
    
    private int insert(Clip clip) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("insert into clip (id, code, author, usecount, lastused, lastmodified) values (?, ?, ?, ?, ?, ?);");
        prepareStatement.setString(1, clip.id);
        prepareStatement.setString(2, clip.code);
        prepareStatement.setString(3, clip.author);
        prepareStatement.setLong(4, clip.useCount);
        prepareStatement.setTimestamp(5, toTimestamp(clip.lastUsed));
        prepareStatement.setTimestamp(6, toTimestamp(clip.lastModified));
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
    
    private int update(Clip clip) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("update clip set code=?, author=?, usecount=?, lastused=?, lastmodified=? where lower(id)=lower(?);");
        prepareStatement.setString(1, clip.code);
        prepareStatement.setString(2, clip.author);
        prepareStatement.setLong(3, clip.useCount);
        prepareStatement.setTimestamp(4, toTimestamp(clip.lastUsed));
        prepareStatement.setTimestamp(5, toTimestamp(clip.lastModified));
        prepareStatement.setString(6, clip.id);
        return prepareStatement.executeUpdate();
    }

    public int delete(String clipId) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("delete from clip where lower(id)=lower(?);");
        prepareStatement.setString(1, clipId);
        return prepareStatement.executeUpdate();
    }

    public Clip load(String clipId) throws SQLException {
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select * from clip where lower(id)=lower(?);");
        prepareStatement.setString(1, clipId);
        ResultSet clipsResult = prepareStatement.executeQuery();
        if (clipsResult.next()) {
            String code = clipsResult.getString("code");
            String author = clipsResult.getString("author");
            long useCount = clipsResult.getLong("usecount");
            Instant lastUsed = toInstant(clipsResult.getTimestamp("lastused"));
            Instant lastModified = toInstant(clipsResult.getTimestamp("lastmodified"));
            return new Clip(clipId, code, author, useCount, lastUsed, lastModified);
        }
        return null;
    }

    public List<String> getClips(int page, int pageSize) throws SQLException {
        List<String> result = new ArrayList<>();
        //sorted by use frequency
        PreparedStatement prepareStatement = psqlConnection.prepareStatement("select id from clip order by (1.0 / (usecount + 1)) * (current_timestamp - lastmodified) asc offset ? limit ?;");
        prepareStatement.setInt(1, page * pageSize);
        prepareStatement.setInt(2, pageSize);
        ResultSet resultSet = prepareStatement.executeQuery();
        while (resultSet.next()) {
            result.add(resultSet.getString(1));
        }
        return result;
    }
}
