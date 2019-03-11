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
package org.apache.flink.runtime.security;


import SecurityOptions.KERBEROS_LOGIN_CONTEXTS;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.security.modules.SecurityModule;
import org.apache.flink.runtime.security.modules.SecurityModuleFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link SecurityUtils}.
 */
public class SecurityUtilsTest {
    static class TestSecurityModule implements SecurityModule {
        boolean installed;

        @Override
        public void install() throws SecurityInstallException {
            installed = true;
        }

        @Override
        public void uninstall() throws SecurityInstallException {
            installed = false;
        }

        static class Factory implements SecurityModuleFactory {
            @Override
            public SecurityModule createModule(SecurityConfiguration securityConfig) {
                return new SecurityUtilsTest.TestSecurityModule();
            }
        }
    }

    @Test
    public void testModuleInstall() throws Exception {
        SecurityConfiguration sc = new SecurityConfiguration(new Configuration(), Collections.singletonList(new SecurityUtilsTest.TestSecurityModule.Factory()));
        SecurityUtils.install(sc);
        Assert.assertEquals(1, SecurityUtils.getInstalledModules().size());
        SecurityUtilsTest.TestSecurityModule testModule = ((SecurityUtilsTest.TestSecurityModule) (SecurityUtils.getInstalledModules().get(0)));
        Assert.assertTrue(testModule.installed);
        SecurityUtils.uninstall();
        Assert.assertNull(SecurityUtils.getInstalledModules());
        Assert.assertFalse(testModule.installed);
    }

    @Test
    public void testSecurityContext() throws Exception {
        SecurityConfiguration sc = new SecurityConfiguration(new Configuration(), Collections.singletonList(new SecurityUtilsTest.TestSecurityModule.Factory()));
        SecurityUtils.install(sc);
        Assert.assertEquals(HadoopSecurityContext.class, SecurityUtils.getInstalledContext().getClass());
        SecurityUtils.uninstall();
        Assert.assertEquals(NoOpSecurityContext.class, SecurityUtils.getInstalledContext().getClass());
    }

    @Test
    public void testKerberosLoginContextParsing() {
        List<String> expectedLoginContexts = Arrays.asList("Foo bar", "Client");
        Configuration testFlinkConf;
        SecurityConfiguration testSecurityConf;
        // ------- no whitespaces
        testFlinkConf = new Configuration();
        testFlinkConf.setString(KERBEROS_LOGIN_CONTEXTS, "Foo bar,Client");
        testSecurityConf = new SecurityConfiguration(testFlinkConf, Collections.singletonList(new SecurityUtilsTest.TestSecurityModule.Factory()));
        Assert.assertEquals(expectedLoginContexts, testSecurityConf.getLoginContextNames());
        // ------- with whitespaces surrounding comma
        testFlinkConf = new Configuration();
        testFlinkConf.setString(KERBEROS_LOGIN_CONTEXTS, "Foo bar , Client");
        testSecurityConf = new SecurityConfiguration(testFlinkConf, Collections.singletonList(new SecurityUtilsTest.TestSecurityModule.Factory()));
        Assert.assertEquals(expectedLoginContexts, testSecurityConf.getLoginContextNames());
        // ------- leading / trailing whitespaces at start and end of list
        testFlinkConf = new Configuration();
        testFlinkConf.setString(KERBEROS_LOGIN_CONTEXTS, " Foo bar , Client ");
        testSecurityConf = new SecurityConfiguration(testFlinkConf, Collections.singletonList(new SecurityUtilsTest.TestSecurityModule.Factory()));
        Assert.assertEquals(expectedLoginContexts, testSecurityConf.getLoginContextNames());
        // ------- empty entries
        testFlinkConf = new Configuration();
        testFlinkConf.setString(KERBEROS_LOGIN_CONTEXTS, "Foo bar,,Client");
        testSecurityConf = new SecurityConfiguration(testFlinkConf, Collections.singletonList(new SecurityUtilsTest.TestSecurityModule.Factory()));
        Assert.assertEquals(expectedLoginContexts, testSecurityConf.getLoginContextNames());
        // ------- empty trailing String entries with whitespaces
        testFlinkConf = new Configuration();
        testFlinkConf.setString(KERBEROS_LOGIN_CONTEXTS, "Foo bar, ,, Client,");
        testSecurityConf = new SecurityConfiguration(testFlinkConf, Collections.singletonList(new SecurityUtilsTest.TestSecurityModule.Factory()));
        Assert.assertEquals(expectedLoginContexts, testSecurityConf.getLoginContextNames());
    }
}

