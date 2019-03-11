package com.orientechnologies.orient.distributed.impl.coordinator.transaction;


import OIndexKeyOperation.PUT;
import ORecordOperation.CREATED;
import OTransactionFirstPhaseResult.Type;
import com.orientechnologies.orient.client.remote.message.tx.ORecordOperationRequest;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.distributed.impl.coordinator.transaction.results.OConcurrentModificationResult;
import com.orientechnologies.orient.distributed.impl.coordinator.transaction.results.OExceptionResult;
import com.orientechnologies.orient.distributed.impl.coordinator.transaction.results.OUniqueKeyViolationResult;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static OIndexKeyOperation.PUT;


public class TransactionMessagesReadWriteTests {
    @Test
    public void testFirstPhaseSuccess() {
        OTransactionFirstPhaseResult result = new OTransactionFirstPhaseResult(Type.SUCCESS, null);
        OTransactionFirstPhaseResult readResult = new OTransactionFirstPhaseResult();
        TransactionMessagesReadWriteTests.writeRead(result, readResult);
        Assert.assertEquals(result.getType(), readResult.getType());
    }

    @Test
    public void testFirstPhaseConcurrentModification() {
        OConcurrentModificationResult payload = new OConcurrentModificationResult(new ORecordId(10, 10), 10, 20);
        OTransactionFirstPhaseResult result = new OTransactionFirstPhaseResult(Type.CONCURRENT_MODIFICATION_EXCEPTION, payload);
        OTransactionFirstPhaseResult readResult = new OTransactionFirstPhaseResult();
        TransactionMessagesReadWriteTests.writeRead(result, readResult);
        Assert.assertEquals(result.getType(), readResult.getType());
        OConcurrentModificationResult readMetadata = ((OConcurrentModificationResult) (readResult.getResultMetadata()));
        Assert.assertEquals(payload.getRecordId(), readMetadata.getRecordId());
        Assert.assertEquals(payload.getPersistentVersion(), readMetadata.getPersistentVersion());
        Assert.assertEquals(payload.getUpdateVersion(), readMetadata.getUpdateVersion());
    }

    @Test
    public void testFirstPhaseUniqueIndex() {
        OUniqueKeyViolationResult payload = new OUniqueKeyViolationResult("hello", new ORecordId(10, 10), new ORecordId(10, 11), "test.index");
        OTransactionFirstPhaseResult result = new OTransactionFirstPhaseResult(Type.UNIQUE_KEY_VIOLATION, payload);
        OTransactionFirstPhaseResult readResult = new OTransactionFirstPhaseResult();
        TransactionMessagesReadWriteTests.writeRead(result, readResult);
        Assert.assertEquals(result.getType(), readResult.getType());
        OUniqueKeyViolationResult readMetadata = ((OUniqueKeyViolationResult) (readResult.getResultMetadata()));
        Assert.assertEquals(payload.getKeyStringified(), readMetadata.getKeyStringified());
        Assert.assertEquals(payload.getIndexName(), readMetadata.getIndexName());
        Assert.assertEquals(payload.getRecordOwner(), readMetadata.getRecordOwner());
        Assert.assertEquals(payload.getRecordRequesting(), readMetadata.getRecordRequesting());
    }

    @Test
    public void testFirstPhaseException() {
        OExceptionResult payload = new OExceptionResult(new RuntimeException("test"));
        OTransactionFirstPhaseResult result = new OTransactionFirstPhaseResult(Type.EXCEPTION, payload);
        OTransactionFirstPhaseResult readResult = new OTransactionFirstPhaseResult();
        TransactionMessagesReadWriteTests.writeRead(result, readResult);
        Assert.assertEquals(result.getType(), readResult.getType());
        OExceptionResult readMetadata = ((OExceptionResult) (readResult.getResultMetadata()));
        Assert.assertEquals(payload.getException().getMessage(), readMetadata.getException().getMessage());
    }

    @Test
    public void testSecondPhase() {
        OTransactionSecondPhaseOperation operation = new OTransactionSecondPhaseOperation(new OSessionOperationId(), true);
        OTransactionSecondPhaseOperation readOperation = new OTransactionSecondPhaseOperation();
        TransactionMessagesReadWriteTests.writeRead(operation, readOperation);
        // assertEquals(operation.getOperationId(), readOperation.getOperationId());
        Assert.assertEquals(operation.isSuccess(), readOperation.isSuccess());
    }

    @Test
    public void testSecondPhaseResult() {
        OTransactionSecondPhaseResponse operation = new OTransactionSecondPhaseResponse(true, new ArrayList(), new ArrayList(), new ArrayList());
        OTransactionSecondPhaseResponse readOperation = new OTransactionSecondPhaseResponse();
        TransactionMessagesReadWriteTests.writeRead(operation, readOperation);
        Assert.assertEquals(operation.isSuccess(), readOperation.isSuccess());
    }

    @Test
    public void testFirstPhase() {
        List<ORecordOperationRequest> records = new ArrayList<>();
        ORecordOperationRequest recordOperation = new ORecordOperationRequest(ORecordOperation.CREATED, ((byte) ('a')), new ORecordId(10, 10), new ORecordId(10, 11), "bytes".getBytes(), 10, true);
        records.add(recordOperation);
        OIndexKeyOperation indexOp = new OIndexKeyOperation(PUT, new ORecordId(20, 30));
        List<OIndexKeyOperation> keyOps = new ArrayList<>();
        keyOps.add(indexOp);
        OIndexKeyChange keyChange = new OIndexKeyChange("string", keyOps);
        List<OIndexKeyChange> indexChanges = new ArrayList<>();
        indexChanges.add(keyChange);
        OIndexOperationRequest indexOperation = new OIndexOperationRequest("one", true, indexChanges);
        List<OIndexOperationRequest> indexes = new ArrayList<>();
        indexes.add(indexOperation);
        OTransactionFirstPhaseOperation operation = new OTransactionFirstPhaseOperation(new OSessionOperationId(), records, indexes);
        OTransactionFirstPhaseOperation readOperation = new OTransactionFirstPhaseOperation();
        TransactionMessagesReadWriteTests.writeRead(operation, readOperation);
        Assert.assertEquals(readOperation.getOperations().size(), 1);
        ORecordOperationRequest readRec = readOperation.getOperations().get(0);
        Assert.assertEquals(readRec.getType(), CREATED);
        Assert.assertEquals(readRec.getRecordType(), 'a');
        Assert.assertEquals(readRec.getId(), new ORecordId(10, 10));
        Assert.assertEquals(readRec.getOldId(), new ORecordId(10, 11));
        Assert.assertEquals(readRec.getVersion(), 10);
        Assert.assertEquals(readOperation.getIndexes().size(), 1);
        OIndexOperationRequest readIndex = readOperation.getIndexes().get(0);
        Assert.assertEquals(readIndex.getIndexName(), "one");
        Assert.assertEquals(readIndex.isCleanIndexValues(), true);
        Assert.assertEquals(readIndex.getIndexKeyChanges().size(), 1);
        OIndexKeyChange readChange = readIndex.getIndexKeyChanges().get(0);
        Assert.assertEquals(readChange.getKey(), "string");
        Assert.assertEquals(readChange.getOperations().size(), 1);
        OIndexKeyOperation readKeyOp = readChange.getOperations().get(0);
        Assert.assertEquals(readKeyOp.getType(), PUT);
        Assert.assertEquals(readKeyOp.getValue(), new ORecordId(20, 30));
    }
}

