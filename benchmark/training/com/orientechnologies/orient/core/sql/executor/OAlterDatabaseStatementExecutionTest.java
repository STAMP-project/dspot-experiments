package com.orientechnologies.orient.core.sql.executor;


import ODatabase.ATTRIBUTES.CUSTOM;
import ODatabase.ATTRIBUTES.MINIMUMCLUSTERS;
import com.orientechnologies.orient.core.config.OStorageEntryConfiguration;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OAlterDatabaseStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testSetProperty() {
        Object previousValue = OAlterDatabaseStatementExecutionTest.db.get(MINIMUMCLUSTERS);
        OResultSet result = OAlterDatabaseStatementExecutionTest.db.command("alter database MINIMUMCLUSTERS 12");
        Object currentValue = OAlterDatabaseStatementExecutionTest.db.get(MINIMUMCLUSTERS);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertNotNull(next);
        Assert.assertEquals(previousValue, next.getProperty("oldValue"));
        Assert.assertEquals(12, currentValue);
        Assert.assertEquals(currentValue, next.getProperty("newValue"));
        result.close();
    }

    @Test
    public void testSetCustom() {
        List<OStorageEntryConfiguration> previousCustoms = ((List<OStorageEntryConfiguration>) (OAlterDatabaseStatementExecutionTest.db.get(CUSTOM)));
        Object prev = null;
        for (OStorageEntryConfiguration entry : previousCustoms) {
            if (entry.name.equals("foo")) {
                prev = entry.value;
            }
        }
        OResultSet result = OAlterDatabaseStatementExecutionTest.db.command("alter database custom foo = 'bar'");
        previousCustoms = ((List<OStorageEntryConfiguration>) (OAlterDatabaseStatementExecutionTest.db.get(CUSTOM)));
        Object after = null;
        for (OStorageEntryConfiguration entry : previousCustoms) {
            if (entry.name.equals("foo")) {
                after = entry.value;
            }
        }
        Assert.assertNotNull(result);
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertNotNull(next);
        Assert.assertEquals(prev, next.getProperty("oldValue"));
        Assert.assertEquals("bar", after);
        Assert.assertEquals("bar", next.getProperty("newValue"));
        Assert.assertFalse(result.hasNext());
        result.close();
    }
}

