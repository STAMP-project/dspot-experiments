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
package com.google.security.zynamics.binnavi.Debug.Debugger;


import com.google.security.zynamics.binnavi.debug.debugger.DebugExceptionWrapper;
import com.google.security.zynamics.binnavi.debug.debugger.MemoryLoader;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CMemoryLoaderTest {
    private MockDebugger debugger;

    private MemoryLoader loader;

    @Test
    public void testRequest_Duplicate() throws DebugExceptionWrapper {
        // Request if the data does not exist
        loader.requestMemory(new CAddress(BigInteger.valueOf(0)), 100);
        // assertEquals("READMEM-0-100;", debugger.requests);
        // Do not request duplicates
        loader.requestMemory(new CAddress(BigInteger.valueOf(0)), 100);
        // assertEquals("READMEM-0-100;", debugger.requests);
    }

    @Test
    public void testRequest_Existing() throws DebugExceptionWrapper {
        getProcessManager().getMemory().store(0, new byte[100]);
        // Do not request if the data is here
        loader.requestMemory(new CAddress(BigInteger.valueOf(0)), 100);
        Assert.assertEquals("", debugger.requests);
    }

    @Test
    public void testRequest_Partially() throws DebugExceptionWrapper {
        getProcessManager().getMemory().store(0, new byte[34]);
        getProcessManager().getMemory().store(60, new byte[34]);
        // Request only the parts that aren't there
        loader.requestMemory(new CAddress(BigInteger.valueOf(0)), 100);
        // assertEquals("READMEM-34-26;READMEM-94-6;", debugger.requests);
    }
}

