package com.orientechnologies.orient.core.sql.executor;


import OClass.INDEX_TYPE.UNIQUE;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OTruncateClusterStatementExecutionTest {
    static ODatabaseDocument database;

    @Test
    public void testClusterWithIndex() {
        final String clusterName = "TruncateClusterWithIndex";
        final int clusterId = OTruncateClusterStatementExecutionTest.database.addCluster(clusterName);
        final String className = "TruncateClusterClass";
        final OSchema schema = OTruncateClusterStatementExecutionTest.database.getMetadata().getSchema();
        final OClass clazz = schema.createClass(className);
        clazz.addClusterId(clusterId);
        clazz.createProperty("value", STRING);
        clazz.createIndex("TruncateClusterIndex", UNIQUE, "value");
        final ODocument document = new ODocument();
        document.field("value", "val");
        document.save(clusterName);
        Assert.assertEquals(OTruncateClusterStatementExecutionTest.database.countClass(className), 1);
        Assert.assertEquals(OTruncateClusterStatementExecutionTest.database.countClusterElements(clusterId), 1);
        OResultSet indexQuery = OTruncateClusterStatementExecutionTest.database.query("select from TruncateClusterClass where value='val'");
        Assert.assertEquals(toList(indexQuery).size(), 1);
        indexQuery.close();
        OTruncateClusterStatementExecutionTest.database.command(("truncate cluster " + clusterName));
        Assert.assertEquals(OTruncateClusterStatementExecutionTest.database.countClass(className), 0);
        Assert.assertEquals(OTruncateClusterStatementExecutionTest.database.countClusterElements(clusterId), 0);
        indexQuery = OTruncateClusterStatementExecutionTest.database.query("select from TruncateClusterClass where value='val'");
        Assert.assertEquals(toList(indexQuery).size(), 0);
        indexQuery.close();
    }
}

