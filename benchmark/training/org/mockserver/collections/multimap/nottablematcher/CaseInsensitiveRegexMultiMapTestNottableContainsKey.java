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
public class CaseInsensitiveRegexMultiMapTestNottableContainsKey {
    @Test
    public void singleValuedMapShouldContainValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("notKeyOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKey(NottableString.not("keyOne")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("notKeyOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKey(NottableString.not("keyOne")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainValueForMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("notKeyTwo"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKey(NottableString.not("keyTwo")), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("key.*e"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsKey(NottableString.not("keyTwo")), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForSingleValueWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("keyOne"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKey(NottableString.not("notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForMultipleValuesWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("keyTwo"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKey(NottableString.not("notKey.*")), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainValueForMultipleValuesWithValueMismatchForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsKey("keyTwo"), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsKey(NottableString.not("key.*e")), CoreMatchers.is(false));
    }
}

