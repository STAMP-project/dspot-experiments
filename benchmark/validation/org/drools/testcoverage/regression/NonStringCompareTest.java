/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.testcoverage.regression;


import Level.ERROR;
import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.KieBuilder;


@RunWith(Parameterized.class)
public class NonStringCompareTest {
    private static final String genericDrl = (((((((((("package " + (TestConstants.PACKAGE_REGRESSION)) + "\n") + "declare Fact\n") + "    field : String\n") + "end\n") + "rule generic\n") + "    when\n") + "       Fact( field == %s )\n") + "    then\n") + "       // consequence\n") + "end\n";

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public NonStringCompareTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void testStringCompare() throws Exception {
        testScenario("\"someString\"", "someString");
    }

    @Test
    public void testNonQuotedStringComapre() {
        final KieBuilder kbuilder = build("someString");
        Assertions.assertThat(kbuilder.getResults().getMessages(ERROR).size()).isEqualTo(1);
    }

    @Test
    public void testIntCompare() throws Exception {
        testScenario("13", "13");
    }
}

