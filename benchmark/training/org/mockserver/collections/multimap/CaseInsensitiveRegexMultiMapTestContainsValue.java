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
public class CaseInsensitiveRegexMultiMapTestContainsValue {
    @Test
    public void singleValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsValue("keyOne_valueOne"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsValue("keyOne_valueOne"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainValueForMultipleValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsValue("keyTwo_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("keyTwo_valueTwo"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForMultipleValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsValue("keyTwo_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("keyTwo_valueTwo"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsValue("notKeyOne_valueOne"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsValue("notKeyOne_valueOne"), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsValue("notKeyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsValue("notKeyTwo_valueTwo"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsValue("notKeyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsValue("notKeyTwo_valueTwo"), CoreMatchers.is(false));
    }
}

