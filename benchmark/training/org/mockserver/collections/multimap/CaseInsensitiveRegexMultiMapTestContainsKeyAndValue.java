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
public class CaseInsensitiveRegexMultiMapTestContainsKeyAndValue {
    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "keyOne_valueOne"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "keyOne_valueOne"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForMultipleValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueTwo"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForMultipleValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueTwo"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyOne", "keyOne_valueOne"), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "notKeyOne_valueOne"), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyAndValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyOne", "notKeyOne_valueOne"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyOne", "keyOne_valueOne"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "notKeyOne_valueOne"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyAndValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyOne", "notKeyOne_valueOne"), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForMultipleValuesWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "keyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "keyTwo_valueTwo"), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "notKeyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "notKeyTwo_valueTwo"), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForMultipleValuesWithKeyAndValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "notKeyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "notKeyTwo_valueTwo"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "keyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "keyTwo_valueTwo"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "notKeyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "notKeyTwo_valueTwo"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithKeyAndValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "notKeyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "notKeyTwo_valueTwo"), CoreMatchers.is(false));
    }
}

