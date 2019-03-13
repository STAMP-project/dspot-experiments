package com.orientechnologies.orient.core.metadata.schema;


import OType.DATE;
import OType.EMBEDDEDLIST;
import OType.LINK;
import OType.LINKMAP;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.junit.Assert;
import org.junit.Test;


public class AlterPropertyTest {
    private ODatabaseDocument db;

    @Test
    public void testPropertyRenaming() {
        OSchema schema = db.getMetadata().getSchema();
        OClass classA = schema.createClass("TestPropertyRenaming");
        OProperty property = classA.createProperty("propertyOld", STRING);
        Assert.assertEquals(property, classA.getProperty("propertyOld"));
        Assert.assertNull(classA.getProperty("propertyNew"));
        property.setName("propertyNew");
        Assert.assertNull(classA.getProperty("propertyOld"));
        Assert.assertEquals(property, classA.getProperty("propertyNew"));
    }

    @Test
    public void testPropertyRenamingReload() {
        OSchema schema = db.getMetadata().getSchema();
        OClass classA = schema.createClass("TestPropertyRenaming");
        OProperty property = classA.createProperty("propertyOld", STRING);
        Assert.assertEquals(property, classA.getProperty("propertyOld"));
        Assert.assertNull(classA.getProperty("propertyNew"));
        property.setName("propertyNew");
        schema.reload();
        classA = schema.getClass("TestPropertyRenaming");
        Assert.assertNull(classA.getProperty("propertyOld"));
        Assert.assertEquals(property, classA.getProperty("propertyNew"));
    }

    @Test
    public void testLinkedMapPropertyLinkedType() {
        OSchema schema = db.getMetadata().getSchema();
        OClass classA = schema.createClass("TestMapProperty");
        try {
            classA.createProperty("propertyMap", LINKMAP, STRING);
            Assert.fail("create linkmap property should not allow linked type");
        } catch (OSchemaException e) {
        }
        OProperty prop = classA.getProperty("propertyMap");
        Assert.assertNull(prop);
    }

    @Test
    public void testLinkedMapPropertyLinkedClass() {
        OSchema schema = db.getMetadata().getSchema();
        OClass classA = schema.createClass("TestMapProperty");
        OClass classLinked = schema.createClass("LinkedClass");
        try {
            classA.createProperty("propertyString", STRING, classLinked);
            Assert.fail("create linkmap property should not allow linked type");
        } catch (OSchemaException e) {
        }
        OProperty prop = classA.getProperty("propertyString");
        Assert.assertNull(prop);
    }

    @Test
    public void testRemoveLinkedClass() {
        OSchema schema = db.getMetadata().getSchema();
        OClass classA = schema.createClass("TestRemoveLinkedClass");
        OClass classLinked = schema.createClass("LinkedClass");
        OProperty prop = classA.createProperty("propertyLink", LINK, classLinked);
        Assert.assertNotNull(prop.getLinkedClass());
        prop.setLinkedClass(null);
        Assert.assertNull(prop.getLinkedClass());
    }

    @Test
    public void testRemoveLinkedClassSQL() {
        OSchema schema = db.getMetadata().getSchema();
        OClass classA = schema.createClass("TestRemoveLinkedClass");
        OClass classLinked = schema.createClass("LinkedClass");
        OProperty prop = classA.createProperty("propertyLink", LINK, classLinked);
        Assert.assertNotNull(prop.getLinkedClass());
        db.command(new OCommandSQL("alter property TestRemoveLinkedClass.propertyLink linkedclass null")).execute();
        Assert.assertNull(prop.getLinkedClass());
    }

    @Test
    public void testMax() {
        OSchema schema = db.getMetadata().getSchema();
        OClass classA = schema.createClass("TestWrongMax");
        OProperty prop = classA.createProperty("dates", EMBEDDEDLIST, DATE);
        db.command(new OCommandSQL("alter property TestWrongMax.dates max 2016-05-25")).execute();
        try {
            db.command(new OCommandSQL("alter property TestWrongMax.dates max '2016-05-25'")).execute();
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testAlterPropertyWithDot() {
        OSchema schema = db.getMetadata().getSchema();
        db.command(new OCommandSQL("create class testAlterPropertyWithDot")).execute();
        db.command(new OCommandSQL("create property testAlterPropertyWithDot.`a.b` STRING")).execute();
        schema.reload();
        Assert.assertNotNull(schema.getClass("testAlterPropertyWithDot").getProperty("a.b"));
        db.command(new OCommandSQL("alter property testAlterPropertyWithDot.`a.b` name c")).execute();
        schema.reload();
        Assert.assertNull(schema.getClass("testAlterPropertyWithDot").getProperty("a.b"));
        Assert.assertNotNull(schema.getClass("testAlterPropertyWithDot").getProperty("c"));
    }

    @Test
    public void testAlterCustomAttributeInProperty() {
        OSchema schema = db.getMetadata().getSchema();
        OClass oClass = schema.createClass("TestCreateCustomAttributeClass");
        OProperty property = oClass.createProperty("property", STRING);
        property.setCustom("customAttribute", "value1");
        Assert.assertEquals("value1", property.getCustom("customAttribute"));
        property.setCustom("custom.attribute", "value2");
        Assert.assertEquals("value2", property.getCustom("custom.attribute"));
    }
}

