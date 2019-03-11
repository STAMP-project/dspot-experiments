/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import DatabaseHistory.SKIP_UNPARSEABLE_DDL_STATEMENTS;
import Testing.Files;
import io.debezium.text.ParsingException;
import java.nio.file.Path;
import org.junit.Test;

import static MySqlSystemVariables.CHARSET_NAME_SERVER;


/**
 *
 *
 * @author Randall Hauch
 */
public class MySqlSchemaTest {
    private static final Path TEST_FILE_PATH = Files.createTestingPath("dbHistory.log");

    private static final String SERVER_NAME = "testServer";

    private Configurator build;

    private MySqlSchema mysql;

    private SourceInfo source;

    @Test
    public void shouldApplyDdlStatementsAndRecover() throws InterruptedException {
        mysql = build.storeDatabaseHistoryInFile(MySqlSchemaTest.TEST_FILE_PATH).serverName(MySqlSchemaTest.SERVER_NAME).createSchemas();
        mysql.start();
        // Testing.Print.enable();
        // Set up the server ...
        source.setBinlogStartPoint("binlog-001", 400);
        mysql.applyDdl(source, "db1", (("SET " + (CHARSET_NAME_SERVER)) + "=utf8mb4"), this::printStatements);
        mysql.applyDdl(source, "db1", readFile("ddl/mysql-products.ddl"), this::printStatements);
        // Check that we have tables ...
        assertTableIncluded("connector_test.products");
        assertTableIncluded("connector_test.products_on_hand");
        assertTableIncluded("connector_test.customers");
        assertTableIncluded("connector_test.orders");
        assertHistoryRecorded();
    }

    @Test
    public void shouldIgnoreUnparseableDdlAndRecover() throws InterruptedException {
        mysql = build.with(SKIP_UNPARSEABLE_DDL_STATEMENTS, true).storeDatabaseHistoryInFile(MySqlSchemaTest.TEST_FILE_PATH).serverName(MySqlSchemaTest.SERVER_NAME).createSchemas();
        mysql.start();
        // Testing.Print.enable();
        // Set up the server ...
        source.setBinlogStartPoint("binlog-001", 400);
        mysql.applyDdl(source, "db1", (("SET " + (CHARSET_NAME_SERVER)) + "=utf8mb4"), this::printStatements);
        mysql.applyDdl(source, "db1", ("xxxCREATE TABLE mytable\n" + (readFile("ddl/mysql-products.ddl"))), this::printStatements);
        mysql.applyDdl(source, "db1", readFile("ddl/mysql-products.ddl"), this::printStatements);
        // Check that we have tables ...
        assertTableIncluded("connector_test.products");
        assertTableIncluded("connector_test.products_on_hand");
        assertTableIncluded("connector_test.customers");
        assertTableIncluded("connector_test.orders");
        assertHistoryRecorded();
    }

    @Test(expected = ParsingException.class)
    public void shouldFailOnUnparseableDdl() throws InterruptedException {
        mysql = build.storeDatabaseHistoryInFile(MySqlSchemaTest.TEST_FILE_PATH).serverName(MySqlSchemaTest.SERVER_NAME).createSchemas();
        mysql.start();
        // Testing.Print.enable();
        // Set up the server ...
        source.setBinlogStartPoint("binlog-001", 400);
        mysql.applyDdl(source, "db1", (("SET " + (CHARSET_NAME_SERVER)) + "=utf8mb4"), this::printStatements);
        mysql.applyDdl(source, "db1", ("xxxCREATE TABLE mytable\n" + (readFile("ddl/mysql-products.ddl"))), this::printStatements);
    }

    @Test
    public void shouldLoadSystemAndNonSystemTablesAndConsumeOnlyFilteredDatabases() throws InterruptedException {
        mysql = build.storeDatabaseHistoryInFile(MySqlSchemaTest.TEST_FILE_PATH).serverName(MySqlSchemaTest.SERVER_NAME).includeDatabases("connector_test").excludeBuiltInTables().createSchemas();
        mysql.start();
        source.setBinlogStartPoint("binlog-001", 400);
        mysql.applyDdl(source, "mysql", (("SET " + (CHARSET_NAME_SERVER)) + "=utf8mb4"), this::printStatements);
        mysql.applyDdl(source, "mysql", readFile("ddl/mysql-test-init-5.7.ddl"), this::printStatements);
        source.setBinlogStartPoint("binlog-001", 1000);
        mysql.applyDdl(source, "db1", readFile("ddl/mysql-products.ddl"), this::printStatements);
        // Check that we have tables ...
        assertTableIncluded("connector_test.products");
        assertTableIncluded("connector_test.products_on_hand");
        assertTableIncluded("connector_test.customers");
        assertTableIncluded("connector_test.orders");
        assertTableExcluded("mysql.columns_priv");
        assertNoTablesExistForDatabase("mysql");
        assertHistoryRecorded();
    }

    @Test
    public void shouldLoadSystemAndNonSystemTablesAndConsumeAllDatabases() throws InterruptedException {
        mysql = build.storeDatabaseHistoryInFile(MySqlSchemaTest.TEST_FILE_PATH).serverName(MySqlSchemaTest.SERVER_NAME).includeDatabases("connector_test,mysql").includeBuiltInTables().createSchemas();
        mysql.start();
        source.setBinlogStartPoint("binlog-001", 400);
        mysql.applyDdl(source, "mysql", (("SET " + (CHARSET_NAME_SERVER)) + "=utf8mb4"), this::printStatements);
        mysql.applyDdl(source, "mysql", readFile("ddl/mysql-test-init-5.7.ddl"), this::printStatements);
        source.setBinlogStartPoint("binlog-001", 1000);
        mysql.applyDdl(source, "db1", readFile("ddl/mysql-products.ddl"), this::printStatements);
        // Check that we have tables ...
        assertTableIncluded("connector_test.products");
        assertTableIncluded("connector_test.products_on_hand");
        assertTableIncluded("connector_test.customers");
        assertTableIncluded("connector_test.orders");
        assertTableIncluded("mysql.columns_priv");
        assertTablesExistForDatabase("mysql");
        assertHistoryRecorded();
    }
}

