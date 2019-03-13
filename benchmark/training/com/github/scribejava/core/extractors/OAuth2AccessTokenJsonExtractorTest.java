package com.github.scribejava.core.extractors;


import OAuth2AccessTokenErrorResponse.ErrorCode.invalid_grant;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class OAuth2AccessTokenJsonExtractorTest {
    private final OAuth2AccessTokenJsonExtractor extractor = OAuth2AccessTokenJsonExtractor.instance();

    @Test
    public void shouldParseResponse() throws IOException {
        final OAuth2AccessToken token = extractor.extract(OAuth2AccessTokenJsonExtractorTest.ok("{ \"access_token\":\"I0122HHJKLEM21F3WLPYHDKGKZULAUO4SGMV3ABKFTDT3T3X\"}"));
        Assert.assertEquals("I0122HHJKLEM21F3WLPYHDKGKZULAUO4SGMV3ABKFTDT3T3X", token.getAccessToken());
    }

    @Test
    public void shouldParseScopeFromResponse() throws IOException {
        OAuth2AccessToken token = extractor.extract(OAuth2AccessTokenJsonExtractorTest.ok(("{ \"access_token\":\"I0122HHJKLEM21F3WLPYHDKGKZULAUO4SGMV3ABKFTDT3T4X\", " + "\"scope\":\"s1\"}")));
        Assert.assertEquals("I0122HHJKLEM21F3WLPYHDKGKZULAUO4SGMV3ABKFTDT3T4X", token.getAccessToken());
        Assert.assertEquals("s1", token.getScope());
        token = extractor.extract(OAuth2AccessTokenJsonExtractorTest.ok(("{ \"access_token\":\"I0122HHJKLEM21F3WLPYHDKGKZULAUO4SGMV3ABKFTDT3T5X\", " + "\"scope\":\"s1 s2\"}")));
        Assert.assertEquals("I0122HHJKLEM21F3WLPYHDKGKZULAUO4SGMV3ABKFTDT3T5X", token.getAccessToken());
        Assert.assertEquals("s1 s2", token.getScope());
        token = extractor.extract(OAuth2AccessTokenJsonExtractorTest.ok(("{ \"access_token\":\"I0122HHJKLEM21F3WLPYHDKGKZULAUO4SGMV3ABKFTDT3T6X\", " + ("\"scope\":\"s3 s4\", " + "\"refresh_token\":\"refresh_token1\"}"))));
        Assert.assertEquals("I0122HHJKLEM21F3WLPYHDKGKZULAUO4SGMV3ABKFTDT3T6X", token.getAccessToken());
        Assert.assertEquals("s3 s4", token.getScope());
        Assert.assertEquals("refresh_token1", token.getRefreshToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfForNullParameters() throws IOException {
        extractor.extract(OAuth2AccessTokenJsonExtractorTest.ok(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfForEmptyStrings() throws IOException {
        extractor.extract(OAuth2AccessTokenJsonExtractorTest.ok(""));
    }

    @Test
    public void shouldThrowExceptionIfResponseIsError() throws IOException {
        final String body = "{" + (("\"error_description\":\"unknown, invalid, or expired refresh token\"," + "\"error\":\"invalid_grant\"") + "}");
        try {
            extractor.extract(OAuth2AccessTokenJsonExtractorTest.error(body));
            Assert.fail();
        } catch (OAuth2AccessTokenErrorResponse oaer) {
            Assert.assertEquals(invalid_grant, oaer.getErrorCode());
            Assert.assertEquals("unknown, invalid, or expired refresh token", oaer.getErrorDescription());
        }
    }
}

