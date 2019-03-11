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
package org.apache.flume.sink.solr.morphline;


import UUIDInterceptor.HEADER_NAME;
import UUIDInterceptor.PREFIX_NAME;
import UUIDInterceptor.PRESERVE_EXISTING_NAME;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.junit.Assert;
import org.junit.Test;


public class TestUUIDInterceptor extends Assert {
    private static final String ID = "id";

    @Test
    public void testBasic() throws Exception {
        Context context = new Context();
        context.put(HEADER_NAME, TestUUIDInterceptor.ID);
        context.put(PRESERVE_EXISTING_NAME, "true");
        Event event = new SimpleEvent();
        Assert.assertTrue(((build(context).intercept(event).getHeaders().get(TestUUIDInterceptor.ID).length()) > 0));
    }

    @Test
    public void testPreserveExisting() throws Exception {
        Context context = new Context();
        context.put(HEADER_NAME, TestUUIDInterceptor.ID);
        context.put(PRESERVE_EXISTING_NAME, "true");
        Event event = new SimpleEvent();
        event.getHeaders().put(TestUUIDInterceptor.ID, "foo");
        Assert.assertEquals("foo", build(context).intercept(event).getHeaders().get(TestUUIDInterceptor.ID));
    }

    @Test
    public void testPrefix() throws Exception {
        Context context = new Context();
        context.put(HEADER_NAME, TestUUIDInterceptor.ID);
        context.put(PREFIX_NAME, "bar#");
        Event event = new SimpleEvent();
        Assert.assertTrue(build(context).intercept(event).getHeaders().get(TestUUIDInterceptor.ID).startsWith("bar#"));
    }
}

