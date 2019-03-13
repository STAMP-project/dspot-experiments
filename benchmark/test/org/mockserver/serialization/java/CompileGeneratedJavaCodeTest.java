package org.mockserver.serialization.java;


import HttpForward.Scheme.HTTPS;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;


/**
 *
 *
 * @author jamesdbloom
 */
@Ignore("ignored due to issue with classpath during maven build")
public class CompileGeneratedJavaCodeTest {
    private static final String commonImports = ((((((((((("" + "import org.mockserver.client.MockServerClient;") + (NEW_LINE)) + "import org.mockserver.matchers.Times;") + (NEW_LINE)) + "import org.mockserver.matchers.TimeToLive;") + (NEW_LINE)) + "import org.mockserver.mock.Expectation;") + (NEW_LINE)) + "import org.mockserver.model.*;") + (NEW_LINE)) + "import static org.mockserver.model.HttpRequest.request;") + (NEW_LINE);

    @Test
    public void shouldCompileExpectationWithHttpResponse() throws URISyntaxException {
        String expectationAsJavaCode = new ExpectationToJavaSerializer().serialize(1, new org.mockserver.mock.Expectation(request().withMethod("GET").withPath("somePath").withQueryStringParameters(new Parameter("requestQueryStringParameterNameOne", "requestQueryStringParameterValueOneOne", "requestQueryStringParameterValueOneTwo"), new Parameter("requestQueryStringParameterNameTwo", "requestQueryStringParameterValueTwo")).withHeaders(new Header("requestHeaderNameOne", "requestHeaderValueOneOne", "requestHeaderValueOneTwo"), new Header("requestHeaderNameTwo", "requestHeaderValueTwo")).withCookies(new Cookie("requestCookieNameOne", "requestCookieValueOne"), new Cookie("requestCookieNameTwo", "requestCookieValueTwo")).withSecure(false).withKeepAlive(true).withBody(new StringBody("somebody")), Times.once(), TimeToLive.unlimited()).thenRespond(response().withStatusCode(304).withHeaders(new Header("responseHeaderNameOne", "responseHeaderValueOneOne", "responseHeaderValueOneTwo"), new Header("responseHeaderNameTwo", "responseHeaderValueTwo")).withCookies(new Cookie("responseCookieNameOne", "responseCookieValueOne"), new Cookie("responseCookieNameTwo", "responseCookieValueTwo")).withBody("responseBody")));
        Assert.assertTrue(compileJavaCode(((((((((((((((CompileGeneratedJavaCodeTest.commonImports) + "import static org.mockserver.model.HttpResponse.response;") + (NEW_LINE)) + (NEW_LINE)) + "class TestClass {") + (NEW_LINE)) + "   static {") + "      ") + expectationAsJavaCode) + "") + (NEW_LINE)) + "   }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldCompileExpectationWithHttpResponseTemplate() throws URISyntaxException {
        String expectationAsJavaCode = new ExpectationToJavaSerializer().serialize(1, new org.mockserver.mock.Expectation(request().withMethod("GET").withPath("somePath").withQueryStringParameters(new Parameter("requestQueryStringParameterNameOne", "requestQueryStringParameterValueOneOne", "requestQueryStringParameterValueOneTwo"), new Parameter("requestQueryStringParameterNameTwo", "requestQueryStringParameterValueTwo")).withHeaders(new Header("requestHeaderNameOne", "requestHeaderValueOneOne", "requestHeaderValueOneTwo"), new Header("requestHeaderNameTwo", "requestHeaderValueTwo")).withCookies(new Cookie("requestCookieNameOne", "requestCookieValueOne"), new Cookie("requestCookieNameTwo", "requestCookieValueTwo")).withSecure(false).withKeepAlive(true).withBody(new StringBody("somebody")), Times.once(), TimeToLive.unlimited()).thenRespond(template(HttpTemplate.TemplateType.JAVASCRIPT).withTemplate("some_random_template")));
        Assert.assertTrue(compileJavaCode(((((((((((((((CompileGeneratedJavaCodeTest.commonImports) + "import static org.mockserver.model.HttpTemplate.template;") + (NEW_LINE)) + (NEW_LINE)) + "class TestClass {") + (NEW_LINE)) + "   static {") + "      ") + expectationAsJavaCode) + "") + (NEW_LINE)) + "   }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldCompileExpectationWithHttpForward() throws URISyntaxException {
        String expectationAsJavaCode = new ExpectationToJavaSerializer().serialize(1, new org.mockserver.mock.Expectation(request().withMethod("GET").withPath("somePath").withQueryStringParameters(new Parameter("requestQueryStringParameterNameOne", "requestQueryStringParameterValueOneOne", "requestQueryStringParameterValueOneTwo"), new Parameter("requestQueryStringParameterNameTwo", "requestQueryStringParameterValueTwo")).withHeaders(new Header("requestHeaderNameOne", "requestHeaderValueOneOne", "requestHeaderValueOneTwo"), new Header("requestHeaderNameTwo", "requestHeaderValueTwo")).withCookies(new Cookie("requestCookieNameOne", "requestCookieValueOne"), new Cookie("requestCookieNameTwo", "requestCookieValueTwo")).withSecure(false).withKeepAlive(true).withBody(new StringBody("somebody")), Times.once(), TimeToLive.unlimited()).thenForward(forward().withHost("localhost").withPort(1080).withScheme(HTTPS)));
        Assert.assertTrue(compileJavaCode(((((((((((((((CompileGeneratedJavaCodeTest.commonImports) + "import static org.mockserver.model.HttpForward.forward;") + (NEW_LINE)) + (NEW_LINE)) + "class TestClass {") + (NEW_LINE)) + "   static {") + "      ") + expectationAsJavaCode) + "") + (NEW_LINE)) + "   }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldCompileExpectationWithClassCallback() throws URISyntaxException {
        String expectationAsJavaCode = new ExpectationToJavaSerializer().serialize(1, new org.mockserver.mock.Expectation(request().withMethod("GET").withPath("somePath").withQueryStringParameters(new Parameter("requestQueryStringParameterNameOne", "requestQueryStringParameterValueOneOne", "requestQueryStringParameterValueOneTwo"), new Parameter("requestQueryStringParameterNameTwo", "requestQueryStringParameterValueTwo")).withHeaders(new Header("requestHeaderNameOne", "requestHeaderValueOneOne", "requestHeaderValueOneTwo"), new Header("requestHeaderNameTwo", "requestHeaderValueTwo")).withCookies(new Cookie("requestCookieNameOne", "requestCookieValueOne"), new Cookie("requestCookieNameTwo", "requestCookieValueTwo")).withSecure(false).withKeepAlive(true).withBody(new StringBody("somebody")), Times.once(), TimeToLive.unlimited()).thenRespond(callback().withCallbackClass("some_random_class")));
        Assert.assertTrue(compileJavaCode(((((((((((((((CompileGeneratedJavaCodeTest.commonImports) + "import static org.mockserver.model.HttpClassCallback.callback;") + (NEW_LINE)) + (NEW_LINE)) + "class TestClass {") + (NEW_LINE)) + "   static {") + "      ") + expectationAsJavaCode) + "") + (NEW_LINE)) + "   }") + (NEW_LINE)) + "}")));
    }

    @Test
    public void shouldCompileExpectationWithObjectCallback() throws URISyntaxException {
        String expectationAsJavaCode = new ExpectationToJavaSerializer().serialize(1, new org.mockserver.mock.Expectation(request().withMethod("GET").withPath("somePath").withQueryStringParameters(new Parameter("requestQueryStringParameterNameOne", "requestQueryStringParameterValueOneOne", "requestQueryStringParameterValueOneTwo"), new Parameter("requestQueryStringParameterNameTwo", "requestQueryStringParameterValueTwo")).withHeaders(new Header("requestHeaderNameOne", "requestHeaderValueOneOne", "requestHeaderValueOneTwo"), new Header("requestHeaderNameTwo", "requestHeaderValueTwo")).withCookies(new Cookie("requestCookieNameOne", "requestCookieValueOne"), new Cookie("requestCookieNameTwo", "requestCookieValueTwo")).withSecure(false).withKeepAlive(true).withBody(new StringBody("somebody")), Times.once(), TimeToLive.unlimited()).thenRespond(new HttpObjectCallback().withClientId("some_random_clientId")));
        Assert.assertTrue(compileJavaCode(((((((((((((((((((((((("" + "import org.mockserver.client.MockServerClient;") + (NEW_LINE)) + "import org.mockserver.matchers.Times;") + (NEW_LINE)) + "import org.mockserver.matchers.TimeToLive;") + (NEW_LINE)) + "import org.mockserver.mock.Expectation;") + (NEW_LINE)) + "import org.mockserver.model.*;") + (NEW_LINE)) + "import static org.mockserver.model.HttpRequest.request;") + (NEW_LINE)) + (NEW_LINE)) + "class TestClass {") + (NEW_LINE)) + "   static {") + "      ") + expectationAsJavaCode) + "") + (NEW_LINE)) + "   }") + (NEW_LINE)) + "}")));
    }
}

