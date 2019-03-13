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


import alluxio.conf.InstancedConfiguration;
import alluxio.security.authentication.AuthenticatedClientUser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.model.Statement;


/**
 * Unit tests for {@link AuthenticatedUserRule}.
 */
public final class AuthenticatedUserRuleTest {
    private static final String TESTCASE_USER = "testcase-user";

    private static final String RULE_USER = "rule-user";

    private static final String OUTSIDE_RULE_USER = "outside-rule-user";

    private InstancedConfiguration mConfiguration = ConfigurationTestUtils.defaults();

    private final Statement mStatement = new Statement() {
        @Override
        public void evaluate() throws Throwable {
            Assert.assertEquals(AuthenticatedUserRuleTest.RULE_USER, AuthenticatedClientUser.get(mConfiguration).getName());
            AuthenticatedClientUser.set(AuthenticatedUserRuleTest.TESTCASE_USER);
            Assert.assertEquals(AuthenticatedUserRuleTest.TESTCASE_USER, AuthenticatedClientUser.get(mConfiguration).getName());
        }
    };

    @Test
    public void userSetBeforeRule() throws Throwable {
        AuthenticatedClientUser.set(AuthenticatedUserRuleTest.OUTSIDE_RULE_USER);
        new AuthenticatedUserRule(AuthenticatedUserRuleTest.RULE_USER, mConfiguration).apply(mStatement, null).evaluate();
        Assert.assertEquals(AuthenticatedUserRuleTest.OUTSIDE_RULE_USER, AuthenticatedClientUser.get(mConfiguration).getName());
    }

    @Test
    public void noUserBeforeRule() throws Throwable {
        AuthenticatedClientUser.remove();
        new AuthenticatedUserRule(AuthenticatedUserRuleTest.RULE_USER, mConfiguration).apply(mStatement, null).evaluate();
        Assert.assertEquals(null, AuthenticatedClientUser.get(mConfiguration));
    }
}

