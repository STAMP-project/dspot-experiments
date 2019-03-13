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


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntDeleteException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.disassembly.MockCreator;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModule;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CAddressSpaceContentTest {
    private CAddressSpace m_addressSpace;

    private final MockAddressSpaceContentListener m_listener = new MockAddressSpaceContentListener();

    private MockSqlProvider m_sql;

    @Test
    public void testAddModule() throws CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        final CModule module = MockCreator.createModule(m_sql);
        module.getConfiguration().setImageBase(new CAddress(12322));
        try {
            m_addressSpace.getContent().addModule(module);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        m_addressSpace.load();
        m_addressSpace.getContent().addListener(m_listener);
        try {
            m_addressSpace.getContent().addModule(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        m_addressSpace.getContent().addModule(module);
        // Check listener
        Assert.assertEquals("addedModule;changedImageBase;", m_listener.events);
        // Check address space
        Assert.assertEquals(1, m_addressSpace.getModuleCount());
        Assert.assertEquals(module, m_addressSpace.getContent().getModules().get(0));
        Assert.assertEquals(BigInteger.valueOf(12322), m_addressSpace.getContent().getImageBase(module).toBigInteger());
        try {
            m_addressSpace.getContent().addModule(module);
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
    }

    @Test
    public void testRelocation() throws CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        final CModule module = MockCreator.createModule(m_sql);
        m_addressSpace.load();
        m_addressSpace.getContent().addListener(m_listener);
        try {
            m_addressSpace.getContent().setImageBase(module, null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            m_addressSpace.getContent().setImageBase(null, new CAddress(291));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            m_addressSpace.getContent().setImageBase(module, new CAddress(291));
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        m_addressSpace.getContent().addModule(module);
        // Check the address space
        Assert.assertEquals("00000666", m_addressSpace.getContent().getImageBase(module).toHexString());
        m_addressSpace.getContent().setImageBase(module, new CAddress(291));
        // Check the listener events
        Assert.assertEquals("addedModule;changedImageBase;changedImageBase;", m_listener.events);
        // Check the address space
        Assert.assertEquals("00000123", m_addressSpace.getContent().getImageBase(module).toHexString());
        Assert.assertEquals("00000666", module.getConfiguration().getImageBase().toHexString());
    }

    @Test
    public void testRemoveModule() throws CouldntDeleteException, CouldntLoadDataException, CouldntSaveDataException, LoadCancelledException {
        final CModule module = MockCreator.createModule(m_sql);
        m_addressSpace.load();
        m_addressSpace.getContent().addListener(m_listener);
        try {
            m_addressSpace.getContent().removeModule(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            m_addressSpace.getContent().removeModule(module);
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        m_addressSpace.getContent().addModule(module);
        m_addressSpace.getContent().removeModule(module);
        // Check the listener events
        Assert.assertEquals("addedModule;changedImageBase;removedModule;", m_listener.events);
        // Check the address space
        Assert.assertEquals(0, m_addressSpace.getModuleCount());
    }
}

