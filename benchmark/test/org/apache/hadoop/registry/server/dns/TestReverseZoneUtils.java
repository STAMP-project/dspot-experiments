/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.registry.server.dns;


import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for the reverse zone utilities.
 */
public class TestReverseZoneUtils {
    private static final String NET = "172.17.4.0";

    private static final int RANGE = 256;

    private static final int INDEX = 0;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetReverseZoneNetworkAddress() throws Exception {
        Assert.assertEquals("172.17.4.0", ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, TestReverseZoneUtils.RANGE, TestReverseZoneUtils.INDEX));
    }

    @Test
    public void testSplitIp() throws Exception {
        long[] splitIp = ReverseZoneUtils.splitIp(TestReverseZoneUtils.NET);
        Assert.assertEquals(172, splitIp[0]);
        Assert.assertEquals(17, splitIp[1]);
        Assert.assertEquals(4, splitIp[2]);
        Assert.assertEquals(0, splitIp[3]);
    }

    @Test
    public void testThrowIllegalArgumentExceptionIfIndexIsNegative() throws Exception {
        exception.expect(IllegalArgumentException.class);
        ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, TestReverseZoneUtils.RANGE, (-1));
    }

    @Test
    public void testThrowUnknownHostExceptionIfIpIsInvalid() throws Exception {
        exception.expect(UnknownHostException.class);
        ReverseZoneUtils.getReverseZoneNetworkAddress("213124.21231.14123.13", TestReverseZoneUtils.RANGE, TestReverseZoneUtils.INDEX);
    }

    @Test
    public void testThrowIllegalArgumentExceptionIfRangeIsNegative() throws Exception {
        exception.expect(IllegalArgumentException.class);
        ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, (-1), TestReverseZoneUtils.INDEX);
    }

    @Test
    public void testVariousRangeAndIndexValues() throws Exception {
        // Given the base address of 172.17.4.0, step 256 IP addresses, 5 times.
        Assert.assertEquals("172.17.9.0", ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, 256, 5));
        Assert.assertEquals("172.17.4.128", ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, 128, 1));
        Assert.assertEquals("172.18.0.0", ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, 256, 252));
        Assert.assertEquals("172.17.12.0", ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, 1024, 2));
        Assert.assertEquals("172.17.4.0", ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, 0, 1));
        Assert.assertEquals("172.17.4.0", ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, 1, 0));
        Assert.assertEquals("172.17.4.1", ReverseZoneUtils.getReverseZoneNetworkAddress(TestReverseZoneUtils.NET, 1, 1));
    }
}

