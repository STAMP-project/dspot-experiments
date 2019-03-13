package org.mockserver.collections.hashmap;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexHashMap;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexHashMapTestContainsValue {
    @Test
    public void singleValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsValue("keyOneValue"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsValue("keyOneValue"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsValue("notKeyOneValue"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsValue("notKeyOneValue"), CoreMatchers.is(false));
    }
}

