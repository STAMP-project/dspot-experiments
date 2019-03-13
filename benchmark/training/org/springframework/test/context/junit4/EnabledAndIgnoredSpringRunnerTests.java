/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.test.context.junit4;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.TestExecutionListeners;


/**
 * Verifies proper handling of JUnit's {@link Ignore &#064;Ignore} and Spring's
 * {@link IfProfileValue &#064;IfProfileValue} and
 * {@link ProfileValueSourceConfiguration &#064;ProfileValueSourceConfiguration}
 * (with the <em>implicit, default {@link ProfileValueSource}</em>) annotations in
 * conjunction with the {@link SpringRunner}.
 * <p>
 * Note that {@link TestExecutionListeners &#064;TestExecutionListeners} is
 * explicitly configured with an empty list, thus disabling all default
 * listeners.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see HardCodedProfileValueSourceSpringRunnerTests
 */
@RunWith(SpringRunner.class)
@TestExecutionListeners({  })
public class EnabledAndIgnoredSpringRunnerTests {
    protected static final String NAME = "EnabledAndIgnoredSpringRunnerTests.profile_value.name";

    protected static final String VALUE = "enigma";

    protected static int numTestsExecuted = 0;

    @Test
    @IfProfileValue(name = EnabledAndIgnoredSpringRunnerTests.NAME, value = (EnabledAndIgnoredSpringRunnerTests.VALUE) + "X")
    public void testIfProfileValueDisabled() {
        (EnabledAndIgnoredSpringRunnerTests.numTestsExecuted)++;
        Assert.fail("The body of a disabled test should never be executed!");
    }

    @Test
    @IfProfileValue(name = EnabledAndIgnoredSpringRunnerTests.NAME, value = EnabledAndIgnoredSpringRunnerTests.VALUE)
    public void testIfProfileValueEnabledViaSingleValue() {
        (EnabledAndIgnoredSpringRunnerTests.numTestsExecuted)++;
    }

    @Test
    @IfProfileValue(name = EnabledAndIgnoredSpringRunnerTests.NAME, values = { "foo", EnabledAndIgnoredSpringRunnerTests.VALUE, "bar" })
    public void testIfProfileValueEnabledViaMultipleValues() {
        (EnabledAndIgnoredSpringRunnerTests.numTestsExecuted)++;
    }

    @Test
    public void testIfProfileValueNotConfigured() {
        (EnabledAndIgnoredSpringRunnerTests.numTestsExecuted)++;
    }
}

