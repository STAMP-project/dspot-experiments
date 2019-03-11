package org.mockserver.collections.hashmap.nottablematcher;


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
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.get("keyT.*"), CoreMatchers.is(NottableString.string("keyOneValue")));
        MatcherAssert.assertThat(hashMap.get(NottableString.not("keyO.*")), CoreMatchers.is(NottableString.string("keyOneValue")));
    }

    @Test
    public void shouldGetFirstMultiValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.not("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.get("key.{1,4}e"), CoreMatchers.is(NottableString.string("keyTwoValue")));
        MatcherAssert.assertThat(hashMap.get(NottableString.not("keyTwo")), CoreMatchers.is(NottableString.string("keyTwoValue")));
    }
}

