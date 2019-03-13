package com.orientechnologies.orient.server.query;


import OType.EMBEDDED;
import OType.EMBEDDEDLIST;
import OType.EMBEDDEDMAP;
import OType.EMBEDDEDSET;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.server.OClientConnection;
import com.orientechnologies.orient.server.OServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 03/01/17.
 */
public class RemoteQuerySupportTest {
    private static final String SERVER_DIRECTORY = "./target/query";

    private OServer server;

    private OrientDB orientDB;

    private ODatabaseDocument session;

    private int oldPageSize;

    @Test
    public void testQuery() {
        for (int i = 0; i < 150; i++) {
            ODocument doc = new ODocument("Some");
            doc.setProperty("prop", "value");
            session.save(doc);
        }
        OResultSet res = session.query("select from Some");
        for (int i = 0; i < 150; i++) {
            Assert.assertTrue(res.hasNext());
            OResult item = res.next();
            Assert.assertEquals(item.getProperty("prop"), "value");
        }
    }

    @Test
    public void testCommandSelect() {
        for (int i = 0; i < 150; i++) {
            ODocument doc = new ODocument("Some");
            doc.setProperty("prop", "value");
            session.save(doc);
        }
        OResultSet res = session.command("select from Some");
        for (int i = 0; i < 150; i++) {
            Assert.assertTrue(res.hasNext());
            OResult item = res.next();
            Assert.assertEquals(item.getProperty("prop"), "value");
        }
    }

    @Test
    public void testCommandInsertWithPageOverflow() {
        for (int i = 0; i < 150; i++) {
            ODocument doc = new ODocument("Some");
            doc.setProperty("prop", "value");
            session.save(doc);
        }
        OResultSet res = session.command("insert into V from select from Some");
        for (int i = 0; i < 150; i++) {
            Assert.assertTrue(res.hasNext());
            OResult item = res.next();
            Assert.assertEquals(item.getProperty("prop"), "value");
        }
    }

    @Test(expected = ODatabaseException.class)
    public void testQueryKilledSession() {
        for (int i = 0; i < 150; i++) {
            ODocument doc = new ODocument("Some");
            doc.setProperty("prop", "value");
            session.save(doc);
        }
        OResultSet res = session.query("select from Some");
        for (OClientConnection conn : server.getClientConnectionManager().getConnections()) {
            conn.close();
        }
        session.activateOnCurrentThread();
        for (int i = 0; i < 150; i++) {
            Assert.assertTrue(res.hasNext());
            OResult item = res.next();
            Assert.assertEquals(item.getProperty("prop"), "value");
        }
    }

    @Test
    public void testQueryEmbedded() {
        ODocument doc = new ODocument("Some");
        doc.setProperty("prop", "value");
        ODocument emb = new ODocument();
        emb.setProperty("one", "value");
        doc.setProperty("emb", emb, EMBEDDED);
        session.save(doc);
        OResultSet res = session.query("select emb from Some");
        OResult item = res.next();
        Assert.assertNotNull(item.getProperty("emb"));
        Assert.assertEquals(getProperty("one"), "value");
    }

    @Test
    public void testQueryDoubleEmbedded() {
        ODocument doc = new ODocument("Some");
        doc.setProperty("prop", "value");
        ODocument emb1 = new ODocument();
        emb1.setProperty("two", "value");
        ODocument emb = new ODocument();
        emb.setProperty("one", "value");
        emb.setProperty("secEmb", emb1, EMBEDDED);
        doc.setProperty("emb", emb, EMBEDDED);
        session.save(doc);
        OResultSet res = session.query("select emb from Some");
        OResult item = res.next();
        Assert.assertNotNull(item.getProperty("emb"));
        OResult resEmb = item.getProperty("emb");
        Assert.assertEquals(resEmb.getProperty("one"), "value");
        Assert.assertEquals(getProperty("two"), "value");
    }

    @Test
    public void testQueryEmbeddedList() {
        ODocument doc = new ODocument("Some");
        doc.setProperty("prop", "value");
        ODocument emb = new ODocument();
        emb.setProperty("one", "value");
        List<Object> list = new ArrayList<>();
        list.add(emb);
        doc.setProperty("list", list, EMBEDDEDLIST);
        session.save(doc);
        OResultSet res = session.query("select list from Some");
        OResult item = res.next();
        Assert.assertNotNull(item.getProperty("list"));
        Assert.assertEquals(((List<OResult>) (item.getProperty("list"))).size(), 1);
        Assert.assertEquals(((List<OResult>) (item.getProperty("list"))).get(0).getProperty("one"), "value");
    }

    @Test
    public void testQueryEmbeddedSet() {
        ODocument doc = new ODocument("Some");
        doc.setProperty("prop", "value");
        ODocument emb = new ODocument();
        emb.setProperty("one", "value");
        Set<ODocument> set = new HashSet<>();
        set.add(emb);
        doc.setProperty("set", set, EMBEDDEDSET);
        session.save(doc);
        OResultSet res = session.query("select set from Some");
        OResult item = res.next();
        Assert.assertNotNull(item.getProperty("set"));
        Assert.assertEquals(((Set<OResult>) (item.getProperty("set"))).size(), 1);
        Assert.assertEquals(getProperty("one"), "value");
    }

    @Test
    public void testQueryEmbeddedMap() {
        ODocument doc = new ODocument("Some");
        doc.setProperty("prop", "value");
        ODocument emb = new ODocument();
        emb.setProperty("one", "value");
        Map<String, ODocument> map = new HashMap<>();
        map.put("key", emb);
        doc.setProperty("map", map, EMBEDDEDMAP);
        session.save(doc);
        OResultSet res = session.query("select map from Some");
        OResult item = res.next();
        Assert.assertNotNull(item.getProperty("map"));
        Assert.assertEquals(((Map<String, OResult>) (item.getProperty("map"))).size(), 1);
        Assert.assertEquals(((Map<String, OResult>) (item.getProperty("map"))).get("key").getProperty("one"), "value");
    }
}

