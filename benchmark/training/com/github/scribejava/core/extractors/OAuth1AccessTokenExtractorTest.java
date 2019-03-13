package com.github.scribejava.core.extractors;


import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth1Token;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class OAuth1AccessTokenExtractorTest {
    private OAuth1AccessTokenExtractor extractor;

    @Test
    public void shouldExtractTokenFromOAuthStandardResponse() throws IOException {
        final String response = "oauth_token=hh5s93j4hdidpola&oauth_token_secret=hdhd0244k9j7ao03";
        final OAuth1Token extracted = extractor.extract(OAuth1AccessTokenExtractorTest.ok(response));
        Assert.assertEquals("hh5s93j4hdidpola", extracted.getToken());
        Assert.assertEquals("hdhd0244k9j7ao03", extracted.getTokenSecret());
    }

    @Test
    public void shouldExtractTokenFromInvertedOAuthStandardResponse() throws IOException {
        final String response = "oauth_token_secret=hh5s93j4hdidpola&oauth_token=hdhd0244k9j7ao03";
        final OAuth1Token extracted = extractor.extract(OAuth1AccessTokenExtractorTest.ok(response));
        Assert.assertEquals("hh5s93j4hdidpola", extracted.getTokenSecret());
        Assert.assertEquals("hdhd0244k9j7ao03", extracted.getToken());
    }

    @Test
    public void shouldExtractTokenFromResponseWithCallbackConfirmed() throws IOException {
        final String response = "oauth_token=hh5s93j4hdidpola&oauth_token_secret=hdhd0244k9j7ao03" + "&callback_confirmed=true";
        final OAuth1Token extracted = extractor.extract(OAuth1AccessTokenExtractorTest.ok(response));
        Assert.assertEquals("hh5s93j4hdidpola", extracted.getToken());
        Assert.assertEquals("hdhd0244k9j7ao03", extracted.getTokenSecret());
    }

    @Test
    public void shouldExtractTokenWithEmptySecret() throws IOException {
        final String response = "oauth_token=hh5s93j4hdidpola&oauth_token_secret=";
        final OAuth1Token extracted = extractor.extract(OAuth1AccessTokenExtractorTest.ok(response));
        Assert.assertEquals("hh5s93j4hdidpola", extracted.getToken());
        Assert.assertEquals("", extracted.getTokenSecret());
    }

    @Test(expected = OAuthException.class)
    public void shouldThrowExceptionIfTokenIsAbsent() throws IOException {
        final String response = "oauth_secret=hh5s93j4hdidpola&callback_confirmed=true";
        extractor.extract(OAuth1AccessTokenExtractorTest.ok(response));
    }

    @Test(expected = OAuthException.class)
    public void shouldThrowExceptionIfSecretIsAbsent() throws IOException {
        final String response = "oauth_token=hh5s93j4hdidpola&callback_confirmed=true";
        extractor.extract(OAuth1AccessTokenExtractorTest.ok(response));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfResponseIsNull() throws IOException {
        extractor.extract(OAuth1AccessTokenExtractorTest.ok(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfResponseIsEmptyString() throws IOException {
        final String response = "";
        extractor.extract(OAuth1AccessTokenExtractorTest.ok(response));
    }
}

