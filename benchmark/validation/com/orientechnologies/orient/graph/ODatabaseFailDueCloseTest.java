package com.orientechnologies.orient.graph;


import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ODatabaseFailDueCloseTest {
    private static OrientGraphFactory pool;

    @Test
    public void test1() {
        final OrientGraph graph = ODatabaseFailDueCloseTest.pool.getTx();
        try {
            String queryText = "SELECT @rid as rid, localName FROM Person WHERE ( 'milano' IN out('lives').localName OR 'roma' IN out('lives').localName ) ORDER BY age ASC";
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(queryText);
            List<ODocument> results = graph.getRawGraph().query(query);
            Assert.assertNotNull(results);
            Assert.assertTrue(((results.size()) > 0));
        } finally {
            graph.shutdown();
        }
    }

    @Test
    public void test2() {
        final OrientGraph graph = ODatabaseFailDueCloseTest.pool.getTx();
        try {
            String queryText = "SELECT @rid as rid, localName FROM Person WHERE age > 30";
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(queryText);
            List<ODocument> results = graph.getRawGraph().query(query);
            Assert.assertNotNull(results);
            Assert.assertTrue(((results.size()) > 0));
        } finally {
            graph.shutdown();
        }
    }
}

