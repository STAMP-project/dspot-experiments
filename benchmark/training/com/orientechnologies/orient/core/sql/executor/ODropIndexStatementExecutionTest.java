package com.orientechnologies.orient.core.sql.executor;


import OClass.INDEX_TYPE.NOTUNIQUE;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.index.OIndex;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class ODropIndexStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        OIndex<?> index = ODropIndexStatementExecutionTest.db.getMetadata().getSchema().createClass("testPlain").createProperty("bar", STRING).createIndex(NOTUNIQUE);
        String indexName = index.getName();
        Assert.assertNotNull(getIndex(indexName));
        OResultSet result = ODropIndexStatementExecutionTest.db.command(("drop index " + indexName));
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertEquals("drop index", next.getProperty("operation"));
        Assert.assertFalse(result.hasNext());
        result.close();
        Assert.assertNull(getIndex(indexName));
    }

    @Test
    public void testAll() {
        OIndex<?> index = ODropIndexStatementExecutionTest.db.getMetadata().getSchema().createClass("testAll").createProperty("baz", STRING).createIndex(NOTUNIQUE);
        String indexName = index.getName();
        Assert.assertNotNull(getIndex(indexName));
        OResultSet result = ODropIndexStatementExecutionTest.db.command("drop index *");
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertEquals("drop index", next.getProperty("operation"));
        result.close();
        Assert.assertNull(getIndex(indexName));
        Assert.assertTrue(ODropIndexStatementExecutionTest.db.getMetadata().getIndexManager().getIndexes().isEmpty());
    }

    @Test
    public void testWrongName() {
        String indexName = "nonexistingindex";
        Assert.assertNull(getIndex(indexName));
        try {
            OResultSet result = ODropIndexStatementExecutionTest.db.command(("drop index " + indexName));
            Assert.fail();
        } catch (OCommandExecutionException ex) {
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testIfExists() {
        String indexName = "nonexistingindex";
        Assert.assertNull(getIndex(indexName));
        try {
            OResultSet result = ODropIndexStatementExecutionTest.db.command((("drop index " + indexName) + " if exists"));
        } catch (Exception e) {
            Assert.fail();
        }
    }
}

