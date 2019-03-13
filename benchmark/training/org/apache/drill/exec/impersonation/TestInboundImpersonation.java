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
package org.apache.drill.exec.impersonation;


import DrillProperties.IMPERSONATION_TARGET;
import DrillProperties.PASSWORD;
import DrillProperties.USER;
import ExecConstants.IMPERSONATION_POLICIES_KEY;
import UserBitShared.DrillPBError.ErrorType;
import java.util.Properties;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.exec.rpc.RpcException;
import org.apache.drill.exec.rpc.user.security.testing.UserAuthenticatorTestImpl;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SlowTest.class, SecurityTest.class })
public class TestInboundImpersonation extends BaseTestImpersonation {
    public static final String OWNER = BaseTestImpersonation.org1Users[0];

    public static final String OWNER_PASSWORD = "owner";

    public static final String TARGET_NAME = BaseTestImpersonation.org1Users[1];

    public static final String TARGET_PASSWORD = "target";

    public static final String DATA_GROUP = BaseTestImpersonation.org1Groups[0];

    public static final String PROXY_NAME = BaseTestImpersonation.org1Users[2];

    public static final String PROXY_PASSWORD = "proxy";

    @Test
    public void selectChainedView() throws Exception {
        // Connect as PROXY_NAME and query for IMPERSONATION_TARGET
        // data belongs to OWNER, however a view is shared with IMPERSONATION_TARGET
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(USER, TestInboundImpersonation.PROXY_NAME);
        connectionProps.setProperty(PASSWORD, TestInboundImpersonation.PROXY_PASSWORD);
        connectionProps.setProperty(IMPERSONATION_TARGET, TestInboundImpersonation.TARGET_NAME);
        BaseTestQuery.updateClient(connectionProps);
        BaseTestQuery.testBuilder().sqlQuery("SELECT * FROM %s.u0_lineitem ORDER BY l_orderkey LIMIT 1", BaseTestImpersonation.getWSSchema(TestInboundImpersonation.OWNER)).ordered().baselineColumns("l_orderkey", "l_partkey").baselineValues(1, 1552).go();
    }

    // PERMISSION ERROR: Proxy user 'user2_1' is not authorized to impersonate target user 'user0_2'.
    @Test(expected = RpcException.class)
    public void unauthorizedTarget() throws Exception {
        final String unauthorizedTarget = BaseTestImpersonation.org2Users[0];
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(USER, TestInboundImpersonation.PROXY_NAME);
        connectionProps.setProperty(PASSWORD, TestInboundImpersonation.PROXY_PASSWORD);
        connectionProps.setProperty(IMPERSONATION_TARGET, unauthorizedTarget);
        BaseTestQuery.updateClient(connectionProps);// throws up

    }

    @Test
    public void invalidPolicy() throws Exception {
        thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION, "Invalid impersonation policies."));
        BaseTestQuery.updateClient(UserAuthenticatorTestImpl.PROCESS_USER, UserAuthenticatorTestImpl.PROCESS_USER_PASSWORD);
        BaseTestQuery.test("ALTER SYSTEM SET `%s`='%s'", IMPERSONATION_POLICIES_KEY, "[ invalid json ]");
    }

    @Test
    public void invalidProxy() throws Exception {
        thrownException.expect(new org.apache.drill.test.UserExceptionMatcher(ErrorType.VALIDATION, "Proxy principals cannot have a wildcard entry."));
        BaseTestQuery.updateClient(UserAuthenticatorTestImpl.PROCESS_USER, UserAuthenticatorTestImpl.PROCESS_USER_PASSWORD);
        BaseTestQuery.test("ALTER SYSTEM SET `%s`='%s'", IMPERSONATION_POLICIES_KEY, ((("[ { proxy_principals : { users: [\"*\" ] }," + "target_principals : { users : [\"") + (TestInboundImpersonation.TARGET_NAME)) + "\"] } } ]"));
    }
}

