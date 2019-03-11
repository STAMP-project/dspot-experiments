package org.mockserver.collections.hashmap;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.collections.CaseInsensitiveRegexHashMap;


/**
 *
 *
 * @author jamesdbloom
 */
public class CaseInsensitiveRegexHashMapTestContainsAll {
    @Test
    public void shouldContainAllForEmptyMapMatchedAgainstMapWithSingleEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{  })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllForEmptyMapMatchedAgainstMapWithMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{  })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchSingleKeyAndSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndSingleValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchMultipleKeyAndMultipleValues() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetMultipleKeyAndMultipleValues() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" })), CoreMatchers.is(true));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleKeySingleEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "notKeyOne", "keyOneValue" })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleValueSingleEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "notKeyOneValue" })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleKeyMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "notKeyOne", "keyOneValue" })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleValueMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "notKeyOneValue" })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchMultipleKeysMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "notKeyOne", "keyOneValue" }, new String[]{ "notKeyTwo", "keyTwoValue" })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchMultipleValuesMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "keyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" }, new String[]{ "keyThree", "keyThreeValue" });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyOne", "notKeyOneValue" }, new String[]{ "keyTwo", "keyTwoValue" })), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new String[]{ "keyTwo", "notKeyTwoValue" })), CoreMatchers.is(false));
    }
}

