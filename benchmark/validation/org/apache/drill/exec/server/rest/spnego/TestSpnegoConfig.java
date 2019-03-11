/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.server.rest.spnego;


import ExecConstants.AUTHENTICATION_MECHANISMS;
import ExecConstants.HTTP_SPNEGO_KEYTAB;
import ExecConstants.HTTP_SPNEGO_PRINCIPAL;
import ExecConstants.USER_AUTHENTICATION_ENABLED;
import ExecConstants.USER_AUTHENTICATOR_IMPL;
import com.typesafe.config.ConfigValueFactory;
import junit.framework.TestCase;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.exceptions.DrillException;
import org.apache.drill.exec.rpc.security.KerberosHelper;
import org.apache.drill.exec.rpc.user.security.testing.UserAuthenticatorTestImpl;
import org.apache.drill.exec.server.rest.auth.SpnegoConfig;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test for validating {@link SpnegoConfig}
 */
@Ignore("See DRILL-5387")
@Category(SecurityTest.class)
public class TestSpnegoConfig {
    // private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestSpnegoConfig.class);
    private static KerberosHelper spnegoHelper;

    private static final String primaryName = "HTTP";

    private static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    /**
     * Test invalid {@link SpnegoConfig} with missing keytab and principal
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInvalidSpnegoConfig() throws Exception {
        // Invalid configuration for SPNEGO
        try {
            final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)));
            final SpnegoConfig spnegoConfig = new SpnegoConfig(newConfig);
            spnegoConfig.validateSpnegoConfig();
            TestCase.fail();
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof DrillException));
        }
    }

    /**
     * Invalid configuration with keytab only and missing principal
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSpnegoConfigOnlyKeytab() throws Exception {
        try {
            final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(HTTP_SPNEGO_KEYTAB, ConfigValueFactory.fromAnyRef(TestSpnegoConfig.spnegoHelper.serverKeytab.toString())).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)));
            final SpnegoConfig spnegoConfig = new SpnegoConfig(newConfig);
            spnegoConfig.validateSpnegoConfig();
            TestCase.fail();
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof DrillException));
        }
    }

    /**
     * Invalid configuration with principal only and missing keytab
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSpnegoConfigOnlyPrincipal() throws Exception {
        try {
            final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(HTTP_SPNEGO_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestSpnegoConfig.spnegoHelper.SERVER_PRINCIPAL)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)));
            final SpnegoConfig spnegoConfig = new SpnegoConfig(newConfig);
            spnegoConfig.validateSpnegoConfig();
            TestCase.fail();
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof DrillException));
        }
    }

    /**
     * Valid Configuration with both keytab & principal
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testValidSpnegoConfig() throws Exception {
        try {
            final DrillConfig newConfig = new DrillConfig(DrillConfig.create().withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(HTTP_SPNEGO_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestSpnegoConfig.spnegoHelper.SERVER_PRINCIPAL)).withValue(HTTP_SPNEGO_KEYTAB, ConfigValueFactory.fromAnyRef(TestSpnegoConfig.spnegoHelper.serverKeytab.toString())).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)));
            final SpnegoConfig spnegoConfig = new SpnegoConfig(newConfig);
            spnegoConfig.validateSpnegoConfig();
            UserGroupInformation ugi = spnegoConfig.getLoggedInUgi();
            Assert.assertEquals(TestSpnegoConfig.primaryName, ugi.getShortUserName());
            Assert.assertEquals(TestSpnegoConfig.spnegoHelper.SERVER_PRINCIPAL, ugi.getUserName());
        } catch (Exception ex) {
            TestCase.fail();
        }
    }
}

