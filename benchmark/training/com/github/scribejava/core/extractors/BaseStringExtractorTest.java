package com.github.scribejava.core.extractors;


import com.github.scribejava.core.exceptions.OAuthParametersMissingException;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import org.junit.Assert;
import org.junit.Test;


public class BaseStringExtractorTest {
    private BaseStringExtractorImpl extractor;

    private OAuthRequest request;

    private OAuthRequest requestPort80;

    private OAuthRequest requestPort80v2;

    private OAuthRequest requestPort8080;

    private OAuthRequest requestPort443;

    private OAuthRequest requestPort443v2;

    @Test
    public void shouldExtractBaseStringFromOAuthRequest() {
        final String expected = "GET&http%3A%2F%2Fexample.com&oauth_callback%3Dhttp%253A%252F%252Fexample%252Fcallback" + ("%26oauth_consumer_key%3DAS%2523%2524%255E%252A%2540%2526%26oauth_signature%3DOAuth-Signature" + "%26oauth_timestamp%3D123456");
        final String baseString = extractor.extract(request);
        Assert.assertEquals(expected, baseString);
    }

    @Test
    public void shouldExcludePort80() {
        final String expected = "GET&http%3A%2F%2Fexample.com&oauth_callback%3Dhttp%253A%252F%252Fexample%252Fcallback" + ("%26oauth_consumer_key%3DAS%2523%2524%255E%252A%2540%2526%26oauth_signature%3DOAuth-Signature" + "%26oauth_timestamp%3D123456");
        final String baseString = extractor.extract(requestPort80);
        Assert.assertEquals(expected, baseString);
    }

    @Test
    public void shouldExcludePort80v2() {
        final String expected = "GET&http%3A%2F%2Fexample.com%2Ftest&oauth_callback%3Dhttp%253A%252F%252Fexample" + ("%252Fcallback%26oauth_consumer_key%3DAS%2523%2524%255E%252A%2540%2526%26oauth_signature" + "%3DOAuth-Signature%26oauth_timestamp%3D123456");
        final String baseString = extractor.extract(requestPort80v2);
        Assert.assertEquals(expected, baseString);
    }

    @Test
    public void shouldIncludePort8080() {
        final String expected = "GET&http%3A%2F%2Fexample.com%3A8080&oauth_callback%3Dhttp%253A%252F%252Fexample" + ("%252Fcallback%26oauth_consumer_key%3DAS%2523%2524%255E%252A%2540%2526%26oauth_signature" + "%3DOAuth-Signature%26oauth_timestamp%3D123456");
        final String baseString = extractor.extract(requestPort8080);
        Assert.assertEquals(expected, baseString);
    }

    @Test
    public void shouldExcludePort443() {
        final String expected = "GET&https%3A%2F%2Fexample.com&oauth_callback%3Dhttp%253A%252F%252Fexample%252Fcallback" + ("%26oauth_consumer_key%3DAS%2523%2524%255E%252A%2540%2526%26oauth_signature%3DOAuth-Signature" + "%26oauth_timestamp%3D123456");
        final String baseString = extractor.extract(requestPort443);
        Assert.assertEquals(expected, baseString);
    }

    @Test
    public void shouldExcludePort443v2() {
        final String expected = "GET&https%3A%2F%2Fexample.com%2Ftest&oauth_callback%3Dhttp%253A%252F%252Fexample" + ("%252Fcallback%26oauth_consumer_key%3DAS%2523%2524%255E%252A%2540%2526%26oauth_signature" + "%3DOAuth-Signature%26oauth_timestamp%3D123456");
        final String baseString = extractor.extract(requestPort443v2);
        Assert.assertEquals(expected, baseString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfRquestIsNull() {
        extractor.extract(null);
    }

    @Test(expected = OAuthParametersMissingException.class)
    public void shouldThrowExceptionIfRquestHasNoOAuthParameters() {
        final OAuthRequest request = new OAuthRequest(Verb.GET, "http://example.com");
        extractor.extract(request);
    }

    @Test
    public void shouldProperlyEncodeSpaces() {
        final String expected = "GET&http%3A%2F%2Fexample.com&body%3Dthis%2520param%2520has%2520whitespace" + ("%26oauth_callback%3Dhttp%253A%252F%252Fexample%252Fcallback%26oauth_consumer_key" + "%3DAS%2523%2524%255E%252A%2540%2526%26oauth_signature%3DOAuth-Signature%26oauth_timestamp%3D123456");
        request.addBodyParameter("body", "this param has whitespace");
        Assert.assertEquals(expected, extractor.extract(request));
    }
}

