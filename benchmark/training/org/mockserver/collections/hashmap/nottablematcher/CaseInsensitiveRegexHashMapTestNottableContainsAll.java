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
public class CaseInsensitiveRegexHashMapTestNottableContainsAll {
    @Test
    public void shouldContainAllExactMatchSingleKeyAndSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("notKeyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchSingleKeyAndSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("notKeyOneValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchSingleKeyAndSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("notKeyOne"), NottableString.string("notKeyOneValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndSingleValueForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("notKeyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndSingleValueForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("notKeyOneValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetSingleKeyAndSingleValueForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("notKeyOne"), NottableString.string("notKeyOneValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchMultipleKeyAndMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwoValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchMultipleKeyAndMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwoValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwoValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllExactMatchMultipleKeyAndMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwoValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwoValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwoValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetMultipleKeyAndMultipleValuesForNottedKey() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("keyTwoValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetMultipleKeyAndMultipleValuesForNottedValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("notKeyTwoValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwoValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllSubSetMultipleKeyAndMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("notKeyTwo"), NottableString.string("notKeyTwoValue") })), CoreMatchers.is(true));
        // and then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwoValue") })), CoreMatchers.is(true));
    }

    @Test
    public void shouldContainAllEmptySubSetMultipleKeyAndMultipleValuesForNottedKeyAndValue() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.not("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{  })), CoreMatchers.is(true));
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
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleValueSingleEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleKeyAndValueSingleEntry() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleKeyMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleValueMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchSingleKeyAndValueMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchMultipleKeysMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyTwo"), NottableString.string("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchMultipleValuesMultipleEntries() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.not("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.not("keyTwoValue") }, new NottableString[]{ NottableString.string("keyThree"), NottableString.string("keyThreeValue"), NottableString.string("keyThree_valueTwo") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") })), CoreMatchers.is(false));
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwo.*") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllNotMatchMultipleValuesMultipleEntriesContradiction() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.string("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.not("keyOne"), NottableString.not("keyOneValue") })), CoreMatchers.is(false));
    }

    @Test
    public void shouldNotContainAllSubSetMultipleKeyForEmptyMap() {
        // given
        CaseInsensitiveRegexHashMap hashMap = CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{  });
        // then
        MatcherAssert.assertThat(hashMap.containsAll(CaseInsensitiveRegexHashMap.hashMap(new NottableString[]{ NottableString.not("keyOne"), NottableString.string("keyOneValue") }, new NottableString[]{ NottableString.string("keyTwo"), NottableString.string("keyTwoValue") })), CoreMatchers.is(false));
    }
}

