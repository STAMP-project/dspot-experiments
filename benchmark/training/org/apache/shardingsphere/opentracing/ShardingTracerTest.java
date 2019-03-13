/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.opentracing;


import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class ShardingTracerTest {
    @Test
    public void assertDuplicatedLoading() {
        ShardingTracer.init(Mockito.mock(Tracer.class));
        Tracer t1 = ShardingTracer.get();
        ShardingTracer.init();
        Assert.assertEquals(t1, ShardingTracer.get());
        ShardingTracer.init(Mockito.mock(Tracer.class));
        Assert.assertEquals(t1, ShardingTracer.get());
    }

    @Test
    public void assertTracer() {
        ShardingTracer.init();
        Assert.assertThat(((GlobalTracer) (ShardingTracer.get())), CoreMatchers.isA(GlobalTracer.class));
        Assert.assertTrue(GlobalTracer.isRegistered());
        Assert.assertThat(ShardingTracer.get(), CoreMatchers.is(ShardingTracer.get()));
    }

    @Test(expected = ShardingException.class)
    public void assertTracerClassError() {
        System.setProperty("org.apache.shardingsphere.opentracing.tracer.class", "com.foo.FooTracer");
        ShardingTracer.init();
    }
}

