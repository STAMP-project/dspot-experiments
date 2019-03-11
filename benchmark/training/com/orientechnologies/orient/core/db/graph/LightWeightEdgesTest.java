package com.orientechnologies.orient.core.db.graph;


import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class LightWeightEdgesTest {
    private OrientDB orientDB;

    private ODatabaseSession session;

    @Test
    public void testSimpleLightWeight() {
        OVertex v = session.newVertex("Vertex");
        OVertex v1 = session.newVertex("Vertex");
        List<OVertex> out = new ArrayList<>();
        out.add(v1);
        List<OVertex> in = new ArrayList<>();
        in.add(v);
        v.setProperty("out_Edge", out);
        v.setProperty("name", "aName");
        v1.setProperty("in_Edge", in);
        v1.setProperty("name", "bName");
        session.save(v);
        try (OResultSet res = session.query(" select expand(out('Edge')) from `Vertex` where name = 'aName'")) {
            Assert.assertTrue(res.hasNext());
            OResult r = res.next();
            Assert.assertEquals(r.getProperty("name"), "bName");
        }
        try (OResultSet res = session.query(" select expand(in('Edge')) from `Vertex` where name = 'bName'")) {
            Assert.assertTrue(res.hasNext());
            OResult r = res.next();
            Assert.assertEquals(r.getProperty("name"), "aName");
        }
    }

    @Test
    public void testSimpleLightWeightMissingClass() {
        OVertex v = session.newVertex("Vertex");
        OVertex v1 = session.newVertex("Vertex");
        List<OVertex> out = new ArrayList<>();
        out.add(v1);
        List<OVertex> in = new ArrayList<>();
        in.add(v);
        v.setProperty("out_Edgea", out);
        v.setProperty("name", "aName");
        v1.setProperty("in_Edgea", in);
        v1.setProperty("name", "bName");
        session.save(v);
        try (OResultSet res = session.query(" select expand(out('Edgea')) from `Vertex` where name = 'aName'")) {
            Assert.assertTrue(res.hasNext());
            OResult r = res.next();
            Assert.assertEquals(r.getProperty("name"), "bName");
        }
        try (OResultSet res = session.query(" select expand(in('Edgea')) from `Vertex` where name = 'bName'")) {
            Assert.assertTrue(res.hasNext());
            OResult r = res.next();
            Assert.assertEquals(r.getProperty("name"), "aName");
        }
    }
}

