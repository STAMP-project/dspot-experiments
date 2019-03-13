/**
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.core.lock.impl;


import OperationType.COMMAND;
import io.atomix.core.election.LeaderElectionType;
import io.atomix.primitive.PrimitiveId;
import io.atomix.primitive.service.ServiceContext;
import io.atomix.primitive.session.Session;
import io.atomix.primitive.session.SessionId;
import io.atomix.storage.buffer.Buffer;
import io.atomix.storage.buffer.HeapBuffer;
import io.atomix.utils.time.WallClock;
import io.atomix.utils.time.WallClockTimestamp;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Leader elector service test.
 */
public class DefaultAtomicLockServiceTest {
    @Test
    public void testSnapshot() throws Exception {
        ServiceContext context = Mockito.mock(ServiceContext.class);
        Mockito.when(context.serviceType()).thenReturn(LeaderElectionType.instance());
        Mockito.when(context.serviceName()).thenReturn("test");
        Mockito.when(context.serviceId()).thenReturn(PrimitiveId.from(1));
        Mockito.when(context.wallClock()).thenReturn(new WallClock());
        Mockito.when(context.currentOperation()).thenReturn(COMMAND);
        Session session = Mockito.mock(Session.class);
        Mockito.when(session.sessionId()).thenReturn(SessionId.from(1));
        Mockito.when(context.currentSession()).thenReturn(session);
        DefaultAtomicLockService service = new DefaultAtomicLockService();
        service.init(context);
        service.register(session);
        service.tick(new WallClockTimestamp());
        Buffer buffer = HeapBuffer.allocate();
        service.backup(new io.atomix.primitive.service.impl.DefaultBackupOutput(buffer, service.serializer()));
        service = new DefaultAtomicLockService();
        service.init(context);
        service.register(session);
        service.tick(new WallClockTimestamp());
        service.restore(new io.atomix.primitive.service.impl.DefaultBackupInput(buffer.flip(), service.serializer()));
        service.lock(1);
        service.lock(2, 1000);
        buffer = HeapBuffer.allocate();
        service.backup(new io.atomix.primitive.service.impl.DefaultBackupOutput(buffer, service.serializer()));
        service = new DefaultAtomicLockService();
        service.init(context);
        service.register(session);
        service.tick(new WallClockTimestamp());
        service.restore(new io.atomix.primitive.service.impl.DefaultBackupInput(buffer.flip(), service.serializer()));
        Assert.assertTrue(service.isLocked(service.lock.index));
        Assert.assertTrue((!(service.queue.isEmpty())));
        Assert.assertTrue((!(service.timers.isEmpty())));
    }
}

