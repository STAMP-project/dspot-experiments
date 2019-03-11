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
package io.atomix.core.map.impl;


import io.atomix.core.map.AtomicMapType;
import io.atomix.primitive.PrimitiveId;
import io.atomix.primitive.service.ServiceContext;
import io.atomix.primitive.session.Session;
import io.atomix.primitive.session.SessionId;
import io.atomix.storage.buffer.Buffer;
import io.atomix.storage.buffer.HeapBuffer;
import io.atomix.utils.concurrent.Scheduled;
import io.atomix.utils.concurrent.Scheduler;
import io.atomix.utils.time.Versioned;
import io.atomix.utils.time.WallClock;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Consistent map service test.
 */
public class DefaultAtomicMapServiceTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testSnapshot() throws Exception {
        ServiceContext context = Mockito.mock(ServiceContext.class);
        Mockito.when(context.serviceType()).thenReturn(AtomicMapType.instance());
        Mockito.when(context.serviceName()).thenReturn("test");
        Mockito.when(context.serviceId()).thenReturn(PrimitiveId.from(1));
        Mockito.when(context.wallClock()).thenReturn(new WallClock());
        Session session = Mockito.mock(Session.class);
        Mockito.when(session.sessionId()).thenReturn(SessionId.from(1));
        AbstractAtomicMapService service = new DefaultAtomicMapServiceTest.TestAtomicMapService();
        service.init(context);
        service.put("foo", "Hello world!".getBytes());
        Buffer buffer = HeapBuffer.allocate();
        service.backup(new io.atomix.primitive.service.impl.DefaultBackupOutput(buffer, service.serializer()));
        service = new DefaultAtomicMapServiceTest.TestAtomicMapService();
        service.restore(new io.atomix.primitive.service.impl.DefaultBackupInput(buffer.flip(), service.serializer()));
        Versioned<byte[]> value = service.get("foo");
        Assert.assertNotNull(value);
        Assert.assertArrayEquals("Hello world!".getBytes(), value.value());
    }

    private static class TestAtomicMapService extends AbstractAtomicMapService {
        TestAtomicMapService() {
            super(AtomicMapType.instance());
        }

        @Override
        protected Scheduler getScheduler() {
            return new Scheduler() {
                @Override
                public Scheduled schedule(Duration delay, Runnable callback) {
                    return Mockito.mock(Scheduled.class);
                }

                @Override
                public Scheduled schedule(Duration initialDelay, Duration interval, Runnable callback) {
                    return Mockito.mock(Scheduled.class);
                }
            };
        }

        @Override
        protected WallClock getWallClock() {
            return new WallClock();
        }
    }
}

