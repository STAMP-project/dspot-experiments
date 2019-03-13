/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;


public class FailureOnRemovalTest extends CommonTestMethodBase {
    private static final String LS = System.getProperty("line.separator");

    private static final String PACKAGE = "failure_on_removal";

    private static final String RULE_1 = "rule_1";

    private static final String RULE_2 = "rule_2";

    private static final String RULE_3 = "rule_3";

    private static final boolean SHARE_BETA_NODES = true;

    private static final boolean NOT_SHARE_BETA_NODES = false;

    @Test
    public void testWithBetaNodeSharing() throws Exception {
        runTest(FailureOnRemovalTest.SHARE_BETA_NODES);
    }

    @Test
    public void testWithoutBetaNodeSharing() throws Exception {
        runTest(FailureOnRemovalTest.NOT_SHARE_BETA_NODES);
    }
}

