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
package org.apache.flume.serialization;


import EventDeserializer.Builder;
import LineDeserializer.MAXLINE_KEY;
import java.io.IOException;
import java.util.List;
import junit.framework.Assert;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.junit.Test;


public class TestLineDeserializer {
    private String mini;

    @Test
    public void testSimple() throws IOException {
        ResettableInputStream in = new ResettableTestStringInputStream(mini);
        EventDeserializer des = new LineDeserializer(new Context(), in);
        validateMiniParse(des);
    }

    @Test
    public void testSimpleViaBuilder() throws IOException {
        ResettableInputStream in = new ResettableTestStringInputStream(mini);
        EventDeserializer.Builder builder = new LineDeserializer.Builder();
        EventDeserializer des = builder.build(new Context(), in);
        validateMiniParse(des);
    }

    @Test
    public void testSimpleViaFactory() throws IOException {
        ResettableInputStream in = new ResettableTestStringInputStream(mini);
        EventDeserializer des;
        des = EventDeserializerFactory.getInstance("LINE", new Context(), in);
        validateMiniParse(des);
    }

    @Test
    public void testBatch() throws IOException {
        ResettableInputStream in = new ResettableTestStringInputStream(mini);
        EventDeserializer des = new LineDeserializer(new Context(), in);
        List<Event> events;
        events = des.readEvents(1);// only try to read 1

        Assert.assertEquals(1, events.size());
        assertEventBodyEquals("line 1", events.get(0));
        events = des.readEvents(10);// try to read more than we should have

        Assert.assertEquals(1, events.size());
        assertEventBodyEquals("line 2", events.get(0));
        des.mark();
        des.close();
    }

    // truncation occurs at maxLineLength boundaries
    @Test
    public void testMaxLineLength() throws IOException {
        String longLine = "abcdefghijklmnopqrstuvwxyz\n";
        Context ctx = new Context();
        ctx.put(MAXLINE_KEY, "10");
        ResettableInputStream in = new ResettableTestStringInputStream(longLine);
        EventDeserializer des = new LineDeserializer(ctx, in);
        assertEventBodyEquals("abcdefghij", des.readEvent());
        assertEventBodyEquals("klmnopqrst", des.readEvent());
        assertEventBodyEquals("uvwxyz", des.readEvent());
        Assert.assertNull(des.readEvent());
    }
}

