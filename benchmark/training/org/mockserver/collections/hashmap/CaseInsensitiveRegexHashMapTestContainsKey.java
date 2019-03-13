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
public class CaseInsensitiveRegexHashMapTestContainsKey {
    @Test
    public void singleValuedMapShouldContainKeyForSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKey("keyOne"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyForSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKey("keyOne"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKey("notKeyOne"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsKey("notKeyOne"), CoreMatchers.is(false));
    }
}

