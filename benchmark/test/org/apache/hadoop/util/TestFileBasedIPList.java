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


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestFileBasedIPList {
    /**
     * Add a bunch of IPS  to the file
     * Check  for inclusion
     * Check for exclusion
     */
    @Test
    public void testSubnetsAndIPs() throws IOException {
        String[] ips = new String[]{ "10.119.103.112", "10.221.102.0/23" };
        TestFileBasedIPList.createFileWithEntries("ips.txt", ips);
        IPList ipList = new FileBasedIPList("ips.txt");
        Assert.assertTrue("10.119.103.112 is not in the list", ipList.isIn("10.119.103.112"));
        Assert.assertFalse("10.119.103.113 is in the list", ipList.isIn("10.119.103.113"));
        Assert.assertTrue("10.221.102.0 is not in the list", ipList.isIn("10.221.102.0"));
        Assert.assertTrue("10.221.102.1 is not in the list", ipList.isIn("10.221.102.1"));
        Assert.assertTrue("10.221.103.1 is not in the list", ipList.isIn("10.221.103.1"));
        Assert.assertTrue("10.221.103.255 is not in the list", ipList.isIn("10.221.103.255"));
        Assert.assertFalse("10.221.104.0 is in the list", ipList.isIn("10.221.104.0"));
        Assert.assertFalse("10.221.104.1 is in the list", ipList.isIn("10.221.104.1"));
    }

    /**
     * Add a bunch of IPS  to the file
     * Check  for inclusion
     * Check for exclusion
     */
    @Test
    public void testNullIP() throws IOException {
        String[] ips = new String[]{ "10.119.103.112", "10.221.102.0/23" };
        TestFileBasedIPList.createFileWithEntries("ips.txt", ips);
        IPList ipList = new FileBasedIPList("ips.txt");
        Assert.assertFalse("Null Ip is in the list", ipList.isIn(null));
    }

    /**
     * Add a bunch of subnets and IPSs to the file
     * Check  for inclusion
     * Check for exclusion
     */
    @Test
    public void testWithMultipleSubnetAndIPs() throws IOException {
        String[] ips = new String[]{ "10.119.103.112", "10.221.102.0/23", "10.222.0.0/16", "10.113.221.221" };
        TestFileBasedIPList.createFileWithEntries("ips.txt", ips);
        IPList ipList = new FileBasedIPList("ips.txt");
        Assert.assertTrue("10.119.103.112 is not in the list", ipList.isIn("10.119.103.112"));
        Assert.assertFalse("10.119.103.113 is in the list", ipList.isIn("10.119.103.113"));
        Assert.assertTrue("10.221.103.121 is not in the list", ipList.isIn("10.221.103.121"));
        Assert.assertFalse("10.221.104.0 is in the list", ipList.isIn("10.221.104.0"));
        Assert.assertTrue("10.222.103.121 is not in the list", ipList.isIn("10.222.103.121"));
        Assert.assertFalse("10.223.104.0 is in the list", ipList.isIn("10.223.104.0"));
        Assert.assertTrue("10.113.221.221 is not in the list", ipList.isIn("10.113.221.221"));
        Assert.assertFalse("10.113.221.222 is in the list", ipList.isIn("10.113.221.222"));
    }

    /**
     * Do not specify the file
     * test for inclusion
     * should be true as if the feature is turned off
     */
    @Test
    public void testFileNotSpecified() {
        IPList ipl = new FileBasedIPList(null);
        Assert.assertFalse("110.113.221.222 is in the list", ipl.isIn("110.113.221.222"));
    }

    /**
     * Specify a non existent file
     * test for inclusion
     * should be true as if the feature is turned off
     */
    @Test
    public void testFileMissing() {
        IPList ipl = new FileBasedIPList("missingips.txt");
        Assert.assertFalse("110.113.221.222 is in the list", ipl.isIn("110.113.221.222"));
    }

    /**
     * Specify an existing file, but empty
     * test for inclusion
     * should be true as if the feature is turned off
     */
    @Test
    public void testWithEmptyList() throws IOException {
        String[] ips = new String[]{  };
        TestFileBasedIPList.createFileWithEntries("ips.txt", ips);
        IPList ipl = new FileBasedIPList("ips.txt");
        Assert.assertFalse("110.113.221.222 is in the list", ipl.isIn("110.113.221.222"));
    }

    /**
     * Specify an existing file, but ips in wrong format
     * test for inclusion
     * should be true as if the feature is turned off
     */
    @Test
    public void testForBadFIle() throws IOException {
        String[] ips = new String[]{ "10.221.102/23" };
        TestFileBasedIPList.createFileWithEntries("ips.txt", ips);
        try {
            new FileBasedIPList("ips.txt");
            Assert.fail();
        } catch (Exception e) {
            // expects Exception
        }
    }

    /**
     * Add a bunch of subnets and IPSs to the file. Keep one entry wrong.
     * The good entries will still be used.
     * Check  for inclusion with good entries
     * Check for exclusion
     */
    @Test
    public void testWithAWrongEntry() throws IOException {
        String[] ips = new String[]{ "10.119.103.112", "10.221.102/23", "10.221.204.1/23" };
        TestFileBasedIPList.createFileWithEntries("ips.txt", ips);
        try {
            new FileBasedIPList("ips.txt");
            Assert.fail();
        } catch (Exception e) {
            // expects Exception
        }
    }
}

