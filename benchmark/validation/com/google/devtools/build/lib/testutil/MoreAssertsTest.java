/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.testutil;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link com.google.devtools.build.lib.testutil.MoreAsserts}.
 */
@RunWith(JUnit4.class)
public class MoreAssertsTest {
    @Test
    public void testAssertContainsSublistSuccess() {
        List<String> actual = Arrays.asList("a", "b", "c");
        // All single-string combinations.
        MoreAsserts.assertContainsSublist(actual, "a");
        MoreAsserts.assertContainsSublist(actual, "b");
        MoreAsserts.assertContainsSublist(actual, "c");
        // All two-string combinations.
        MoreAsserts.assertContainsSublist(actual, "a", "b");
        MoreAsserts.assertContainsSublist(actual, "b", "c");
        // The whole list.
        MoreAsserts.assertContainsSublist(actual, "a", "b", "c");
    }

    @Test
    public void testAssertContainsSublistFailure() {
        List<String> actual = Arrays.asList("a", "b", "c");
        try {
            MoreAsserts.assertContainsSublist(actual, "d");
            Assert.fail("no exception thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().startsWith("Did not find [d] as a sublist of [a, b, c]");
        }
        try {
            MoreAsserts.assertContainsSublist(actual, "a", "c");
            Assert.fail("no exception thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().startsWith("Did not find [a, c] as a sublist of [a, b, c]");
        }
        try {
            MoreAsserts.assertContainsSublist(actual, "b", "c", "d");
            Assert.fail("no exception thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessageThat().startsWith("Did not find [b, c, d] as a sublist of [a, b, c]");
        }
    }

    @Test
    public void testAssertDoesNotContainSublistSuccess() {
        List<String> actual = Arrays.asList("a", "b", "c");
        MoreAsserts.assertDoesNotContainSublist(actual, "d");
        MoreAsserts.assertDoesNotContainSublist(actual, "a", "c");
        MoreAsserts.assertDoesNotContainSublist(actual, "b", "c", "d");
    }

    @Test
    public void testAssertDoesNotContainSublistFailure() {
        List<String> actual = Arrays.asList("a", "b", "c");
        // All single-string combinations.
        try {
            MoreAsserts.assertDoesNotContainSublist(actual, "a");
            Assert.fail("no exception thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("Found [a] as a sublist of [a, b, c]");
        }
        try {
            MoreAsserts.assertDoesNotContainSublist(actual, "b");
            Assert.fail("no exception thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("Found [b] as a sublist of [a, b, c]");
        }
        try {
            MoreAsserts.assertDoesNotContainSublist(actual, "c");
            Assert.fail("no exception thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("Found [c] as a sublist of [a, b, c]");
        }
        // All two-string combinations.
        try {
            MoreAsserts.assertDoesNotContainSublist(actual, "a", "b");
            Assert.fail("no exception thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("Found [a, b] as a sublist of [a, b, c]");
        }
        try {
            MoreAsserts.assertDoesNotContainSublist(actual, "b", "c");
            Assert.fail("no exception thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("Found [b, c] as a sublist of [a, b, c]");
        }
        // The whole list.
        try {
            MoreAsserts.assertDoesNotContainSublist(actual, "a", "b", "c");
            Assert.fail("no exception thrown");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("Found [a, b, c] as a sublist of [a, b, c]");
        }
    }
}

