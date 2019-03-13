package com.orientechnologies.orient.core.tx;


import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OVertex;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 12/04/17.
 */
public class TransactionTest {
    private OrientDB orientDB;

    private ODatabaseDocument db;

    @Test
    public void test() {
        db.begin();
        OVertex v = db.newVertex("V");
        v.setProperty("name", "Foo");
        db.save(v);
        db.commit();
        db.begin();
        v.setProperty("name", "Bar");
        db.save(v);
        db.rollback();
        Assert.assertEquals("Foo", v.getProperty("name"));
    }
}

