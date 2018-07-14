//package com.etherblood.twitch.chat.bot.persistence;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Arrays;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// *
// * @author Philipp
// */
//public class Repository<T, K> {
//
//    private final Connection psqlConnection;
//    private final EntityMapping<T> mapping;
//
//    public Repository(Connection psqlConnection, EntityMapping<T> mapping) {
//        this.psqlConnection = psqlConnection;
//        this.mapping = mapping;
//    }
//    
//    public K save(T entity) throws SQLException {
//        String[] columns = mapping.getColumns();
//        Function<T, Object[]> deconstructor = mapping.getDeconstructor();
//        Object[] values = deconstructor.apply(entity);
//        StringBuilder queryBuilder = new StringBuilder();
//        queryBuilder.append("insert into ");
//        queryBuilder.append(mapping.getTableName());
////        queryBuilder.append(Arrays.stream(columns).map(EntityColumn::getColumnName).collect(Collectors.joining(", ", " (", ") ")));
//        queryBuilder.append(Arrays.stream(columns).map(x -> "?").collect(Collectors.joining(",", " values (", ")")));
//        queryBuilder.append(" returning id;");
//        PreparedStatement query = psqlConnection.prepareStatement(queryBuilder.toString(), mapping.getColumns());
//        for (int i = 0; i < values.length; i++) {
//            query.setObject(i + 1, values[i]);
//        }
//        ResultSet result = query.executeQuery();
//        return result.unwrap(null);
//    }
//    
//    public T load(K id) throws SQLException {
//        String[] columns = mapping.getColumns();
//        StringBuilder queryBuilder = new StringBuilder();
//        queryBuilder.append("select * from ");
//        queryBuilder.append(mapping.getTableName());
////        queryBuilder.append(Arrays.stream(columns).map(EntityColumn::getColumnName).collect(Collectors.joining(", ", " (", ") ")));
//        queryBuilder.append(Arrays.stream(columns).map(x -> "?").collect(Collectors.joining(",", " values (", ");")));
//        PreparedStatement query = psqlConnection.prepareStatement(queryBuilder.toString(), mapping.getColumns());
//        for (int i = 0; i < values.length; i++) {
//            query.setObject(i + 1, values[i]);
//        }
//        query.execute();
//    }
//    
//}
