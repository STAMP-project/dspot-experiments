/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.interceptor;


import Constants.KEY;
import Constants.PRESERVE;
import Constants.VALUE;
import Interceptor.Builder;
import InterceptorType.STATIC;
import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Assert;
import org.junit.Test;


public class TestStaticInterceptor {
    @Test
    public void testDefaultKeyValue() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(STATIC.toString());
        builder.configure(new Context());
        Interceptor interceptor = builder.build();
        Event event = EventBuilder.withBody("test", Charsets.UTF_8);
        Assert.assertNull(event.getHeaders().get(KEY));
        event = interceptor.intercept(event);
        String val = event.getHeaders().get(KEY);
        Assert.assertNotNull(val);
        Assert.assertEquals(VALUE, val);
    }

    @Test
    public void testCustomKeyValue() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(STATIC.toString());
        Context ctx = new Context();
        ctx.put(KEY, "myKey");
        ctx.put(VALUE, "myVal");
        builder.configure(ctx);
        Interceptor interceptor = builder.build();
        Event event = EventBuilder.withBody("test", Charsets.UTF_8);
        Assert.assertNull(event.getHeaders().get("myKey"));
        event = interceptor.intercept(event);
        String val = event.getHeaders().get("myKey");
        Assert.assertNotNull(val);
        Assert.assertEquals("myVal", val);
    }

    @Test
    public void testReplace() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(STATIC.toString());
        Context ctx = new Context();
        ctx.put(PRESERVE, "false");
        ctx.put(VALUE, "replacement value");
        builder.configure(ctx);
        Interceptor interceptor = builder.build();
        Event event = EventBuilder.withBody("test", Charsets.UTF_8);
        event.getHeaders().put(KEY, "incumbent value");
        Assert.assertNotNull(event.getHeaders().get(KEY));
        event = interceptor.intercept(event);
        String val = event.getHeaders().get(KEY);
        Assert.assertNotNull(val);
        Assert.assertEquals("replacement value", val);
    }

    @Test
    public void testPreserve() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(STATIC.toString());
        Context ctx = new Context();
        ctx.put(PRESERVE, "true");
        ctx.put(VALUE, "replacement value");
        builder.configure(ctx);
        Interceptor interceptor = builder.build();
        Event event = EventBuilder.withBody("test", Charsets.UTF_8);
        event.getHeaders().put(KEY, "incumbent value");
        Assert.assertNotNull(event.getHeaders().get(KEY));
        event = interceptor.intercept(event);
        String val = event.getHeaders().get(KEY);
        Assert.assertNotNull(val);
        Assert.assertEquals("incumbent value", val);
    }
}

