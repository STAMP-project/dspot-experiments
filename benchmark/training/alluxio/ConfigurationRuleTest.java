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
package alluxio;


import PropertyKey.MASTER_HOSTNAME;
import PropertyKey.SECURITY_LOGIN_USERNAME;
import alluxio.conf.InstancedConfiguration;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.model.Statement;


/**
 * Unit tests for {@link ConfigurationRule}.
 */
public final class ConfigurationRuleTest {
    @Test
    public void changeConfiguration() throws Throwable {
        InstancedConfiguration conf = ConfigurationTestUtils.defaults();
        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Assert.assertEquals("testValue", conf.get(MASTER_HOSTNAME));
            }
        };
        new ConfigurationRule(ImmutableMap.of(MASTER_HOSTNAME, "testValue"), conf).apply(statement, null).evaluate();
    }

    @Test
    public void changeConfigurationForDefaultNullValue() throws Throwable {
        InstancedConfiguration conf = ConfigurationTestUtils.defaults();
        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Assert.assertEquals("testValue", conf.get(SECURITY_LOGIN_USERNAME));
            }
        };
        Assert.assertFalse(conf.isSet(SECURITY_LOGIN_USERNAME));
        new ConfigurationRule(ImmutableMap.of(SECURITY_LOGIN_USERNAME, "testValue"), conf).apply(statement, null).evaluate();
        Assert.assertFalse(conf.isSet(SECURITY_LOGIN_USERNAME));
    }
}

