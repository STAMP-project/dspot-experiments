/**
 * Copyright (c) 2016 Google, Inc.
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
package com.google.common.truth;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for Java 8 {@link Stream} Subjects.
 *
 * @author Kurt Alfred Kluever
 */
@RunWith(JUnit4.class)
public final class StreamSubjectTest {
    @Test
    public void testIsEqualTo() throws Exception {
        Stream<String> stream = Stream.of("hello");
        Truth.assertThat(stream).isEqualTo(stream);
    }

    @Test
    public void testIsEqualToList() throws Exception {
        Stream<String> stream = Stream.of("hello");
        List<String> list = Arrays.asList("hello");
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(stream).isEqualTo(list));
    }

    @Test
    public void testNullStream_fails() throws Exception {
        Stream<String> nullStream = null;
        try {
            Truth.assertThat(nullStream).isEmpty();
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testNullStreamIsNull() throws Exception {
        Stream<String> nullStream = null;
        Truth.assertThat(nullStream).isNull();
    }

    @Test
    public void testIsSameAs() throws Exception {
        Stream<String> stream = Stream.of("hello");
        Truth.assertThat(stream).isSameAs(stream);
    }

    @Test
    public void testIsEmpty() throws Exception {
        Truth.assertThat(Stream.of()).isEmpty();
    }

    @Test
    public void testIsEmpty_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hello")).isEmpty());
    }

    @Test
    public void testIsNotEmpty() throws Exception {
        Truth.assertThat(Stream.of("hello")).isNotEmpty();
    }

    @Test
    public void testIsNotEmpty_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of()).isNotEmpty());
    }

    @Test
    public void testHasSize() throws Exception {
        Truth.assertThat(Stream.of("hello")).hasSize(1);
    }

    @Test
    public void testHasSize_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hello")).hasSize(2));
    }

    @Test
    public void testContainsNoDuplicates() throws Exception {
        Truth.assertThat(Stream.of("hello")).containsNoDuplicates();
    }

    @Test
    public void testContainsNoDuplicates_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hello", "hello")).containsNoDuplicates());
    }

    @Test
    public void testContains() throws Exception {
        Truth.assertThat(Stream.of("hello")).contains("hello");
    }

    @Test
    public void testContains_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hello")).contains("goodbye"));
    }

    @Test
    public void testContainsAnyOf() throws Exception {
        Truth.assertThat(Stream.of("hello")).containsAnyOf("hello", "hell");
    }

    @Test
    public void testContainsAnyOf_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hello")).containsAnyOf("goodbye", "good"));
    }

    @Test
    public void testContainsAnyIn() throws Exception {
        Truth.assertThat(Stream.of("hello")).containsAnyIn(Arrays.asList("hello", "hell"));
    }

    @Test
    public void testContainsAnyIn_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hello")).containsAnyIn(asList("goodbye", "good")));
    }

    @Test
    public void testDoesNotContain() throws Exception {
        Truth.assertThat(Stream.of("hello")).doesNotContain("goodbye");
    }

    @Test
    public void testDoesNotContain_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hello")).doesNotContain("hello"));
    }

    @Test
    public void testContainsNoneOf() throws Exception {
        Truth.assertThat(Stream.of("hello")).containsNoneOf("goodbye", "good");
    }

    @Test
    public void testContainsNoneOf_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hello")).containsNoneOf("hello", "hell"));
    }

    @Test
    public void testContainsNoneIn() throws Exception {
        Truth.assertThat(Stream.of("hello")).containsNoneIn(Arrays.asList("goodbye", "good"));
    }

    @Test
    public void testContainsNoneIn_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hello")).containsNoneIn(asList("hello", "hell")));
    }

    @Test
    public void testContainsAllOf() throws Exception {
        Truth.assertThat(Stream.of("hell", "hello")).containsAllOf("hell", "hello");
    }

    @Test
    public void testContainsAllOf_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hell", "hello")).containsAllOf("hell", "hello", "goodbye"));
    }

    @Test
    public void testContainsAllOf_inOrder() throws Exception {
        Truth.assertThat(Stream.of("hell", "hello")).containsAllOf("hell", "hello").inOrder();
    }

    @Test
    public void testContainsAllOf_inOrder_fails() throws Exception {
        try {
            Truth.assertThat(Stream.of("hell", "hello")).containsAllOf("hello", "hell").inOrder();
            Assert.fail();
        } catch (AssertionError expected) {
            FailureAssertions.assertFailureKeys(expected, "required elements were all found, but order was wrong", "expected order for required elements", "but was");
            FailureAssertions.assertFailureValue(expected, "expected order for required elements", "[hello, hell]");
        }
    }

    @Test
    public void testContainsAllIn() throws Exception {
        Truth.assertThat(Stream.of("hell", "hello")).containsAllIn(Arrays.asList("hell", "hello"));
    }

    @Test
    public void testContainsAllIn_fails() throws Exception {
        AssertionError unused = expectFailure(( whenTesting) -> whenTesting.that(Stream.of("hell", "hello")).containsAllIn(asList("hell", "hello", "goodbye")));
    }

    @Test
    public void testContainsAllIn_inOrder() throws Exception {
        Truth.assertThat(Stream.of("hell", "hello")).containsAllIn(Arrays.asList("hell", "hello")).inOrder();
    }

    @Test
    public void testContainsAllIn_inOrder_fails() throws Exception {
        try {
            Truth.assertThat(Stream.of("hell", "hello")).containsAllIn(Arrays.asList("hello", "hell")).inOrder();
            Assert.fail();
        } catch (AssertionError expected) {
            FailureAssertions.assertFailureKeys(expected, "required elements were all found, but order was wrong", "expected order for required elements", "but was");
            FailureAssertions.assertFailureValue(expected, "expected order for required elements", "[hello, hell]");
        }
    }

    @Test
    public void testContainsExactly() throws Exception {
        Truth.assertThat(Stream.of("hell", "hello")).containsExactly("hell", "hello");
        Truth.assertThat(Stream.of("hell", "hello")).containsExactly("hello", "hell");
    }

    @Test
    public void testContainsExactly_fails() throws Exception {
        try {
            Truth.assertThat(Stream.of("hell", "hello")).containsExactly("hell");
            Assert.fail();
        } catch (AssertionError expected) {
            FailureAssertions.assertFailureKeys(expected, "unexpected (1)", "---", "expected", "but was");
            FailureAssertions.assertFailureValue(expected, "expected", "[hell]");
        }
    }

    @Test
    public void testContainsExactly_inOrder() throws Exception {
        Truth.assertThat(Stream.of("hell", "hello")).containsExactly("hell", "hello").inOrder();
    }

    @Test
    public void testContainsExactly_inOrder_fails() throws Exception {
        try {
            Truth.assertThat(Stream.of("hell", "hello")).containsExactly("hello", "hell").inOrder();
            Assert.fail();
        } catch (AssertionError expected) {
            FailureAssertions.assertFailureKeys(expected, "contents match, but order was wrong", "expected", "but was");
            FailureAssertions.assertFailureValue(expected, "expected", "[hello, hell]");
        }
    }

    @Test
    public void testContainsExactlyElementsIn() throws Exception {
        Truth.assertThat(Stream.of("hell", "hello")).containsExactlyElementsIn(Arrays.asList("hell", "hello"));
        Truth.assertThat(Stream.of("hell", "hello")).containsExactlyElementsIn(Arrays.asList("hello", "hell"));
    }

    @Test
    public void testContainsExactlyElementsIn_fails() throws Exception {
        try {
            Truth.assertThat(Stream.of("hell", "hello")).containsExactlyElementsIn(Arrays.asList("hell"));
            Assert.fail();
        } catch (AssertionError expected) {
            FailureAssertions.assertFailureKeys(expected, "unexpected (1)", "---", "expected", "but was");
            FailureAssertions.assertFailureValue(expected, "expected", "[hell]");
        }
    }

    @Test
    public void testContainsExactlyElementsIn_inOrder() throws Exception {
        Truth.assertThat(Stream.of("hell", "hello")).containsExactlyElementsIn(Arrays.asList("hell", "hello")).inOrder();
    }

    @Test
    public void testContainsExactlyElementsIn_inOrder_fails() throws Exception {
        try {
            Truth.assertThat(Stream.of("hell", "hello")).containsExactlyElementsIn(Arrays.asList("hello", "hell")).inOrder();
            Assert.fail();
        } catch (AssertionError expected) {
            FailureAssertions.assertFailureKeys(expected, "contents match, but order was wrong", "expected", "but was");
            FailureAssertions.assertFailureValue(expected, "expected", "[hello, hell]");
        }
    }
}

