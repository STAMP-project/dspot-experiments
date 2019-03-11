/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.grpc;


import com.google.cloud.grpc.GrpcTransportOptions.DefaultExecutorFactory;
import com.google.cloud.grpc.GrpcTransportOptions.ExecutorFactory;
import java.util.concurrent.ScheduledExecutorService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class GrpcTransportOptionsTest {
    private static final ExecutorFactory MOCK_EXECUTOR_FACTORY = EasyMock.createMock(ExecutorFactory.class);

    private static final GrpcTransportOptions OPTIONS = GrpcTransportOptions.newBuilder().setExecutorFactory(GrpcTransportOptionsTest.MOCK_EXECUTOR_FACTORY).build();

    private static final GrpcTransportOptions DEFAULT_OPTIONS = GrpcTransportOptions.newBuilder().build();

    private static final GrpcTransportOptions OPTIONS_COPY = GrpcTransportOptionsTest.OPTIONS.toBuilder().build();

    @Test
    public void testBuilder() {
        Assert.assertSame(GrpcTransportOptionsTest.MOCK_EXECUTOR_FACTORY, GrpcTransportOptionsTest.OPTIONS.getExecutorFactory());
        Assert.assertTrue(((GrpcTransportOptionsTest.DEFAULT_OPTIONS.getExecutorFactory()) instanceof DefaultExecutorFactory));
    }

    @Test
    public void testBaseEquals() {
        Assert.assertEquals(GrpcTransportOptionsTest.OPTIONS, GrpcTransportOptionsTest.OPTIONS_COPY);
        Assert.assertNotEquals(GrpcTransportOptionsTest.DEFAULT_OPTIONS, GrpcTransportOptionsTest.OPTIONS);
        GrpcTransportOptions options = GrpcTransportOptionsTest.OPTIONS.toBuilder().setExecutorFactory(new DefaultExecutorFactory()).build();
        Assert.assertNotEquals(GrpcTransportOptionsTest.OPTIONS, options);
    }

    @Test
    public void testBaseHashCode() {
        Assert.assertEquals(GrpcTransportOptionsTest.OPTIONS.hashCode(), GrpcTransportOptionsTest.OPTIONS_COPY.hashCode());
        Assert.assertNotEquals(GrpcTransportOptionsTest.DEFAULT_OPTIONS.hashCode(), GrpcTransportOptionsTest.OPTIONS.hashCode());
        GrpcTransportOptions options = GrpcTransportOptionsTest.OPTIONS.toBuilder().setExecutorFactory(new DefaultExecutorFactory()).build();
        Assert.assertNotEquals(GrpcTransportOptionsTest.OPTIONS.hashCode(), options.hashCode());
    }

    @Test
    public void testDefaultExecutorFactory() {
        ExecutorFactory<ScheduledExecutorService> executorFactory = new DefaultExecutorFactory();
        ScheduledExecutorService executorService = executorFactory.get();
        Assert.assertSame(executorService, executorFactory.get());
    }
}

