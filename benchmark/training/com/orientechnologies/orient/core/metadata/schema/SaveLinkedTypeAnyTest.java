package com.orientechnologies.orient.core.metadata.schema;


import OType.ANY;
import OType.EMBEDDEDLIST;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 10/06/16.
 */
public class SaveLinkedTypeAnyTest {
    private ODatabaseDocument db;

    @Test
    public void testRemoveLinkedType() {
        OSchema schema = db.getMetadata().getSchema();
        OClass classA = schema.createClass("TestRemoveLinkedType");
        OProperty prop = classA.createProperty("prop", EMBEDDEDLIST, ANY);
        // db.command(new OCommandSQL("alter property TestRemoveLinkedType.prop linkedtype null")).execute();
        db.command(new OCommandSQL("insert into TestRemoveLinkedType set prop = [4]")).execute();
        List<ODocument> result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from TestRemoveLinkedType"));
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
        Collection coll = result.get(0).field("prop");
        Assert.assertEquals(coll.size(), 1);
        Assert.assertEquals(coll.iterator().next(), 4);
    }

    @Test
    public void testAlterRemoveLinkedType() {
        OSchema schema = db.getMetadata().getSchema();
        OClass classA = schema.createClass("TestRemoveLinkedType");
        OProperty prop = classA.createProperty("prop", EMBEDDEDLIST, ANY);
        db.command(new OCommandSQL("alter property TestRemoveLinkedType.prop linkedtype null")).execute();
        db.command(new OCommandSQL("insert into TestRemoveLinkedType set prop = [4]")).execute();
        List<ODocument> result = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from TestRemoveLinkedType"));
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
        Collection coll = result.get(0).field("prop");
        Assert.assertEquals(coll.size(), 1);
        Assert.assertEquals(coll.iterator().next(), 4);
    }
}

