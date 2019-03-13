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
package org.apache.nifi.cluster.firewall.impl;


import java.io.File;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileBasedClusterNodeFirewallTest {
    private FileBasedClusterNodeFirewall ipsFirewall;

    private FileBasedClusterNodeFirewall acceptAllFirewall;

    private File ipsConfig;

    private File emptyConfig;

    private File restoreDirectory;

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    private static final String NONEXISTENT_HOSTNAME = "abc";

    private static boolean badHostsDoNotResolve = false;

    /**
     * We have two garbage lines in our test config file, ensure they didn't get turned into hosts.
     */
    @Test
    public void ensureBadDataWasIgnored() {
        Assume.assumeTrue(FileBasedClusterNodeFirewallTest.badHostsDoNotResolve);
        Assert.assertFalse(("firewall treated our malformed data as a host. If " + ("`host \"bad data should be skipped\"` works locally, this test should have been " + "skipped.")), ipsFirewall.isPermissible("bad data should be skipped"));
        Assert.assertFalse(("firewall treated our malformed data as a host. If " + ("`host \"more bad data\"` works locally, this test should have been " + "skipped.")), ipsFirewall.isPermissible("more bad data"));
    }

    @Test
    public void testSyncWithRestore() {
        Assert.assertEquals(ipsConfig.length(), new File(restoreDirectory, ipsConfig.getName()).length());
    }

    @Test
    public void testIsPermissibleWithExactMatch() {
        Assert.assertTrue(ipsFirewall.isPermissible("2.2.2.2"));
    }

    @Test
    public void testIsPermissibleWithSubnetMatch() {
        Assert.assertTrue(ipsFirewall.isPermissible("3.3.3.255"));
    }

    @Test
    public void testIsPermissibleWithNoMatch() {
        Assert.assertFalse(ipsFirewall.isPermissible("255.255.255.255"));
    }

    @Test
    public void testIsPermissibleWithMalformedData() {
        Assume.assumeTrue(FileBasedClusterNodeFirewallTest.badHostsDoNotResolve);
        Assert.assertFalse(((((("firewall allowed host '" + (FileBasedClusterNodeFirewallTest.NONEXISTENT_HOSTNAME)) + "' rather than rejecting as malformed. If `host ") + (FileBasedClusterNodeFirewallTest.NONEXISTENT_HOSTNAME)) + "` ") + "works locally, this test should have been skipped."), ipsFirewall.isPermissible(FileBasedClusterNodeFirewallTest.NONEXISTENT_HOSTNAME));
    }

    @Test
    public void testIsPermissibleWithEmptyConfig() {
        Assert.assertTrue(acceptAllFirewall.isPermissible("1.1.1.1"));
    }

    @Test
    public void testIsPermissibleWithEmptyConfigWithMalformedData() {
        Assume.assumeTrue(FileBasedClusterNodeFirewallTest.badHostsDoNotResolve);
        Assert.assertTrue(((((("firewall did not allow malformed host '" + (FileBasedClusterNodeFirewallTest.NONEXISTENT_HOSTNAME)) + "' under permissive configs. If ") + "`host ") + (FileBasedClusterNodeFirewallTest.NONEXISTENT_HOSTNAME)) + "` works locally, this test should have been skipped."), acceptAllFirewall.isPermissible(FileBasedClusterNodeFirewallTest.NONEXISTENT_HOSTNAME));
    }
}

