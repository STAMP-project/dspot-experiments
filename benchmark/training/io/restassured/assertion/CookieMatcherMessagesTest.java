package io.restassured.assertion;


import io.restassured.http.Cookies;
import java.util.Arrays;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;


/**
 *
 *
 * @author Maciej Gawinecki
 */
@SuppressWarnings("unchecked")
public class CookieMatcherMessagesTest {
    private String[] cookies = new String[]{ "DEVICE_ID=123; Domain=.test.com; Expires=Thu, 12-Oct-2023 09:34:31 GMT; Path=/; Secure; HttpOnly;" };

    @Test
    public void shouldPrintValidErrorMessageForStandardMatchers() {
        CookieMatcher cookieMatcher = new CookieMatcher();
        cookieMatcher.setCookieName("DEVICE_ID");
        cookieMatcher.setMatcher(Matchers.containsString("X"));
        Map<String, Object> result = ((Map<String, Object>) (cookieMatcher.validateCookies(Arrays.asList(cookies), new Cookies())));
        MatcherAssert.assertThat(((Boolean) (result.get("success"))), CoreMatchers.equalTo(false));
        MatcherAssert.assertThat(result.get("errorMessage").toString(), CoreMatchers.equalTo("Expected cookie \"DEVICE_ID\" was not a string containing \"X\", was \"123\".\n"));
    }

    @Test
    public void shouldPrintValidErrorMessageForCustomMatcher() {
        CookieMatcher cookieMatcher = new CookieMatcher();
        cookieMatcher.setCookieName("DEVICE_ID");
        cookieMatcher.setMatcher(new CookieMatcherMessagesTest.ContainsXMatcher());
        Map<String, Object> result = ((Map<String, Object>) (cookieMatcher.validateCookies(Arrays.asList(cookies), new Cookies())));
        MatcherAssert.assertThat(((Boolean) (result.get("success"))), CoreMatchers.equalTo(false));
        MatcherAssert.assertThat(result.get("errorMessage").toString(), CoreMatchers.equalTo("Expected cookie \"DEVICE_ID\" was not containing \'X\', \"123\" not containing \'X\'.\n"));
    }

    private static class ContainsXMatcher extends TypeSafeDiagnosingMatcher<String> {
        @Override
        protected boolean matchesSafely(String actual, Description mismatchDescription) {
            // Not this method will be called twice due to https://github.com/hamcrest/JavaHamcrest/issues/144
            if (actual.contains("X")) {
                return true;
            }
            mismatchDescription.appendValue(actual).appendText(" not containing 'X'");
            return false;
        }

        public void describeTo(Description description) {
            description.appendText("containing 'X'");
        }
    }
}

