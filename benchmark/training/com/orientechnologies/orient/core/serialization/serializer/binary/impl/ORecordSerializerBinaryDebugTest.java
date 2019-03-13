package com.orientechnologies.orient.core.serialization.serializer.binary.impl;


import OType.DOUBLE;
import OType.INTEGER;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.ORecordSerializationDebug;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.ORecordSerializerBinaryDebug;
import org.junit.Assert;
import org.junit.Test;


public class ORecordSerializerBinaryDebugTest {
    private ORecordSerializer previous;

    @Test
    public void testSimpleDocumentDebug() {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:" + (ORecordSerializerBinaryDebugTest.class.getSimpleName())));
        db.create();
        try {
            ODocument doc = new ODocument();
            doc.field("test", "test");
            doc.field("anInt", 2);
            doc.field("anDouble", 2.0);
            byte[] bytes = doc.toStream();
            ORecordSerializerBinaryDebug debugger = new ORecordSerializerBinaryDebug();
            ORecordSerializationDebug debug = debugger.deserializeDebug(bytes, db);
            Assert.assertEquals(debug.properties.size(), 3);
            Assert.assertEquals(debug.properties.get(0).name, "test");
            Assert.assertEquals(debug.properties.get(0).type, STRING);
            Assert.assertEquals(debug.properties.get(0).value, "test");
            Assert.assertEquals(debug.properties.get(1).name, "anInt");
            Assert.assertEquals(debug.properties.get(1).type, INTEGER);
            Assert.assertEquals(debug.properties.get(1).value, 2);
            Assert.assertEquals(debug.properties.get(2).name, "anDouble");
            Assert.assertEquals(debug.properties.get(2).type, DOUBLE);
            Assert.assertEquals(debug.properties.get(2).value, 2.0);
        } finally {
            db.drop();
        }
    }

    @Test
    public void testSchemaFullDocumentDebug() {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:" + (ORecordSerializerBinaryDebugTest.class.getSimpleName())));
        db.create();
        try {
            OClass clazz = db.getMetadata().getSchema().createClass("some");
            clazz.createProperty("testP", STRING);
            clazz.createProperty("theInt", INTEGER);
            ODocument doc = new ODocument("some");
            doc.field("testP", "test");
            doc.field("theInt", 2);
            doc.field("anDouble", 2.0);
            byte[] bytes = doc.toStream();
            ORecordSerializerBinaryDebug debugger = new ORecordSerializerBinaryDebug();
            ORecordSerializationDebug debug = debugger.deserializeDebug(bytes, db);
            Assert.assertEquals(debug.properties.size(), 3);
            Assert.assertEquals(debug.properties.get(0).name, "testP");
            Assert.assertEquals(debug.properties.get(0).type, STRING);
            Assert.assertEquals(debug.properties.get(0).value, "test");
            Assert.assertEquals(debug.properties.get(1).name, "theInt");
            Assert.assertEquals(debug.properties.get(1).type, INTEGER);
            Assert.assertEquals(debug.properties.get(1).value, 2);
            Assert.assertEquals(debug.properties.get(2).name, "anDouble");
            Assert.assertEquals(debug.properties.get(2).type, DOUBLE);
            Assert.assertEquals(debug.properties.get(2).value, 2.0);
        } finally {
            db.drop();
        }
    }

    @Test
    public void testSimpleBrokenDocumentDebug() {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:" + (ORecordSerializerBinaryDebugTest.class.getSimpleName())));
        db.create();
        try {
            ODocument doc = new ODocument();
            doc.field("test", "test");
            doc.field("anInt", 2);
            doc.field("anDouble", 2.0);
            byte[] bytes = doc.toStream();
            byte[] brokenBytes = new byte[(bytes.length) - 10];
            System.arraycopy(bytes, 0, brokenBytes, 0, ((bytes.length) - 10));
            ORecordSerializerBinaryDebug debugger = new ORecordSerializerBinaryDebug();
            ORecordSerializationDebug debug = debugger.deserializeDebug(brokenBytes, db);
            Assert.assertEquals(debug.properties.size(), 3);
            Assert.assertEquals(debug.properties.get(0).name, "test");
            Assert.assertEquals(debug.properties.get(0).type, STRING);
            Assert.assertEquals(debug.properties.get(0).faildToRead, true);
            Assert.assertNotNull(debug.properties.get(0).readingException);
            Assert.assertEquals(debug.properties.get(1).name, "anInt");
            Assert.assertEquals(debug.properties.get(1).type, INTEGER);
            Assert.assertEquals(debug.properties.get(1).faildToRead, true);
            Assert.assertNotNull(debug.properties.get(1).readingException);
            Assert.assertEquals(debug.properties.get(2).name, "anDouble");
            Assert.assertEquals(debug.properties.get(2).type, DOUBLE);
            Assert.assertEquals(debug.properties.get(2).faildToRead, true);
            Assert.assertNotNull(debug.properties.get(2).readingException);
        } finally {
            db.drop();
        }
    }

    @Test
    public void testBrokenSchemaFullDocumentDebug() {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:" + (ORecordSerializerBinaryDebugTest.class.getSimpleName())));
        db.create();
        try {
            OClass clazz = db.getMetadata().getSchema().createClass("some");
            clazz.createProperty("testP", STRING);
            clazz.createProperty("theInt", INTEGER);
            ODocument doc = new ODocument("some");
            doc.field("testP", "test");
            doc.field("theInt", 2);
            doc.field("anDouble", 2.0);
            byte[] bytes = doc.toStream();
            byte[] brokenBytes = new byte[(bytes.length) - 10];
            System.arraycopy(bytes, 0, brokenBytes, 0, ((bytes.length) - 10));
            ORecordSerializerBinaryDebug debugger = new ORecordSerializerBinaryDebug();
            ORecordSerializationDebug debug = debugger.deserializeDebug(brokenBytes, db);
            Assert.assertEquals(debug.properties.size(), 3);
            Assert.assertEquals(debug.properties.get(0).name, "testP");
            Assert.assertEquals(debug.properties.get(0).type, STRING);
            Assert.assertEquals(debug.properties.get(0).faildToRead, true);
            Assert.assertNotNull(debug.properties.get(0).readingException);
            Assert.assertEquals(debug.properties.get(1).name, "theInt");
            Assert.assertEquals(debug.properties.get(1).type, INTEGER);
            Assert.assertEquals(debug.properties.get(1).faildToRead, true);
            Assert.assertNotNull(debug.properties.get(1).readingException);
            Assert.assertEquals(debug.properties.get(2).name, "anDouble");
            Assert.assertEquals(debug.properties.get(2).type, DOUBLE);
            Assert.assertEquals(debug.properties.get(2).faildToRead, true);
            Assert.assertNotNull(debug.properties.get(2).readingException);
        } finally {
            db.drop();
        }
    }
}

