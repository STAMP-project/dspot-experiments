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
package com.google.security.zynamics.binnavi.disassembly.AddressSpaces;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate;
import com.google.security.zynamics.binnavi.disassembly.MockCreator;
import com.google.security.zynamics.binnavi.disassembly.MockProject;
import java.util.Date;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CAddressSpaceTest {
    private CAddressSpace m_addressSpace;

    private final MockAddressSpaceConfigurationListener m_listener = new MockAddressSpaceConfigurationListener();

    private MockSqlProvider m_sql;

    @Test
    public void test_C_Constructors() {
        final MockSqlProvider sql = new MockSqlProvider();
        try {
            new CAddressSpace(0, "AS Name", "AS Description", new Date(), new Date(), new HashMap<com.google.security.zynamics.binnavi.disassembly.INaviModule, com.google.security.zynamics.zylib.disassembly.IAddress>(), null, sql, new MockProject());
            Assert.fail();
        } catch (final Exception exception) {
        }
        try {
            new CAddressSpace(1, null, "AS Description", new Date(), new Date(), new HashMap<com.google.security.zynamics.binnavi.disassembly.INaviModule, com.google.security.zynamics.zylib.disassembly.IAddress>(), null, sql, new MockProject());
            Assert.fail();
        } catch (final Exception exception) {
        }
        try {
            new CAddressSpace(1, "AS Name", null, new Date(), new Date(), new HashMap<com.google.security.zynamics.binnavi.disassembly.INaviModule, com.google.security.zynamics.zylib.disassembly.IAddress>(), null, sql, new MockProject());
            Assert.fail();
        } catch (final Exception exception) {
        }
        try {
            new CAddressSpace(1, "AS Name", "AS Description", null, new Date(), new HashMap<com.google.security.zynamics.binnavi.disassembly.INaviModule, com.google.security.zynamics.zylib.disassembly.IAddress>(), null, sql, new MockProject());
            Assert.fail();
        } catch (final Exception exception) {
        }
        try {
            new CAddressSpace(1, "AS Name", "AS Description", new Date(), null, new HashMap<com.google.security.zynamics.binnavi.disassembly.INaviModule, com.google.security.zynamics.zylib.disassembly.IAddress>(), null, sql, new MockProject());
            Assert.fail();
        } catch (final Exception exception) {
        }
        try {
            new CAddressSpace(1, "AS Name", "AS Description", new Date(), new Date(), null, null, sql, new MockProject());
            Assert.fail();
        } catch (final Exception exception) {
        }
        try {
            new CAddressSpace(1, "AS Name", "AS Description", new Date(), new Date(), new HashMap<com.google.security.zynamics.binnavi.disassembly.INaviModule, com.google.security.zynamics.zylib.disassembly.IAddress>(), null, null, new MockProject());
            Assert.fail();
        } catch (final Exception exception) {
        }
        final CAddressSpace addressSpace = new CAddressSpace(1, "AS Name", "AS Description", new Date(), new Date(), new HashMap<com.google.security.zynamics.binnavi.disassembly.INaviModule, com.google.security.zynamics.zylib.disassembly.IAddress>(), null, sql, new MockProject());
        Assert.assertEquals(1, addressSpace.getConfiguration().getId());
        Assert.assertEquals("AS Name", addressSpace.getConfiguration().getName());
        Assert.assertEquals("AS Description", addressSpace.getConfiguration().getDescription());
    }

    @Test
    public void testDebugger() throws CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        Assert.assertNull(m_addressSpace.getConfiguration().getDebugger());
        Assert.assertNull(m_addressSpace.getConfiguration().getDebuggerTemplate());
        final DebuggerTemplate template = MockCreator.createDebuggerTemplate(m_sql);
        m_addressSpace.getConfiguration().setDebuggerTemplate(template);
        Assert.assertNull(m_addressSpace.getConfiguration().getDebugger());
        Assert.assertEquals(template, m_addressSpace.getConfiguration().getDebuggerTemplate());
        m_addressSpace.load();
        Assert.assertNotNull(m_addressSpace.getConfiguration().getDebugger());
        Assert.assertEquals(template, m_addressSpace.getConfiguration().getDebuggerTemplate());
        m_addressSpace.getConfiguration().setDebuggerTemplate(null);
        Assert.assertNull(m_addressSpace.getConfiguration().getDebugger());
        Assert.assertNull(m_addressSpace.getConfiguration().getDebuggerTemplate());
    }

    @Test
    public void testSetDescription() throws CouldntSaveDataException {
        try {
            m_addressSpace.getConfiguration().setDescription(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        m_addressSpace.getConfiguration().setDescription("New Description");
        // Check listener events
        Assert.assertEquals("changedDescription;changedModificationDate;", m_listener.events);
        // Check address space
        Assert.assertEquals("New Description", m_addressSpace.getConfiguration().getDescription());
        m_addressSpace.getConfiguration().setDescription("New Description");
        // Check listener events
        Assert.assertEquals("changedDescription;changedModificationDate;", m_listener.events);
        // Check address space
        Assert.assertEquals("New Description", m_addressSpace.getConfiguration().getDescription());
    }

    @Test
    public void testSetName() throws CouldntSaveDataException {
        try {
            m_addressSpace.getConfiguration().setName(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        m_addressSpace.getConfiguration().setName("New Name");
        // Check listener events
        Assert.assertEquals("changedName;changedModificationDate;", m_listener.events);
        // Check address space
        Assert.assertEquals("New Name", m_addressSpace.getConfiguration().getName());
        m_addressSpace.getConfiguration().setName("New Name");
        // Check listener events
        Assert.assertEquals("changedName;changedModificationDate;", m_listener.events);
        // Check address space
        Assert.assertEquals("New Name", m_addressSpace.getConfiguration().getName());
    }
}

