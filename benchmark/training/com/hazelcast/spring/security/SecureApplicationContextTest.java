/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.spring.security;


import LoginModuleUsage.OPTIONAL;
import LoginModuleUsage.REQUIRED;
import com.hazelcast.config.Config;
import com.hazelcast.config.LoginModuleConfig;
import com.hazelcast.config.PermissionConfig;
import com.hazelcast.config.SecurityConfig;
import com.hazelcast.config.SecurityInterceptorConfig;
import com.hazelcast.security.ICredentialsFactory;
import com.hazelcast.security.IPermissionPolicy;
import com.hazelcast.security.SecurityInterceptor;
import com.hazelcast.spring.CustomSpringJUnit4ClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CustomSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "secure-applicationContext-hazelcast.xml" })
@Category(QuickTest.class)
public class SecureApplicationContextTest {
    @Resource
    private Config config;

    private SecurityConfig securityConfig;

    @Resource
    private ICredentialsFactory dummyCredentialsFactory;

    @Resource
    private IPermissionPolicy dummyPermissionPolicy;

    @Test
    public void testBasics() {
        Assert.assertNotNull(securityConfig);
        Assert.assertTrue(securityConfig.isEnabled());
        Assert.assertTrue(securityConfig.getClientBlockUnmappedActions());
        Assert.assertNotNull(securityConfig.getClientLoginModuleConfigs());
        Assert.assertFalse(securityConfig.getClientLoginModuleConfigs().isEmpty());
        Assert.assertNotNull(securityConfig.getClientPermissionConfigs());
        Assert.assertFalse(securityConfig.getClientPermissionConfigs().isEmpty());
        Assert.assertNotNull(securityConfig.getMemberLoginModuleConfigs());
        Assert.assertFalse(securityConfig.getMemberLoginModuleConfigs().isEmpty());
        Assert.assertNotNull(securityConfig.getClientPolicyConfig());
        Assert.assertNotNull(securityConfig.getMemberCredentialsConfig());
        Assert.assertEquals(1, securityConfig.getSecurityInterceptorConfigs().size());
    }

    @Test
    public void testMemberLoginConfigs() {
        List<LoginModuleConfig> list = securityConfig.getMemberLoginModuleConfigs();
        Assert.assertTrue(((list.size()) == 1));
        LoginModuleConfig lm = list.get(0);
        Assert.assertEquals("com.hazelcast.examples.MyRequiredLoginModule", lm.getClassName());
        Assert.assertFalse(lm.getProperties().isEmpty());
        Assert.assertEquals(REQUIRED, lm.getUsage());
    }

    @Test
    public void testClientLoginConfigs() {
        List<LoginModuleConfig> list = securityConfig.getClientLoginModuleConfigs();
        Assert.assertTrue(((list.size()) == 2));
        LoginModuleConfig lm1 = list.get(0);
        Assert.assertEquals("com.hazelcast.examples.MyOptionalLoginModule", lm1.getClassName());
        Assert.assertFalse(lm1.getProperties().isEmpty());
        Assert.assertEquals(OPTIONAL, lm1.getUsage());
        LoginModuleConfig lm2 = list.get(1);
        Assert.assertEquals("com.hazelcast.examples.MyRequiredLoginModule", lm2.getClassName());
        Assert.assertFalse(lm2.getProperties().isEmpty());
        Assert.assertEquals(REQUIRED, lm2.getUsage());
    }

    @Test
    public void testCredentialsFactory() {
        Assert.assertEquals("com.hazelcast.examples.MyCredentialsFactory", securityConfig.getMemberCredentialsConfig().getClassName());
        Assert.assertFalse(securityConfig.getMemberCredentialsConfig().getProperties().isEmpty());
        Assert.assertEquals(dummyCredentialsFactory, securityConfig.getMemberCredentialsConfig().getImplementation());
    }

    @Test
    public void testPermissionPolicy() {
        Assert.assertEquals("com.hazelcast.examples.MyPermissionPolicy", securityConfig.getClientPolicyConfig().getClassName());
        Assert.assertFalse(securityConfig.getClientPolicyConfig().getProperties().isEmpty());
        Assert.assertEquals(dummyPermissionPolicy, securityConfig.getClientPolicyConfig().getImplementation());
    }

    @Test
    public void testPermissions() {
        Set<PermissionConfig> perms = securityConfig.getClientPermissionConfigs();
        Assert.assertFalse(perms.isEmpty());
        for (PermissionConfig permConfig : perms) {
            switch (permConfig.getType()) {
                case ALL :
                    Assert.assertEquals("admin", permConfig.getPrincipal());
                    Assert.assertEquals(1, permConfig.getEndpoints().size());
                    Assert.assertEquals("127.0.0.1", permConfig.getEndpoints().iterator().next());
                    break;
                case MAP :
                    Assert.assertEquals("customMap", permConfig.getName());
                    Assert.assertEquals("dev", permConfig.getPrincipal());
                    Assert.assertEquals(1, permConfig.getEndpoints().size());
                    Assert.assertEquals("127.0.0.1", permConfig.getEndpoints().iterator().next());
                    break;
                case QUEUE :
                    Assert.assertEquals("customQ", permConfig.getName());
                    Assert.assertEquals("dev", permConfig.getPrincipal());
                    Assert.assertEquals(1, permConfig.getEndpoints().size());
                    Assert.assertEquals("127.0.0.1", permConfig.getEndpoints().iterator().next());
                    break;
                case CACHE :
                    Assert.assertEquals("test-cache", permConfig.getName());
                    Assert.assertEquals("dev", permConfig.getPrincipal());
                    Assert.assertEquals(1, permConfig.getEndpoints().size());
                    Assert.assertEquals("127.0.0.1", permConfig.getEndpoints().iterator().next());
                    Assert.assertEquals(4, permConfig.getActions().size());
                    String[] expectedActions = new String[]{ "create", "add", "read", "destroy" };
                    String[] actualActions = permConfig.getActions().toArray(new String[0]);
                    Assert.assertArrayEquals(expectedActions, actualActions);
                    break;
                case CONFIG :
                    Assert.assertEquals("dev", permConfig.getPrincipal());
                    Assert.assertEquals(1, permConfig.getEndpoints().size());
                    Assert.assertEquals("127.0.0.1", permConfig.getEndpoints().iterator().next());
                    break;
            }
        }
    }

    @Test
    public void testSecurityInterceptors() {
        List<SecurityInterceptorConfig> interceptorConfigs = securityConfig.getSecurityInterceptorConfigs();
        Assert.assertEquals(1, interceptorConfigs.size());
        SecurityInterceptorConfig interceptorConfig = interceptorConfigs.get(0);
        String className = interceptorConfig.getClassName();
        Assert.assertEquals(DummySecurityInterceptor.class.getName(), className);
        SecurityInterceptor securityInterceptor = interceptorConfig.getImplementation();
        Assert.assertTrue((securityInterceptor instanceof DummySecurityInterceptor));
    }
}

