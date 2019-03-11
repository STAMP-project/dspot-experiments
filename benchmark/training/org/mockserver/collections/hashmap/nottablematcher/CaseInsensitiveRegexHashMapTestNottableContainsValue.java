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
public class CaseInsensitiveRegexHashMapTestNottableContainsValue {
    @Test
    public void singleValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsValue(NottableString.not("key.*")), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsValue("notKey.*"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsValue(NottableString.not("key.*")), CoreMatchers.is(true));
        MatcherAssert.assertThat(hashMap.containsValue("notKey.*"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsValue("keyOneValue"), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsValue(NottableString.not("notKey.*")), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsValue("key.*"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsValue(NottableString.not("notKey.*")), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsValue("key.*"), CoreMatchers.is(false));
    }
}

