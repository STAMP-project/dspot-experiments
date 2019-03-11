package org.mockserver.collections.hashmap.nottablematched;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
    public void shouldRemoveEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // when
        MatcherAssert.assertThat(hashMap.remove(NottableString.not("keyT.*")), CoreMatchers.is(NottableString.string("keyOneValue")));
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(hashMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyTwoValue")));
        MatcherAssert.assertThat(hashMap.get("keyThree"), CoreMatchers.is(NottableString.string("keyThreeValue")));
    }

    @Test
    public void shouldRemoveNotMatchingEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // when
        MatcherAssert.assertThat(hashMap.remove(NottableString.not("key.*")), CoreMatchers.is(Matchers.nullValue()));
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyOneValue")));
        MatcherAssert.assertThat(hashMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyTwoValue")));
        MatcherAssert.assertThat(hashMap.get("keyThree"), CoreMatchers.is(NottableString.string("keyThreeValue")));
    }
}

