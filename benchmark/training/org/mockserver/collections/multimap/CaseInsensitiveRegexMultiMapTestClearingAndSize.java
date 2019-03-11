package org.mockserver.collections.multimap;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexMultiMap;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexMultiMapTestClearingAndSize {
    @Test
    public void shouldReturnSize() {
        // when
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void shouldReturnSizeWhenEmpty() {
        // when
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{  });
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(multiMap.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void shouldClear() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // when
        multiMap.clear();
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(multiMap.isEmpty(), CoreMatchers.is(true));
    }
}

