package com.orientechnologies.orient.server.distributed;


import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.server.OServer;
import org.junit.Assert;
import org.junit.Test;


public class SimpleIndexDistributedIT {
    private OServer server0;

    private OServer server1;

    private OServer server2;

    private OrientDB remote;

    private ODatabaseSession session;

    private String indexName;

    @Test
    public void test() {
        OElement doc = session.newElement("test");
        doc.setProperty("test", "a value");
        doc = session.save(doc);
        doc.setProperty("test", "some");
        session.save(doc);
        try (OResultSet res = session.query((("select from index:" + (indexName)) + " where key =\"some\""))) {
            Assert.assertTrue(res.hasNext());
        }
        OrientDB remote1 = new OrientDB("remote:localhost:2425", OrientDBConfig.defaultConfig());
        ODatabaseSession session1 = remote1.open("test", "admin", "admin");
        try (OResultSet res1 = session1.query((("select from index:" + (indexName)) + " where key =\"some\""))) {
            Assert.assertTrue(res1.hasNext());
        }
        session1.close();
    }
}

