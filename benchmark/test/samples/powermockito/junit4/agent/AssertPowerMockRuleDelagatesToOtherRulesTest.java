/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samples.powermockito.junit4.agent;


import java.util.LinkedList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.modules.junit4.rule.PowerMockRule;


/**
 * This test demonstrates that the PowerMockRule delegates to other rules.
 */
public class AssertPowerMockRuleDelagatesToOtherRulesTest {
    private static final AssertPowerMockRuleDelagatesToOtherRulesTest.MyObject BEFORE = new AssertPowerMockRuleDelagatesToOtherRulesTest.MyObject();

    private final List<Object> objects = new LinkedList<Object>();

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Rule
    public AssertPowerMockRuleDelagatesToOtherRulesTest.MyRule rule = new AssertPowerMockRuleDelagatesToOtherRulesTest.MyRule();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void assertPowerMockRuleDelegatesToOtherRules() throws Exception {
        assertThat(this.getClass().getClassLoader()).isNotInstanceOf(MockClassLoader.class);
        assertThat(objects).hasSize(1).containsExactly(AssertPowerMockRuleDelagatesToOtherRulesTest.BEFORE);
        assertThat(testName.getMethodName()).isEqualTo("assertPowerMockRuleDelegatesToOtherRules");
    }

    private static class MyObject {
        private final String state = "state";

        @Override
        public int hashCode() {
            return state.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            AssertPowerMockRuleDelagatesToOtherRulesTest.MyObject myObject = ((AssertPowerMockRuleDelagatesToOtherRulesTest.MyObject) (o));
            return state.equals(myObject.state);
        }
    }

    private class MyRule implements MethodRule {
        @Override
        public Statement apply(final Statement base, FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    objects.add(AssertPowerMockRuleDelagatesToOtherRulesTest.BEFORE);
                    base.evaluate();
                }
            };
        }
    }
}

