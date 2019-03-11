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
public class CaseInsensitiveRegexHashMapTestClearingAndSize {
    @Test
    public void shouldReturnSize() {
        // when
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(hashMap.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void shouldReturnSizeWhenEmpty() {
        // when
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{  });
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(hashMap.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void shouldClear() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // when
        hashMap.clear();
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(hashMap.isEmpty(), CoreMatchers.is(true));
    }
}

