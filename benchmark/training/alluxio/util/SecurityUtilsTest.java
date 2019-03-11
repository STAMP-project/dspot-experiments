/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.util;


import AuthType.NOSASL;
import AuthType.SIMPLE;
import PropertyKey.SECURITY_AUTHENTICATION_TYPE;
import PropertyKey.SECURITY_GROUP_MAPPING_CLASS;
import PropertyKey.SECURITY_LOGIN_USERNAME;
import alluxio.conf.InstancedConfiguration;
import alluxio.security.LoginUserTestUtils;
import alluxio.security.authentication.AuthenticatedClientUser;
import alluxio.security.group.provider.IdentityUserGroupsMapping;
import org.junit.Assert;
import org.junit.Test;


public final class SecurityUtilsTest {
    private InstancedConfiguration mConfiguration;

    /**
     * Tests the {@link SecurityUtils#getOwnerFromGrpcClient()} ()} method.
     */
    @Test
    public void getOwnerFromGrpcClient() throws Exception {
        // When security is not enabled, user and group are not set
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, NOSASL.getAuthName());
        Assert.assertEquals("", SecurityUtils.getOwnerFromGrpcClient(mConfiguration));
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, SIMPLE.getAuthName());
        mConfiguration.set(SECURITY_GROUP_MAPPING_CLASS, IdentityUserGroupsMapping.class.getName());
        AuthenticatedClientUser.set("test_client_user");
        Assert.assertEquals("test_client_user", SecurityUtils.getOwnerFromGrpcClient(mConfiguration));
    }

    /**
     * Tests the {@link SecurityUtils#getGroupFromGrpcClient()} ()} method.
     */
    @Test
    public void getGroupFromGrpcClient() throws Exception {
        // When security is not enabled, user and group are not set
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, NOSASL.getAuthName());
        Assert.assertEquals("", SecurityUtils.getGroupFromGrpcClient(mConfiguration));
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, SIMPLE.getAuthName());
        mConfiguration.set(SECURITY_GROUP_MAPPING_CLASS, IdentityUserGroupsMapping.class.getName());
        AuthenticatedClientUser.set("test_client_user");
        Assert.assertEquals("test_client_user", SecurityUtils.getGroupFromGrpcClient(mConfiguration));
    }

    /**
     * Tests the {@link SecurityUtils#getOwnerFromLoginModule()} method.
     */
    @Test
    public void getOwnerFromLoginModule() throws Exception {
        // When security is not enabled, user and group are not set
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, NOSASL.getAuthName());
        Assert.assertEquals("", SecurityUtils.getOwnerFromLoginModule(mConfiguration));
        // When authentication is enabled, user and group are inferred from login module
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, SIMPLE.getAuthName());
        mConfiguration.set(SECURITY_LOGIN_USERNAME, "test_login_user");
        mConfiguration.set(SECURITY_GROUP_MAPPING_CLASS, IdentityUserGroupsMapping.class.getName());
        Assert.assertEquals("test_login_user", SecurityUtils.getOwnerFromLoginModule(mConfiguration));
    }

    /**
     * Tests the {@link SecurityUtils#getGroupFromLoginModule()} method.
     */
    @Test
    public void getGroupFromLoginModuleError() throws Exception {
        // When security is not enabled, user and group are not set
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, NOSASL.getAuthName());
        Assert.assertEquals("", SecurityUtils.getGroupFromLoginModule(mConfiguration));
        // When authentication is enabled, user and group are inferred from login module
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, SIMPLE.getAuthName());
        mConfiguration.set(SECURITY_LOGIN_USERNAME, "test_login_user");
        mConfiguration.set(SECURITY_GROUP_MAPPING_CLASS, IdentityUserGroupsMapping.class.getName());
        LoginUserTestUtils.resetLoginUser();
        Assert.assertEquals("test_login_user", SecurityUtils.getGroupFromLoginModule(mConfiguration));
    }
}

