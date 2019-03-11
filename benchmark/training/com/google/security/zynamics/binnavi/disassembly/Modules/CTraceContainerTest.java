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
package com.google.security.zynamics.binnavi.disassembly.Modules;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Interfaces.SQLProvider;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.debug.models.trace.TraceList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CTraceContainerTest {
    private final SQLProvider m_sql = new MockSqlProvider();

    private MockTraceContainerListener m_listener;

    private CTraceContainer m_content;

    /**
     * Tests trace creation.
     */
    @Test
    public void testTraces() throws CouldntDeleteException, CouldntSaveDataException {
        Assert.assertEquals(0, m_content.getTraceCount());
        try {
            m_content.createTrace(null, "New Trace Description");
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            m_content.createTrace("New Trace", null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        final TraceList newTrace = m_content.createTrace("New Trace", "New Trace Description");
        // Check listener events
        Assert.assertEquals("addedTrace/", m_listener.eventList);
        Assert.assertEquals(newTrace, m_listener.addedTraces.get(0));
        // Check module
        Assert.assertEquals(1, m_content.getTraceCount());
        Assert.assertEquals(newTrace, m_content.getTraces().get(0));
        // Check trace
        Assert.assertEquals("New Trace", newTrace.getName());
        Assert.assertEquals("New Trace Description", newTrace.getDescription());
        final TraceList newTrace2 = m_content.createTrace("New Trace II", "New Trace Description II");
        // Check listener events
        Assert.assertEquals("addedTrace/addedTrace/", m_listener.eventList);
        Assert.assertEquals(newTrace2, m_listener.addedTraces.get(1));
        // Check module
        Assert.assertEquals(2, m_content.getTraceCount());
        Assert.assertEquals(newTrace, m_content.getTraces().get(0));
        Assert.assertEquals(newTrace2, m_content.getTraces().get(1));
        // Check trace
        Assert.assertEquals("New Trace II", newTrace2.getName());
        Assert.assertEquals("New Trace Description II", newTrace2.getDescription());
        // ----------------------------------------- Delete the traces again
        // ------------------------------------------------
        m_content.deleteTrace(newTrace);
        // Check listener events
        Assert.assertEquals("addedTrace/addedTrace/deletedTrace/", m_listener.eventList);
        Assert.assertEquals(newTrace, m_listener.deletedTraces.get(0));
        // Check module
        Assert.assertEquals(1, m_content.getTraceCount());
        Assert.assertEquals(newTrace2, m_content.getTraces().get(0));
        m_content.deleteTrace(newTrace2);
        // Check listener events
        Assert.assertEquals("addedTrace/addedTrace/deletedTrace/deletedTrace/", m_listener.eventList);
        Assert.assertEquals(newTrace2, m_listener.deletedTraces.get(1));
        // Check module
        Assert.assertEquals(0, m_content.getTraceCount());
        try {
            m_content.deleteTrace(newTrace2);
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        try {
            m_content.deleteTrace(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
    }
}

