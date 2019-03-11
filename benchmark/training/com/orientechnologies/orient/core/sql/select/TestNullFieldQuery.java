package com.orientechnologies.orient.core.sql.select;


import OType.ANY;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 21/07/16.
 */
public class TestNullFieldQuery {
    private ODatabaseDocument database;

    @Test
    public void testQueryNullValue() {
        database.getMetadata().getSchema().createClass("Test");
        ODocument doc = new ODocument("Test");
        doc.field("name", ((Object) (null)));
        database.save(doc);
        List<ODocument> res = database.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from Test where name= 'some' "));
        Assert.assertTrue(res.isEmpty());
    }

    @Test
    public void testQueryNullValueSchemaFull() {
        OClass clazz = database.getMetadata().getSchema().createClass("Test");
        clazz.createProperty("name", ANY);
        ODocument doc = new ODocument("Test");
        doc.field("name", ((Object) (null)));
        database.save(doc);
        List<ODocument> res = database.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select from Test where name= 'some' "));
        Assert.assertTrue(res.isEmpty());
    }
}

