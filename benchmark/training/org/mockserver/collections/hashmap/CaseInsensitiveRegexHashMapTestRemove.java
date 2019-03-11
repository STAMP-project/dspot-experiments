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
public class CaseInsensitiveRegexHashMapTestRemove {
    @Test
    public void shouldRemoveFromMapWithSingleEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // when
        hashMap.remove("keyOne");
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldRemoveFromMapWithMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // when
        hashMap.remove("keyOne");
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(hashMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyTwoValue")));
        MatcherAssert.assertThat(hashMap.get("keyThree"), CoreMatchers.is(NottableString.string("keyThreeValue")));
    }

    @Test
    public void shouldRemoveNoMatchingEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // when
        hashMap.remove("keyFour");
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyOneValue")));
        MatcherAssert.assertThat(hashMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyTwoValue")));
        MatcherAssert.assertThat(hashMap.get("keyThree"), CoreMatchers.is(NottableString.string("keyThreeValue")));
    }
}

