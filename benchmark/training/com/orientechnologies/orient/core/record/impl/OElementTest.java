package com.orientechnologies.orient.core.record.impl;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by luigidellaquila on 02/07/16.
 */
public class OElementTest {
    private static ODatabaseDocument db;

    @Test
    public void testGetSetProperty() {
        OElement elem = OElementTest.db.newElement();
        elem.setProperty("foo", "foo1");
        elem.setProperty("foo.bar", "foobar");
        elem.setProperty("  ", "spaces");
        Set<String> names = elem.getPropertyNames();
        Assert.assertTrue(names.contains("foo"));
        Assert.assertTrue(names.contains("foo.bar"));
        Assert.assertTrue(names.contains("  "));
    }

    @Test
    public void testLoadAndSave() {
        OElementTest.db.createClassIfNotExist("TestLoadAndSave");
        OElement elem = OElementTest.db.newElement("TestLoadAndSave");
        elem.setProperty("name", "foo");
        OElementTest.db.save(elem);
        OResultSet result = OElementTest.db.query("select from TestLoadAndSave where name = 'foo'");
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals("foo", result.next().getProperty("name"));
        result.close();
    }
}

