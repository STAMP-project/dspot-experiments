package com.mongodb.hadoop.hive;


import java.sql.SQLException;
import org.junit.Test;


public class TestBsonToHive extends HiveTest {
    @Test
    public void testSameDataMongoAndBSONHiveTables() throws SQLException {
        testTransfer(HiveTest.BSON_BACKED_TABLE, HiveTest.MONGO_BACKED_TABLE);
    }

    @Test
    public void testSameDataHDFSAndBSONHiveTables() throws SQLException {
        testTransfer(HiveTest.BSON_BACKED_TABLE, HiveTest.MONGO_BACKED_TABLE);
    }
}

