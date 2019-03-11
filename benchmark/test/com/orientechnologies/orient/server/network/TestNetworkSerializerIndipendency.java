package com.orientechnologies.orient.server.network;


import ORecordSerializerSchemaAware2CSV.INSTANCE;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OStorageException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.record.ORecordSerializer;
import com.orientechnologies.orient.server.OServer;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestNetworkSerializerIndipendency {
    private OServer server;

    @Test(expected = OStorageException.class)
    public void createCsvDatabaseConnectBinary() throws IOException {
        ORecordSerializer prev = ODatabaseDocumentTx.getDefaultSerializer();
        ODatabaseDocumentTx.setDefaultSerializer(INSTANCE);
        createDatabase();
        ODatabaseDocumentTx dbTx = null;
        try {
            ODatabaseDocumentTx.setDefaultSerializer(ORecordSerializerBinary.INSTANCE);
            dbTx = new ODatabaseDocumentTx("remote:localhost/test");
            dbTx.open("admin", "admin");
            ODocument document = new ODocument();
            document.field("name", "something");
            document.field("surname", "something-else");
            document = dbTx.save(document, dbTx.getClusterNameById(dbTx.getDefaultClusterId()));
            dbTx.commit();
            ODocument doc = dbTx.load(document.getIdentity());
            Assert.assertEquals(doc.fields(), document.fields());
            Assert.assertEquals(doc.<Object>field("name"), document.field("name"));
            Assert.assertEquals(doc.<Object>field("surname"), document.field("surname"));
        } finally {
            if ((dbTx != null) && (!(dbTx.isClosed()))) {
                dbTx.close();
                dbTx.getStorage().close();
            }
            dropDatabase();
            ODatabaseDocumentTx.setDefaultSerializer(prev);
        }
    }

    @Test
    public void createBinaryDatabaseConnectCsv() throws IOException {
        ORecordSerializer prev = ODatabaseDocumentTx.getDefaultSerializer();
        ODatabaseDocumentTx.setDefaultSerializer(ORecordSerializerBinary.INSTANCE);
        createDatabase();
        ODatabaseDocumentTx dbTx = null;
        try {
            ODatabaseDocumentTx.setDefaultSerializer(INSTANCE);
            dbTx = new ODatabaseDocumentTx("remote:localhost/test");
            dbTx.open("admin", "admin");
            ODocument document = new ODocument();
            document.field("name", "something");
            document.field("surname", "something-else");
            document = dbTx.save(document, dbTx.getClusterNameById(dbTx.getDefaultClusterId()));
            dbTx.commit();
            ODocument doc = dbTx.load(document.getIdentity());
            Assert.assertEquals(doc.fields(), document.fields());
            Assert.assertEquals(doc.<Object>field("name"), document.field("name"));
            Assert.assertEquals(doc.<Object>field("surname"), document.field("surname"));
        } finally {
            if (dbTx != null) {
                dbTx.close();
                dbTx.getStorage().close();
            }
            dropDatabase();
            ODatabaseDocumentTx.setDefaultSerializer(prev);
        }
    }
}

