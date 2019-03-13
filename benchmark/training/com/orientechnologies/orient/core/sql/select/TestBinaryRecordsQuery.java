package com.orientechnologies.orient.core.sql.select;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 15/04/16.
 */
public class TestBinaryRecordsQuery {
    private ODatabaseDocument database;

    @Test
    public void testSelectBinary() {
        database.save(new ORecordBytes("blabla".getBytes()), "BlobCluster");
        List<ORecord> res = database.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from cluster:BlobCluster"));
        Assert.assertEquals(1, res.size());
    }

    @Test
    public void testSelectRidBinary() {
        ORecord rec = database.save(new ORecordBytes("blabla".getBytes()), "BlobCluster");
        List<ORecord> res = database.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select @rid from cluster:BlobCluster"));
        Assert.assertEquals(1, res.size());
    }

    @Test
    public void testDeleteBinary() {
        ORecord rec = database.save(new ORecordBytes("blabla".getBytes()), "BlobCluster");
        Integer res = database.command(new OCommandSQL("delete from (select from cluster:BlobCluster)")).execute();
        database.getLocalCache().clear();
        Assert.assertEquals(1, res.intValue());
        rec = database.load(rec.getIdentity());
        Assert.assertNull(rec);
    }

    @Test
    public void testSelectDeleteBinary() {
        ORecord rec = database.save(new ORecordBytes("blabla".getBytes()), "BlobCluster");
        database.getMetadata().getSchema().createClass("RecordPointer");
        ODocument doc = new ODocument("RecordPointer");
        doc.field("ref", rec);
        database.save(doc);
        Integer res = database.command(new OCommandSQL("delete from cluster:BlobCluster where @rid in (select ref from RecordPointer)")).execute();
        database.getLocalCache().clear();
        Assert.assertEquals(1, res.intValue());
        rec = database.load(rec.getIdentity());
        Assert.assertNull(rec);
    }

    @Test
    public void testDeleteFromSelectBinary() {
        ORecord rec = database.save(new ORecordBytes("blabla".getBytes()), "BlobCluster");
        ORecord rec1 = database.save(new ORecordBytes("blabla".getBytes()), "BlobCluster");
        database.getMetadata().getSchema().createClass("RecordPointer");
        ODocument doc = new ODocument("RecordPointer");
        doc.field("ref", rec);
        database.save(doc);
        ODocument doc1 = new ODocument("RecordPointer");
        doc1.field("ref", rec1);
        database.save(doc1);
        Integer res = database.command(new OCommandSQL("delete from (select expand(ref) from RecordPointer)")).execute();
        database.getLocalCache().clear();
        Assert.assertEquals(2, res.intValue());
        rec = database.load(rec.getIdentity());
        Assert.assertNull(rec);
        rec = database.load(rec1.getIdentity());
        Assert.assertNull(rec);
    }
}

