package com.thinkaurelius.titan.graphdb.inmemory;


import GraphDatabaseConfiguration.STORAGE_READONLY;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class InMemoryConfigurationTest {
    StandardTitanGraph graph;

    @Test
    public void testReadOnly() {
        initialize(STORAGE_READONLY, true);
        TitanTransaction tx = graph.newTransaction();
        try {
            tx.addVertex();
            Assert.fail();
        } catch (Exception e) {
        } finally {
            tx.rollback();
        }
        try {
            graph.addVertex();
            Assert.fail();
        } catch (Exception e) {
        } finally {
            graph.tx().rollback();
        }
    }
}

