package com.orientechnologies.orient.core.record.impl;


import OType.EMBEDDEDMAP;
import OType.LINKMAP;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TestSerializationCompatibilityRecord {
    private ODatabaseDocument database;

    @Test
    public void testDataNotMatchSchema() {
        OClass klass = database.getMetadata().getSchema().createClass("Test", database.getMetadata().getSchema().getClass("V"));
        ODocument doc = new ODocument("Test");
        Map<String, ORID> map = new HashMap<String, ORID>();
        map.put("some", new ORecordId(10, 20));
        doc.field("map", map, LINKMAP);
        ORID id = database.save(doc).getIdentity();
        klass.createProperty("map", EMBEDDEDMAP, ((OType) (null)), true);
        database.getMetadata().reload();
        database.getLocalCache().clear();
        ODocument record = database.load(id);
        // Force deserialize + serialize;
        record.field("some", "aa");
        database.save(record);
        database.getLocalCache().clear();
        ODocument record1 = database.load(id);
        Assert.assertEquals(record1.fieldType("map"), LINKMAP);
    }
}

