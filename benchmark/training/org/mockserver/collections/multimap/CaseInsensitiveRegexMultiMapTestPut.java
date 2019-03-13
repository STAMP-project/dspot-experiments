package org.mockserver.collections.multimap;


import java.util.Arrays;
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
public class CaseInsensitiveRegexMultiMapTestPut {
    @Test
    public void shouldPutSingleValueForNewKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{  });
        // when
        multiMap.put("keyOne", "keyOne_valueOne");
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
    }

    @Test
    public void shouldPutSingleValueForExistingKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne" });
        // when
        multiMap.put("keyTwo", "keyTwo_valueTwo");
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldPutSingleValueForExistingKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // when
        multiMap.put("keyTwo", "keyTwo_valueTwo");
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldPutSingleMultiValueForNewKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{  });
        // when
        multiMap.put("keyTwo", Arrays.asList("keyTwo_valueOne", "keyTwo_valueTwo"));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldPutSingleMultiValueForExistingKey() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyThree", "keyThree_valueOne" });
        // when
        multiMap.put("keyThree", Arrays.asList("keyThree_valueTwo", "keyThree_valueThree"));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
    }

    @Test
    public void shouldPutSingleMultiValueForExistingKeyAndValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyThree", "keyThree_valueOne", "keyThree_valueTwo" });
        // when
        multiMap.put("keyThree", Arrays.asList("keyThree_valueTwo", "keyThree_valueThree"));
        // then
        MatcherAssert.assertThat(multiMap.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(multiMap.getAll("keyThree"), containsInAnyOrder(NottableString.string("keyThree_valueOne"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueTwo"), NottableString.string("keyThree_valueThree")));
    }
}

