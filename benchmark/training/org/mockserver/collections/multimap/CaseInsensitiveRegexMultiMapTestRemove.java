package org.mockserver.collections.multimap;


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
public class CaseInsensitiveRegexMultiMapTestRemove {
    @Test
    public void shouldRemoveSingleValueEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // when
        MatcherAssert.assertThat(multiMap.remove("keyOne"), CoreMatchers.is(NottableString.string("keyOne_valueOne")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), Matchers.empty());
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
    }

    @Test
    public void shouldRemoveMultiValueEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // when
        MatcherAssert.assertThat(multiMap.remove("keyTwo"), CoreMatchers.is(NottableString.string("keyTwo_valueOne")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueTwo")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
    }

    @Test
    public void shouldRemoveNoMatchingEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // when
        MatcherAssert.assertThat(multiMap.remove("keyFour"), CoreMatchers.is(CoreMatchers.nullValue()));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
    }

    @Test
    public void shouldRemoveAllSingleValueEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // when
        MatcherAssert.assertThat(multiMap.removeAll("keyOne"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), Matchers.empty());
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
    }

    @Test
    public void shouldRemoveAllMultiValueEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // when
        MatcherAssert.assertThat(multiMap.removeAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), Matchers.empty());
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
    }

    @Test
    public void shouldRemoveAllNoMatchingEntry() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" }, new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo", "keyThree_valueThree" });
        // when
        MatcherAssert.assertThat(multiMap.removeAll("keyFour"), Matchers.empty());
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
    }
}

