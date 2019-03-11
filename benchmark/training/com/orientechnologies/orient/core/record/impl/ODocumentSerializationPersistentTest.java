package com.orientechnologies.orient.core.record.impl;


import OType.EMBEDDEDLIST;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that {@link ODocument} is serializable.
 *
 * @author Artem Orobets (enisher-at-gmail.com)
 * @since 12/20/12
 */
public class ODocumentSerializationPersistentTest {
    private static ODatabaseDocumentTx db;

    private static ORID docId;

    private static ORID linkedId;

    @Test
    public void testSerialization() throws Exception {
        final ODocument doc = ODocumentSerializationPersistentTest.db.load(ODocumentSerializationPersistentTest.docId);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
        out.writeObject(doc);
        ODocumentSerializationPersistentTest.db.close();
        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        final ODocument loadedDoc = ((ODocument) (in.readObject()));
        Assert.assertEquals(loadedDoc.getIdentity(), ODocumentSerializationPersistentTest.docId);
        Assert.assertEquals(loadedDoc.getVersion(), doc.getVersion());
        Assert.assertEquals(loadedDoc.field("name"), "Artem");
        Assert.assertEquals(loadedDoc.field("country"), ODocumentSerializationPersistentTest.linkedId);
        final List<Integer> numbers = loadedDoc.field("numbers");
        for (int i = 0; i < (numbers.size()); i++) {
            Assert.assertEquals(((int) (numbers.get(i))), i);
        }
    }

    @Test
    public void testRidBagInEmbeddedDocument() {
        ODatabaseRecordThreadLocal.instance().set(ODocumentSerializationPersistentTest.db);
        ODocument doc = new ODocument();
        ORidBag rids = new ORidBag();
        rids.add(new ORecordId(2, 3));
        rids.add(new ORecordId(2, 4));
        rids.add(new ORecordId(2, 5));
        rids.add(new ORecordId(2, 6));
        List<ODocument> docs = new ArrayList<ODocument>();
        ODocument doc1 = new ODocument();
        doc1.field("rids", rids);
        docs.add(doc1);
        ODocument doc2 = new ODocument();
        doc2.field("text", "text");
        docs.add(doc2);
        doc.field("emb", docs, EMBEDDEDLIST);
        doc.field("some", "test");
        byte[] res = ODocumentSerializationPersistentTest.db.getSerializer().toStream(doc, false);
        ODocument extr = ((ODocument) (ODocumentSerializationPersistentTest.db.getSerializer().fromStream(res, new ODocument(), new String[]{  })));
        List<ODocument> emb = extr.field("emb");
        Assert.assertNotNull(emb);
        Assert.assertEquals(size(), rids.size());
        Assert.assertEquals(emb.get(1).<String>field("text"), doc2.field("text"));
        Assert.assertEquals(extr.<String>field("name"), doc.field("name"));
    }
}

