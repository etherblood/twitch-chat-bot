package com.etherblood.twitch.chat.bot.persistence;

import java.util.function.Function;

/**
 *
 * @author Philipp
 */
public class EntityMapping<T> {

    private final String tableName;
    private final String[] columns;
    private final Function<Object[], T> constructor;
    private final Function<T, Object[]> deconstructor;

    public EntityMapping(String tableName, String[] columns, Function<Object[], T> constructor, Function<T, Object[]> deconstructor) {
        this.tableName = tableName;
        this.columns = columns;
        this.constructor = constructor;
        this.deconstructor = deconstructor;
    }

    public String getTableName() {
        return tableName;
    }

    public String[] getColumns() {
        return columns;
    }

    public Function<Object[], T> getConstructor() {
        return constructor;
    }

    public Function<T, Object[]> getDeconstructor() {
        return deconstructor;
    }
}
