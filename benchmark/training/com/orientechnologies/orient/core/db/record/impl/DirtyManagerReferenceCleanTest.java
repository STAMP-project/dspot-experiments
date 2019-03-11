package com.orientechnologies.orient.core.db.record.impl;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 01/10/15.
 */
public class DirtyManagerReferenceCleanTest {
    private ODatabaseDocument db;

    @Test
    public void testNoTxRefClean() {
        ODocument doc = new ODocument("test");
        ODocument doc1 = new ODocument("test");
        doc.field("ref", doc1);
        Assert.assertEquals(1, ORecordInternal.getDirtyManager(doc).getReferences().size());
        db.save(doc);
        Assert.assertNull(ORecordInternal.getDirtyManager(doc).getReferences());
    }

    @Test
    public void testTxRefClean() {
        db.begin();
        ODocument doc = new ODocument("test");
        ODocument doc1 = new ODocument("test");
        doc.field("ref", doc1);
        Assert.assertEquals(1, ORecordInternal.getDirtyManager(doc).getReferences().size());
        db.save(doc);
        Assert.assertNull(ORecordInternal.getDirtyManager(doc).getReferences());
        db.commit();
        Assert.assertNull(ORecordInternal.getDirtyManager(doc).getReferences());
    }

    @Test
    public void testReferDeletedDocument() {
        ODocument doc = new ODocument();
        ODocument doc1 = new ODocument();
        doc1.field("aa", "aa");
        doc.field("ref", doc1);
        doc.field("bb");
        OIdentifiable id = doc.save(db.getClusterNameById(db.getDefaultClusterId()));
        doc = db.load(id.getIdentity());
        doc1 = doc.field("ref");
        doc1.delete();
        doc.field("ab", "ab");
        Assert.assertFalse(ORecordInternal.getDirtyManager(doc).getUpdateRecords().contains(doc1));
    }
}

