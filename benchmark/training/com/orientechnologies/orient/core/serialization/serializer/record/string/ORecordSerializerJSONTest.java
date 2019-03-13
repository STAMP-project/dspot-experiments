package com.orientechnologies.orient.core.serialization.serializer.record.string;


import OType.CUSTOM;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseImportTest;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 25/05/16.
 */
public class ORecordSerializerJSONTest {
    @Test
    public void testCustomSerialization() {
        ODatabaseDocument db = new ODatabaseDocumentTx(("memory:" + (ODatabaseImportTest.class.getSimpleName())));
        db.create();
        try {
            OClass klass = db.getMetadata().getSchema().createClass("TestCustom");
            klass.createProperty("test", CUSTOM);
            ODocument doc = new ODocument("TestCustom");
            doc.field("test", ORecordSerializerJSONTest.TestCustom.ONE, CUSTOM);
            String json = doc.toJSON();
            ODocument doc1 = new ODocument();
            doc1.fromJSON(json);
            Assert.assertEquals(ORecordSerializerJSONTest.TestCustom.valueOf(((String) (doc1.field("test")))), ORecordSerializerJSONTest.TestCustom.ONE);
        } finally {
            db.drop();
        }
    }

    public enum TestCustom {

        ONE,
        TWO;}
}

