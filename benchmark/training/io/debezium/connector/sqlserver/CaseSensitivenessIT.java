/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.sqlserver;


import io.debezium.connector.sqlserver.util.TestHelper;
import io.debezium.doc.FixFor;
import io.debezium.embedded.AbstractConnectorTest;
import org.junit.Test;


/**
 * Integration test to verify behaviour of database with and without case sensitive names.
 *
 * @author Jiri Pechanec
 */
public class CaseSensitivenessIT extends AbstractConnectorTest {
    private SqlServerConnection connection;

    @Test
    @FixFor("DBZ-1051")
    public void caseInsensitiveDatabase() throws Exception {
        connection.execute("CREATE TABLE MyTableOne (Id int primary key, ColA varchar(30))", "INSERT INTO MyTableOne VALUES(1, 'a')");
        TestHelper.enableTableCdc(connection, "MyTableOne");
        testDatabase();
    }

    @Test
    @FixFor("DBZ-1051")
    public void caseSensitiveDatabase() throws Exception {
        connection.execute("ALTER DATABASE testDB COLLATE Latin1_General_BIN", "CREATE TABLE MyTableOne (Id int primary key, ColA varchar(30))", "INSERT INTO MyTableOne VALUES(1, 'a')");
        TestHelper.enableTableCdc(connection, "MyTableOne");
        testDatabase();
    }
}

