/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.inprocess;


import com.google.common.collect.Iterables;
import io.grpc.internal.FakeClock;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.ObjectPool;
import io.grpc.internal.SharedResourcePool;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link InProcessServerBuilder}.
 */
@RunWith(JUnit4.class)
public class InProcessServerBuilderTest {
    @Test
    public void generateName() {
        String name1 = InProcessServerBuilder.generateName();
        Assert.assertNotNull(name1);
        Assert.assertFalse(name1.isEmpty());
        String name2 = InProcessServerBuilder.generateName();
        Assert.assertNotNull(name2);
        Assert.assertFalse(name2.isEmpty());
        Assert.assertNotEquals(name1, name2);
    }

    @Test
    public void scheduledExecutorService_default() {
        InProcessServerBuilder builder = InProcessServerBuilder.forName("foo");
        InProcessServer server = Iterables.getOnlyElement(builder.buildTransportServers(new ArrayList<io.grpc.ServerStreamTracer.Factory>()));
        ObjectPool<ScheduledExecutorService> scheduledExecutorServicePool = server.getScheduledExecutorServicePool();
        ObjectPool<ScheduledExecutorService> expectedPool = SharedResourcePool.forResource(GrpcUtil.TIMER_SERVICE);
        ScheduledExecutorService expected = expectedPool.getObject();
        ScheduledExecutorService actual = scheduledExecutorServicePool.getObject();
        Assert.assertSame(expected, actual);
        expectedPool.returnObject(expected);
        scheduledExecutorServicePool.returnObject(actual);
    }

    @Test
    public void scheduledExecutorService_custom() {
        InProcessServerBuilder builder = InProcessServerBuilder.forName("foo");
        ScheduledExecutorService scheduledExecutorService = new FakeClock().getScheduledExecutorService();
        InProcessServerBuilder builder1 = builder.scheduledExecutorService(scheduledExecutorService);
        Assert.assertSame(builder, builder1);
        InProcessServer server = Iterables.getOnlyElement(builder1.buildTransportServers(new ArrayList<io.grpc.ServerStreamTracer.Factory>()));
        ObjectPool<ScheduledExecutorService> scheduledExecutorServicePool = server.getScheduledExecutorServicePool();
        Assert.assertSame(scheduledExecutorService, scheduledExecutorServicePool.getObject());
        scheduledExecutorServicePool.returnObject(scheduledExecutorService);
    }
}

