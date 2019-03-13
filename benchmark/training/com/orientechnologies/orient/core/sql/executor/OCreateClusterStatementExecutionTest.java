package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.storage.OCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OCreateClusterStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        String clusterName = "testPlain";
        OResultSet result = OCreateClusterStatementExecutionTest.db.command(("create cluster " + clusterName));
        Assert.assertTrue(((OCreateClusterStatementExecutionTest.db.getClusterIdByName(clusterName)) > 0));
        result.close();
    }

    @Test
    public void testExisting() {
        OClass clazz = OCreateClusterStatementExecutionTest.db.getMetadata().getSchema().createClass("testExisting");
        String clusterName = OCreateClusterStatementExecutionTest.db.getClusterNameById(clazz.getClusterIds()[0]);
        try {
            OCreateClusterStatementExecutionTest.db.command(("create cluster " + clusterName));
            Assert.fail();
        } catch (OCommandExecutionException ex) {
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testWithNumber() {
        String clusterName = "testWithNumber";
        OResultSet result = OCreateClusterStatementExecutionTest.db.command((("create cluster " + clusterName) + " id 1000"));
        Assert.assertTrue(((OCreateClusterStatementExecutionTest.db.getClusterIdByName(clusterName)) > 0));
        Assert.assertNotNull(OCreateClusterStatementExecutionTest.db.getClusterNameById(1000));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertFalse(result.hasNext());
        Assert.assertNotNull(next);
        Assert.assertEquals(((Object) (1000)), next.getProperty("requestedId"));
        result.close();
    }

    @Test
    public void testBlob() {
        String clusterName = "testBlob";
        OResultSet result = OCreateClusterStatementExecutionTest.db.command(("create blob cluster " + clusterName));
        Assert.assertTrue(((OCreateClusterStatementExecutionTest.db.getClusterIdByName(clusterName)) > 0));
        OCluster cluster = getStorage().getClusterByName(clusterName);
        // TODO test that it's a blob cluster
        result.close();
    }

    @Test
    public void testIfNotExists() {
        String clusterName = "testIfNotExists";
        OResultSet result = OCreateClusterStatementExecutionTest.db.command((("create cluster " + clusterName) + " IF NOT EXISTS id 2000"));
        Assert.assertTrue(((OCreateClusterStatementExecutionTest.db.getClusterIdByName(clusterName)) > 0));
        Assert.assertNotNull(OCreateClusterStatementExecutionTest.db.getClusterNameById(2000));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertFalse(result.hasNext());
        Assert.assertNotNull(next);
        Assert.assertEquals(((Object) (2000)), next.getProperty("requestedId"));
        result.close();
        result = OCreateClusterStatementExecutionTest.db.command((("create cluster " + clusterName) + " IF NOT EXISTS id 1000"));
        Assert.assertFalse(result.hasNext());
        result.close();
    }
}

