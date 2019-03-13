package com.mongodb.hadoop.hive;


import com.mongodb.client.MongoCollection;
import java.sql.SQLException;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;


public class HiveQueryTest extends HiveTest {
    private static MongoCollection<Document> coll;

    @Test
    public void testQueryPushdown() throws SQLException {
        execute(("CREATE EXTERNAL TABLE querytest (id STRING, i INT, j INT) " + (((("STORED BY \"com.mongodb.hadoop.hive.MongoStorageHandler\" " + "WITH SERDEPROPERTIES(\"mongo.columns.mapping\"=") + "\'{\"id\":\"_id\"}\') ") + "TBLPROPERTIES(\"mongo.uri\"=") + "\"mongodb://localhost:27017/mongo_hadoop.hive_query\")")));
        Results results = query("SELECT * FROM querytest WHERE i > 20");
        Assert.assertEquals(979, results.size());
    }

    @Test
    public void testQueryPushdownWithQueryTable() throws SQLException {
        execute(("CREATE EXTERNAL TABLE querytest (id STRING, i INT, j INT) " + ((((("STORED BY \"com.mongodb.hadoop.hive.MongoStorageHandler\" " + "WITH SERDEPROPERTIES(\"mongo.columns.mapping\"=") + "\'{\"id\":\"_id\"}\') ") + "TBLPROPERTIES(\"mongo.uri\"=") + "\"mongodb://localhost:27017/mongo_hadoop.hive_query\",") + "\"mongo.input.query\"=\'{\"j\":0}\')")));
        Results results = query("SELECT * FROM querytest WHERE i > 20");
        Assert.assertEquals(195, results.size());
        results = query("SELECT * from querytest WHERE j > 2");
        Assert.assertEquals(0, results.size());
    }
}

