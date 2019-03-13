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
public class CaseInsensitiveRegexHashMapTestNottableRemove {
    @Test
    public void shouldRemoveSingleValueEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThreeValue") });
        // when
        MatcherAssert.assertThat(hashMap.remove("keyT.*"), CoreMatchers.is(NottableString.string("keyOneValue")));
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyTwoValue")));
        MatcherAssert.assertThat(hashMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyThreeValue")));
        MatcherAssert.assertThat(hashMap.get("keyThree"), CoreMatchers.is(NottableString.string("keyTwoValue")));
    }

    @Test
    public void shouldRemoveSingleValueEntryWithNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThreeValue") });
        // when
        MatcherAssert.assertThat(hashMap.remove(NottableString.not("keyO.*")), CoreMatchers.is(NottableString.string("keyOneValue")));
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyTwoValue")));
        MatcherAssert.assertThat(hashMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyThreeValue")));
        MatcherAssert.assertThat(hashMap.get("keyThree"), CoreMatchers.is(NottableString.string("keyTwoValue")));
    }

    @Test
    public void shouldRemoveNotMatchingEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThreeValue") });
        // when
        MatcherAssert.assertThat(hashMap.remove("key.*"), CoreMatchers.nullValue());
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyTwoValue")));
        MatcherAssert.assertThat(hashMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyOneValue")));
        MatcherAssert.assertThat(hashMap.get("keyThree"), CoreMatchers.is(NottableString.string("keyOneValue")));
    }

    @Test
    public void shouldRemoveNotMatchingEntryWithNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThreeValue") });
        // when
        MatcherAssert.assertThat(hashMap.remove(NottableString.not("key.*")), CoreMatchers.is(NottableString.string("keyOneValue")));
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyTwoValue")));
        MatcherAssert.assertThat(hashMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyThreeValue")));
        MatcherAssert.assertThat(hashMap.get("keyThree"), CoreMatchers.is(NottableString.string("keyTwoValue")));
    }
}

