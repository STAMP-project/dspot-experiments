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
public class CaseInsensitiveRegexHashMapTestNottableContainsValue {
    @Test
    public void singleValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsValue(NottableString.not("notKeyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsValue(NottableString.not("notKeyOneValue")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsValue(NottableString.not("key.*")), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsValue(NottableString.not("keyOneValue")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsValue(NottableString.not("key.*")), CoreMatchers.is(false));
    }
}

