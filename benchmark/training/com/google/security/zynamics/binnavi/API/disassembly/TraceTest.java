/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.API.disassembly;


import TraceEventType.Breakpoint;
import TraceEventType.EchoBreakpoint;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.debug.models.trace.TraceList;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class TraceTest {
    @Test
    public void testConstructor() {
        final Trace trace = new Trace(new TraceList(1, "Name", "Description", new MockSqlProvider()));
        Assert.assertEquals("Name", trace.getName());
        Assert.assertEquals("Description", trace.getDescription());
        Assert.assertEquals("Trace 'Name' [0 events]", trace.toString());
        Assert.assertEquals(0, trace.getEvents().size());
    }

    @Test
    public void testEvent() {
        final Trace trace = new Trace(new TraceList(1, "Name", "Description", new MockSqlProvider()));
        final MockTraceListener listener = new MockTraceListener();
        trace.addListener(listener);
        final MockModule module = new MockModule();
        final Module m = ModuleFactory.get(module);
        trace.addEvent(0, m, new Address(123), Breakpoint);
        trace.addEvent(0, m, new Address(124), EchoBreakpoint);
        Assert.assertEquals(2, trace.getEvents().size());
        Assert.assertEquals(123, trace.getEvents().get(0).getAddress().toLong());
        Assert.assertEquals(Breakpoint, trace.getEvents().get(0).getType());
        Assert.assertEquals(124, trace.getEvents().get(1).getAddress().toLong());
        Assert.assertEquals(EchoBreakpoint, trace.getEvents().get(1).getType());
        Assert.assertEquals("addedEvent;addedEvent;", listener.events);
        trace.removeListener(listener);
    }

    @Test
    public void testSave() throws CouldntSaveDataException {
        final Trace trace = new Trace(new TraceList(1, "Name", "Description", new MockSqlProvider()));
        trace.save();
    }

    @Test
    public void testSetDescription() throws CouldntSaveDataException {
        final Trace trace = new Trace(new TraceList(1, "Name", "Description", new MockSqlProvider()));
        final MockTraceListener listener = new MockTraceListener();
        trace.addListener(listener);
        trace.setDescription("D1");
        Assert.assertEquals("D1", trace.getDescription());
        Assert.assertEquals("changedDescription;", listener.events);
        trace.removeListener(listener);
    }

    @Test
    public void testSetName() throws CouldntSaveDataException {
        final Trace trace = new Trace(new TraceList(1, "Name", "Description", new MockSqlProvider()));
        final MockTraceListener listener = new MockTraceListener();
        trace.addListener(listener);
        trace.setName("N1");
        Assert.assertEquals("N1", trace.getName());
        Assert.assertEquals("changedName;", listener.events);
        trace.removeListener(listener);
    }
}

