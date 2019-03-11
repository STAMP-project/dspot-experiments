package com.orientechnologies.orient.server.distributed;


import OGlobalConfiguration.QUERY_REMOTE_RESULTSET_PAGE_SIZE;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.server.OServer;
import org.junit.Assert;
import org.junit.Test;


public class SimpleQueryDistributedIT {
    private OServer server0;

    private OServer server1;

    private OServer server2;

    private OrientDB remote;

    private ODatabaseSession session;

    @Test
    public void test() {
        OVertex vertex = session.newVertex("V");
        vertex.setProperty("name", "one");
        session.save(vertex);
        OResultSet res = session.query("select from V");
        Assert.assertTrue(res.hasNext());
        Assert.assertEquals(res.next().getProperty("name"), "one");
    }

    @Test
    public void testExecute() {
        OVertex vertex = session.newVertex("V");
        vertex.setProperty("name", "one");
        session.save(vertex);
        OResultSet res = session.execute("sql", "select from V");
        Assert.assertTrue(res.hasNext());
        Assert.assertEquals(res.next().getProperty("name"), "one");
    }

    @Test
    public void testRecords() {
        int records = (QUERY_REMOTE_RESULTSET_PAGE_SIZE.getValueAsInteger()) + 10;
        for (int i = 0; i < records; i++) {
            OVertex vertex = session.newVertex("V");
            vertex.setProperty("name", "one");
            vertex.setProperty("pos", i);
            session.save(vertex);
        }
        OResultSet res = session.query("select from V order by pos");
        for (int i = 0; i < records; i++) {
            Assert.assertTrue(res.hasNext());
            OResult ele = res.next();
            Assert.assertEquals(((int) (ele.getProperty("pos"))), i);
            Assert.assertEquals(ele.getProperty("name"), "one");
        }
    }
}

