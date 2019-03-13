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
package org.apache.ambari.server.checks;


import UpgradeCheckStatus.FAIL;
import UpgradeCheckStatus.PASS;
import org.apache.ambari.server.stack.upgrade.orchestrate.UpgradeHelper;
import org.apache.ambari.spi.upgrade.UpgradeCheckResult;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class KerberosAdminPersistedCredentialCheckTest extends EasyMockSupport {
    @Mock
    private UpgradeHelper upgradeHelper;

    @Test
    public void testMissingCredentialStoreKerberosEnabledManagingIdentities() throws Exception {
        UpgradeCheckResult result = executeCheck(true, true, false, false);
        Assert.assertEquals(FAIL, result.getStatus());
        Assert.assertTrue(result.getFailReason().startsWith("Ambari's credential store has not been configured."));
    }

    @Test
    public void testMissingCredentialStoreKerberosEnabledNotManagingIdentities() throws Exception {
        UpgradeCheckResult result = executeCheck(true, false, false, false);
        Assert.assertEquals(PASS, result.getStatus());
    }

    @Test
    public void testMissingCredentialStoreKerberosNotEnabled() throws Exception {
        UpgradeCheckResult result = executeCheck(false, false, false, false);
        Assert.assertEquals(PASS, result.getStatus());
    }

    @Test
    public void testMissingCredentialKerberosEnabledManagingIdentities() throws Exception {
        UpgradeCheckResult result = executeCheck(true, true, true, false);
        Assert.assertEquals(FAIL, result.getStatus());
        Assert.assertTrue(result.getFailReason().startsWith("The KDC administrator credential has not been stored in the persisted credential store."));
    }

    @Test
    public void testMissingCredentialKerberosEnabledNotManagingIdentities() throws Exception {
        UpgradeCheckResult result = executeCheck(true, false, true, false);
        Assert.assertEquals(PASS, result.getStatus());
    }

    @Test
    public void testMissingCredentialKerberosNotEnabled() throws Exception {
        UpgradeCheckResult result = executeCheck(false, true, true, false);
        Assert.assertEquals(PASS, result.getStatus());
    }

    @Test
    public void testCredentialsSetKerberosNotEnabled() throws Exception {
        UpgradeCheckResult result = executeCheck(false, false, true, true);
        Assert.assertEquals(PASS, result.getStatus());
    }

    @Test
    public void testCredentialsSetKerberosEnabledNotManagingIdentities() throws Exception {
        UpgradeCheckResult result = executeCheck(true, false, true, true);
        Assert.assertEquals(PASS, result.getStatus());
    }

    @Test
    public void testCredentialsSetKerberosEnabledManagingIdentities() throws Exception {
        UpgradeCheckResult result = executeCheck(true, true, true, true);
        Assert.assertEquals(PASS, result.getStatus());
    }
}

