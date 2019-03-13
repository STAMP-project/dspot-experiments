/**
 * Copyright 2017-present Open Networking Foundation
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
package io.atomix.core.workqueue.impl;


import io.atomix.core.workqueue.Task;
import io.atomix.core.workqueue.WorkQueueType;
import io.atomix.primitive.PrimitiveId;
import io.atomix.primitive.service.ServiceContext;
import io.atomix.primitive.session.Session;
import io.atomix.primitive.session.SessionId;
import io.atomix.storage.buffer.Buffer;
import io.atomix.storage.buffer.HeapBuffer;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Work queue service test.
 */
public class DefaultWorkQueueServiceTest {
    @Test
    public void testSnapshot() throws Exception {
        ServiceContext context = Mockito.mock(ServiceContext.class);
        Mockito.when(context.serviceType()).thenReturn(WorkQueueType.instance());
        Mockito.when(context.serviceName()).thenReturn("test");
        Mockito.when(context.serviceId()).thenReturn(PrimitiveId.from(1));
        Session session = Mockito.mock(Session.class);
        Mockito.when(session.sessionId()).thenReturn(SessionId.from(1));
        Mockito.when(context.currentSession()).thenReturn(session);
        DefaultWorkQueueService service = new DefaultWorkQueueService();
        service.init(context);
        service.register(session);
        service.add(Arrays.asList("Hello world!".getBytes()));
        Buffer buffer = HeapBuffer.allocate();
        service.backup(new io.atomix.primitive.service.impl.DefaultBackupOutput(buffer, service.serializer()));
        service = new DefaultWorkQueueService();
        service.init(context);
        service.register(session);
        service.restore(new io.atomix.primitive.service.impl.DefaultBackupInput(buffer.flip(), service.serializer()));
        Collection<Task<byte[]>> value = service.take(1);
        Assert.assertNotNull(value);
        Assert.assertEquals(1, value.size());
        Assert.assertArrayEquals("Hello world!".getBytes(), value.iterator().next().payload());
    }
}

