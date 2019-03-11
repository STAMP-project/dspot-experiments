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
public class CaseInsensitiveRegexMultiMapTestContainsKey {
    @Test
    public void singleValuedMapShouldContainKeyForSingleValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("keyOne"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyForSingleValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("keyOne"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyForMultipleValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("keyTwo"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyForMultipleValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("keyTwo"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("notKeyOne"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("notKeyOne"), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyForMultipleValuesWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("notKeyTwo"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyForMultipleValuesWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("notKeyTwo"), CoreMatchers.is(false));
    }
}

