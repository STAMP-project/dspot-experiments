package com.orientechnologies.orient.server.tx;


import OTransaction.TXTYPE.OPTIMISTIC;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.orientechnologies.orient.server.OClientConnection;
import com.orientechnologies.orient.server.OServer;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 03/01/17.
 */
public class RemoteTransactionSupportTest {
    private static final String CLASS_1 = "SomeClass";

    private static final String CLASS_2 = "AnotherClass";

    private static final String EDGE = "SomeEdge";

    private static final String FIELD_VALUE = "VALUE";

    private static final String SERVER_DIRECTORY = "./target/transaction";

    private OServer server;

    private OrientDB orientDB;

    private ODatabaseDocument database;

    @Test
    public void testQueryUpdateUpdatedInTxTransaction() {
        ODocument doc = new ODocument("SomeTx");
        doc.setProperty("name", "Joe");
        OIdentifiable id = database.save(doc);
        database.begin();
        ODocument doc2 = database.load(id.getIdentity());
        doc2.setProperty("name", "Jane");
        database.save(doc2);
        OResultSet result = database.command("update SomeTx set name='July' where name = 'Jane' ");
        Assert.assertEquals(((long) (result.next().getProperty("count"))), 1L);
        ODocument doc3 = database.load(id.getIdentity());
        Assert.assertEquals(doc3.getProperty("name"), "July");
    }

    @Test
    public void testResetUpdatedInTxTransaction() {
        database.begin();
        ODocument doc1 = new ODocument();
        doc1.setProperty("name", "Jane");
        database.save(doc1);
        ODocument doc2 = new ODocument("SomeTx");
        doc2.setProperty("name", "Jane");
        database.save(doc2);
        OResultSet result = database.command("update SomeTx set name='July' where name = 'Jane' ");
        Assert.assertEquals(((long) (result.next().getProperty("count"))), 1L);
        Assert.assertEquals(doc2.getProperty("name"), "July");
        result.close();
    }

    @Test
    public void testQueryUpdateCreatedInTxTransaction() throws InterruptedException {
        database.begin();
        ODocument doc1 = new ODocument("SomeTx");
        doc1.setProperty("name", "Jane");
        OIdentifiable id = database.save(doc1);
        ODocument docx = new ODocument("SomeTx2");
        docx.setProperty("name", "Jane");
        database.save(docx);
        OResultSet result = database.command("update SomeTx set name='July' where name = 'Jane' ");
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals(((long) (result.next().getProperty("count"))), 1L);
        ODocument doc2 = database.load(id.getIdentity());
        Assert.assertEquals(doc2.getProperty("name"), "July");
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testRollbackTxTransaction() {
        ODocument doc = new ODocument("SomeTx");
        doc.setProperty("name", "Jane");
        database.save(doc);
        database.begin();
        ODocument doc1 = new ODocument("SomeTx");
        doc1.setProperty("name", "Jane");
        database.save(doc1);
        OResultSet result = database.command("update SomeTx set name='July' where name = 'Jane' ");
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals(((long) (result.next().getProperty("count"))), 2L);
        result.close();
        database.rollback();
        OResultSet result1 = database.command("select count(*) from SomeTx where name='Jane'");
        Assert.assertTrue(result1.hasNext());
        Assert.assertEquals(((long) (result1.next().getProperty("count(*)"))), 1L);
        result1.close();
    }

    @Test
    public void testRollbackTxCheckStatusTransaction() {
        ODocument doc = new ODocument("SomeTx");
        doc.setProperty("name", "Jane");
        database.save(doc);
        database.begin();
        ODocument doc1 = new ODocument("SomeTx");
        doc1.setProperty("name", "Jane");
        database.save(doc1);
        OResultSet result = database.command("select count(*) from SomeTx where name='Jane' ");
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals(((long) (result.next().getProperty("count(*)"))), 2L);
        Assert.assertTrue(database.getTransaction().isActive());
        result.close();
        database.rollback();
        OResultSet result1 = database.command("select count(*) from SomeTx where name='Jane'");
        Assert.assertTrue(result1.hasNext());
        Assert.assertEquals(((long) (result1.next().getProperty("count(*)"))), 1L);
        Assert.assertFalse(database.getTransaction().isActive());
        result1.close();
    }

    @Test
    public void testDownloadTransactionAtStart() {
        database.begin();
        database.command("insert into SomeTx set name ='Jane' ").close();
        Assert.assertEquals(database.getTransaction().getEntryCount(), 1);
    }

    @Test
    public void testQueryUpdateCreatedInTxSQLTransaction() {
        database.begin();
        database.command("insert into SomeTx set name ='Jane' ").close();
        OResultSet result = database.command("update SomeTx set name='July' where name = 'Jane' ");
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals(((long) (result.next().getProperty("count"))), 1L);
        result.close();
        OResultSet result1 = database.query("select from SomeTx where name='July'");
        Assert.assertTrue(result1.hasNext());
        Assert.assertEquals(result1.next().getProperty("name"), "July");
        Assert.assertFalse(result.hasNext());
        result1.close();
    }

    @Test
    public void testQueryDeleteTxSQLTransaction() {
        OElement someTx = database.newElement("SomeTx");
        someTx.setProperty("name", "foo");
        someTx.save();
        database.begin();
        database.command("delete from SomeTx");
        database.commit();
        OResultSet result = database.command("select from SomeTx");
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testDoubleSaveTransaction() {
        database.begin();
        OElement someTx = database.newElement("SomeTx");
        someTx.setProperty("name", "foo");
        database.save(someTx);
        database.save(someTx);
        Assert.assertEquals(database.getTransaction().getEntryCount(), 1);
        Assert.assertEquals(database.countClass("SomeTx"), 1);
        database.commit();
        Assert.assertEquals(database.countClass("SomeTx"), 1);
    }

    @Test
    public void testDoubleSaveDoubleFlushTransaction() {
        database.begin();
        OElement someTx = database.newElement("SomeTx");
        someTx.setProperty("name", "foo");
        database.save(someTx);
        database.save(someTx);
        OResultSet result = database.query("select from SomeTx");
        Assert.assertEquals(1, result.stream().count());
        result.close();
        database.save(someTx);
        database.save(someTx);
        result = database.query("select from SomeTx");
        Assert.assertEquals(1, result.stream().count());
        result.close();
        Assert.assertEquals(database.getTransaction().getEntryCount(), 1);
        Assert.assertEquals(database.countClass("SomeTx"), 1);
        database.commit();
        Assert.assertEquals(database.countClass("SomeTx"), 1);
    }

    @Test
    public void testRefFlushedInTransaction() {
        database.begin();
        OElement someTx = database.newElement("SomeTx");
        someTx.setProperty("name", "foo");
        database.save(someTx);
        OElement oneMore = database.newElement("SomeTx");
        oneMore.setProperty("name", "bar");
        oneMore.setProperty("ref", someTx);
        OResultSet result = database.query("select from SomeTx");
        Assert.assertEquals(1, result.stream().count());
        result.close();
        database.save(oneMore);
        database.commit();
        OResultSet result1 = database.query("select ref from SomeTx where name='bar'");
        Assert.assertTrue(result1.hasNext());
        Assert.assertEquals(someTx.getIdentity(), result1.next().getProperty("ref"));
        result1.close();
    }

    @Test
    public void testDoubleRefFlushedInTransaction() {
        database.begin();
        OElement someTx = database.newElement("SomeTx");
        someTx.setProperty("name", "foo");
        database.save(someTx);
        OElement oneMore = database.newElement("SomeTx");
        oneMore.setProperty("name", "bar");
        oneMore.setProperty("ref", someTx.getIdentity());
        OResultSet result = database.query("select from SomeTx");
        Assert.assertEquals(1, result.stream().count());
        result.close();
        OElement ref2 = database.newElement("SomeTx");
        ref2.setProperty("name", "other");
        database.save(ref2);
        oneMore.setProperty("ref2", ref2.getIdentity());
        result = database.query("select from SomeTx");
        Assert.assertEquals(2, result.stream().count());
        result.close();
        database.save(oneMore);
        OResultSet result1 = database.query("select ref,ref2 from SomeTx where name='bar'");
        Assert.assertTrue(result1.hasNext());
        OResult next = result1.next();
        Assert.assertEquals(someTx.getIdentity(), next.getProperty("ref"));
        Assert.assertEquals(ref2.getIdentity(), next.getProperty("ref2"));
        result1.close();
        database.commit();
        result1 = database.query("select ref,ref2 from SomeTx where name='bar'");
        Assert.assertTrue(result1.hasNext());
        next = result1.next();
        Assert.assertEquals(someTx.getIdentity(), next.getProperty("ref"));
        Assert.assertEquals(ref2.getIdentity(), next.getProperty("ref2"));
        result1.close();
    }

    @Test
    public void testUpdateCreatedInTxIndexGetTransaction() {
        OIndex<?> index = database.getClass("IndexedTx").getProperty("name").getAllIndexes().iterator().next();
        database.begin();
        ODocument doc1 = new ODocument("IndexedTx");
        doc1.setProperty("name", "Jane");
        database.save(doc1);
        OResultSet result = database.command("update IndexedTx set name='July' where name = 'Jane' ");
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals(((long) (result.next().getProperty("count"))), 1L);
        Collection<OIdentifiable> entry = ((Collection<OIdentifiable>) (index.get("July")));
        Assert.assertEquals(entry.size(), 1);
        result.close();
        database.commit();
        entry = ((Collection<OIdentifiable>) (index.get("July")));
        Assert.assertEquals(entry.size(), 1);
    }

    @Test
    public void testGenerateIdCounterTransaction() {
        database.begin();
        ODocument doc = new ODocument("SomeTx");
        doc.setProperty("name", "Jane");
        database.save(doc);
        database.command("insert into SomeTx set name ='Jane1' ").close();
        database.command("insert into SomeTx set name ='Jane2' ").close();
        ODocument doc1 = new ODocument("SomeTx");
        doc1.setProperty("name", "Jane3");
        database.save(doc1);
        doc1 = new ODocument("SomeTx");
        doc1.setProperty("name", "Jane4");
        database.save(doc1);
        database.command("insert into SomeTx set name ='Jane2' ").close();
        OResultSet result = database.command("select count(*) from SomeTx");
        System.out.println(result.getExecutionPlan().toString());
        Assert.assertTrue(result.hasNext());
        Assert.assertEquals(((long) (result.next().getProperty("count(*)"))), 6L);
        result.close();
        Assert.assertTrue(database.getTransaction().isActive());
        database.commit();
        OResultSet result1 = database.command("select count(*) from SomeTx ");
        Assert.assertTrue(result1.hasNext());
        Assert.assertEquals(((long) (result1.next().getProperty("count(*)"))), 6L);
        result1.close();
        Assert.assertFalse(database.getTransaction().isActive());
    }

    @Test
    public void testGraphInTx() {
        database.createVertexClass("MyV");
        database.createEdgeClass("MyE");
        database.begin();
        OVertex v1 = database.newVertex("MyV");
        OVertex v2 = database.newVertex("MyV");
        OEdge edge = v1.addEdge(v2, "MyE");
        edge.setProperty("some", "value");
        database.save(v1);
        OResultSet result1 = database.query("select out_MyE from MyV  where out_MyE is not null");
        Assert.assertTrue(result1.hasNext());
        ArrayList<Object> val = new ArrayList<>();
        val.add(edge.getIdentity());
        Assert.assertEquals(result1.next().getProperty("out_MyE"), val);
        result1.close();
    }

    @Test
    public void testRidbagsTx() {
        database.begin();
        OElement v1 = database.newElement("SomeTx");
        OElement v2 = database.newElement("SomeTx");
        database.save(v2);
        ORidBag ridbag = new ORidBag();
        ridbag.add(v2.getIdentity());
        v1.setProperty("rids", ridbag);
        database.save(v1);
        OResultSet result1 = database.query("select rids from SomeTx where rids is not null");
        Assert.assertTrue(result1.hasNext());
        OElement v3 = database.newElement("SomeTx");
        database.save(v3);
        ArrayList<Object> val = new ArrayList<>();
        val.add(v2.getIdentity());
        Assert.assertEquals(result1.next().getProperty("rids"), val);
        result1.close();
        result1 = database.query("select rids from SomeTx where rids is not null");
        Assert.assertTrue(result1.hasNext());
        Assert.assertEquals(result1.next().getProperty("rids"), val);
        result1.close();
    }

    @Test
    public void testProperIndexingOnDoubleInternalBegin() {
        database.begin(OPTIMISTIC);
        OElement idx = database.newElement("IndexedTx");
        idx.setProperty("name", RemoteTransactionSupportTest.FIELD_VALUE);
        database.save(idx);
        OElement someTx = database.newElement("SomeTx");
        someTx.setProperty("name", "foo");
        ORecord id = database.save(someTx);
        try (OResultSet rs = database.query("select from ?", id)) {
        }
        database.commit();
        // nothing is found (unexpected behaviour)
        try (OResultSet rs = database.query("select * from IndexedTx where name = ?", RemoteTransactionSupportTest.FIELD_VALUE)) {
            Assert.assertEquals(rs.stream().count(), 1);
        }
    }

    @Test(expected = ORecordDuplicatedException.class)
    public void testDuplicateIndexTx() {
        database.begin();
        OElement v1 = database.newElement("UniqueIndexedTx");
        v1.setProperty("name", "a");
        database.save(v1);
        OElement v2 = database.newElement("UniqueIndexedTx");
        v2.setProperty("name", "a");
        database.save(v2);
        database.commit();
    }

    @Test
    public void testKilledSession() {
        database.begin();
        OElement v2 = database.newElement("SomeTx");
        v2.setProperty("name", "a");
        database.save(v2);
        OResultSet result1 = database.query("select rids from SomeTx ");
        Assert.assertTrue(result1.hasNext());
        result1.close();
        for (OClientConnection conn : server.getClientConnectionManager().getConnections()) {
            conn.close();
        }
        database.activateOnCurrentThread();
        database.commit();
        result1 = database.query("select rids from SomeTx ");
        Assert.assertTrue(result1.hasNext());
        result1.close();
    }
}

