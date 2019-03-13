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
package org.apache.hadoop.yarn.util;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ResourceCalculatorProcessTree.UNAVAILABLE;


public class TestWindowsBasedProcessTree {
    private static final Logger LOG = LoggerFactory.getLogger(TestWindowsBasedProcessTree.class);

    class WindowsBasedProcessTreeTester extends WindowsBasedProcessTree {
        String infoStr = null;

        public WindowsBasedProcessTreeTester(String pid, Clock clock) {
            super(pid, clock);
        }

        @Override
        String getAllProcessInfoFromShell() {
            return infoStr;
        }
    }

    @Test(timeout = 30000)
    @SuppressWarnings("deprecation")
    public void tree() {
        assumeWindows();
        Assert.assertTrue("WindowsBasedProcessTree should be available on Windows", WindowsBasedProcessTree.isAvailable());
        ControlledClock testClock = new ControlledClock();
        long elapsedTimeBetweenUpdatesMsec = 0;
        testClock.setTime(elapsedTimeBetweenUpdatesMsec);
        TestWindowsBasedProcessTree.WindowsBasedProcessTreeTester pTree = new TestWindowsBasedProcessTree.WindowsBasedProcessTreeTester("-1", testClock);
        pTree.infoStr = "3524,1024,1024,500\r\n2844,1024,1024,500\r\n";
        updateProcessTree();
        Assert.assertTrue(((pTree.getVirtualMemorySize()) == 2048));
        Assert.assertTrue(((getVirtualMemorySize(0)) == 2048));
        Assert.assertTrue(((pTree.getRssMemorySize()) == 2048));
        Assert.assertTrue(((getRssMemorySize(0)) == 2048));
        Assert.assertTrue(((getCumulativeCpuTime()) == 1000));
        Assert.assertTrue(((getCpuUsagePercent()) == (UNAVAILABLE)));
        pTree.infoStr = "3524,1024,1024,1000\r\n2844,1024,1024,1000\r\n1234,1024,1024,1000\r\n";
        elapsedTimeBetweenUpdatesMsec = 1000;
        testClock.setTime(elapsedTimeBetweenUpdatesMsec);
        updateProcessTree();
        Assert.assertTrue(((pTree.getVirtualMemorySize()) == 3072));
        Assert.assertTrue(((getVirtualMemorySize(1)) == 2048));
        Assert.assertTrue(((pTree.getRssMemorySize()) == 3072));
        Assert.assertTrue(((getRssMemorySize(1)) == 2048));
        Assert.assertTrue(((getCumulativeCpuTime()) == 3000));
        Assert.assertTrue(((getCpuUsagePercent()) == 200));
        Assert.assertEquals("Percent CPU time is not correct", getCpuUsagePercent(), 200, 0.01);
        pTree.infoStr = "3524,1024,1024,1500\r\n2844,1024,1024,1500\r\n";
        elapsedTimeBetweenUpdatesMsec = 2000;
        testClock.setTime(elapsedTimeBetweenUpdatesMsec);
        updateProcessTree();
        Assert.assertTrue(((pTree.getVirtualMemorySize()) == 2048));
        Assert.assertTrue(((getVirtualMemorySize(2)) == 2048));
        Assert.assertTrue(((pTree.getRssMemorySize()) == 2048));
        Assert.assertTrue(((getRssMemorySize(2)) == 2048));
        Assert.assertTrue(((getCumulativeCpuTime()) == 4000));
        Assert.assertEquals("Percent CPU time is not correct", getCpuUsagePercent(), 0, 0.01);
    }
}

