package com.orientechnologies.orient.core.record.impl;


import OType.CUSTOM;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 25/01/16.
 */
public class OJsonReadWriteTest {
    @Test
    public void testCustomField() {
        ODocument doc = new ODocument();
        doc.field("test", String.class, CUSTOM);
        String json = doc.toJSON();
        System.out.println(json);
        ODocument doc1 = new ODocument();
        doc1.fromJSON(json);
        Assert.assertEquals(doc.<String>field("test"), doc1.field("test"));
    }
}

