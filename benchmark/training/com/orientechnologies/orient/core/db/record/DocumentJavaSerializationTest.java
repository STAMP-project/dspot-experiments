package com.orientechnologies.orient.core.db.record;


import OGlobalConfiguration.DB_DOCUMENT_SERIALIZER;
import ORecordSerializerSchemaAware2CSV.NAME;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer;
import com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializerFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class DocumentJavaSerializationTest {
    private String previousSerializerConf;

    private ORecordSerializer previousSerializerInstance;

    @Test
    public void testSimpleSerialization() throws IOException, ClassNotFoundException {
        ODocument doc = new ODocument();
        doc.field("one", "one");
        doc.field("two", "two");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(doc);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        ODocument doc1 = ((ODocument) (ois.readObject()));
        Assert.assertEquals("one", doc1.field("one"));
        Assert.assertEquals("two", doc1.field("two"));
    }

    @Test
    public void testCsvBinarySerialization() throws IOException, ClassNotFoundException {
        DB_DOCUMENT_SERIALIZER.setValue(NAME);
        ODatabaseDocumentTx.setDefaultSerializer(ORecordSerializerFactory.instance().getFormat(NAME));
        ODocument doc = new ODocument();
        ORecordInternal.setRecordSerializer(doc, ORecordSerializerFactory.instance().getFormat(NAME));
        doc.field("one", "one");
        doc.field("two", "two");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(doc);
        DB_DOCUMENT_SERIALIZER.setValue(ORecordSerializerBinary.NAME);
        ODatabaseDocumentTx.setDefaultSerializer(ORecordSerializerFactory.instance().getFormat(ORecordSerializerBinary.NAME));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        ODocument doc1 = ((ODocument) (ois.readObject()));
        Assert.assertEquals("one", doc1.field("one"));
        Assert.assertEquals("two", doc1.field("two"));
    }

    @Test
    public void testBinaryCsvSerialization() throws IOException, ClassNotFoundException {
        DB_DOCUMENT_SERIALIZER.setValue(ORecordSerializerBinary.NAME);
        ODatabaseDocumentTx.setDefaultSerializer(ORecordSerializerFactory.instance().getFormat(ORecordSerializerBinary.NAME));
        ODocument doc = new ODocument();
        ORecordInternal.setRecordSerializer(doc, ORecordSerializerFactory.instance().getFormat(ORecordSerializerBinary.NAME));
        doc.field("one", "one");
        doc.field("two", "two");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(doc);
        DB_DOCUMENT_SERIALIZER.setValue(NAME);
        ODatabaseDocumentTx.setDefaultSerializer(ORecordSerializerFactory.instance().getFormat(NAME));
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        ODocument doc1 = ((ODocument) (ois.readObject()));
        Assert.assertEquals("one", doc1.field("one"));
        Assert.assertEquals("two", doc1.field("two"));
    }
}

