package com.tinkerpop.blueprints.impls.orient;


import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.server.OServer;
import java.util.Collection;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 24/05/16.
 */
public class BatchRemoteResultSetTest {
    private String serverHome;

    private String oldOrientDBHome;

    private OServer server;

    @Test
    public void runBatchQuery() {
        String batchQuery = "begin; LET t0 = CREATE VERTEX V set mame=\"a\" ;\n LET t1 = CREATE VERTEX V set name=\"b\" ;\n" + ("LET t2 = CREATE EDGE E FROM $t0 TO $t1 ;\n commit retry 100\n" + "return [$t0,$t1,$t2]");
        OrientGraph graph = new OrientGraph(("remote:localhost:3064/" + (OrientGraphRemoteTest.class.getSimpleName())), "root", "root");
        Iterable<OIdentifiable> res = graph.getRawGraph().command(new OCommandScript("sql", batchQuery)).execute();
        Iterator iter = res.iterator();
        Assert.assertTrue(((iter.next()) instanceof OIdentifiable));
        Assert.assertTrue(((iter.next()) instanceof OIdentifiable));
        Object edges = iter.next();
        Assert.assertTrue((edges instanceof Collection));
        Assert.assertTrue(((((Collection) (edges)).iterator().next()) instanceof OIdentifiable));
    }
}

