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
package com.google.security.zynamics.binnavi.API.debug;


import com.google.common.collect.Lists;
import com.google.security.zynamics.binnavi.API.disassembly.Address;
import com.google.security.zynamics.binnavi.API.disassembly.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.API.disassembly.Module;
import com.google.security.zynamics.binnavi.API.disassembly.ModuleFactory;
import com.google.security.zynamics.binnavi.API.disassembly.Trace;
import com.google.security.zynamics.binnavi.Debug.Connection.MockDebugConnection;
import com.google.security.zynamics.binnavi.Debug.Debugger.MockDebugger;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.EchoBreakpointHitReply;
import com.google.security.zynamics.binnavi.debug.debugger.DebugExceptionWrapper;
import com.google.security.zynamics.binnavi.debug.debugger.ModuleTargetSettings;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValue;
import com.google.security.zynamics.binnavi.debug.models.targetinformation.ThreadRegisters;
import com.google.security.zynamics.binnavi.disassembly.Modules.MockModule;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class TraceLoggerTest {
    private TraceLogger m_logger;

    private MockDebugger m_mockDebugger;

    private TraceLogger m_projectLogger;

    private final MockModule mockModule = new MockModule();

    private Module m_mockModule = ModuleFactory.get(mockModule);

    private ModuleTargetSettings m_debugSettings;

    @Test
    public void testEmptyHits() throws CouldntSaveDataException, DebugExceptionWrapper {
        m_mockDebugger.connect();
        final Trace trace = m_logger.start("Name", "Description", Lists.newArrayList(new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, new Address(256))));
        m_logger.stop();
        Assert.assertEquals("Name", trace.getName());
        Assert.assertEquals("Description", trace.getDescription());
        Assert.assertTrue(trace.getEvents().isEmpty());
    }

    @Test
    public void testHits() throws CouldntSaveDataException, DebugExceptionWrapper {
        m_mockDebugger.connect();
        final Trace trace = m_logger.start("Name", "Description", Lists.newArrayList(new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, new Address(256)), new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, new Address(256))));
        m_mockDebugger.connection.m_synchronizer.receivedEvent(new EchoBreakpointHitReply(0, 0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(0, Lists.newArrayList(new RegisterValue("esp", BigInteger.valueOf(4352), new byte[0], true, false)))))));
        m_logger.stop();
        Assert.assertEquals("Name", trace.getName());
        Assert.assertEquals("Description", trace.getDescription());
        Assert.assertEquals(1, trace.getEvents().size());
        Assert.assertEquals(256, trace.getEvents().get(0).getAddress().toLong());
        Assert.assertEquals("TraceLogger [Debugger 'Mock' : Mock Module]", m_logger.toString());
    }

    @Test
    public void testHitsProject() throws CouldntSaveDataException, DebugExceptionWrapper {
        m_mockDebugger.connect();
        final Trace trace = m_projectLogger.start("Name", "Description", Lists.newArrayList(new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, new Address(256))));
        m_mockDebugger.connection.m_synchronizer.receivedEvent(new EchoBreakpointHitReply(0, 0, 0, new com.google.security.zynamics.binnavi.debug.models.targetinformation.RegisterValues(Lists.<ThreadRegisters>newArrayList(new ThreadRegisters(0, Lists.newArrayList(new RegisterValue("esp", BigInteger.valueOf(4352), new byte[0], true, false)))))));
        m_projectLogger.stop();
        Assert.assertEquals("Name", trace.getName());
        Assert.assertEquals("Description", trace.getDescription());
        Assert.assertEquals(1, trace.getEvents().size());
        Assert.assertEquals(256, trace.getEvents().get(0).getAddress().toLong());
        Assert.assertEquals("TraceLogger [Debugger 'Mock' : Mock Project]", m_projectLogger.toString());
    }

    @Test
    public void testStartErrors() throws CouldntSaveDataException, DebugExceptionWrapper {
        try {
            @SuppressWarnings("unused")
            final Trace trace = m_logger.start("Name", "Description", Lists.newArrayList(new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, new Address(256))));
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        m_mockDebugger.connect();
        try {
            @SuppressWarnings("unused")
            final Trace trace = m_logger.start(null, "Description", Lists.newArrayList(new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, new Address(256))));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            @SuppressWarnings("unused")
            final Trace trace = m_logger.start("Name", null, Lists.newArrayList(new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, new Address(256))));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            @SuppressWarnings("unused")
            final Trace trace = m_logger.start("Name", "Description", null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            @SuppressWarnings("unused")
            final Trace trace = m_logger.start("Name", "Description", Lists.newArrayList(new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, null)));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        @SuppressWarnings("unused")
        final Trace trace = m_logger.start("Name", "Description", Lists.newArrayList(new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, new Address(256))));
        try {
            m_logger.start("Name", "Description", Lists.newArrayList(new com.google.security.zynamics.binnavi.API.disassembly.TracePoint(m_mockModule, new Address(256))));
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        m_logger.stop();
        try {
            m_logger.stop();
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
    }
}

