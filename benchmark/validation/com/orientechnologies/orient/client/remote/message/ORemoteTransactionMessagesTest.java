package com.orientechnologies.orient.client.remote.message;


import OCommit37Response.OCreatedRecordResponse;
import OCommit37Response.ODeletedRecordResponse;
import OCommit37Response.OUpdatedRecordResponse;
import OPERATION.PUT;
import OPERATION.REMOVE;
import ORecordOperation.CREATED;
import ORecordOperation.DELETED;
import ORecordOperation.UPDATED;
import ORecordSerializerNetworkFactory.INSTANCE;
import com.orientechnologies.common.comparator.ODefaultComparator;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.index.sbtreebonsai.local.OBonsaiBucketPointer;
import com.orientechnologies.orient.core.storage.ridbag.sbtree.OBonsaiCollectionPointer;
import com.orientechnologies.orient.core.tx.OTransactionIndexChanges;
import com.orientechnologies.orient.core.tx.OTransactionIndexChangesPerKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class ORemoteTransactionMessagesTest {
    @Test
    public void testBeginTransactionEmptyWriteRead() throws IOException {
        MockChannel channel = new MockChannel();
        OBeginTransactionRequest request = new OBeginTransactionRequest(0, false, true, null, null);
        request.write(channel, null);
        channel.close();
        OBeginTransactionRequest readRequest = new OBeginTransactionRequest();
        readRequest.read(channel, 0, null);
        Assert.assertFalse(readRequest.isHasContent());
    }

    @Test
    public void testBeginTransactionWriteRead() throws IOException {
        List<ORecordOperation> operations = new ArrayList<>();
        operations.add(new ORecordOperation(new ODocument(), ORecordOperation.CREATED));
        Map<String, OTransactionIndexChanges> changes = new HashMap<>();
        OTransactionIndexChanges change = new OTransactionIndexChanges();
        change.cleared = false;
        change.changesPerKey = new java.util.TreeMap(ODefaultComparator.INSTANCE);
        OTransactionIndexChangesPerKey keyChange = new OTransactionIndexChangesPerKey("key");
        keyChange.add(new ORecordId(1, 2), PUT);
        keyChange.add(new ORecordId(2, 2), REMOVE);
        change.changesPerKey.put(keyChange.key, keyChange);
        changes.put("some", change);
        MockChannel channel = new MockChannel();
        OBeginTransactionRequest request = new OBeginTransactionRequest(0, true, true, operations, changes);
        request.write(channel, null);
        channel.close();
        OBeginTransactionRequest readRequest = new OBeginTransactionRequest();
        readRequest.read(channel, 0, INSTANCE.current());
        Assert.assertTrue(readRequest.isUsingLog());
        Assert.assertEquals(readRequest.getOperations().size(), 1);
        Assert.assertEquals(readRequest.getTxId(), 0);
        Assert.assertEquals(readRequest.getIndexChanges().size(), 1);
        Assert.assertEquals(readRequest.getIndexChanges().get(0).getName(), "some");
        OTransactionIndexChanges val = readRequest.getIndexChanges().get(0).getKeyChanges();
        Assert.assertEquals(val.cleared, false);
        Assert.assertEquals(val.changesPerKey.size(), 1);
        OTransactionIndexChangesPerKey entryChange = val.changesPerKey.firstEntry().getValue();
        Assert.assertEquals(entryChange.key, "key");
        Assert.assertEquals(entryChange.entries.size(), 2);
        Assert.assertEquals(entryChange.entries.get(0).value, new ORecordId(1, 2));
        Assert.assertEquals(entryChange.entries.get(0).operation, PUT);
        Assert.assertEquals(entryChange.entries.get(1).value, new ORecordId(2, 2));
        Assert.assertEquals(entryChange.entries.get(1).operation, REMOVE);
    }

    @Test
    public void testFullCommitTransactionWriteRead() throws IOException {
        List<ORecordOperation> operations = new ArrayList<>();
        operations.add(new ORecordOperation(new ODocument(), ORecordOperation.CREATED));
        Map<String, OTransactionIndexChanges> changes = new HashMap<>();
        OTransactionIndexChanges change = new OTransactionIndexChanges();
        change.cleared = false;
        change.changesPerKey = new java.util.TreeMap(ODefaultComparator.INSTANCE);
        OTransactionIndexChangesPerKey keyChange = new OTransactionIndexChangesPerKey("key");
        keyChange.add(new ORecordId(1, 2), PUT);
        keyChange.add(new ORecordId(2, 2), REMOVE);
        change.changesPerKey.put(keyChange.key, keyChange);
        changes.put("some", change);
        MockChannel channel = new MockChannel();
        OCommit37Request request = new OCommit37Request(0, true, true, operations, changes);
        request.write(channel, null);
        channel.close();
        OCommit37Request readRequest = new OCommit37Request();
        readRequest.read(channel, 0, INSTANCE.current());
        Assert.assertTrue(readRequest.isUsingLog());
        Assert.assertEquals(readRequest.getOperations().size(), 1);
        Assert.assertEquals(readRequest.getTxId(), 0);
        Assert.assertEquals(readRequest.getIndexChanges().size(), 1);
        Assert.assertEquals(readRequest.getIndexChanges().get(0).getName(), "some");
        OTransactionIndexChanges val = readRequest.getIndexChanges().get(0).getKeyChanges();
        Assert.assertEquals(val.cleared, false);
        Assert.assertEquals(val.changesPerKey.size(), 1);
        OTransactionIndexChangesPerKey entryChange = val.changesPerKey.firstEntry().getValue();
        Assert.assertEquals(entryChange.key, "key");
        Assert.assertEquals(entryChange.entries.size(), 2);
        Assert.assertEquals(entryChange.entries.get(0).value, new ORecordId(1, 2));
        Assert.assertEquals(entryChange.entries.get(0).operation, PUT);
        Assert.assertEquals(entryChange.entries.get(1).value, new ORecordId(2, 2));
        Assert.assertEquals(entryChange.entries.get(1).operation, REMOVE);
    }

    @Test
    public void testCommitResponseTransactionWriteRead() throws IOException {
        MockChannel channel = new MockChannel();
        List<OCommit37Response.OCreatedRecordResponse> creates = new ArrayList<>();
        creates.add(new OCommit37Response.OCreatedRecordResponse(new ORecordId(1, 2), new ORecordId((-1), (-2)), 10));
        creates.add(new OCommit37Response.OCreatedRecordResponse(new ORecordId(1, 3), new ORecordId((-1), (-3)), 20));
        List<OCommit37Response.OUpdatedRecordResponse> updates = new ArrayList<>();
        updates.add(new OCommit37Response.OUpdatedRecordResponse(new ORecordId(10, 20), 3));
        updates.add(new OCommit37Response.OUpdatedRecordResponse(new ORecordId(10, 21), 4));
        List<OCommit37Response.ODeletedRecordResponse> deletes = new ArrayList<>();
        deletes.add(new OCommit37Response.ODeletedRecordResponse(new ORecordId(10, 50)));
        deletes.add(new OCommit37Response.ODeletedRecordResponse(new ORecordId(10, 51)));
        Map<UUID, OBonsaiCollectionPointer> changes = new HashMap<>();
        UUID val = UUID.randomUUID();
        changes.put(val, new OBonsaiCollectionPointer(10, new OBonsaiBucketPointer(30, 40)));
        OCommit37Response response = new OCommit37Response(creates, updates, deletes, changes);
        response.write(channel, 0, null);
        channel.close();
        OCommit37Response readResponse = new OCommit37Response();
        readResponse.read(channel, null);
        Assert.assertEquals(readResponse.getCreated().size(), 2);
        Assert.assertEquals(readResponse.getCreated().get(0).getCurrentRid(), new ORecordId(1, 2));
        Assert.assertEquals(readResponse.getCreated().get(0).getCreatedRid(), new ORecordId((-1), (-2)));
        Assert.assertEquals(readResponse.getCreated().get(0).getVersion(), 10);
        Assert.assertEquals(readResponse.getCreated().get(1).getCurrentRid(), new ORecordId(1, 3));
        Assert.assertEquals(readResponse.getCreated().get(1).getCreatedRid(), new ORecordId((-1), (-3)));
        Assert.assertEquals(readResponse.getCreated().get(1).getVersion(), 20);
        Assert.assertEquals(readResponse.getUpdated().size(), 2);
        Assert.assertEquals(readResponse.getUpdated().get(0).getRid(), new ORecordId(10, 20));
        Assert.assertEquals(readResponse.getUpdated().get(0).getVersion(), 3);
        Assert.assertEquals(readResponse.getUpdated().get(1).getRid(), new ORecordId(10, 21));
        Assert.assertEquals(readResponse.getUpdated().get(1).getVersion(), 4);
        Assert.assertEquals(readResponse.getDeleted().size(), 2);
        Assert.assertEquals(readResponse.getDeleted().get(0).getRid(), new ORecordId(10, 50));
        Assert.assertEquals(readResponse.getDeleted().get(1).getRid(), new ORecordId(10, 51));
        Assert.assertEquals(readResponse.getCollectionChanges().size(), 1);
        Assert.assertNotNull(readResponse.getCollectionChanges().get(val));
        Assert.assertEquals(readResponse.getCollectionChanges().get(val).getFileId(), 10);
        Assert.assertEquals(readResponse.getCollectionChanges().get(val).getRootPointer().getPageIndex(), 30);
        Assert.assertEquals(readResponse.getCollectionChanges().get(val).getRootPointer().getPageOffset(), 40);
    }

    @Test
    public void testEmptyCommitTransactionWriteRead() throws IOException {
        MockChannel channel = new MockChannel();
        OCommit37Request request = new OCommit37Request(0, false, true, null, null);
        request.write(channel, null);
        channel.close();
        OCommit37Request readRequest = new OCommit37Request();
        readRequest.read(channel, 0, INSTANCE.current());
        Assert.assertTrue(readRequest.isUsingLog());
        Assert.assertNull(readRequest.getOperations());
        Assert.assertEquals(readRequest.getTxId(), 0);
        Assert.assertNull(readRequest.getIndexChanges());
    }

    @Test
    public void testTransactionFetchResponseWriteRead() throws IOException {
        List<ORecordOperation> operations = new ArrayList<>();
        operations.add(new ORecordOperation(new ODocument(), ORecordOperation.CREATED));
        operations.add(new ORecordOperation(new ODocument(new ORecordId(10, 2)), ORecordOperation.UPDATED));
        operations.add(new ORecordOperation(new ODocument(new ORecordId(10, 1)), ORecordOperation.DELETED));
        Map<String, OTransactionIndexChanges> changes = new HashMap<>();
        OTransactionIndexChanges change = new OTransactionIndexChanges();
        change.cleared = false;
        change.changesPerKey = new java.util.TreeMap(ODefaultComparator.INSTANCE);
        OTransactionIndexChangesPerKey keyChange = new OTransactionIndexChangesPerKey("key");
        keyChange.add(new ORecordId(1, 2), PUT);
        keyChange.add(new ORecordId(2, 2), REMOVE);
        change.changesPerKey.put(keyChange.key, keyChange);
        changes.put("some", change);
        MockChannel channel = new MockChannel();
        OFetchTransactionResponse response = new OFetchTransactionResponse(10, operations, changes, new HashMap());
        response.write(channel, 0, ORecordSerializerNetworkV37.INSTANCE);
        channel.close();
        OFetchTransactionResponse readResponse = new OFetchTransactionResponse(10, operations, changes, new HashMap());
        readResponse.read(channel, null);
        Assert.assertEquals(readResponse.getOperations().size(), 3);
        Assert.assertEquals(readResponse.getOperations().get(0).getType(), CREATED);
        Assert.assertNotNull(readResponse.getOperations().get(0).getRecord());
        Assert.assertEquals(readResponse.getOperations().get(1).getType(), UPDATED);
        Assert.assertNotNull(readResponse.getOperations().get(1).getRecord());
        Assert.assertEquals(readResponse.getOperations().get(2).getType(), DELETED);
        Assert.assertNotNull(readResponse.getOperations().get(2).getRecord());
        Assert.assertEquals(readResponse.getTxId(), 10);
        Assert.assertEquals(readResponse.getIndexChanges().size(), 1);
        Assert.assertEquals(readResponse.getIndexChanges().get(0).getName(), "some");
        OTransactionIndexChanges val = readResponse.getIndexChanges().get(0).getKeyChanges();
        Assert.assertEquals(val.cleared, false);
        Assert.assertEquals(val.changesPerKey.size(), 1);
        OTransactionIndexChangesPerKey entryChange = val.changesPerKey.firstEntry().getValue();
        Assert.assertEquals(entryChange.key, "key");
        Assert.assertEquals(entryChange.entries.size(), 2);
        Assert.assertEquals(entryChange.entries.get(0).value, new ORecordId(1, 2));
        Assert.assertEquals(entryChange.entries.get(0).operation, PUT);
        Assert.assertEquals(entryChange.entries.get(1).value, new ORecordId(2, 2));
        Assert.assertEquals(entryChange.entries.get(1).operation, REMOVE);
    }

    @Test
    public void testTransactionClearIndexFetchResponseWriteRead() throws IOException {
        List<ORecordOperation> operations = new ArrayList<>();
        Map<String, OTransactionIndexChanges> changes = new HashMap<>();
        OTransactionIndexChanges change = new OTransactionIndexChanges();
        change.cleared = true;
        change.changesPerKey = new java.util.TreeMap(ODefaultComparator.INSTANCE);
        OTransactionIndexChangesPerKey keyChange = new OTransactionIndexChangesPerKey("key");
        keyChange.add(new ORecordId(1, 2), PUT);
        keyChange.add(new ORecordId(2, 2), REMOVE);
        change.changesPerKey.put(keyChange.key, keyChange);
        changes.put("some", change);
        MockChannel channel = new MockChannel();
        OFetchTransactionResponse response = new OFetchTransactionResponse(10, operations, changes, new HashMap());
        response.write(channel, 0, ORecordSerializerNetworkV37.INSTANCE);
        channel.close();
        OFetchTransactionResponse readResponse = new OFetchTransactionResponse(10, operations, changes, new HashMap());
        readResponse.read(channel, null);
        Assert.assertEquals(readResponse.getTxId(), 10);
        Assert.assertEquals(readResponse.getIndexChanges().size(), 1);
        Assert.assertEquals(readResponse.getIndexChanges().get(0).getName(), "some");
        OTransactionIndexChanges val = readResponse.getIndexChanges().get(0).getKeyChanges();
        Assert.assertEquals(val.cleared, true);
        Assert.assertEquals(val.changesPerKey.size(), 1);
        OTransactionIndexChangesPerKey entryChange = val.changesPerKey.firstEntry().getValue();
        Assert.assertEquals(entryChange.key, "key");
        Assert.assertEquals(entryChange.entries.size(), 2);
        Assert.assertEquals(entryChange.entries.get(0).value, new ORecordId(1, 2));
        Assert.assertEquals(entryChange.entries.get(0).operation, PUT);
        Assert.assertEquals(entryChange.entries.get(1).value, new ORecordId(2, 2));
        Assert.assertEquals(entryChange.entries.get(1).operation, REMOVE);
    }
}

