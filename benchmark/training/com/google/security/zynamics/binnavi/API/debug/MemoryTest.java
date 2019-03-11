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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class MemoryTest {
    private final Memory m_internalMemory = new com.google.security.zynamics.zylib.general.memmanager.Memory();

    private final Memory m_apiMemory = new Memory(m_internalMemory);

    @Test
    public void testGetData() throws MissingDataException {
        try {
            m_apiMemory.getData(5, 0);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
        try {
            m_apiMemory.getData((-5), 5);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
        m_internalMemory.store(0, new byte[]{ 0, 1, 2, 3 });
        Assert.assertArrayEquals(new byte[]{ 0, 1, 2, 3 }, m_apiMemory.getData(0, 4));
        try {
            Assert.assertArrayEquals(new byte[]{ 0, 1, 2, 3 }, m_apiMemory.getData(0, 5));
            Assert.fail();
        } catch (final MissingDataException exception) {
        }
        Assert.assertEquals("Simulated Memory (Size: 4 Bytes)", m_apiMemory.toString());
    }

    @Test
    public void testHasData() {
        m_internalMemory.store(0, new byte[]{ 0, 1, 2, 3 });
        Assert.assertTrue(m_apiMemory.hasData(0, 4));
        Assert.assertFalse(m_apiMemory.hasData(0, 5));
    }

    @Test
    public void testListeners() {
        final MockMemoryListener mockListener = new MockMemoryListener();
        m_apiMemory.addListener(mockListener);
        m_internalMemory.store(0, new byte[]{ 1, 2, 3, 4 });
        Assert.assertEquals("changedMemory/0/4;", mockListener.events);
        m_internalMemory.clear();
        Assert.assertEquals("changedMemory/0/4;clearedMemory;", mockListener.events);
        m_apiMemory.removeListener(mockListener);
    }
}

