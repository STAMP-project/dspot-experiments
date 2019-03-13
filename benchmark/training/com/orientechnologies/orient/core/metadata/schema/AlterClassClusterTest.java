package com.orientechnologies.orient.core.metadata.schema;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.exception.OSchemaException;
import org.junit.Assert;
import org.junit.Test;


public class AlterClassClusterTest {
    private ODatabaseDocument db;

    @Test
    public void testRemoveClusterDefaultCluster() {
        OClass clazz = db.getMetadata().getSchema().createClass("Test", 1, null);
        clazz.addCluster("TestOneMore");
        clazz.removeClusterId(db.getClusterIdByName("Test"));
        db.getMetadata().getSchema().reload();
        clazz = db.getMetadata().getSchema().getClass("Test");
        Assert.assertEquals(clazz.getDefaultClusterId(), db.getClusterIdByName("TestOneMore"));
    }

    @Test(expected = ODatabaseException.class)
    public void testRemoveLastClassCluster() {
        OClass clazz = db.getMetadata().getSchema().createClass("Test", 1, null);
        clazz.removeClusterId(db.getClusterIdByName("Test"));
    }

    @Test(expected = OSchemaException.class)
    public void testAddClusterToAbstracClass() {
        OClass clazz = db.getMetadata().getSchema().createAbstractClass("Test");
        clazz.addCluster("TestOneMore");
    }

    @Test(expected = OSchemaException.class)
    public void testAddClusterIdToAbstracClass() {
        OClass clazz = db.getMetadata().getSchema().createAbstractClass("Test");
        int id = db.addCluster("TestOneMore");
        clazz.addClusterId(id);
    }

    @Test
    public void testSetAbstractRestrictedClass() {
        OSchema oSchema = db.getMetadata().getSchema();
        OClass oRestricted = oSchema.getClass("ORestricted");
        OClass v = oSchema.getClass("V");
        v.addSuperClass(oRestricted);
        OClass ovt = oSchema.createClass("Some", v);
        ovt.setAbstract(true);
        Assert.assertTrue(ovt.isAbstract());
    }
}

