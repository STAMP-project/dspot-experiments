package com.orientechnologies.orient.server.distributed.impl.coordinator.transaction;


import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.server.distributed.impl.coordinator.transaction.OTransactionFirstPhaseResult.Type;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FirstPhaseResponseHandlerTest {
    @Mock
    private ODistributedCoordinator coordinator;

    @Test
    public void testFirstPhaseQuorumSuccess() {
        OSessionOperationId operationId = new OSessionOperationId();
        ODistributedMember member1 = new ODistributedMember("one", null, null);
        ODistributedMember member2 = new ODistributedMember("two", null, null);
        ODistributedMember member3 = new ODistributedMember("three", null, null);
        List<ODistributedMember> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
        members.add(member3);
        OTransactionFirstPhaseResponseHandler handler = new OTransactionFirstPhaseResponseHandler(operationId, null, member1, null);
        OLogId id = new OLogId(1);
        ORequestContext context = new ORequestContext(null, null, null, members, handler, id);
        handler.receive(coordinator, context, member1, new OTransactionFirstPhaseResult(Type.SUCCESS, null));
        handler.receive(coordinator, context, member2, new OTransactionFirstPhaseResult(Type.SUCCESS, null));
        handler.receive(coordinator, context, member3, new OTransactionFirstPhaseResult(Type.SUCCESS, null));
        Mockito.verify(coordinator, Mockito.times(1)).sendOperation(ArgumentMatchers.any(OSubmitRequest.class), ArgumentMatchers.eq(new OTransactionSecondPhaseOperation(operationId, true)), ArgumentMatchers.any(OTransactionSecondPhaseResponseHandler.class));
        Mockito.verify(coordinator, Mockito.times(0)).reply(ArgumentMatchers.same(member1), ArgumentMatchers.any(OSessionOperationId.class), ArgumentMatchers.any(OTransactionResponse.class));
    }

    @Test
    public void testFirstPhaseQuorumCME() {
        OSessionOperationId operationId = new OSessionOperationId();
        ODistributedMember member1 = new ODistributedMember("one", null, null);
        ODistributedMember member2 = new ODistributedMember("two", null, null);
        ODistributedMember member3 = new ODistributedMember("three", null, null);
        List<ODistributedMember> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
        members.add(member3);
        OTransactionFirstPhaseResponseHandler handler = new OTransactionFirstPhaseResponseHandler(operationId, null, member1, null);
        OLogId id = new OLogId(1);
        ORequestContext context = new ORequestContext(null, null, null, members, handler, id);
        handler.receive(coordinator, context, member1, new OTransactionFirstPhaseResult(Type.CONCURRENT_MODIFICATION_EXCEPTION, new com.orientechnologies.orient.server.distributed.impl.coordinator.transaction.results.OConcurrentModificationResult(new ORecordId(10, 10), 0, 1)));
        handler.receive(coordinator, context, member2, new OTransactionFirstPhaseResult(Type.SUCCESS, null));
        handler.receive(coordinator, context, member3, new OTransactionFirstPhaseResult(Type.CONCURRENT_MODIFICATION_EXCEPTION, new com.orientechnologies.orient.server.distributed.impl.coordinator.transaction.results.OConcurrentModificationResult(new ORecordId(10, 10), 0, 1)));
        Mockito.verify(coordinator, Mockito.times(1)).sendOperation(ArgumentMatchers.any(OSubmitRequest.class), ArgumentMatchers.eq(new OTransactionSecondPhaseOperation(operationId, false)), ArgumentMatchers.any(OTransactionSecondPhaseResponseHandler.class));
        Mockito.verify(coordinator, Mockito.times(1)).reply(ArgumentMatchers.same(member1), ArgumentMatchers.any(OSessionOperationId.class), ArgumentMatchers.any(OTransactionResponse.class));
    }

    @Test
    public void testFirstPhaseQuorumUnique() {
        OSessionOperationId operationId = new OSessionOperationId();
        ODistributedMember member1 = new ODistributedMember("one", null, null);
        ODistributedMember member2 = new ODistributedMember("two", null, null);
        ODistributedMember member3 = new ODistributedMember("three", null, null);
        List<ODistributedMember> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
        members.add(member3);
        OTransactionFirstPhaseResponseHandler handler = new OTransactionFirstPhaseResponseHandler(operationId, null, member1, null);
        OLogId id = new OLogId(1);
        ORequestContext context = new ORequestContext(null, null, null, members, handler, id);
        handler.receive(coordinator, context, member1, new OTransactionFirstPhaseResult(Type.UNIQUE_KEY_VIOLATION, new com.orientechnologies.orient.server.distributed.impl.coordinator.transaction.results.OUniqueKeyViolationResult("Key", new ORecordId(10, 10), new ORecordId(10, 11), "Class.property")));
        handler.receive(coordinator, context, member2, new OTransactionFirstPhaseResult(Type.SUCCESS, null));
        handler.receive(coordinator, context, member3, new OTransactionFirstPhaseResult(Type.UNIQUE_KEY_VIOLATION, new com.orientechnologies.orient.server.distributed.impl.coordinator.transaction.results.OUniqueKeyViolationResult("Key", new ORecordId(10, 10), new ORecordId(10, 11), "Class.property")));
        Mockito.verify(coordinator, Mockito.times(1)).sendOperation(ArgumentMatchers.any(OSubmitRequest.class), ArgumentMatchers.eq(new OTransactionSecondPhaseOperation(operationId, false)), ArgumentMatchers.any(OTransactionSecondPhaseResponseHandler.class));
        Mockito.verify(coordinator, Mockito.times(1)).reply(ArgumentMatchers.same(member1), ArgumentMatchers.any(OSessionOperationId.class), ArgumentMatchers.any(OTransactionResponse.class));
    }
}

