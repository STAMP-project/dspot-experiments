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
public class CaseInsensitiveRegexMultiMapTestNottableGet {
    @Test
    public void shouldGetSingeValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.get("keyT.*"), CoreMatchers.is(NottableString.string("keyOne_valueOne")));
        MatcherAssert.assertThat(multiMap.get(NottableString.not("keyO.*")), CoreMatchers.is(NottableString.string("keyOne_valueOne")));
    }

    @Test
    public void shouldGetFirstMultiValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.get("key.{1,4}e"), CoreMatchers.is(NottableString.string("keyTwo_valueOne")));
        MatcherAssert.assertThat(multiMap.get(NottableString.not("keyTwo")), CoreMatchers.is(NottableString.string("keyTwo_valueOne")));
    }

    @Test
    public void shouldGetAllMultiValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.getAll("keyO.*"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
        MatcherAssert.assertThat(multiMap.getAll(NottableString.not("keyT.*")), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldGetAllSingeValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.getAll("keyT.*"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        MatcherAssert.assertThat(multiMap.getAll(NottableString.not("keyO.*")), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
    }

    @Test
    public void shouldGetAllMultiValuesFromMultipleKeys() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.getAll("keyO.*"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll(NottableString.not("keyT.*")), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
    }

    @Test
    public void shouldGetAllSingeValueFromMultipleKeys() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.getAll("keyT.*"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        MatcherAssert.assertThat(multiMap.getAll(NottableString.not("keyO.*")), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
    }
}

