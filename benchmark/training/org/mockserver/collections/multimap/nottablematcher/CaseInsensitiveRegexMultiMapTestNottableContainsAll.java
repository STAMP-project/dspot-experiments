package org.mockserver.collections.multimap.nottablematcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexHashMap;
import org.mockserver.collections.CaseInsensitiveRegexMultiMap;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexMultiMapTestNottableContainsAll {
    @Test
    public void shouldContainAllExactMatchSingleKeyAndSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchSingleKeyAndSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("notKeyOne_valueOne") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchSingleKeyAndSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyOne"), NottableString.string("notKeyOne_valueOne") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOne_valueOne") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("notKeyOne_valueOne") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyOne"), NottableString.string("notKeyOne_valueOne") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOne_valueOne") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchSingleKeyAndMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchSingleKeyAndMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchSingleKeyAndMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchMultipleKeyAndMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchMultipleKeyAndMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchMultipleKeyAndMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetMultipleKeyAndMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetMultipleKeyAndMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetMultipleKeyAndMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwo_valueOne"), NottableString.string("notKeyTwo_valueTwo") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") })), CoreMatchers.is(true));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllEmptySubSetMultipleKeyAndMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{  })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetMultipleKeyForEmptyMap() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{  });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleKeySingleEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleValueSingleEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleKeyAndValueSingleEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOne_valueOne") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleKeyMultipleEntries() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleValueMultipleEntries() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleKeyAndValueMultipleEntries() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchMultipleKeysMultipleEntries() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchMultipleValuesMultipleEntries() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwo_valueOne"), NottableString.not("keyTwo_valueTwo") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") })), CoreMatchers.is(false));
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo.*") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchMultipleValuesMultipleEntriesContradiction() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllSubSetMultipleKeyForEmptyMap() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{  });
        // then
        MatcherAssert.assertThat(multiMap.containsAll(CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") })), CoreMatchers.is(false));
    }
}

