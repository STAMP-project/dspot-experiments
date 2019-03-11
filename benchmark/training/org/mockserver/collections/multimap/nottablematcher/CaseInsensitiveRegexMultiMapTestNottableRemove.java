package org.mockserver.collections.multimap.nottablematcher;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexMultiMap;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexMultiMapTestNottableRemove {
    @Test
    public void shouldRemoveSingleValueEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.remove("keyT.*"), CoreMatchers.is(NottableString.string("keyOne_valueOne")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveSingleValueEntryWithNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.remove(NottableString.not("keyO.*")), CoreMatchers.is(NottableString.string("keyOne_valueOne")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveMultiValueEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.remove("keyO.*"), CoreMatchers.is(NottableString.string("keyTwo_valueOne")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveMultiValueEntryWithNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.remove(NottableString.not("keyT.*")), CoreMatchers.is(NottableString.string("keyTwo_valueOne")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveNotMatchingEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.remove("key.*"), CoreMatchers.nullValue());
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveNotMatchingEntryWithNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.remove(NottableString.not("notKey.*")), CoreMatchers.nullValue());
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveAllSingleValueEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.removeAll("keyT.*"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveAllSingleValueEntryWithNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.removeAll(NottableString.not("keyO.*")), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveAllMultiValueEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.removeAll("key.{3}"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveAllMultiValueEntryWithNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.removeAll(NottableString.not("key.{5}")), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveAllNoMatchingEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.removeAll("key.*"), Matchers.empty());
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldRemoveAllNoMatchingEntryWithNottedKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOne_valueOne") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree") });
        // when
        MatcherAssert.assertThat(multiMap.removeAll(NottableString.not("notKey.*")), Matchers.empty());
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }
}

