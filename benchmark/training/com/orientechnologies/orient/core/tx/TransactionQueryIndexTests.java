package com.orientechnologies.orient.core.tx;


import OClass.INDEX_TYPE.NOTUNIQUE;
import OType.STRING;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 12/04/17.
 */
public class TransactionQueryIndexTests {
    private OrientDB orientDB;

    private ODatabaseDocument database;

    @Test
    public void test() {
        OClass clazz = database.createClass("test");
        OProperty prop = clazz.createProperty("test", STRING);
        prop.createIndex(NOTUNIQUE);
        database.begin();
        ODocument doc = database.newInstance("test");
        doc.setProperty("test", "abcdefg");
        database.save(doc);
        OResultSet res = database.query("select from Test where test='abcdefg' ");
        Assert.assertEquals(1L, res.stream().count());
        res.close();
        res = database.query("select from Test where test='aaaaa' ");
        System.out.println(res.getExecutionPlan().get().prettyPrint(0, 0));
        Assert.assertEquals(0L, res.stream().count());
        res.close();
    }

    @Test
    public void test2() {
        OClass clazz = database.createClass("Test2");
        clazz.createProperty("foo", STRING);
        clazz.createProperty("bar", STRING);
        clazz.createIndex("Test2.foo_bar", NOTUNIQUE, "foo", "bar");
        database.begin();
        ODocument doc = database.newInstance("Test2");
        doc.setProperty("foo", "abcdefg");
        doc.setProperty("bar", "abcdefg");
        database.save(doc);
        OResultSet res = database.query("select from Test2 where foo='abcdefg' and bar = 'abcdefg' ");
        Assert.assertEquals(1L, res.stream().count());
        res.close();
        res = database.query("select from Test2 where foo='aaaaa' and bar = 'aaa'");
        System.out.println(res.getExecutionPlan().get().prettyPrint(0, 0));
        Assert.assertEquals(0L, res.stream().count());
        res.close();
    }
}

