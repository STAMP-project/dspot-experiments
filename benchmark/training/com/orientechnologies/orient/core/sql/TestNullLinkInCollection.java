package com.orientechnologies.orient.core.sql;


import OType.LINKLIST;
import OType.LINKSET;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 02/12/15.
 */
public class TestNullLinkInCollection {
    private ODatabaseDocument db;

    @Test
    public void testLinkListRemovedRecord() {
        ODocument doc = new ODocument("Test");
        List<ORecordId> docs = new ArrayList<ORecordId>();
        docs.add(new ORecordId(10, 20));
        doc.field("items", docs, LINKLIST);
        db.save(doc);
        List<ODocument> res = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select items from Test"));
        Assert.assertNull(((List) (res.get(0).field("items"))).get(0));
    }

    @Test
    public void testLinkSetRemovedRecord() {
        ODocument doc = new ODocument("Test");
        Set<ORecordId> docs = new HashSet<ORecordId>();
        docs.add(new ORecordId(10, 20));
        doc.field("items", docs, LINKSET);
        db.save(doc);
        List<ODocument> res = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select items from Test"));
        Assert.assertNull(((Set) (res.get(0).field("items"))).iterator().next());
    }
}

