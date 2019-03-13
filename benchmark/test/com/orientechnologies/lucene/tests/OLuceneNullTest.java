package com.orientechnologies.lucene.tests;


import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Enrico Risa on 05/10/16.
 */
public class OLuceneNullTest extends OLuceneBaseTest {
    @Test
    public void testNullChangeToNotNullWithLists() {
        db.begin();
        ODocument doc = new ODocument("Test");
        db.save(doc);
        db.commit();
        db.begin();
        doc.field("names", new String[]{ "foo" });
        db.save(doc);
        db.commit();
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("Test.names");
        Assert.assertEquals(2, index.getSize());
    }

    @Test
    public void testNotNullChangeToNullWithLists() {
        ODocument doc = new ODocument("Test");
        db.begin();
        doc.field("names", new String[]{ "foo" });
        db.save(doc);
        db.commit();
        db.begin();
        doc.removeField("names");
        db.save(doc);
        db.commit();
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("Test.names");
        Assert.assertEquals(1, index.getSize());
    }
}

