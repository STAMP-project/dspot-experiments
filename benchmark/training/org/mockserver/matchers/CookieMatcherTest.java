package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.Cookie;
import org.mockserver.model.Cookies;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class CookieMatcherTest {
    @Test
    public void shouldMatchSingleCookieMatcherAndSingleMatchingCookie() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"))));
    }

    @Test
    public void shouldNotMatchSingleCookieMatcherAndSingleNoneMatchingCookie() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"))).matches(null, new Cookies().withEntries(new Cookie("notCookieOneName", "cookieOneValue"))));
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "notCookieOneValue"))));
    }

    @Test
    public void shouldMatchMultipleCookieMatcherAndMultipleMatchingCookies() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookie.*", "cookie.*"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchMultipleCookieMatcherAndMultipleNoneMatchingCookiesWithOneMismatch() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("notCookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "notCookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchMultipleCookieMatcherAndMultipleNoneMatchingCookiesWithMultipleMismatches() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("notCookieOneName", "cookieOneValue"), new Cookie("notCookieTwoName", "cookieTwoValue"))));
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "notCookieOneValue"), new Cookie("cookieTwoName", "notCookieTwoValue"))));
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookie.*", "cookie.*"))).matches(null, new Cookies().withEntries(new Cookie("notCookieOneName", "cookieOneValue"), new Cookie("notCookieTwoName", "cookieTwoValue"))));
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookie.*", "cookie.*"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "notCookieOneValue"), new Cookie("cookieTwoName", "notCookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchMultipleCookieMatcherAndMultipleNotEnoughMatchingCookies() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieTwoName", "cookieTwoValue"))));
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"))));
    }

    @Test
    public void shouldMatchMatchingCookie() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookie.*", "cookie.*"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingCookieWhenNotAppliedToMatcher() {
        // given
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        // then - not matcher
        Assert.assertFalse(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        // and - not cookie
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie(NottableString.not("cookie.*Name"), NottableString.not("cookie.*Value")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        // and - not matcher and not cookie
        Assert.assertTrue(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie(NottableString.not("cookie.*Name"), NottableString.not("cookie.*Value"))))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingCookieWithNotCookieAndNormalCookie() {
        // not matching cookie
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie(NottableString.not("cookie.*Name"), NottableString.not("cookie.*Value")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        // not extra cookie
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"), new Cookie(NottableString.not("cookie.*Name"), NottableString.not("cookie.*Value")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        // not extra cookie
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"), new Cookie(NottableString.not("cookieThreeName"), NottableString.not("cookieThreeValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        // not only cookie
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("cookieThreeName"), NottableString.not("cookieThreeValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        // not all cookies (but matching)
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("cookie.*"), NottableString.not(".*")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        // not all cookies (but not matching name)
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("cookie.*"), NottableString.not("cookie.*")))).matches(null, new Cookies().withEntries(new Cookie("notCookieOneName", "cookieOneValue"), new Cookie("notCookieTwoName", "cookieTwoValue"))));
        // not all cookies (but not matching value)
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.string("cookie.*"), NottableString.not("cookie.*")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "notCookieOneValue"), new Cookie("cookieTwoName", "notCookieTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingCookieWithOnlyCookie() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("cookieThreeName"), NottableString.not("cookieThreeValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieThree", "cookieThreeValueOne"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("cookieOneName"), NottableString.not("cookieOneValue")))).matches(null, new Cookies().withEntries(new Cookie("notCookieOneName", "notCookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("cookieOneName"), NottableString.not("cookieOneValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldMatchMatchingCookieWithOnlyCookieForEmptyList() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies()).matches(null, new Cookies().withEntries(new Cookie("cookieThree", "cookieThreeValueOne"))));
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieThree", "cookieThreeValueOne"))).matches(null, new Cookies()));
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("cookieThree"), NottableString.not("cookieThreeValueOne")))).matches(null, new Cookies()));
    }

    @Test
    public void shouldNotMatchMatchingCookieWithNotCookieAndNormalCookie() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie(NottableString.not("cookieTwoName"), NottableString.not("cookieTwoValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingCookieWithOnlyNotCookie() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("cookie.*"), NottableString.not("cookie.*")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchMatchingCookieWithOnlyNotCookieForBodyWithSingleCookie() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie(NottableString.not("cookieTwoName"), NottableString.not("cookieTwoValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldMatchNullExpectation() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), null).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchNullExpectationWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), null)).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldMatchEmptyExpectation() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies()).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchEmptyExpectationWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies())).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectCookieName() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("INCORRECTcookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectCookieNameWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("INCORRECTcookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectCookieValue() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "INCORRECTcookieTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectCookieValueWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "INCORRECTcookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchIncorrectCookieNameAndValue() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("INCORRECTcookieTwoName", "INCORRECTcookieTwoValue"))));
    }

    @Test
    public void shouldMatchIncorrectCookieNameAndValueWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("INCORRECTcookieTwoName", "INCORRECTcookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchNullCookieValue() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", null))));
    }

    @Test
    public void shouldMatchNullCookieValueWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", null))));
    }

    @Test
    public void shouldMatchNullCookieValueInExpectation() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", ""))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))));
    }

    @Test
    public void shouldNotMatchMissingCookie() {
        Assert.assertFalse(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue"))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"))));
    }

    @Test
    public void shouldMatchMissingCookieWhenNotApplied() {
        Assert.assertTrue(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"), new Cookie("cookieTwoName", "cookieTwoValue")))).matches(null, new Cookies().withEntries(new Cookie("cookieOneName", "cookieOneValue"))));
    }

    @Test
    public void shouldMatchNullTest() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies()).matches(null, new Cookies()));
    }

    @Test
    public void shouldNotMatchNullTestWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies())).matches(null, new Cookies()));
    }

    @Test
    public void shouldMatchEmptyTest() {
        Assert.assertTrue(new HashMapMatcher(new MockServerLogger(), new Cookies()).matches(null, new Cookies()));
    }

    @Test
    public void shouldNotMatchEmptyTestWhenNotApplied() {
        Assert.assertFalse(NotMatcher.not(new HashMapMatcher(new MockServerLogger(), new Cookies())).matches(null, new Cookies()));
    }
}

