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


import Charsets.UTF_8;
import java.io.IOException;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.shaded.guava.com.google.common.io.Files;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(SecurityTest.class)
public class TestInboundImpersonationPrivileges extends BaseTestImpersonation {
    private static final Logger logger = LoggerFactory.getLogger(TestInboundImpersonationPrivileges.class);

    // policies on which the tests are based
    private static final String IMPERSONATION_POLICIES;

    static {
        try {
            IMPERSONATION_POLICIES = Files.asCharSource(DrillFileUtils.getResourceAsFile("/inbound_impersonation_policies.json"), UTF_8).read();
        } catch (final IOException e) {
            throw new RuntimeException("Cannot load impersonation policies.", e);
        }
    }

    @Test
    public void allTargetUsers() {
        for (final String user : BaseTestImpersonation.org1Users) {
            TestInboundImpersonationPrivileges.run("user0_1", user, true);
        }
        for (final String user : BaseTestImpersonation.org2Users) {
            TestInboundImpersonationPrivileges.run("user0_1", user, true);
        }
    }

    @Test
    public void noTargetUsers() {
        for (final String user : BaseTestImpersonation.org1Users) {
            TestInboundImpersonationPrivileges.run("user1_1", user, false);
        }
        for (final String user : BaseTestImpersonation.org2Users) {
            TestInboundImpersonationPrivileges.run("user1_1", user, false);
        }
    }

    @Test
    public void someTargetUsersAndGroups() {
        TestInboundImpersonationPrivileges.run("user2_1", "user3_1", true);
        TestInboundImpersonationPrivileges.run("user2_1", "user3_1", true);
        TestInboundImpersonationPrivileges.run("user2_1", "user1_1", false);
        TestInboundImpersonationPrivileges.run("user2_1", "user4_1", false);
        for (final String user : BaseTestImpersonation.org1Users) {
            if ((!(user.equals("user3_1"))) && (!(user.equals("user2_1")))) {
                TestInboundImpersonationPrivileges.run("user2_1", user, false);
            }
        }
        for (final String user : BaseTestImpersonation.org2Users) {
            TestInboundImpersonationPrivileges.run("user2_1", user, false);
        }
    }

    @Test
    public void someTargetUsers() {
        TestInboundImpersonationPrivileges.run("user4_1", "user1_1", true);
        TestInboundImpersonationPrivileges.run("user4_1", "user3_1", true);
        for (final String user : BaseTestImpersonation.org1Users) {
            if ((!(user.equals("user1_1"))) && (!(user.equals("user3_1")))) {
                TestInboundImpersonationPrivileges.run("user4_1", user, false);
            }
        }
        for (final String user : BaseTestImpersonation.org2Users) {
            TestInboundImpersonationPrivileges.run("user4_1", user, false);
        }
    }

    @Test
    public void oneTargetGroup() {
        TestInboundImpersonationPrivileges.run("user5_1", "user4_2", true);
        TestInboundImpersonationPrivileges.run("user5_1", "user5_2", true);
        TestInboundImpersonationPrivileges.run("user5_1", "user4_1", false);
        TestInboundImpersonationPrivileges.run("user5_1", "user3_2", false);
    }

    @Test
    public void twoTargetUsers() {
        TestInboundImpersonationPrivileges.run("user5_2", "user0_2", true);
        TestInboundImpersonationPrivileges.run("user5_2", "user1_2", true);
        TestInboundImpersonationPrivileges.run("user5_2", "user2_2", false);
        TestInboundImpersonationPrivileges.run("user5_2", "user0_1", false);
        TestInboundImpersonationPrivileges.run("user5_2", "user1_1", false);
    }

    @Test
    public void twoTargetGroups() {
        TestInboundImpersonationPrivileges.run("user3_2", "user4_2", true);
        TestInboundImpersonationPrivileges.run("user3_2", "user1_2", true);
        TestInboundImpersonationPrivileges.run("user3_2", "user2_2", true);
        TestInboundImpersonationPrivileges.run("user3_2", "user0_2", false);
        TestInboundImpersonationPrivileges.run("user3_2", "user5_2", false);
        for (final String user : BaseTestImpersonation.org1Users) {
            TestInboundImpersonationPrivileges.run("user3_2", user, false);
        }
    }
}

