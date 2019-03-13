/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.tests;


import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Test;


/**
 * Tests for {@link Assume}.
 *
 * @author Sam Brannen
 * @since 5.0
 */
public class AssumeTests {
    private String originalTestGroups;

    @Test
    public void assumeGroupWithNoActiveTestGroups() {
        setTestGroups("");
        Assume.group(TestGroup.JMXMP);
        Assert.fail("assumption should have failed");
    }

    @Test
    public void assumeGroupWithNoMatchingActiveTestGroup() {
        setTestGroups(TestGroup.PERFORMANCE, TestGroup.CI);
        Assume.group(TestGroup.JMXMP);
        Assert.fail("assumption should have failed");
    }

    @Test
    public void assumeGroupWithMatchingActiveTestGroup() {
        setTestGroups(TestGroup.JMXMP);
        try {
            Assume.group(TestGroup.JMXMP);
        } catch (AssumptionViolatedException ex) {
            Assert.fail("assumption should NOT have failed");
        }
    }

    @Test
    public void assumeGroupWithBogusActiveTestGroup() {
        assertBogusActiveTestGroupBehavior("bogus");
    }

    @Test
    public void assumeGroupWithAllMinusBogusActiveTestGroup() {
        assertBogusActiveTestGroupBehavior("all-bogus");
    }
}

