/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.security;


import io.helidon.security.providers.PathBasedProvider;
import io.helidon.security.providers.ResourceBasedProvider;
import org.junit.jupiter.api.Test;

import static CompositeProviderFlag.FORBIDDEN;
import static CompositeProviderFlag.MAY_FAIL;
import static CompositeProviderFlag.MUST_FAIL;
import static CompositeProviderFlag.OPTIONAL;
import static CompositeProviderFlag.REQUIRED;
import static CompositeProviderFlag.SUFFICIENT;
import static SecurityStatus.FAILURE;
import static SecurityStatus.SUCCESS;


/**
 * Unit test for {@link CompositeOutboundProvider}.
 */
public class CompositePolicyFlagsTest {
    private static final PathBasedProvider PATH_BASED_PROVIDER = new PathBasedProvider();

    private static final ResourceBasedProvider RESOURCE_BASED_PROVIDER = new ResourceBasedProvider();

    @Test
    public void testMustFail() {
        CompositePolicyFlagsTest.TestConfig tc = new CompositePolicyFlagsTest.TestConfig(MUST_FAIL, REQUIRED);
        tc.okOk = FAILURE;
        tc.okAbstain = FAILURE;
        tc.okFail = FAILURE;
        tc.abstainOk = FAILURE;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = SUCCESS;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
        tc = new CompositePolicyFlagsTest.TestConfig(REQUIRED, MUST_FAIL);
        tc.okOk = FAILURE;
        tc.okAbstain = FAILURE;
        tc.okFail = SUCCESS;
        tc.abstainOk = FAILURE;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = FAILURE;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
    }

    @Test
    public void testForbidden() {
        CompositePolicyFlagsTest.TestConfig tc = new CompositePolicyFlagsTest.TestConfig(FORBIDDEN, REQUIRED);
        tc.okOk = FAILURE;
        tc.okAbstain = FAILURE;
        tc.okFail = FAILURE;
        tc.abstainOk = SUCCESS;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = SUCCESS;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
        tc = new CompositePolicyFlagsTest.TestConfig(REQUIRED, FORBIDDEN);
        tc.okOk = FAILURE;
        tc.okAbstain = SUCCESS;
        tc.okFail = SUCCESS;
        tc.abstainOk = FAILURE;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = FAILURE;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
    }

    @Test
    public void testRequired() {
        CompositePolicyFlagsTest.TestConfig tc = new CompositePolicyFlagsTest.TestConfig(REQUIRED, REQUIRED);
        tc.okOk = SUCCESS;
        tc.okAbstain = FAILURE;
        tc.okFail = FAILURE;
        tc.abstainOk = FAILURE;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = FAILURE;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
    }

    @Test
    public void testOptional() {
        CompositePolicyFlagsTest.TestConfig tc = new CompositePolicyFlagsTest.TestConfig(OPTIONAL, REQUIRED);
        tc.okOk = SUCCESS;
        tc.okAbstain = FAILURE;
        tc.okFail = FAILURE;
        tc.abstainOk = SUCCESS;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = FAILURE;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
        tc = new CompositePolicyFlagsTest.TestConfig(REQUIRED, OPTIONAL);
        tc.okOk = SUCCESS;
        tc.okAbstain = SUCCESS;
        tc.okFail = FAILURE;
        tc.abstainOk = FAILURE;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = FAILURE;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
    }

    @Test
    public void testSufficient() {
        CompositePolicyFlagsTest.TestConfig tc = new CompositePolicyFlagsTest.TestConfig(SUFFICIENT, REQUIRED);
        tc.okOk = SUCCESS;
        tc.okAbstain = SUCCESS;
        tc.okFail = SUCCESS;
        tc.abstainOk = SUCCESS;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = FAILURE;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
        tc = new CompositePolicyFlagsTest.TestConfig(REQUIRED, SUFFICIENT);
        tc.okOk = SUCCESS;
        tc.okAbstain = SUCCESS;
        tc.okFail = FAILURE;
        tc.abstainOk = FAILURE;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = FAILURE;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
    }

    @Test
    public void testMayFail() {
        CompositePolicyFlagsTest.TestConfig tc = new CompositePolicyFlagsTest.TestConfig(MAY_FAIL, REQUIRED);
        tc.okOk = SUCCESS;
        tc.okAbstain = FAILURE;
        tc.okFail = FAILURE;
        tc.abstainOk = SUCCESS;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = SUCCESS;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
        tc = new CompositePolicyFlagsTest.TestConfig(REQUIRED, MAY_FAIL);
        tc.okOk = SUCCESS;
        tc.okAbstain = SUCCESS;
        tc.okFail = SUCCESS;
        tc.abstainOk = FAILURE;
        tc.abstainAbstain = FAILURE;
        tc.abstainFail = FAILURE;
        tc.failOk = FAILURE;
        tc.failAbstain = FAILURE;
        tc.failFail = FAILURE;
        testIt(tc);
    }

    private class TestConfig {
        private CompositeProviderFlag firstFlag;

        private CompositeProviderFlag secondFlag;

        private io.helidon.security.SecurityResponse.SecurityStatus okOk;

        private io.helidon.security.SecurityResponse.SecurityStatus okAbstain;

        private io.helidon.security.SecurityResponse.SecurityStatus okFail;

        private io.helidon.security.SecurityResponse.SecurityStatus abstainOk;

        private io.helidon.security.SecurityResponse.SecurityStatus abstainAbstain;

        private io.helidon.security.SecurityResponse.SecurityStatus abstainFail;

        private io.helidon.security.SecurityResponse.SecurityStatus failOk;

        private io.helidon.security.SecurityResponse.SecurityStatus failAbstain;

        private io.helidon.security.SecurityResponse.SecurityStatus failFail;

        private TestConfig(CompositeProviderFlag firstFlag, CompositeProviderFlag secondFlag) {
            this.firstFlag = firstFlag;
            this.secondFlag = secondFlag;
        }
    }
}

