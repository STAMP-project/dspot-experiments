/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.test.json;


import JSONCompareMode.LENIENT;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.JSONComparator;


/**
 * Tests for {@link JsonContentAssert}. Some tests here are based on Spring Framework
 * tests for {@link JsonPathExpectationsHelper}.
 *
 * @author Phillip Webb
 */
public class JsonContentAssertTests {
    private static final String SOURCE = JsonContentAssertTests.loadJson("source.json");

    private static final String LENIENT_SAME = JsonContentAssertTests.loadJson("lenient-same.json");

    private static final String DIFFERENT = JsonContentAssertTests.loadJson("different.json");

    private static final String TYPES = JsonContentAssertTests.loadJson("types.json");

    private static final String SIMPSONS = JsonContentAssertTests.loadJson("simpsons.json");

    private static JSONComparator COMPARATOR = new org.skyscreamer.jsonassert.comparator.DefaultComparator(JSONCompareMode.LENIENT);

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void isEqualToWhenStringIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualTo(JsonContentAssertTests.LENIENT_SAME);
    }

    @Test
    public void isEqualToWhenNullActualShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(null)).isEqualTo(SOURCE));
    }

    @Test
    public void isEqualToWhenStringIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualTo(DIFFERENT));
    }

    @Test
    public void isEqualToWhenResourcePathIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualTo("lenient-same.json");
    }

    @Test
    public void isEqualToWhenResourcePathIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualTo("different.json"));
    }

    @Test
    public void isEqualToWhenBytesAreMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualTo(JsonContentAssertTests.LENIENT_SAME.getBytes());
    }

    @Test
    public void isEqualToWhenBytesAreNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualTo(DIFFERENT.getBytes()));
    }

    @Test
    public void isEqualToWhenFileIsMatchingShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualTo(createFile(JsonContentAssertTests.LENIENT_SAME));
    }

    @Test
    public void isEqualToWhenFileIsNotMatchingShouldFail() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualTo(createFile(DIFFERENT)));
    }

    @Test
    public void isEqualToWhenInputStreamIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualTo(createInputStream(JsonContentAssertTests.LENIENT_SAME));
    }

    @Test
    public void isEqualToWhenInputStreamIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualTo(createInputStream(DIFFERENT)));
    }

    @Test
    public void isEqualToWhenResourceIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualTo(createResource(JsonContentAssertTests.LENIENT_SAME));
    }

    @Test
    public void isEqualToWhenResourceIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualTo(createResource(DIFFERENT)));
    }

    @Test
    public void isEqualToJsonWhenStringIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(JsonContentAssertTests.LENIENT_SAME);
    }

    @Test
    public void isEqualToJsonWhenNullActualShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(null)).isEqualToJson(SOURCE));
    }

    @Test
    public void isEqualToJsonWhenStringIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(DIFFERENT));
    }

    @Test
    public void isEqualToJsonWhenResourcePathIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson("lenient-same.json");
    }

    @Test
    public void isEqualToJsonWhenResourcePathIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson("different.json"));
    }

    @Test
    public void isEqualToJsonWhenResourcePathAndClassIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson("lenient-same.json", getClass());
    }

    @Test
    public void isEqualToJsonWhenResourcePathAndClassIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson("different.json", getClass()));
    }

    @Test
    public void isEqualToJsonWhenBytesAreMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(JsonContentAssertTests.LENIENT_SAME.getBytes());
    }

    @Test
    public void isEqualToJsonWhenBytesAreNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(DIFFERENT.getBytes()));
    }

    @Test
    public void isEqualToJsonWhenFileIsMatchingShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(createFile(JsonContentAssertTests.LENIENT_SAME));
    }

    @Test
    public void isEqualToJsonWhenFileIsNotMatchingShouldFail() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(createFile(DIFFERENT)));
    }

    @Test
    public void isEqualToJsonWhenInputStreamIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(createInputStream(JsonContentAssertTests.LENIENT_SAME));
    }

    @Test
    public void isEqualToJsonWhenInputStreamIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(createInputStream(DIFFERENT)));
    }

    @Test
    public void isEqualToJsonWhenResourceIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(createResource(JsonContentAssertTests.LENIENT_SAME));
    }

    @Test
    public void isEqualToJsonWhenResourceIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(createResource(DIFFERENT)));
    }

    @Test
    public void isStrictlyEqualToJsonWhenStringIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isStrictlyEqualToJson(JsonContentAssertTests.SOURCE);
    }

    @Test
    public void isStrictlyEqualToJsonWhenStringIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isStrictlyEqualToJson(LENIENT_SAME));
    }

    @Test
    public void isStrictlyEqualToJsonWhenResourcePathIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isStrictlyEqualToJson("source.json");
    }

    @Test
    public void isStrictlyEqualToJsonWhenResourcePathIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isStrictlyEqualToJson("lenient-same.json"));
    }

    @Test
    public void isStrictlyEqualToJsonWhenResourcePathAndClassIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isStrictlyEqualToJson("source.json", getClass());
    }

    @Test
    public void isStrictlyEqualToJsonWhenResourcePathAndClassIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isStrictlyEqualToJson("lenient-same.json", getClass()));
    }

    @Test
    public void isStrictlyEqualToJsonWhenBytesAreMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isStrictlyEqualToJson(JsonContentAssertTests.SOURCE.getBytes());
    }

    @Test
    public void isStrictlyEqualToJsonWhenBytesAreNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isStrictlyEqualToJson(LENIENT_SAME.getBytes()));
    }

    @Test
    public void isStrictlyEqualToJsonWhenFileIsMatchingShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isStrictlyEqualToJson(createFile(JsonContentAssertTests.SOURCE));
    }

    @Test
    public void isStrictlyEqualToJsonWhenFileIsNotMatchingShouldFail() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isStrictlyEqualToJson(createFile(LENIENT_SAME)));
    }

    @Test
    public void isStrictlyEqualToJsonWhenInputStreamIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isStrictlyEqualToJson(createInputStream(JsonContentAssertTests.SOURCE));
    }

    @Test
    public void isStrictlyEqualToJsonWhenInputStreamIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isStrictlyEqualToJson(createInputStream(LENIENT_SAME)));
    }

    @Test
    public void isStrictlyEqualToJsonWhenResourceIsMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isStrictlyEqualToJson(createResource(JsonContentAssertTests.SOURCE));
    }

    @Test
    public void isStrictlyEqualToJsonWhenResourceIsNotMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isStrictlyEqualToJson(createResource(LENIENT_SAME)));
    }

    @Test
    public void isEqualToJsonWhenStringIsMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(JsonContentAssertTests.LENIENT_SAME, LENIENT);
    }

    @Test
    public void isEqualToJsonWhenStringIsNotMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(DIFFERENT, JSONCompareMode.LENIENT));
    }

    @Test
    public void isEqualToJsonWhenResourcePathIsMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson("lenient-same.json", LENIENT);
    }

    @Test
    public void isEqualToJsonWhenResourcePathIsNotMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson("different.json", JSONCompareMode.LENIENT));
    }

    @Test
    public void isEqualToJsonWhenResourcePathAndClassIsMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson("lenient-same.json", getClass(), LENIENT);
    }

    @Test
    public void isEqualToJsonWhenResourcePathAndClassIsNotMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson("different.json", getClass(), JSONCompareMode.LENIENT));
    }

    @Test
    public void isEqualToJsonWhenBytesAreMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(JsonContentAssertTests.LENIENT_SAME.getBytes(), LENIENT);
    }

    @Test
    public void isEqualToJsonWhenBytesAreNotMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(DIFFERENT.getBytes(), JSONCompareMode.LENIENT));
    }

    @Test
    public void isEqualToJsonWhenFileIsMatchingAndLenientShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(createFile(JsonContentAssertTests.LENIENT_SAME), LENIENT);
    }

    @Test
    public void isEqualToJsonWhenFileIsNotMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(createFile(DIFFERENT), JSONCompareMode.LENIENT));
    }

    @Test
    public void isEqualToJsonWhenInputStreamIsMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(createInputStream(JsonContentAssertTests.LENIENT_SAME), LENIENT);
    }

    @Test
    public void isEqualToJsonWhenInputStreamIsNotMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(createInputStream(DIFFERENT), JSONCompareMode.LENIENT));
    }

    @Test
    public void isEqualToJsonWhenResourceIsMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(createResource(JsonContentAssertTests.LENIENT_SAME), LENIENT);
    }

    @Test
    public void isEqualToJsonWhenResourceIsNotMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(createResource(DIFFERENT), JSONCompareMode.LENIENT));
    }

    @Test
    public void isEqualToJsonWhenStringIsMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(JsonContentAssertTests.LENIENT_SAME, JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isEqualToJsonWhenStringIsNotMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(DIFFERENT, JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isEqualToJsonWhenResourcePathIsMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson("lenient-same.json", JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isEqualToJsonWhenResourcePathIsNotMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson("different.json", JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isEqualToJsonWhenResourcePathAndClassAreMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson("lenient-same.json", getClass(), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isEqualToJsonWhenResourcePathAndClassAreNotMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson("different.json", getClass(), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isEqualToJsonWhenBytesAreMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(JsonContentAssertTests.LENIENT_SAME.getBytes(), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isEqualToJsonWhenBytesAreNotMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(DIFFERENT.getBytes(), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isEqualToJsonWhenFileIsMatchingAndComparatorShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(createFile(JsonContentAssertTests.LENIENT_SAME), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isEqualToJsonWhenFileIsNotMatchingAndComparatorShouldFail() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(createFile(DIFFERENT), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isEqualToJsonWhenInputStreamIsMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(createInputStream(JsonContentAssertTests.LENIENT_SAME), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isEqualToJsonWhenInputStreamIsNotMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(createInputStream(DIFFERENT), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isEqualToJsonWhenResourceIsMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isEqualToJson(createResource(JsonContentAssertTests.LENIENT_SAME), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isEqualToJsonWhenResourceIsNotMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isEqualToJson(createResource(DIFFERENT), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isNotEqualToWhenStringIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualTo(LENIENT_SAME));
    }

    @Test
    public void isNotEqualToWhenNullActualShouldPass() {
        assertThat(forJson(null)).isNotEqualTo(JsonContentAssertTests.SOURCE);
    }

    @Test
    public void isNotEqualToWhenStringIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualTo(JsonContentAssertTests.DIFFERENT);
    }

    @Test
    public void isNotEqualToWhenResourcePathIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualTo("lenient-same.json"));
    }

    @Test
    public void isNotEqualToWhenResourcePathIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualTo("different.json");
    }

    @Test
    public void isNotEqualToWhenBytesAreMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualTo(LENIENT_SAME.getBytes()));
    }

    @Test
    public void isNotEqualToWhenBytesAreNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualTo(JsonContentAssertTests.DIFFERENT.getBytes());
    }

    @Test
    public void isNotEqualToWhenFileIsMatchingShouldFail() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualTo(createFile(LENIENT_SAME)));
    }

    @Test
    public void isNotEqualToWhenFileIsNotMatchingShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualTo(createFile(JsonContentAssertTests.DIFFERENT));
    }

    @Test
    public void isNotEqualToWhenInputStreamIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualTo(createInputStream(LENIENT_SAME)));
    }

    @Test
    public void isNotEqualToWhenInputStreamIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualTo(createInputStream(JsonContentAssertTests.DIFFERENT));
    }

    @Test
    public void isNotEqualToWhenResourceIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualTo(createResource(LENIENT_SAME)));
    }

    @Test
    public void isNotEqualToWhenResourceIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualTo(createResource(JsonContentAssertTests.DIFFERENT));
    }

    @Test
    public void isNotEqualToJsonWhenStringIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(LENIENT_SAME));
    }

    @Test
    public void isNotEqualToJsonWhenNullActualShouldPass() {
        assertThat(forJson(null)).isNotEqualToJson(JsonContentAssertTests.SOURCE);
    }

    @Test
    public void isNotEqualToJsonWhenStringIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(JsonContentAssertTests.DIFFERENT);
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson("lenient-same.json"));
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson("different.json");
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathAndClassAreMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson("lenient-same.json", getClass()));
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathAndClassAreNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson("different.json", getClass());
    }

    @Test
    public void isNotEqualToJsonWhenBytesAreMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(LENIENT_SAME.getBytes()));
    }

    @Test
    public void isNotEqualToJsonWhenBytesAreNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(JsonContentAssertTests.DIFFERENT.getBytes());
    }

    @Test
    public void isNotEqualToJsonWhenFileIsMatchingShouldFail() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(createFile(LENIENT_SAME)));
    }

    @Test
    public void isNotEqualToJsonWhenFileIsNotMatchingShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(createFile(JsonContentAssertTests.DIFFERENT));
    }

    @Test
    public void isNotEqualToJsonWhenInputStreamIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(createInputStream(LENIENT_SAME)));
    }

    @Test
    public void isNotEqualToJsonWhenInputStreamIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(createInputStream(JsonContentAssertTests.DIFFERENT));
    }

    @Test
    public void isNotEqualToJsonWhenResourceIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(createResource(LENIENT_SAME)));
    }

    @Test
    public void isNotEqualToJsonWhenResourceIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(createResource(JsonContentAssertTests.DIFFERENT));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenStringIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotStrictlyEqualToJson(SOURCE));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenStringIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotStrictlyEqualToJson(JsonContentAssertTests.LENIENT_SAME);
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenResourcePathIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotStrictlyEqualToJson("source.json"));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenResourcePathIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotStrictlyEqualToJson("lenient-same.json");
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenResourcePathAndClassAreMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotStrictlyEqualToJson("source.json", getClass()));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenResourcePathAndClassAreNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotStrictlyEqualToJson("lenient-same.json", getClass());
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenBytesAreMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotStrictlyEqualToJson(SOURCE.getBytes()));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenBytesAreNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotStrictlyEqualToJson(JsonContentAssertTests.LENIENT_SAME.getBytes());
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenFileIsMatchingShouldFail() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotStrictlyEqualToJson(createFile(SOURCE)));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenFileIsNotMatchingShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotStrictlyEqualToJson(createFile(JsonContentAssertTests.LENIENT_SAME));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenInputStreamIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotStrictlyEqualToJson(createInputStream(SOURCE)));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenInputStreamIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotStrictlyEqualToJson(createInputStream(JsonContentAssertTests.LENIENT_SAME));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenResourceIsMatchingShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotStrictlyEqualToJson(createResource(SOURCE)));
    }

    @Test
    public void isNotStrictlyEqualToJsonWhenResourceIsNotMatchingShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotStrictlyEqualToJson(createResource(JsonContentAssertTests.LENIENT_SAME));
    }

    @Test
    public void isNotEqualToJsonWhenStringIsMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(LENIENT_SAME, JSONCompareMode.LENIENT));
    }

    @Test
    public void isNotEqualToJsonWhenStringIsNotMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(JsonContentAssertTests.DIFFERENT, LENIENT);
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathIsMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson("lenient-same.json", JSONCompareMode.LENIENT));
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathIsNotMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson("different.json", LENIENT);
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathAndClassAreMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson("lenient-same.json", getClass(), JSONCompareMode.LENIENT));
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathAndClassAreNotMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson("different.json", getClass(), LENIENT);
    }

    @Test
    public void isNotEqualToJsonWhenBytesAreMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(LENIENT_SAME.getBytes(), JSONCompareMode.LENIENT));
    }

    @Test
    public void isNotEqualToJsonWhenBytesAreNotMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(JsonContentAssertTests.DIFFERENT.getBytes(), LENIENT);
    }

    @Test
    public void isNotEqualToJsonWhenFileIsMatchingAndLenientShouldFail() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(createFile(LENIENT_SAME), JSONCompareMode.LENIENT));
    }

    @Test
    public void isNotEqualToJsonWhenFileIsNotMatchingAndLenientShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(createFile(JsonContentAssertTests.DIFFERENT), LENIENT);
    }

    @Test
    public void isNotEqualToJsonWhenInputStreamIsMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(createInputStream(LENIENT_SAME), JSONCompareMode.LENIENT));
    }

    @Test
    public void isNotEqualToJsonWhenInputStreamIsNotMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(createInputStream(JsonContentAssertTests.DIFFERENT), LENIENT);
    }

    @Test
    public void isNotEqualToJsonWhenResourceIsMatchingAndLenientShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(createResource(LENIENT_SAME), JSONCompareMode.LENIENT));
    }

    @Test
    public void isNotEqualToJsonWhenResourceIsNotMatchingAndLenientShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(createResource(JsonContentAssertTests.DIFFERENT), LENIENT);
    }

    @Test
    public void isNotEqualToJsonWhenStringIsMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(LENIENT_SAME, JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isNotEqualToJsonWhenStringIsNotMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(JsonContentAssertTests.DIFFERENT, JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathIsMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson("lenient-same.json", JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathIsNotMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson("different.json", JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathAndClassAreMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson("lenient-same.json", getClass(), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isNotEqualToJsonWhenResourcePathAndClassAreNotMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson("different.json", getClass(), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isNotEqualToJsonWhenBytesAreMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(LENIENT_SAME.getBytes(), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isNotEqualToJsonWhenBytesAreNotMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(JsonContentAssertTests.DIFFERENT.getBytes(), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isNotEqualToJsonWhenFileIsMatchingAndComparatorShouldFail() throws Exception {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(createFile(LENIENT_SAME), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isNotEqualToJsonWhenFileIsNotMatchingAndComparatorShouldPass() throws Exception {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(createFile(JsonContentAssertTests.DIFFERENT), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isNotEqualToJsonWhenInputStreamIsMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(createInputStream(LENIENT_SAME), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isNotEqualToJsonWhenInputStreamIsNotMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(createInputStream(JsonContentAssertTests.DIFFERENT), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void isNotEqualToJsonWhenResourceIsMatchingAndComparatorShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SOURCE)).isNotEqualToJson(createResource(LENIENT_SAME), JsonContentAssertTests.COMPARATOR));
    }

    @Test
    public void isNotEqualToJsonWhenResourceIsNotMatchingAndComparatorShouldPass() {
        assertThat(forJson(JsonContentAssertTests.SOURCE)).isNotEqualToJson(createResource(JsonContentAssertTests.DIFFERENT), JsonContentAssertTests.COMPARATOR);
    }

    @Test
    public void hasJsonPathValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathValue("$.str");
    }

    @Test
    public void hasJsonPathValueForAnEmptyArray() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathValue("$.emptyArray");
    }

    @Test
    public void hasJsonPathValueForAnEmptyMap() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathValue("$.emptyMap");
    }

    @Test
    public void hasJsonPathValueForIndefinitePathWithResults() {
        assertThat(forJson(JsonContentAssertTests.SIMPSONS)).hasJsonPathValue("$.familyMembers[?(@.name == 'Bart')]");
    }

    @Test
    public void hasJsonPathValueForIndefinitePathWithEmptyResults() {
        String expression = "$.familyMembers[?(@.name == 'Dilbert')]";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SIMPSONS)).hasJsonPathValue(expression)).withMessageContaining((("No value at JSON path \"" + expression) + "\""));
    }

    @Test
    public void doesNotHaveJsonPathValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).doesNotHaveJsonPathValue("$.bogus");
    }

    @Test
    public void doesNotHaveJsonPathValueForAnEmptyArray() {
        String expression = "$.emptyArray";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).doesNotHaveJsonPathValue(expression)).withMessageContaining((("Expected no value at JSON path \"" + expression) + "\" but found: []"));
    }

    @Test
    public void doesNotHaveJsonPathValueForAnEmptyMap() {
        String expression = "$.emptyMap";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).doesNotHaveJsonPathValue(expression)).withMessageContaining((("Expected no value at JSON path \"" + expression) + "\" but found: {}"));
    }

    @Test
    public void doesNotHaveJsonPathValueForIndefinitePathWithResults() {
        String expression = "$.familyMembers[?(@.name == 'Bart')]";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SIMPSONS)).doesNotHaveJsonPathValue(expression)).withMessageContaining((("Expected no value at JSON path \"" + expression) + "\" but found: [{\"name\":\"Bart\"}]"));
    }

    @Test
    public void doesNotHaveJsonPathValueForIndefinitePathWithEmptyResults() {
        assertThat(forJson(JsonContentAssertTests.SIMPSONS)).doesNotHaveJsonPathValue("$.familyMembers[?(@.name == 'Dilbert')]");
    }

    @Test
    public void hasEmptyJsonPathValueForAnEmptyString() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasEmptyJsonPathValue("$.emptyString");
    }

    @Test
    public void hasEmptyJsonPathValueForAnEmptyArray() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasEmptyJsonPathValue("$.emptyArray");
    }

    @Test
    public void hasEmptyJsonPathValueForAnEmptyMap() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasEmptyJsonPathValue("$.emptyMap");
    }

    @Test
    public void hasEmptyJsonPathValueForIndefinitePathWithEmptyResults() {
        assertThat(forJson(JsonContentAssertTests.SIMPSONS)).hasEmptyJsonPathValue("$.familyMembers[?(@.name == 'Dilbert')]");
    }

    @Test
    public void hasEmptyJsonPathValueForIndefinitePathWithResults() {
        String expression = "$.familyMembers[?(@.name == 'Bart')]";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SIMPSONS)).hasEmptyJsonPathValue(expression)).withMessageContaining((("Expected an empty value at JSON path \"" + expression) + "\" but found: [{\"name\":\"Bart\"}]"));
    }

    @Test
    public void hasEmptyJsonPathValueForWhitespace() {
        String expression = "$.whitespace";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).hasEmptyJsonPathValue(expression)).withMessageContaining((("Expected an empty value at JSON path \"" + expression) + "\" but found: \'    \'"));
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForString() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).doesNotHaveEmptyJsonPathValue("$.str");
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForNumber() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).doesNotHaveEmptyJsonPathValue("$.num");
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForBoolean() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).doesNotHaveEmptyJsonPathValue("$.bool");
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForArray() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).doesNotHaveEmptyJsonPathValue("$.arr");
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForMap() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).doesNotHaveEmptyJsonPathValue("$.colorMap");
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForIndefinitePathWithResults() {
        assertThat(forJson(JsonContentAssertTests.SIMPSONS)).doesNotHaveEmptyJsonPathValue("$.familyMembers[?(@.name == 'Bart')]");
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForIndefinitePathWithEmptyResults() {
        String expression = "$.familyMembers[?(@.name == 'Dilbert')]";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(SIMPSONS)).doesNotHaveEmptyJsonPathValue(expression)).withMessageContaining((("Expected a non-empty value at JSON path \"" + expression) + "\" but found: []"));
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForAnEmptyString() {
        String expression = "$.emptyString";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).doesNotHaveEmptyJsonPathValue(expression)).withMessageContaining((("Expected a non-empty value at JSON path \"" + expression) + "\" but found: \'\'"));
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForForAnEmptyArray() {
        String expression = "$.emptyArray";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).doesNotHaveEmptyJsonPathValue(expression)).withMessageContaining((("Expected a non-empty value at JSON path \"" + expression) + "\" but found: []"));
    }

    @Test
    public void doesNotHaveEmptyJsonPathValueForAnEmptyMap() {
        String expression = "$.emptyMap";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).doesNotHaveEmptyJsonPathValue(expression)).withMessageContaining((("Expected a non-empty value at JSON path \"" + expression) + "\" but found: {}"));
    }

    @Test
    public void hasJsonPathStringValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathStringValue("$.str");
    }

    @Test
    public void hasJsonPathStringValueForAnEmptyString() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathStringValue("$.emptyString");
    }

    @Test
    public void hasJsonPathStringValueForNonString() {
        String expression = "$.bool";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).hasJsonPathStringValue(expression)).withMessageContaining((("Expected a string at JSON path \"" + expression) + "\" but found: true"));
    }

    @Test
    public void hasJsonPathNumberValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathNumberValue("$.num");
    }

    @Test
    public void hasJsonPathNumberValueForNonNumber() {
        String expression = "$.bool";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).hasJsonPathNumberValue(expression)).withMessageContaining((("Expected a number at JSON path \"" + expression) + "\" but found: true"));
    }

    @Test
    public void hasJsonPathBooleanValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathBooleanValue("$.bool");
    }

    @Test
    public void hasJsonPathBooleanValueForNonBoolean() {
        String expression = "$.num";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).hasJsonPathBooleanValue(expression)).withMessageContaining((("Expected a boolean at JSON path \"" + expression) + "\" but found: 5"));
    }

    @Test
    public void hasJsonPathArrayValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathArrayValue("$.arr");
    }

    @Test
    public void hasJsonPathArrayValueForAnEmptyArray() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathArrayValue("$.emptyArray");
    }

    @Test
    public void hasJsonPathArrayValueForNonArray() {
        String expression = "$.str";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).hasJsonPathArrayValue(expression)).withMessageContaining((("Expected an array at JSON path \"" + expression) + "\" but found: \'foo\'"));
    }

    @Test
    public void hasJsonPathMapValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathMapValue("$.colorMap");
    }

    @Test
    public void hasJsonPathMapValueForAnEmptyMap() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).hasJsonPathMapValue("$.emptyMap");
    }

    @Test
    public void hasJsonPathMapValueForNonMap() {
        String expression = "$.str";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).hasJsonPathMapValue(expression)).withMessageContaining((("Expected a map at JSON path \"" + expression) + "\" but found: \'foo\'"));
    }

    @Test
    public void extractingJsonPathValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathValue("@.str").isEqualTo("foo");
    }

    @Test
    public void extractingJsonPathValueForMissing() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathValue("@.bogus").isNull();
    }

    @Test
    public void extractingJsonPathStringValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathStringValue("@.str").isEqualTo("foo");
    }

    @Test
    public void extractingJsonPathStringValueForMissing() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathStringValue("@.bogus").isNull();
    }

    @Test
    public void extractingJsonPathStringValueForEmptyString() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathStringValue("@.emptyString").isEmpty();
    }

    @Test
    public void extractingJsonPathStringValueForWrongType() {
        String expression = "$.num";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).extractingJsonPathStringValue(expression)).withMessageContaining((("Expected a string at JSON path \"" + expression) + "\" but found: 5"));
    }

    @Test
    public void extractingJsonPathNumberValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathNumberValue("@.num").isEqualTo(5);
    }

    @Test
    public void extractingJsonPathNumberValueForMissing() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathNumberValue("@.bogus").isNull();
    }

    @Test
    public void extractingJsonPathNumberValueForWrongType() {
        String expression = "$.str";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).extractingJsonPathNumberValue(expression)).withMessageContaining((("Expected a number at JSON path \"" + expression) + "\" but found: \'foo\'"));
    }

    @Test
    public void extractingJsonPathBooleanValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathBooleanValue("@.bool").isTrue();
    }

    @Test
    public void extractingJsonPathBooleanValueForMissing() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathBooleanValue("@.bogus").isNull();
    }

    @Test
    public void extractingJsonPathBooleanValueForWrongType() {
        String expression = "$.str";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).extractingJsonPathBooleanValue(expression)).withMessageContaining((("Expected a boolean at JSON path \"" + expression) + "\" but found: \'foo\'"));
    }

    @Test
    public void extractingJsonPathArrayValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathArrayValue("@.arr").containsExactly(42);
    }

    @Test
    public void extractingJsonPathArrayValueForMissing() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathArrayValue("@.bogus").isNull();
    }

    @Test
    public void extractingJsonPathArrayValueForEmpty() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathArrayValue("@.emptyArray").isEmpty();
    }

    @Test
    public void extractingJsonPathArrayValueForWrongType() {
        String expression = "$.str";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).extractingJsonPathArrayValue(expression)).withMessageContaining((("Expected an array at JSON path \"" + expression) + "\" but found: \'foo\'"));
    }

    @Test
    public void extractingJsonPathMapValue() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathMapValue("@.colorMap").contains(entry("red", "rojo"));
    }

    @Test
    public void extractingJsonPathMapValueForMissing() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathMapValue("@.bogus").isNull();
    }

    @Test
    public void extractingJsonPathMapValueForEmpty() {
        assertThat(forJson(JsonContentAssertTests.TYPES)).extractingJsonPathMapValue("@.emptyMap").isEmpty();
    }

    @Test
    public void extractingJsonPathMapValueForWrongType() {
        String expression = "$.str";
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forJson(TYPES)).extractingJsonPathMapValue(expression)).withMessageContaining((("Expected a map at JSON path \"" + expression) + "\" but found: \'foo\'"));
    }

    @Test
    public void isNullWhenActualIsNullShouldPass() {
        assertThat(forJson(null)).isNull();
    }
}

