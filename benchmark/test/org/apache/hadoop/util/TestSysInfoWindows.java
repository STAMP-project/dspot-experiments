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
package org.apache.hadoop.util;


import CpuTimeTracker.UNAVAILABLE;
import org.junit.Assert;
import org.junit.Test;

import static SysInfoWindows.REFRESH_INTERVAL_MS;


public class TestSysInfoWindows {
    static class SysInfoWindowsMock extends SysInfoWindows {
        private long time = (REFRESH_INTERVAL_MS) + 1;

        private String infoStr = null;

        void setSysinfoString(String infoStr) {
            this.infoStr = infoStr;
        }

        void advance(long dur) {
            time += dur;
        }

        @Override
        String getSystemInfoInfoFromShell() {
            return infoStr;
        }

        @Override
        long now() {
            return time;
        }
    }

    @Test(timeout = 10000)
    public void parseSystemInfoString() {
        TestSysInfoWindows.SysInfoWindowsMock tester = new TestSysInfoWindows.SysInfoWindowsMock();
        tester.setSysinfoString(("17177038848,8589467648,15232745472,6400417792,1,2805000,6261812," + "1234567,2345678,3456789,4567890\r\n"));
        // info str derived from windows shell command has \r\n termination
        Assert.assertEquals(17177038848L, getVirtualMemorySize());
        Assert.assertEquals(8589467648L, getPhysicalMemorySize());
        Assert.assertEquals(15232745472L, getAvailableVirtualMemorySize());
        Assert.assertEquals(6400417792L, getAvailablePhysicalMemorySize());
        Assert.assertEquals(1, getNumProcessors());
        Assert.assertEquals(1, getNumCores());
        Assert.assertEquals(2805000L, getCpuFrequency());
        Assert.assertEquals(6261812L, getCumulativeCpuTime());
        Assert.assertEquals(1234567L, getStorageBytesRead());
        Assert.assertEquals(2345678L, getStorageBytesWritten());
        Assert.assertEquals(3456789L, getNetworkBytesRead());
        Assert.assertEquals(4567890L, getNetworkBytesWritten());
        // undef on first call
        Assert.assertEquals(((float) (UNAVAILABLE)), getCpuUsagePercentage(), 0.0);
        Assert.assertEquals(((float) (UNAVAILABLE)), getNumVCoresUsed(), 0.0);
    }

    @Test(timeout = 10000)
    public void refreshAndCpuUsage() throws InterruptedException {
        TestSysInfoWindows.SysInfoWindowsMock tester = new TestSysInfoWindows.SysInfoWindowsMock();
        tester.setSysinfoString(("17177038848,8589467648,15232745472,6400417792,1,2805000,6261812," + "1234567,2345678,3456789,4567890\r\n"));
        // info str derived from windows shell command has \r\n termination
        getAvailablePhysicalMemorySize();
        // verify information has been refreshed
        Assert.assertEquals(6400417792L, getAvailablePhysicalMemorySize());
        Assert.assertEquals(((float) (UNAVAILABLE)), getCpuUsagePercentage(), 0.0);
        Assert.assertEquals(((float) (UNAVAILABLE)), getNumVCoresUsed(), 0.0);
        tester.setSysinfoString(("17177038848,8589467648,15232745472,5400417792,1,2805000,6263012," + "1234567,2345678,3456789,4567890\r\n"));
        getAvailablePhysicalMemorySize();
        // verify information has not been refreshed
        Assert.assertEquals(6400417792L, getAvailablePhysicalMemorySize());
        Assert.assertEquals(((float) (UNAVAILABLE)), getCpuUsagePercentage(), 0.0);
        Assert.assertEquals(((float) (UNAVAILABLE)), getNumVCoresUsed(), 0.0);
        // advance clock
        tester.advance(((SysInfoWindows.REFRESH_INTERVAL_MS) + 1));
        // verify information has been refreshed
        Assert.assertEquals(5400417792L, getAvailablePhysicalMemorySize());
        Assert.assertEquals(((((6263012 - 6261812) * 100.0F) / ((SysInfoWindows.REFRESH_INTERVAL_MS) + 1.0F)) / 1), getCpuUsagePercentage(), 0.0);
        Assert.assertEquals((((6263012 - 6261812) / ((SysInfoWindows.REFRESH_INTERVAL_MS) + 1.0F)) / 1), getNumVCoresUsed(), 0.0);
    }

    @Test(timeout = 10000)
    public void refreshAndCpuUsageMulticore() throws InterruptedException {
        // test with 12 cores
        TestSysInfoWindows.SysInfoWindowsMock tester = new TestSysInfoWindows.SysInfoWindowsMock();
        tester.setSysinfoString(("17177038848,8589467648,15232745472,6400417792,12,2805000,6261812," + "1234567,2345678,3456789,4567890\r\n"));
        // verify information has been refreshed
        Assert.assertEquals(6400417792L, getAvailablePhysicalMemorySize());
        tester.setSysinfoString(("17177038848,8589467648,15232745472,5400417792,12,2805000,6263012," + "1234567,2345678,3456789,4567890\r\n"));
        // verify information has not been refreshed
        Assert.assertEquals(6400417792L, getAvailablePhysicalMemorySize());
        // advance clock
        tester.advance(((SysInfoWindows.REFRESH_INTERVAL_MS) + 1));
        // verify information has been refreshed
        Assert.assertEquals(5400417792L, getAvailablePhysicalMemorySize());
        // verify information has been refreshed
        Assert.assertEquals(((((6263012 - 6261812) * 100.0F) / ((SysInfoWindows.REFRESH_INTERVAL_MS) + 1.0F)) / 12), getCpuUsagePercentage(), 0.0);
        Assert.assertEquals(((6263012 - 6261812) / ((SysInfoWindows.REFRESH_INTERVAL_MS) + 1.0F)), getNumVCoresUsed(), 0.0);
    }

    @Test(timeout = 10000)
    public void errorInGetSystemInfo() {
        TestSysInfoWindows.SysInfoWindowsMock tester = new TestSysInfoWindows.SysInfoWindowsMock();
        // info str derived from windows shell command is null
        tester.setSysinfoString(null);
        // call a method to refresh values
        getAvailablePhysicalMemorySize();
        // info str derived from windows shell command with no \r\n termination
        tester.setSysinfoString("");
        // call a method to refresh values
        getAvailablePhysicalMemorySize();
    }
}

