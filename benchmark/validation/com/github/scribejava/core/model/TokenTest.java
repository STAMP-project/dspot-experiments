package com.github.scribejava.core.model;


import org.junit.Assert;
import org.junit.Test;


public class TokenTest {
    @Test
    public void shouldTestEqualityBasedOnTokenAndSecret() {
        final Token expected = new OAuth1AccessToken("access", "secret");
        final Token actual = new OAuth1AccessToken("access", "secret");
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(actual, actual);
    }

    @Test
    public void shouldNotDependOnRawString() {
        final Token expected = new OAuth1AccessToken("access", "secret", "raw_string");
        final Token actual = new OAuth1AccessToken("access", "secret", "different_raw_string");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnSameHashCodeForEqualObjects() {
        final Token expected = new OAuth1AccessToken("access", "secret");
        final Token actual = new OAuth1AccessToken("access", "secret");
        Assert.assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    public void shouldNotBeEqualToNullOrOtherObjects() {
        final Token expected = new OAuth1AccessToken("access", "secret", "response");
        Assert.assertNotSame(expected, null);
        Assert.assertNotSame(expected, new Object());
    }

    @Test
    public void shouldReturnUrlParam() {
        final Token actual = new OAuth1AccessToken("acccess", "secret", "user_id=3107154759&screen_name=someuser&empty=&=");
        Assert.assertEquals("someuser", actual.getParameter("screen_name"));
        Assert.assertEquals("3107154759", actual.getParameter("user_id"));
        Assert.assertEquals(null, actual.getParameter("empty"));
        Assert.assertEquals(null, actual.getParameter(null));
    }
}

