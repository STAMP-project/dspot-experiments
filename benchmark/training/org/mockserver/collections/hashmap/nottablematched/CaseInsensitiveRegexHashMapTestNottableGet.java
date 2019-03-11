package org.mockserver.collections.hashmap.nottablematched;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexHashMap;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexHashMapTestNottableGet {
    @Test
    public void shouldGetSingeValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" });
        // then
        MatcherAssert.assertThat(hashMap.get(NottableString.not("keyTwo")), CoreMatchers.is(NottableString.string("keyOneValue")));
    }

    @Test
    public void shouldGetFirstMultiValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" });
        // then
        MatcherAssert.assertThat(hashMap.get(NottableString.not("keyOne")), CoreMatchers.is(NottableString.string("keyTwoValue")));
    }
}

