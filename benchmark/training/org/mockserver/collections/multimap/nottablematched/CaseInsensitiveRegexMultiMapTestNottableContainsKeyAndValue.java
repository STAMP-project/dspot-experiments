package org.mockserver.collections.multimap.nottablematched;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexMultiMap;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexMultiMapTestNottableContainsKeyAndValue {
    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyOne"), NottableString.string("keyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("notKeyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyOne"), NottableString.not("notKeyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyOne"), NottableString.string("keyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("notKeyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyOne"), NottableString.not("notKeyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyTwo"), NottableString.string("keyTwo_valueOne")), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyTwo"), NottableString.string("keyTwo_valueTwo")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyTwo"), NottableString.not("notKeyTwo_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyTwo"), NottableString.not("notKeyTwo_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyTwo"), NottableString.string("keyTwo_valueOne")), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyTwo"), NottableString.string("keyTwo_valueTwo")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyTwo"), NottableString.not("notKeyTwo_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKeyTwo"), NottableString.not("notKeyTwo_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyOne"), NottableString.string("keyOne_valueOne")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("keyOne_valueOne")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyAndValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyOne"), NottableString.not("keyOne_valueOne")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForSingleValueWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyOne"), NottableString.string("keyOne_valueOne")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("keyOne_valueOne")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForMultipleValuesWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne")), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.string("keyTwo_valueTwo")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyTwo"), NottableString.not("keyTwo.*")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForMultipleValuesWithKeyAndValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.not("keyTwo.*")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithKeyMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne")), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.string("keyTwo_valueTwo")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyTwo"), NottableString.not("keyTwo.*")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithKeyAndValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("key.*"), NottableString.not("key.*")), CoreMatchers.is(false));
    }
}

