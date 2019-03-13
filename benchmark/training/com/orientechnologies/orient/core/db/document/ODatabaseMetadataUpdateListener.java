package com.orientechnologies.orient.core.db.document;


import OClass.INDEX_TYPE.NOTUNIQUE;
import ODatabase.ATTRIBUTES.LOCALECOUNTRY;
import OSequence.SEQUENCE_TYPE.ORDERED;
import OType.STRING;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class ODatabaseMetadataUpdateListener {
    private OrientDB orientDB;

    private ODatabaseSession session;

    private int count;

    @Test
    public void testSchemaUpdateListener() {
        session.createClass("test1");
        Assert.assertEquals(count, 1);
    }

    @Test
    public void testFunctionUpdateListener() {
        session.getMetadata().getFunctionLibrary().createFunction("some");
        Assert.assertEquals(count, 1);
    }

    @Test
    public void testSequenceUpdate() {
        try {
            session.getMetadata().getSequenceLibrary().createSequence("sequence1", ORDERED, null);
        } catch (ODatabaseException exc) {
            Assert.assertTrue("Failed to create sequence", false);
        }
        Assert.assertEquals(count, 1);
    }

    @Test
    public void testIndexUpdate() {
        session.createClass("Some").createProperty("test", STRING).createIndex(NOTUNIQUE);
        Assert.assertEquals(count, 3);
    }

    @Test
    public void testIndexConfigurationUpdate() {
        session.set(LOCALECOUNTRY, Locale.GERMAN);
        Assert.assertEquals(count, 1);
    }
}

