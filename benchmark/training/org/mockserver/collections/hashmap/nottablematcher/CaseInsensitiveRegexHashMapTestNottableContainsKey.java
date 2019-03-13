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
public class CaseInsensitiveRegexHashMapTestNottableContainsKey {
    @Test
    public void singleValuedMapShouldContainValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKey("notKeyOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsKey(NottableString.not("keyOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKey("notKeyOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsKey(NottableString.not("keyOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForSingleValueWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKey("keyOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsKey(NottableString.not("notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainValueForMultipleValuesWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsKey("keyOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsKey(NottableString.not("keyTwo|notKey.*")), CoreMatchers.is(false));
    }
}

