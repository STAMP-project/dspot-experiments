/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.test.junit.rules;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.geode.test.junit.runners.TestRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Result;


/**
 * Unit tests for {@link RuleList}.
 */
public class RuleListTest {
    private static AtomicInteger counter;

    private static RuleListTest.Invocations[] invocations;

    @Test
    public void firstShouldBeFirstBeforeLastAfter() {
        Result result = TestRunner.runTest(RuleListTest.ThreeRules.class);
        assertThat(result.wasSuccessful()).isTrue();
        assertThat(RuleListTest.counter.get()).isEqualTo(9);
        assertThat(RuleListTest.invocations[0].beforeInvocation).isEqualTo(1);
        assertThat(RuleListTest.invocations[1].beforeInvocation).isEqualTo(2);
        assertThat(RuleListTest.invocations[2].beforeInvocation).isEqualTo(3);
        assertThat(RuleListTest.invocations[0].testInvocation).isEqualTo(4);
        assertThat(RuleListTest.invocations[1].testInvocation).isEqualTo(5);
        assertThat(RuleListTest.invocations[2].testInvocation).isEqualTo(6);
        assertThat(RuleListTest.invocations[2].afterInvocation).isEqualTo(7);
        assertThat(RuleListTest.invocations[1].afterInvocation).isEqualTo(8);
        assertThat(RuleListTest.invocations[0].afterInvocation).isEqualTo(9);
    }

    /**
     * Used by test {@link #firstShouldBeFirstBeforeLastAfter()}
     */
    public static class ThreeRules {
        static RuleList ruleListStatic;

        public RuleListTest.SpyRule ruleOne = new RuleListTest.SpyRule("ruleOne", RuleListTest.invocations[0]);

        public RuleListTest.SpyRule ruleTwo = new RuleListTest.SpyRule("ruleTwo", RuleListTest.invocations[1]);

        public RuleListTest.SpyRule ruleThree = new RuleListTest.SpyRule("ruleThree", RuleListTest.invocations[2]);

        @Rule
        public RuleList ruleList = new RuleList().add(ruleThree).add(ruleTwo).add(ruleOne);

        @Test
        public void doTest() throws Exception {
            RuleListTest.ThreeRules.ruleListStatic = ruleList;
            RuleListTest.invocations[0].invokedTest();
            RuleListTest.invocations[1].invokedTest();
            RuleListTest.invocations[2].invokedTest();
        }
    }

    /**
     * Structure of rule callback and test invocations
     */
    public static class Invocations {
        private final AtomicInteger counter;

        int beforeInvocation = 0;

        int testInvocation = 0;

        int afterInvocation = 0;

        Invocations(AtomicInteger counter) {
            this.counter = counter;
        }

        void invokedTest() {
            testInvocation = counter.incrementAndGet();
        }

        void invokedBefore() {
            beforeInvocation = counter.incrementAndGet();
        }

        void invokedAfter() {
            afterInvocation = counter.incrementAndGet();
        }

        @Override
        public String toString() {
            return (((((((("Invocations{" + "counter=") + (counter)) + ", beforeInvocation=") + (beforeInvocation)) + ", testInvocation=") + (testInvocation)) + ", afterInvocation=") + (afterInvocation)) + '}';
        }
    }

    /**
     * Implementation of TestRule that records the order of callbacks invoked on it. Used by
     * {@link RuleListTest}.
     */
    public static class SpyRule extends ExternalResource {
        static RuleListTest.SpyRuleBuilder builder() {
            return new RuleListTest.SpyRuleBuilder();
        }

        private final String name;

        private final RuleListTest.Invocations invocations;

        private final Throwable beforeThrowable;

        SpyRule(String name, RuleListTest.Invocations invocations) {
            this.name = name;
            this.invocations = invocations;
            this.beforeThrowable = null;
        }

        SpyRule(RuleListTest.SpyRuleBuilder builder) {
            this.name = builder.name;
            this.invocations = builder.invocations;
            this.beforeThrowable = builder.beforeThrowable;
        }

        RuleListTest.Invocations invocations() {
            return this.invocations;
        }

        void test() {
            this.invocations.invokedTest();
        }

        @Override
        protected void before() throws Throwable {
            this.invocations.invokedBefore();
            if ((this.beforeThrowable) != null) {
                throw this.beforeThrowable;
            }
        }

        @Override
        protected void after() {
            this.invocations.invokedAfter();
        }

        @Override
        public String toString() {
            return ((("SpyRule{" + "name='") + (name)) + '\'') + '}';
        }
    }

    /**
     * Builder for more control of constructing an instance of {@link SpyRule}
     */
    public static class SpyRuleBuilder {
        String name;

        RuleListTest.Invocations invocations;

        Throwable beforeThrowable;

        RuleListTest.SpyRuleBuilder withName(String name) {
            this.name = name;
            return this;
        }

        RuleListTest.SpyRuleBuilder withInvocations(RuleListTest.Invocations invocations) {
            this.invocations = invocations;
            return this;
        }

        RuleListTest.SpyRuleBuilder beforeThrows(Throwable throwable) {
            this.beforeThrowable = throwable;
            return this;
        }

        RuleListTest.SpyRule build() {
            return new RuleListTest.SpyRule(this);
        }
    }
}

