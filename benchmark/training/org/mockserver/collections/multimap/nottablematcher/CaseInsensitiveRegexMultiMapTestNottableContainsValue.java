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
public class CaseInsensitiveRegexMultiMapTestNottableContainsValue {
    @Test
    public void singleValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsValue(NottableString.not("key.*")), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("notKey.*"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForSingleValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsValue(NottableString.not("key.*")), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("notKey.*"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldContainValueForMultipleValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsValue(NottableString.not("key.*")), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("notKey.*"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("keyTwo_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("keyTwo_valueTwo"), CoreMatchers.is(true));
    }

    @Test
    public void multiValuedMapShouldContainValueForMultipleValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsValue(NottableString.not("key.*")), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("notKey.*"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("keyTwo_valueOne"), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsValue("keyTwo_valueTwo"), CoreMatchers.is(true));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForSingleValueWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsValue("keyOne_valueOne"), CoreMatchers.is(false));
    }

    @Test
    public void singleValuedMapShouldNotContainValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsValue(NottableString.not("notKey.*")), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsValue("key.*"), CoreMatchers.is(false));
    }

    @Test
    public void multiValuedMapShouldNotContainValueForMultipleValuesWithValueMismatch() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.not("keyThree_valueOne"), NottableString.not("keyThree_valueTwo"), NottableString.not("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsValue(NottableString.not("notKey.*")), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsValue("key.*"), CoreMatchers.is(false));
    }
}

