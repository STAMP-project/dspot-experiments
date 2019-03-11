package com.orientechnologies.orient.core.metadata.schema;


import OType.DOUBLE;
import OType.INTEGER;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TestMultiSuperClasses {
    private ODatabaseDocumentTx db;

    @Test
    public void testClassCreation() {
        OSchema oSchema = db.getMetadata().getSchema();
        OClass aClass = oSchema.createAbstractClass("javaA");
        OClass bClass = oSchema.createAbstractClass("javaB");
        aClass.createProperty("propertyInt", INTEGER);
        bClass.createProperty("propertyDouble", DOUBLE);
        OClass cClass = oSchema.createClass("javaC", aClass, bClass);
        testClassCreationBranch(aClass, bClass, cClass);
        oSchema.reload();
        testClassCreationBranch(aClass, bClass, cClass);
        oSchema = db.getMetadata().getImmutableSchemaSnapshot();
        aClass = oSchema.getClass("javaA");
        bClass = oSchema.getClass("javaB");
        cClass = oSchema.getClass("javaC");
        testClassCreationBranch(aClass, bClass, cClass);
    }

    @Test
    public void testSql() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass aClass = oSchema.createAbstractClass("sqlA");
        OClass bClass = oSchema.createAbstractClass("sqlB");
        OClass cClass = oSchema.createClass("sqlC");
        db.command(new OCommandSQL("alter class sqlC superclasses sqlA, sqlB")).execute();
        oSchema.reload();
        Assert.assertTrue(cClass.isSubClassOf(aClass));
        Assert.assertTrue(cClass.isSubClassOf(bClass));
        db.command(new OCommandSQL("alter class sqlC superclass sqlA")).execute();
        oSchema.reload();
        Assert.assertTrue(cClass.isSubClassOf(aClass));
        Assert.assertFalse(cClass.isSubClassOf(bClass));
        db.command(new OCommandSQL("alter class sqlC superclass +sqlB")).execute();
        oSchema.reload();
        Assert.assertTrue(cClass.isSubClassOf(aClass));
        Assert.assertTrue(cClass.isSubClassOf(bClass));
        db.command(new OCommandSQL("alter class sqlC superclass -sqlA")).execute();
        oSchema.reload();
        Assert.assertFalse(cClass.isSubClassOf(aClass));
        Assert.assertTrue(cClass.isSubClassOf(bClass));
    }

    @Test
    public void testCreationBySql() {
        final OSchema oSchema = db.getMetadata().getSchema();
        db.command(new OCommandSQL("create class sql2A abstract")).execute();
        db.command(new OCommandSQL("create class sql2B abstract")).execute();
        db.command(new OCommandSQL("create class sql2C extends sql2A, sql2B abstract")).execute();
        oSchema.reload();
        OClass aClass = oSchema.getClass("sql2A");
        OClass bClass = oSchema.getClass("sql2B");
        OClass cClass = oSchema.getClass("sql2C");
        Assert.assertNotNull(aClass);
        Assert.assertNotNull(bClass);
        Assert.assertNotNull(cClass);
        Assert.assertTrue(cClass.isSubClassOf(aClass));
        Assert.assertTrue(cClass.isSubClassOf(bClass));
    }

    // , expectedExceptionsMessageRegExp = "(?s).*recursion.*"
    // )
    @Test(expected = OSchemaException.class)
    public void testPreventionOfCycles() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass aClass = oSchema.createAbstractClass("cycleA");
        OClass bClass = oSchema.createAbstractClass("cycleB", aClass);
        OClass cClass = oSchema.createAbstractClass("cycleC", bClass);
        aClass.setSuperClasses(Arrays.asList(cClass));
    }

    @Test
    public void testParametersImpactGoodScenario() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass aClass = oSchema.createAbstractClass("impactGoodA");
        aClass.createProperty("property", STRING);
        OClass bClass = oSchema.createAbstractClass("impactGoodB");
        bClass.createProperty("property", STRING);
        OClass cClass = oSchema.createAbstractClass("impactGoodC", aClass, bClass);
        Assert.assertTrue(cClass.existsProperty("property"));
    }

    // }, expectedExceptionsMessageRegExp = "(?s).*conflict.*")
    @Test(expected = OSchemaException.class)
    public void testParametersImpactBadScenario() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass aClass = oSchema.createAbstractClass("impactBadA");
        aClass.createProperty("property", STRING);
        OClass bClass = oSchema.createAbstractClass("impactBadB");
        bClass.createProperty("property", INTEGER);
        oSchema.createAbstractClass("impactBadC", aClass, bClass);
    }

    @Test
    public void testCreationOfClassWithV() {
        final OSchema oSchema = db.getMetadata().getSchema();
        OClass oRestrictedClass = oSchema.getClass("ORestricted");
        OClass vClass = oSchema.getClass("V");
        vClass.setSuperClasses(Arrays.asList(oRestrictedClass));
        OClass dummy1Class = oSchema.createClass("Dummy1", oRestrictedClass, vClass);
        OClass dummy2Class = oSchema.createClass("Dummy2");
        OClass dummy3Class = oSchema.createClass("Dummy3", dummy1Class, dummy2Class);
        Assert.assertNotNull(dummy3Class);
    }
}

