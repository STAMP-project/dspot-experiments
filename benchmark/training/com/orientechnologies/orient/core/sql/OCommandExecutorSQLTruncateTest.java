package com.orientechnologies.orient.core.sql;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


public class OCommandExecutorSQLTruncateTest {
    private ODatabaseDocument database;

    @Test
    public void testTruncatePlain() {
        OClass vcl = database.getMetadata().getSchema().createClass("A");
        database.getMetadata().getSchema().createClass("ab", vcl);
        ODocument doc = new ODocument("A");
        database.save(doc);
        doc = new ODocument("ab");
        database.save(doc);
        Number ret = database.command(new OCommandSQL("truncate class A ")).execute();
        Assert.assertEquals(ret.intValue(), 1);
    }

    @Test
    public void testTruncatePolimorphic() {
        OClass vcl = database.getMetadata().getSchema().createClass("A");
        database.getMetadata().getSchema().createClass("ab", vcl);
        ODocument doc = new ODocument("A");
        database.save(doc);
        doc = new ODocument("ab");
        database.save(doc);
        Number ret = database.command(new OCommandSQL("truncate class A POLYMORPHIC")).execute();
        Assert.assertEquals(ret.intValue(), 2);
    }
}

