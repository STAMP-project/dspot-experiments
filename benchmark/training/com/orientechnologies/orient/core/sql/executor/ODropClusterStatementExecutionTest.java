package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class ODropClusterStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        String cluster = "testPlain";
        getStorage().addCluster(cluster);
        Assert.assertTrue(((ODropClusterStatementExecutionTest.db.getClusterIdByName(cluster)) > 0));
        OResultSet result = ODropClusterStatementExecutionTest.db.command(("drop cluster " + cluster));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertEquals("drop cluster", next.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        result.close();
        Assert.assertTrue(((ODropClusterStatementExecutionTest.db.getClusterIdByName(cluster)) < 0));
    }

    @Test
    public void testDropClusterIfExists() {
        String cluster = "testDropClusterIfExists";
        getStorage().addCluster(cluster);
        Assert.assertTrue(((ODropClusterStatementExecutionTest.db.getClusterIdByName(cluster)) > 0));
        OResultSet result = ODropClusterStatementExecutionTest.db.command((("drop cluster " + cluster) + " IF EXISTS"));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertEquals("drop cluster", next.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        result.close();
        Assert.assertTrue(((ODropClusterStatementExecutionTest.db.getClusterIdByName(cluster)) < 0));
        result = ODropClusterStatementExecutionTest.db.command((("drop cluster " + cluster) + " IF EXISTS"));
        Assert.assertFalse(result.hasNext());
        result.close();
    }
}

