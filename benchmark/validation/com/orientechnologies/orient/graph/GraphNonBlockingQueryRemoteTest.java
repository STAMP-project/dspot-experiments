package com.orientechnologies.orient.graph;


import OType.STRING;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLNonBlockingQuery;
import com.orientechnologies.orient.server.OServer;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 01/07/16.
 */
public class GraphNonBlockingQueryRemoteTest {
    private OServer server;

    private String serverHome;

    private String oldOrientDBHome;

    @Test
    public void testNonBlockingClose() throws InterruptedException, ExecutionException {
        OrientGraph database = new OrientGraph(("remote:localhost:3064/" + (GraphNonBlockingQueryRemoteTest.class.getSimpleName())));
        database.createVertexType("Prod").createProperty("something", STRING);
        for (int i = 0; i < 21; i++) {
            OrientVertex vertex = database.addVertex("class:Prod");
            vertex.setProperty("something", "value");
            vertex.save();
        }
        database.commit();
        final CountDownLatch ended = new CountDownLatch(21);
        try {
            OSQLNonBlockingQuery<Object> test = new OSQLNonBlockingQuery<Object>("select * from Prod ", new OCommandResultListener() {
                int resultCount = 0;

                @Override
                public boolean result(Object iRecord) {
                    (resultCount)++;
                    ODocument odoc = ((ODocument) (iRecord));
                    for (String name : odoc.fieldNames()) {
                        // <----------- PROBLEM
                        Assert.assertEquals("something", name);
                    }
                    ended.countDown();
                    return (resultCount) > 20 ? false : true;
                }

                @Override
                public void end() {
                    ended.countDown();
                }

                @Override
                public Object getResult() {
                    return resultCount;
                }
            });
            database.command(test).execute();
            Assert.assertTrue(ended.await(10, TimeUnit.SECONDS));
        } finally {
            database.shutdown();
        }
    }
}

