package com.mongodb.hadoop.util;


import MongoConfigUtil.INPUT_URI;
import com.mongodb.MongoClientURI;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class MongoConfigUtilTest {
    @Test
    public void testGetMongoURIs() {
        Configuration conf = new Configuration();
        String[] connStrings = new String[]{ "mongodb://rshost1:10000,rshost2:10001/foo.bar?replicaSet=rs", "mongodb://standalone:27017/db.collection" };
        // Separated by ", "
        conf.set(INPUT_URI, (((connStrings[0]) + ", ") + (connStrings[1])));
        List<MongoClientURI> uris = MongoConfigUtil.getMongoURIs(conf, INPUT_URI);
        assertSameURIs(connStrings, uris);
        // No delimiter
        conf.set(INPUT_URI, ((connStrings[0]) + (connStrings[1])));
        uris = MongoConfigUtil.getMongoURIs(conf, INPUT_URI);
        assertSameURIs(connStrings, uris);
        // No value set
        uris = MongoConfigUtil.getMongoURIs(conf, "this key does not exist");
        Assert.assertEquals(0, uris.size());
        // Only one input URI.
        String connString = connStrings[1];
        conf.set(INPUT_URI, connString);
        uris = MongoConfigUtil.getMongoURIs(conf, INPUT_URI);
        assertSameURIs(new String[]{ connString }, uris);
    }
}

