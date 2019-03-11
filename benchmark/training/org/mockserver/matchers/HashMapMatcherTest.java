package org.mockserver.matchers;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.Cookie;
import org.mockserver.model.Cookies;
import org.mockserver.model.NottableString;


public class HashMapMatcherTest {
    @Test
    public void shouldMatchSingleKeyAndValueForEmptyListMatcher() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies());
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("keyOne", "keyOneValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForEmptyListMatcher() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies());
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("keyOne", "keyOneValue"), new Cookie("keyTwo", "keyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchEmptyKeyAndValueForMatcherWithOnlySingleNottedKey() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("keyOne"), NottableString.string("keyOneValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies()), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchEmptyKeyAndValueForMatcherWithOnlyMultipleNottedKeys() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("keyOne"), NottableString.string("keyOneValue")), new Cookie(NottableString.not("keyTwo"), NottableString.string("keyTwoValue")), new Cookie(NottableString.not("keyThree"), NottableString.string("keyThreeValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies()), CoreMatchers.is(true));
    }

    @Test
    public void shouldNotMatchEmptyKeyAndValueForMatcherWithOnlyAtLeastOneNotNottedKey() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("keyOne"), NottableString.string("keyOneValue")), new Cookie(NottableString.not("keyTwo"), NottableString.string("keyTwoValue")), new Cookie(NottableString.string("keyThree"), NottableString.string("keyThreeValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies()), CoreMatchers.is(false));
    }

    @Test
    public void shouldMatchSingleKeyAndValueForSingleItemMatcher() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("keyOne", "keyOneValue")));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("keyOne", "keyOneValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForSingleItemMatcher() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("keyOne", "keyOneValue")));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("keyOne", "keyOneValue"), new Cookie("keyTwo", "keyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForMultiItemMatcherButSubSet() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("keyOne", "keyOneValue"), new Cookie("keyTwo", "keyTwoValue")));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("keyOne", "keyOneValue"), new Cookie("keyTwo", "keyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForMultiItemMatcherButExactMatch() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("keyOne", "keyOneValue"), new Cookie("keyTwo", "keyTwoValue"), new Cookie("keyThree", "keyThreeValue")));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("keyOne", "keyOneValue"), new Cookie("keyTwo", "keyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }
}

