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


import com.google.security.zynamics.binnavi.debug.models.processmanager.MemorySection;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class MemorySectionTest {
    @Test
    public void testConstructor() {
        final MemorySection internalSection = new MemorySection(new CAddress(512), new CAddress(1024));
        final MemorySection section = new MemorySection(internalSection);
        Assert.assertEquals(512, section.getStart().toLong());
        Assert.assertEquals(1024, section.getEnd().toLong());
        Assert.assertEquals("Memory Section [200 - 400]", section.toString());
    }
}

