package com.mongodb.hadoop.hive;


import com.mongodb.client.MongoCollection;
import java.sql.SQLException;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;


public class TablePropertiesTest extends HiveTest {
    private MongoCollection<Document> collection;

    @Test
    public void testPropertiesFile() throws SQLException {
        // Create the table.
        execute(((("CREATE TABLE props_file_test" + (((" (id STRING, i INT)" + " STORED BY 'com.mongodb.hadoop.hive.MongoStorageHandler'") + " WITH SERDEPROPERTIES(\'mongo.columns.mapping\'=\'{\"id\":\"_id\"}\')") + " TBLPROPERTIES('mongo.properties.path'='")) + (getPath("hivetable.properties"))) + "')"));
        // Read and write some data through the table.
        Results results = query("SELECT i FROM props_file_test WHERE i >= 20");
        Assert.assertEquals(490, results.size());
        execute("INSERT INTO props_file_test VALUES ('55d5005b6e32ab5664606195', 42)");
        Assert.assertEquals(2, collection.count(new Document("i", 42)));
    }
}

