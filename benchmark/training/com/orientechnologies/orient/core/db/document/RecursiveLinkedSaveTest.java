package com.orientechnologies.orient.core.db.document;


import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 26/10/15.
 */
public class RecursiveLinkedSaveTest {
    private ODatabaseDocument db;

    @Test
    public void testLinked() {
        db.getMetadata().getSchema().createClass("Test");
        ODocument doc = new ODocument("Test");
        ODocument doc1 = new ODocument("Test");
        doc.field("link", doc1);
        ODocument doc2 = new ODocument("Test");
        doc1.field("link", doc2);
        doc2.field("link", doc);
        doc = db.save(doc);
        Assert.assertEquals(3, db.countClass("Test"));
        doc = db.load(doc.getIdentity());
        doc1 = doc.field("link");
        doc2 = doc1.field("link");
        Assert.assertEquals(doc, doc2.field("link"));
    }

    @Test
    public void testTxLinked() {
        db.getMetadata().getSchema().createClass("Test");
        db.begin();
        ODocument doc = new ODocument("Test");
        ODocument doc1 = new ODocument("Test");
        doc.field("link", doc1);
        ODocument doc2 = new ODocument("Test");
        doc1.field("link", doc2);
        doc2.field("link", doc);
        db.save(doc);
        db.commit();
        Assert.assertEquals(3, db.countClass("Test"));
        doc = db.load(doc.getIdentity());
        doc1 = doc.field("link");
        doc2 = doc1.field("link");
        Assert.assertEquals(doc, doc2.field("link"));
    }
}

