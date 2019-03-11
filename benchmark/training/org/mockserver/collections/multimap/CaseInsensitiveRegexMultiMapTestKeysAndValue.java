package org.mockserver.collections.multimap;


import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexMultiMap;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexMultiMapTestKeysAndValue {
    @Test
    public void shouldReturnKeys() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.keySet(), containsInAnyOrder(NottableString.string("keyOne"), NottableString.string("keyTwo")));
    }

    @Test
    public void shouldReturnValues() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.values(), containsInAnyOrder(NottableString.string("keyOne_valueOne"), NottableString.string("keyTwo_valueOne"), NottableString.string("keyTwo_valueTwo")));
    }

    @Test
    public void shouldReturnEntrySet() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.entrySet(), containsInAnyOrder(CaseInsensitiveRegexMultiMap.entry("keyOne", "keyOne_valueOne"), CaseInsensitiveRegexMultiMap.entry("keyTwo", "keyTwo_valueOne"), CaseInsensitiveRegexMultiMap.entry("keyTwo", "keyTwo_valueTwo")));
    }

    @Test
    public void shouldReturnEntryList() {
        // given
        CaseInsensitiveRegexMultiMap multiMap = CaseInsensitiveRegexMultiMap.multiMap(new String[]{ "keyOne", "keyOne_valueOne" }, new String[]{ "keyTwo", "keyTwo_valueOne", "keyTwo_valueOne", "keyTwo_valueTwo" });
        // then
        MatcherAssert.assertThat(multiMap.entryList(), containsInAnyOrder(CaseInsensitiveRegexMultiMap.entry("keyOne", "keyOne_valueOne"), CaseInsensitiveRegexMultiMap.entry("keyTwo", "keyTwo_valueOne"), CaseInsensitiveRegexMultiMap.entry("keyTwo", "keyTwo_valueOne"), CaseInsensitiveRegexMultiMap.entry("keyTwo", "keyTwo_valueTwo")));
    }
}

