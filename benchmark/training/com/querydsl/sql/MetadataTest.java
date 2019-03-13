package com.querydsl.sql;


import java.sql.SQLException;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class MetadataTest {
    // CUBRID
    // Apache Derby
    // H2
    // HSQL Database Engine
    // MySQL
    // Oracle
    // PostgreSQL
    // SQLite
    @Test
    public void test() throws ClassNotFoundException, SQLException {
        Connections.initCubrid();
        printMetadata();
        Connections.initDerby();
        printMetadata();
        Connections.initH2();
        printMetadata();
        Connections.initHSQL();
        printMetadata();
        Connections.initMySQL();
        printMetadata();
        Connections.initOracle();
        printMetadata();
        Connections.initPostgreSQL();
        printMetadata();
        Connections.initSQLite();
        printMetadata();
        // Connections.initSQLServer()
        // printMetadata();
    }
}

