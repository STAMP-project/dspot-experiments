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
package org.apache.hadoop.fs.azurebfs;


import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.azurebfs.constants.AbfsHttpConstants;
import org.apache.hadoop.fs.azurebfs.constants.ConfigurationKeys;
import org.apache.hadoop.fs.azurebfs.oauth2.IdentityTransformer;
import org.apache.hadoop.fs.azurebfs.utils.AclTestHelpers;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test IdentityTransformer.
 */
// @RunWith(Parameterized.class)
public class ITestAbfsIdentityTransformer extends AbstractAbfsScaleTest {
    private final UserGroupInformation userGroupInfo;

    private final String localUser;

    private final String localGroup;

    private static final String DAEMON = "daemon";

    private static final String ASTERISK = "*";

    private static final String SHORT_NAME = "abc";

    private static final String DOMAIN = "domain.com";

    private static final String FULLY_QUALIFIED_NAME = "abc@domain.com";

    private static final String SERVICE_PRINCIPAL_ID = UUID.randomUUID().toString();

    public ITestAbfsIdentityTransformer() throws Exception {
        super();
        UserGroupInformation.reset();
        userGroupInfo = UserGroupInformation.getCurrentUser();
        localUser = userGroupInfo.getShortUserName();
        localGroup = userGroupInfo.getPrimaryGroupName();
    }

    @Test
    public void testDaemonServiceSettingIdentity() throws IOException {
        Configuration config = this.getRawConfiguration();
        resetIdentityConfig(config);
        // Default config
        IdentityTransformer identityTransformer = getTransformerWithDefaultIdentityConfig(config);
        Assert.assertEquals("Identity should not change for default config", ITestAbfsIdentityTransformer.DAEMON, identityTransformer.transformUserOrGroupForSetRequest(ITestAbfsIdentityTransformer.DAEMON));
        // Add service principal id
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP, ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID);
        // case 1: substitution list doesn't contain daemon
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, "a,b,c,d");
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("Identity should not change when substitution list doesn't contain daemon", ITestAbfsIdentityTransformer.DAEMON, identityTransformer.transformUserOrGroupForSetRequest(ITestAbfsIdentityTransformer.DAEMON));
        // case 2: substitution list contains daemon name
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, ((ITestAbfsIdentityTransformer.DAEMON) + ",a,b,c,d"));
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("Identity should be replaced to servicePrincipalId", ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, identityTransformer.transformUserOrGroupForSetRequest(ITestAbfsIdentityTransformer.DAEMON));
        // case 3: substitution list is *
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, ITestAbfsIdentityTransformer.ASTERISK);
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("Identity should be replaced to servicePrincipalId", ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, identityTransformer.transformUserOrGroupForSetRequest(ITestAbfsIdentityTransformer.DAEMON));
    }

    @Test
    public void testFullyQualifiedNameSettingIdentity() throws IOException {
        Configuration config = this.getRawConfiguration();
        // Default config
        IdentityTransformer identityTransformer = getTransformerWithDefaultIdentityConfig(config);
        Assert.assertEquals("short name should not be converted to full name by default", ITestAbfsIdentityTransformer.SHORT_NAME, identityTransformer.transformUserOrGroupForSetRequest(ITestAbfsIdentityTransformer.SHORT_NAME));
        resetIdentityConfig(config);
        // Add config to get fully qualified username
        config.setBoolean(ConfigurationKeys.FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME, true);
        config.set(ConfigurationKeys.FS_AZURE_FILE_OWNER_DOMAINNAME, ITestAbfsIdentityTransformer.DOMAIN);
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("short name should be converted to full name", ITestAbfsIdentityTransformer.FULLY_QUALIFIED_NAME, identityTransformer.transformUserOrGroupForSetRequest(ITestAbfsIdentityTransformer.SHORT_NAME));
    }

    @Test
    public void testNoOpForSettingOidAsIdentity() throws IOException {
        Configuration config = this.getRawConfiguration();
        resetIdentityConfig(config);
        config.setBoolean(ConfigurationKeys.FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME, true);
        config.set(ConfigurationKeys.FS_AZURE_FILE_OWNER_DOMAINNAME, ITestAbfsIdentityTransformer.DOMAIN);
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP, UUID.randomUUID().toString());
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, "a,b,c,d");
        IdentityTransformer identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        final String principalId = UUID.randomUUID().toString();
        Assert.assertEquals("Identity should not be changed when owner is already a principal id ", principalId, identityTransformer.transformUserOrGroupForSetRequest(principalId));
    }

    @Test
    public void testNoOpWhenSettingSuperUserAsdentity() throws IOException {
        Configuration config = this.getRawConfiguration();
        resetIdentityConfig(config);
        config.setBoolean(ConfigurationKeys.FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME, true);
        config.set(ConfigurationKeys.FS_AZURE_FILE_OWNER_DOMAINNAME, ITestAbfsIdentityTransformer.DOMAIN);
        // Default config
        IdentityTransformer identityTransformer = getTransformerWithDefaultIdentityConfig(config);
        Assert.assertEquals("Identity should not be changed because it is not in substitution list", AbfsHttpConstants.SUPER_USER, identityTransformer.transformUserOrGroupForSetRequest(AbfsHttpConstants.SUPER_USER));
    }

    @Test
    public void testIdentityReplacementForSuperUserGetRequest() throws IOException {
        Configuration config = this.getRawConfiguration();
        resetIdentityConfig(config);
        // with default config, identityTransformer should do $superUser replacement
        IdentityTransformer identityTransformer = getTransformerWithDefaultIdentityConfig(config);
        Assert.assertEquals("$superuser should be replaced with local user by default", localUser, identityTransformer.transformIdentityForGetRequest(AbfsHttpConstants.SUPER_USER, true, localUser));
        // Disable $supeuser replacement
        config.setBoolean(ConfigurationKeys.FS_AZURE_SKIP_SUPER_USER_REPLACEMENT, true);
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("$superuser should not be replaced", AbfsHttpConstants.SUPER_USER, identityTransformer.transformIdentityForGetRequest(AbfsHttpConstants.SUPER_USER, true, localUser));
    }

    @Test
    public void testIdentityReplacementForDaemonServiceGetRequest() throws IOException {
        Configuration config = this.getRawConfiguration();
        resetIdentityConfig(config);
        // Default config
        IdentityTransformer identityTransformer = getTransformerWithDefaultIdentityConfig(config);
        Assert.assertEquals("By default servicePrincipalId should not be converted for GetFileStatus(), listFileStatus(), getAcl()", ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, identityTransformer.transformIdentityForGetRequest(ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, true, localUser));
        resetIdentityConfig(config);
        // 1. substitution list doesn't contain currentUser
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, "a,b,c,d");
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("servicePrincipalId should not be replaced if local daemon user is not in substitution list", ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, identityTransformer.transformIdentityForGetRequest(ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, true, localUser));
        resetIdentityConfig(config);
        // 2. substitution list contains currentUser(daemon name) but the service principal id in config doesn't match
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, ((localUser) + ",a,b,c,d"));
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP, UUID.randomUUID().toString());
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("servicePrincipalId should not be replaced if it is not equal to the SPN set in config", ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, identityTransformer.transformIdentityForGetRequest(ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, true, localUser));
        resetIdentityConfig(config);
        // 3. substitution list contains currentUser(daemon name) and the service principal id in config matches
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, ((localUser) + ",a,b,c,d"));
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP, ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID);
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("servicePrincipalId should be transformed to local use", localUser, identityTransformer.transformIdentityForGetRequest(ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, true, localUser));
        resetIdentityConfig(config);
        // 4. substitution is "*" but the service principal id in config doesn't match the input
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, ITestAbfsIdentityTransformer.ASTERISK);
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP, UUID.randomUUID().toString());
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("servicePrincipalId should not be replaced if it is not equal to the SPN set in config", ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, identityTransformer.transformIdentityForGetRequest(ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, true, localUser));
        resetIdentityConfig(config);
        // 5. substitution is "*" and the service principal id in config match the input
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, ITestAbfsIdentityTransformer.ASTERISK);
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP, ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID);
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("servicePrincipalId should be transformed to local user", localUser, identityTransformer.transformIdentityForGetRequest(ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, true, localUser));
    }

    @Test
    public void testIdentityReplacementForKinitUserGetRequest() throws IOException {
        Configuration config = this.getRawConfiguration();
        resetIdentityConfig(config);
        // Default config
        IdentityTransformer identityTransformer = getTransformerWithDefaultIdentityConfig(config);
        Assert.assertEquals("full name should not be transformed if shortname is not enabled", ITestAbfsIdentityTransformer.FULLY_QUALIFIED_NAME, identityTransformer.transformIdentityForGetRequest(ITestAbfsIdentityTransformer.FULLY_QUALIFIED_NAME, true, localUser));
        // add config to get short name
        config.setBoolean(ConfigurationKeys.FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME, true);
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        Assert.assertEquals("should convert the full owner name to shortname ", ITestAbfsIdentityTransformer.SHORT_NAME, identityTransformer.transformIdentityForGetRequest(ITestAbfsIdentityTransformer.FULLY_QUALIFIED_NAME, true, localUser));
        Assert.assertEquals("group name should not be converted to shortname ", ITestAbfsIdentityTransformer.FULLY_QUALIFIED_NAME, identityTransformer.transformIdentityForGetRequest(ITestAbfsIdentityTransformer.FULLY_QUALIFIED_NAME, false, localGroup));
    }

    @Test
    public void transformAclEntriesForSetRequest() throws IOException {
        Configuration config = this.getRawConfiguration();
        resetIdentityConfig(config);
        List<AclEntry> aclEntriesToBeTransformed = // Notice: for group type ACL entry, if name is shortName,
        // It won't be converted to Full Name. This is
        // to make the behavior consistent with HDI.
        Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAbfsIdentityTransformer.DAEMON, ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAbfsIdentityTransformer.FULLY_QUALIFIED_NAME, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, AbfsHttpConstants.SUPER_USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAbfsIdentityTransformer.SHORT_NAME, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAbfsIdentityTransformer.DAEMON, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAbfsIdentityTransformer.SHORT_NAME, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, ALL), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL));
        // Default config should not change the identities
        IdentityTransformer identityTransformer = getTransformerWithDefaultIdentityConfig(config);
        checkAclEntriesList(aclEntriesToBeTransformed, identityTransformer.transformAclEntriesForSetRequest(aclEntriesToBeTransformed));
        resetIdentityConfig(config);
        // With config
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP_LIST, ((ITestAbfsIdentityTransformer.DAEMON) + ",a,b,c,d"));
        config.setBoolean(ConfigurationKeys.FS_AZURE_FILE_OWNER_ENABLE_SHORTNAME, true);
        config.set(ConfigurationKeys.FS_AZURE_FILE_OWNER_DOMAINNAME, ITestAbfsIdentityTransformer.DOMAIN);
        config.set(ConfigurationKeys.FS_AZURE_OVERRIDE_OWNER_SP, ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID);
        identityTransformer = getTransformerWithCustomizedIdentityConfig(config);
        // expected acl entries
        List<AclEntry> expectedAclEntries = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAbfsIdentityTransformer.FULLY_QUALIFIED_NAME, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, AbfsHttpConstants.SUPER_USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAbfsIdentityTransformer.FULLY_QUALIFIED_NAME, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAbfsIdentityTransformer.SERVICE_PRINCIPAL_ID, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAbfsIdentityTransformer.SHORT_NAME, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, ALL), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL));
        checkAclEntriesList(identityTransformer.transformAclEntriesForSetRequest(aclEntriesToBeTransformed), expectedAclEntries);
    }
}

