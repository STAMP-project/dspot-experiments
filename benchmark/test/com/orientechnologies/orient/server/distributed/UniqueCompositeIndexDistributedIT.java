package com.orientechnologies.orient.server.distributed;


import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.server.OServer;
import org.junit.Assert;
import org.junit.Test;


public class UniqueCompositeIndexDistributedIT {
    private OServer server0;

    private OServer server1;

    private OServer server2;

    private OrientDB remote;

    private ODatabaseSession session;

    private String indexName;

    @Test
    public void test() {
        OElement doc = session.newElement("test");
        doc.setProperty("test", "1");
        doc.setProperty("testa", "2");
        doc = session.save(doc);
        session.begin();
        session.delete(doc.getIdentity());
        OElement doc1 = session.newElement("test");
        doc1.setProperty("test", "1");
        doc1.setProperty("testa", "2");
        doc1 = session.save(doc1);
        session.commit();
        try (OResultSet res = session.query("select from test")) {
            Assert.assertTrue(res.hasNext());
            Assert.assertEquals(res.next().getIdentity().get(), doc1.getIdentity());
        }
        try (OResultSet res = session.query((("select from index:" + (indexName)) + " where key =[\"1\",\"2\"]"))) {
            Assert.assertTrue(res.hasNext());
        }
    }
}

