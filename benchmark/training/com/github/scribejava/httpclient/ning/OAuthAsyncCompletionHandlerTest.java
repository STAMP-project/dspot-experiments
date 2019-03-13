package com.github.scribejava.httpclient.ning;


import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuthAsyncRequestCallback;
import com.github.scribejava.core.model.OAuthRequest;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Response;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class OAuthAsyncCompletionHandlerTest {
    private static final OAuthAsyncCompletionHandlerTest.AllGoodResponseConverter ALL_GOOD_RESPONSE_CONVERTER = new OAuthAsyncCompletionHandlerTest.AllGoodResponseConverter();

    private static final OAuthAsyncCompletionHandlerTest.OAuthExceptionResponseConverter OAUTH_EXCEPTION_RESPONSE_CONVERTER = new OAuthAsyncCompletionHandlerTest.OAuthExceptionResponseConverter();

    private OAuthAsyncCompletionHandler<String> handler;

    private OAuthAsyncCompletionHandlerTest.TestCallback callback;

    private static class TestCallback implements OAuthAsyncRequestCallback<String> {
        private Throwable throwable;

        private String response;

        @Override
        public void onCompleted(String response) {
            this.response = response;
        }

        @Override
        public void onThrowable(Throwable throwable) {
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public String getResponse() {
            return response;
        }
    }

    @Test
    public void shouldReleaseLatchOnSuccess() throws Exception {
        handler = new OAuthAsyncCompletionHandler(callback, OAuthAsyncCompletionHandlerTest.ALL_GOOD_RESPONSE_CONVERTER);
        final Response response = new MockResponse(200, "ok", new FluentCaseInsensitiveStringsMap(), new byte[0]);
        handler.onCompleted(response);
        Assert.assertNotNull(callback.getResponse());
        Assert.assertNull(callback.getThrowable());
        // verify latch is released
        Assert.assertEquals("All good", callback.getResponse());
    }

    @Test
    public void shouldReportOAuthException() throws Exception {
        handler = new OAuthAsyncCompletionHandler(callback, OAuthAsyncCompletionHandlerTest.OAUTH_EXCEPTION_RESPONSE_CONVERTER);
        final Response response = new MockResponse(200, "ok", new FluentCaseInsensitiveStringsMap(), new byte[0]);
        handler.onCompleted(response);
        Assert.assertNull(callback.getResponse());
        Assert.assertNotNull(callback.getThrowable());
        Assert.assertTrue(((callback.getThrowable()) instanceof OAuthException));
    }

    private static class AllGoodResponseConverter implements OAuthRequest.ResponseConverter<String> {
        @Override
        public String convert(com.github.scribejava.core.model.Response response) throws IOException {
            return "All good";
        }
    }

    private static class OAuthExceptionResponseConverter implements OAuthRequest.ResponseConverter<String> {
        @Override
        public String convert(com.github.scribejava.core.model.Response response) throws IOException {
            throw new OAuthException("bad oauth");
        }
    }
}

