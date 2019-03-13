package com.orientechnologies.orient.server;


import com.orientechnologies.orient.client.remote.OBinaryResponse;
import com.orientechnologies.orient.client.remote.message.OBatchOperationsRequest;
import com.orientechnologies.orient.client.remote.message.OBatchOperationsResponse;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.server.network.protocol.ONetworkProtocolData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 * Created by tglman on 29/12/16.
 */
public class OConnetionExecutorTest {
    @Mock
    private OServer server;

    @Mock
    private OClientConnection connection;

    @Mock
    private ONetworkProtocolData data;

    private OrientDB orientDb;

    private ODatabaseDocumentInternal database;

    @Test
    public void testBatchOperationsNoTX() {
        ODocument toUpdate = database.save(new ODocument("test").field("name", "foo"));
        ODocument toDelete = database.save(new ODocument("test").field("name", "delete"));
        OConnectionBinaryExecutor executor = new OConnectionBinaryExecutor(connection, server);
        List<ORecordOperation> operations = new ArrayList<>();
        ODocument toInsert = new ODocument("test").field("name", "insert");
        toUpdate.field("name", "update");
        operations.add(new ORecordOperation(toInsert, ORecordOperation.CREATED));
        operations.add(new ORecordOperation(toDelete, ORecordOperation.DELETED));
        operations.add(new ORecordOperation(toUpdate, ORecordOperation.UPDATED));
        OBatchOperationsRequest batchRequest = new OBatchOperationsRequest(10, operations);
        OBinaryResponse batchResponse = batchRequest.execute(executor);
        Assert.assertTrue((batchResponse instanceof OBatchOperationsResponse));
        Assert.assertFalse(database.getTransaction().isActive());
        Assert.assertEquals(1, getCreated().size());
        Assert.assertEquals(1, getUpdated().size());
        Assert.assertEquals(2, database.countClass("test"));
        OResultSet query = database.query("select from test where name = 'update'");
        List<OResult> results = query.stream().collect(Collectors.toList());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("update", results.get(0).getProperty("name"));
        query.close();
    }
}

