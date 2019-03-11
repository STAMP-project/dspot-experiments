package org.mockserver.matchers;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.Cookie;
import org.mockserver.model.Cookies;
import org.mockserver.model.NottableString;


public class HashMapMatcherWithNottedStringsTest {
    @Test
    public void shouldMatchSingleKeyAndValueForSingleItemMatcherForNottedKey() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("keyOne"), NottableString.string("keyOneValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("notKeyOne", "keyOneValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchSingleKeyAndValueForSingleItemMatcherForNottedValue() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.string("keyOne"), NottableString.not("keyOneValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("keyOne", "notKeyOneValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchSingleKeyAndValueForSingleItemMatcherForNottedKeyAndValue() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("keyOne"), NottableString.not("keyOneValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("notKeyOne", "notKeyOneValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForSingleItemMatcherForNottedKey() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("keyOne"), NottableString.string("keyOneValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("notKeyOne", "keyOneValue"), new Cookie("keyTwo", "keyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForSingleItemMatcherForNottedValue() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.string("keyOne"), NottableString.not("keyOneValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("keyOne", "notKeyOneValue"), new Cookie("keyTwo", "keyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForSingleItemMatcherForNottedKeyAndValue() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("keyOne"), NottableString.not("keyOneValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("notKeyOne", "notKeyOneValue"), new Cookie("keyTwo", "keyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForMultipleItemMatcherForNottedKey() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("keyOne"), NottableString.string("keyOneValue")), new Cookie(NottableString.not("keyTwo"), NottableString.string("keyTwoValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("notKeyOne", "keyOneValue"), new Cookie("notKeyTwo", "keyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForMultipleItemMatcherForNottedValue() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.string("keyOne"), NottableString.not("keyOneValue")), new Cookie(NottableString.string("keyTwo"), NottableString.not("keyTwoValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("keyOne", "notKeyOneValue"), new Cookie("keyTwo", "notKeyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }

    @Test
    public void shouldMatchMultipleKeyAndValueForMultipleItemMatcherForNottedKeyAndValue() {
        // given
        HashMapMatcher hashMapMatcher = new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("keyOne"), NottableString.not("keyOneValue")), new Cookie(NottableString.not("keyTwo"), NottableString.not("keyTwoValue"))));
        // then
        MatcherAssert.assertThat(hashMapMatcher.matches(null, new Cookies().withEntries(new Cookie("notKeyOne", "notKeyOneValue"), new Cookie("notKeyTwo", "notKeyTwoValue"), new Cookie("keyThree", "keyThreeValue"))), CoreMatchers.is(true));
    }
}

