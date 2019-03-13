package com.orientechnologies.orient.core.record.impl;


import OType.DATE;
import OType.DATETIME;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.util.ODateHelper;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class DefaultValueTest {
    private ODatabaseDocument database;

    @Test
    public void testKeepValueSerialization() {
        // create example schema
        OSchema schema = database.getMetadata().getSchema();
        OClass classA = schema.createClass("ClassC");
        OProperty prop = classA.createProperty("name", STRING);
        prop.setDefaultValue("uuid()");
        ODocument doc = new ODocument("ClassC");
        byte[] val = doc.toStream();
        ODocument doc1 = new ODocument();
        doc1.fromStream(val);
        doc1.deserializeFields();
        Assert.assertEquals(((String) (doc.field("name"))), ((String) (doc1.field("name"))));
    }

    @Test
    public void testDefaultValueDate() {
        OSchema schema = database.getMetadata().getSchema();
        OClass classA = schema.createClass("ClassA");
        OProperty prop = classA.createProperty("date", DATE);
        prop.setDefaultValue(ODateHelper.getDateTimeFormatInstance().format(new Date()));
        OProperty some = classA.createProperty("id", STRING);
        some.setDefaultValue("uuid()");
        ODocument doc = new ODocument(classA);
        ODocument saved = database.save(doc);
        Assert.assertNotNull(saved.field("date"));
        Assert.assertTrue(((saved.field("date")) instanceof Date));
        Assert.assertNotNull(saved.field("id"));
        OIdentifiable id = database.command(new OCommandSQL("insert into ClassA content {}")).execute();
        ODocument seved1 = database.load(id.getIdentity());
        Assert.assertNotNull(seved1.field("date"));
        Assert.assertNotNull(seved1.field("id"));
        Assert.assertTrue(((seved1.field("date")) instanceof Date));
    }

    @Test
    public void testDefaultValueFromJson() {
        OSchema schema = database.getMetadata().getSchema();
        OClass classA = schema.createClass("ClassA");
        OProperty prop = classA.createProperty("date", DATE);
        prop.setDefaultValue(ODateHelper.getDateTimeFormatInstance().format(new Date()));
        ODocument doc = new ODocument().fromJSON("{'@class':'ClassA','other':'other'}");
        ODocument saved = database.save(doc);
        Assert.assertNotNull(saved.field("date"));
        Assert.assertTrue(((saved.field("date")) instanceof Date));
        Assert.assertNotNull(saved.field("other"));
    }

    @Test
    public void testDefaultValueProvidedFromJson() {
        OSchema schema = database.getMetadata().getSchema();
        OClass classA = schema.createClass("ClassA");
        OProperty prop = classA.createProperty("date", DATETIME);
        prop.setDefaultValue(ODateHelper.getDateTimeFormatInstance().format(new Date()));
        String value1 = ODateHelper.getDateTimeFormatInstance().format(new Date());
        ODocument doc = new ODocument().fromJSON((("{'@class':'ClassA','date':'" + value1) + "','other':'other'}"));
        ODocument saved = database.save(doc);
        Assert.assertNotNull(saved.field("date"));
        Assert.assertEquals(ODateHelper.getDateTimeFormatInstance().format(saved.field("date")), value1);
        Assert.assertNotNull(saved.field("other"));
    }

    @Test
    public void testDefaultValueMandatoryReadonlyFromJson() {
        OSchema schema = database.getMetadata().getSchema();
        OClass classA = schema.createClass("ClassA");
        OProperty prop = classA.createProperty("date", DATE);
        prop.setMandatory(true);
        prop.setReadonly(true);
        prop.setDefaultValue(ODateHelper.getDateTimeFormatInstance().format(new Date()));
        ODocument doc = new ODocument().fromJSON("{'@class':'ClassA','other':'other'}");
        ODocument saved = database.save(doc);
        Assert.assertNotNull(saved.field("date"));
        Assert.assertTrue(((saved.field("date")) instanceof Date));
        Assert.assertNotNull(saved.field("other"));
    }

    @Test
    public void testDefaultValueProvidedMandatoryReadonlyFromJson() {
        OSchema schema = database.getMetadata().getSchema();
        OClass classA = schema.createClass("ClassA");
        OProperty prop = classA.createProperty("date", DATETIME);
        prop.setMandatory(true);
        prop.setReadonly(true);
        prop.setDefaultValue(ODateHelper.getDateTimeFormatInstance().format(new Date()));
        String value1 = ODateHelper.getDateTimeFormatInstance().format(new Date());
        ODocument doc = new ODocument().fromJSON((("{'@class':'ClassA','date':'" + value1) + "','other':'other'}"));
        ODocument saved = database.save(doc);
        Assert.assertNotNull(saved.field("date"));
        Assert.assertEquals(ODateHelper.getDateTimeFormatInstance().format(saved.field("date")), value1);
        Assert.assertNotNull(saved.field("other"));
    }

    @Test
    public void testDefaultValueUpdateMandatoryReadonlyFromJson() {
        OSchema schema = database.getMetadata().getSchema();
        OClass classA = schema.createClass("ClassA");
        OProperty prop = classA.createProperty("date", DATETIME);
        prop.setMandatory(true);
        prop.setReadonly(true);
        prop.setDefaultValue(ODateHelper.getDateTimeFormatInstance().format(new Date()));
        ODocument doc = new ODocument().fromJSON("{'@class':'ClassA','other':'other'}");
        ODocument saved = database.save(doc);
        Assert.assertNotNull(saved.field("date"));
        Assert.assertTrue(((saved.field("date")) instanceof Date));
        Assert.assertNotNull(saved.field("other"));
        String val = ODateHelper.getDateTimeFormatInstance().format(doc.field("date"));
        ODocument doc1 = new ODocument().fromJSON((("{'@class':'ClassA','date':'" + val) + "','other':'other1'}"));
        saved.merge(doc1, true, true);
        saved = database.save(saved);
        Assert.assertNotNull(saved.field("date"));
        Assert.assertEquals(ODateHelper.getDateTimeFormatInstance().format(saved.field("date")), val);
        Assert.assertEquals(saved.field("other"), "other1");
    }
}

