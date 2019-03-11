package org.mockserver.collections.hashmap;


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
public class CaseInsensitiveRegexHashMapTestPut {
    @Test
    public void shouldPutSingleValueForNewKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{  });
        // when
        hashMap.put("keyOne", "keyOneValue");
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyOneValue")));
    }

    @Test
    public void shouldPutSingleValueForExistingKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // when
        hashMap.put("keyTwo", "keyTwoValue");
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyOneValue")));
        MatcherAssert.assertThat(hashMap.get("keyTwo"), CoreMatchers.is(NottableString.string("keyTwoValue")));
    }

    @Test
    public void shouldPutSingleValueForExistingKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // when
        hashMap.put("keyOne", "keyOneValue");
        // then
        MatcherAssert.assertThat(hashMap.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(hashMap.get("keyOne"), CoreMatchers.is(NottableString.string("keyOneValue")));
    }
}

