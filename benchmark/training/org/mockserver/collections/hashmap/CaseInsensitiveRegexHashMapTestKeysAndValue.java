package org.mockserver.collections.hashmap;


import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexHashMap;
import org.mockserver.collections.CaseInsensitiveRegexMultiMap;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexHashMapTestKeysAndValue {
    @Test
    public void shouldReturnKeysForMapWithSingleEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.keySet(), containsInAnyOrder(NottableString.string("keyOne")));
    }

    @Test
    public void shouldReturnKeysForMapWithMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" });
        // then
        MatcherAssert.assertThat(hashMap.keySet(), containsInAnyOrder(NottableString.string("keyOne"), NottableString.string("keyTwo")));
    }

    @Test
    public void shouldReturnValuesForMapWithSingleEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.values(), containsInAnyOrder(NottableString.string("keyOneValue")));
    }

    @Test
    public void shouldReturnValuesForMapWithMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" });
        // then
        MatcherAssert.assertThat(hashMap.values(), containsInAnyOrder(NottableString.string("keyOneValue"), NottableString.string("keyTwoValue")));
    }

    @Test
    public void shouldReturnEntrySet() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" });
        // then
        MatcherAssert.assertThat(hashMap.entrySet(), containsInAnyOrder(CaseInsensitiveRegexMultiMap.entry("keyOne", "keyOneValue"), CaseInsensitiveRegexMultiMap.entry("keyTwo", "keyTwoValue")));
    }
}

