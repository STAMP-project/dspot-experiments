package org.mockserver.collections.hashmap.nottablematched;


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
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("notKeyOne"), NottableString.string("keyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("notKeyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("notKeyOne"), NottableString.not("notKeyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("notKeyOne"), NottableString.string("keyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("notKeyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("notKeyOne"), NottableString.not("notKeyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("keyOne"), NottableString.string("keyOneValue")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("keyOneValue")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyAndValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("keyOne"), NottableString.not("keyOneValue")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("keyOne"), NottableString.string("keyOneValue")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("keyOneValue")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithKeyAndValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKeyValue(NottableString.not("key.*"), NottableString.not("key.*")), CoreMatchers.is(false));
    }
}

