package org.mockserver.collections.hashmap.nottablematcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexHashMap;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexHashMapTestNottableContainsKeyAndValue {
    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("notKeyOne", "keyOneValue"), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("keyOne"), NottableString.string("keyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("keyOne", "notKeyOneValue"), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("keyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("notKeyOne", "notKeyOneValue"), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("keyOne"), NottableString.not("keyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("notKeyOne", "keyOneValue"), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("keyOne"), NottableString.string("keyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("keyOne", "notKeyOneValue"), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("keyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("notKeyOne", "notKeyOneValue"), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("keyOne"), NottableString.not("keyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("keyOne", "keyOneValue"), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.string("keyOneValue")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatchForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("keyOne", "keyOneValue"), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatchForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("keyOne", "keyOneValue"), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.not("notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("keyTwo", "keyTwoValue"), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.string("keyTwoValue")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatchForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("keyTwo", "keyTwoValue"), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.string("keyTwo"), NottableString.not("notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatchForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue("keyTwo", "keyTwoValue"), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("key.*e"), NottableString.not("key.*e_.*")), CoreMatchers.is(false));
    }
}

