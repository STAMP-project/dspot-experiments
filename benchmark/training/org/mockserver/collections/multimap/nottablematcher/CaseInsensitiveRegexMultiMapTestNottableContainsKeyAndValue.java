package org.mockserver.collections.multimap.nottablematcher;


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
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyOne", "keyOne_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyOne"), NottableString.string("keyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "notKeyOne_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("keyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyOne", "notKeyOne_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyOne"), NottableString.not("keyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyOne", "keyOne_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyOne"), NottableString.string("keyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "notKeyOne_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("keyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyOne", "notKeyOne_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyOne"), NottableString.not("keyOne_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "keyTwo_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "keyTwo_valueTwo"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne")), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.string("keyTwo_valueTwo")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "notKey.*"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainKeyAndValueForMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKeyTwo", "notKey.*"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.not("key.*")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("key.*e", "keyTwo_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue("key.*e", "keyTwo_valueTwo"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne")), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("keyTwo"), NottableString.string("keyTwo_valueTwo")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "notKey.*"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("key.*e"), NottableString.not("key.*")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainKeyAndValueForMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("notKey.*", "notKey.*"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("key.*"), NottableString.not("key.*")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "keyOne_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.string("keyOne_valueOne")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatchForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "keyOne_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyOne"), NottableString.not("notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForSingleValueWithValueMismatchForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "keyOne_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.not("notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueTwo"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.string("keyTwo_valueOne")), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.string("keyTwo_valueTwo")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatchForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "keyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyTwo"), NottableString.not("keyTwo_valueTwo|notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatchForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyOne", "keyTwo_valueTwo|keyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.not("notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueTwo"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.string("keyTwo_valueOne")), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("notKey.*"), NottableString.string("keyTwo_valueTwo")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatchForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.string("keyTwo"), NottableString.not("keyTwo_valueTwo|notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainKeyAndValueForMultipleValuesWithValueMismatchForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsKeyValue("keyTwo", "keyTwo_valueTwo|keyTwo_valueOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKeyValue(NottableString.not("key.*e"), NottableString.not("key.*e_.*")), CoreMatchers.is(false));
    }
}

