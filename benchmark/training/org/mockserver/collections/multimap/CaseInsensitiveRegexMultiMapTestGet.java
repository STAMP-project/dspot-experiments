package org.mockserver.collections.multimap;


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
public class CaseInsensitiveRegexMultiMapTestGet {
    @Test
    public void shouldGetSingeValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyOne_valueOne")));
    }

    @Test
    public void shouldGetFirstMultiValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyTwo_valueOne")));
    }

    @Test
    public void shouldGetAllSingeValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.getAll("keyOne"), containsInAnyOrder(NottableString.string("keyOne_valueOne")));
    }

    @Test
    public void shouldGetAllMultiValue() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.getAll("keyTwo"), containsInAnyOrder(NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }
}

