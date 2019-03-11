package org.mockserver.collections.hashmap;


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
public class CaseInsensitiveRegexHashMapTestGet {
    @Test
    public void shouldGetValueFromMapWithSingleEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyOneValue")));
    }

    @Test
    public void shouldGetValueFromMapWithMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" });
        // then
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyOneValue")));
    }
}

