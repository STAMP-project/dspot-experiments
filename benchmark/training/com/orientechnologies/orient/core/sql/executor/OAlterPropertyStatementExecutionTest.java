package com.orientechnologies.orient.core.sql.executor;


import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OAlterPropertyStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testSetProperty() {
        String className = "testSetProperty";
        OClass clazz = OAlterPropertyStatementExecutionTest.db.getMetadata().getSchema().createClass(className);
        OProperty prop = clazz.createProperty("name", STRING);
        prop.setMax("15");
        OResultSet result = OAlterPropertyStatementExecutionTest.db.command((("alter property " + className) + ".name max 30"));
        ExecutionPlanPrintUtils.printExecutionPlan(null, result);
        Object currentValue = prop.getMax();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertNotNull(next);
        Assert.assertEquals("15", next.getProperty("oldValue"));
        Assert.assertEquals("30", currentValue);
        Assert.assertEquals(currentValue, next.getProperty("newValue"));
        result.close();
    }

    @Test
    public void testSetCustom() {
        String className = "testSetCustom";
        OClass clazz = OAlterPropertyStatementExecutionTest.db.getMetadata().getSchema().createClass(className);
        OProperty prop = clazz.createProperty("name", STRING);
        prop.setCustom("foo", "bar");
        OResultSet result = OAlterPropertyStatementExecutionTest.db.command((("alter property " + className) + ".name custom foo='baz'"));
        ExecutionPlanPrintUtils.printExecutionPlan(null, result);
        Object currentValue = prop.getCustom("foo");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertNotNull(next);
        Assert.assertEquals("bar", next.getProperty("oldValue"));
        Assert.assertEquals("baz", currentValue);
        Assert.assertEquals(currentValue, next.getProperty("newValue"));
        result.close();
    }
}

