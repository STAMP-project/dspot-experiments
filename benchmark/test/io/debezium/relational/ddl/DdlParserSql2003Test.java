/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.relational.ddl;


import io.debezium.relational.Table;
import io.debezium.relational.TableId;
import io.debezium.relational.Tables;
import java.sql.Types;
import org.junit.Test;


public class DdlParserSql2003Test {
    private LegacyDdlParser parser;

    private Tables tables;

    @Test
    public void shouldParseMultipleStatements() {
        String ddl = (((((((((("CREATE TABLE foo ( " + (System.lineSeparator())) + " c1 INTEGER NOT NULL, ") + (System.lineSeparator())) + " c2 VARCHAR(22) ") + (System.lineSeparator())) + "); ") + (System.lineSeparator())) + "-- This is a comment") + (System.lineSeparator())) + "DROP TABLE foo;") + (System.lineSeparator());
        parser.parse(ddl, tables);
        assertThat(tables.size()).isEqualTo(0);// table created and dropped

    }

    @Test
    public void shouldParseCreateTableStatementWithSingleGeneratedAndPrimaryKeyColumn() {
        String ddl = (((((("CREATE TABLE foo ( " + (System.lineSeparator())) + " c1 INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY, ") + (System.lineSeparator())) + " c2 VARCHAR(22) ") + (System.lineSeparator())) + "); ") + (System.lineSeparator());
        parser.parse(ddl, tables);
        assertThat(tables.size()).isEqualTo(1);
        Table foo = tables.forTable(new TableId(null, null, "foo"));
        assertThat(foo).isNotNull();
        assertThat(foo.retrieveColumnNames()).containsExactly("c1", "c2");
        assertThat(foo.primaryKeyColumnNames()).containsExactly("c1");
        assertColumn(foo, "c1", "INTEGER", Types.INTEGER, (-1), (-1), false, true, true);
        assertColumn(foo, "c2", "VARCHAR", Types.VARCHAR, 22, (-1), true, false, false);
    }

    @Test
    public void shouldParseCreateTableStatementWithSingleGeneratedColumnAsPrimaryKey() {
        String ddl = (((((((("CREATE TABLE my.foo ( " + (System.lineSeparator())) + " c1 INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL, ") + (System.lineSeparator())) + " c2 VARCHAR(22), ") + (System.lineSeparator())) + " PRIMARY KEY (c1)") + (System.lineSeparator())) + "); ") + (System.lineSeparator());
        parser.parse(ddl, tables);
        assertThat(tables.size()).isEqualTo(1);
        Table foo = tables.forTable(new TableId("my", null, "foo"));
        assertThat(foo).isNotNull();
        assertThat(foo.retrieveColumnNames()).containsExactly("c1", "c2");
        assertThat(foo.primaryKeyColumnNames()).containsExactly("c1");
        assertColumn(foo, "c1", "INTEGER", Types.INTEGER, (-1), (-1), false, true, true);
        assertColumn(foo, "c2", "VARCHAR", Types.VARCHAR, 22, (-1), true, false, false);
        parser.parse("DROP TABLE my.foo", tables);
        assertThat(tables.size()).isEqualTo(0);
    }
}

