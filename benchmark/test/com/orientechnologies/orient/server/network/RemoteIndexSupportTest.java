package com.orientechnologies.orient.server.network;


import OClass.INDEX_TYPE.NOTUNIQUE;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;
import java.io.IOException;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 26/04/16.
 */
public class RemoteIndexSupportTest {
    private OServer server;

    @Test
    public void testOneValueIndexInTxLookup() throws IOException {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/test");
        db.open("admin", "admin");
        OClass clazz = db.getMetadata().getSchema().createClass("TestIndex");
        clazz.createProperty("test", STRING).createIndex(NOTUNIQUE);
        db.begin();
        ODocument doc = new ODocument("TestIndex");
        doc.field("test", "testKey");
        db.save(doc);
        OIndex<Collection<OIdentifiable>> idx = ((OIndex<Collection<OIdentifiable>>) (db.getMetadata().getIndexManager().getIndex("TestIndex.test")));
        Collection<OIdentifiable> res = idx.get("testKey");
        Assert.assertEquals(1, res.size());
        db.close();
    }
}

