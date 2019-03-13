package com.github.scribejava.httpclient.apache;


import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuthAsyncRequestCallback;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.Test;


public class OAuthAsyncCompletionHandlerTest {
    private static final OAuthAsyncCompletionHandlerTest.AllGoodResponseConverter ALL_GOOD_RESPONSE_CONVERTER = new OAuthAsyncCompletionHandlerTest.AllGoodResponseConverter();

    private static final OAuthAsyncCompletionHandlerTest.ExceptionResponseConverter EXCEPTION_RESPONSE_CONVERTER = new OAuthAsyncCompletionHandlerTest.ExceptionResponseConverter();

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
        final HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("4", 1, 1), 200, "ok"));
        final BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(new byte[0]));
        response.setEntity(entity);
        handler.completed(response);
        Assert.assertNotNull(callback.getResponse());
        Assert.assertNull(callback.getThrowable());
        // verify latch is released
        Assert.assertEquals("All good", handler.getResult());
    }

    @Test
    public void shouldReleaseLatchOnIOException() throws Exception {
        handler = new OAuthAsyncCompletionHandler(callback, OAuthAsyncCompletionHandlerTest.EXCEPTION_RESPONSE_CONVERTER);
        final HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("4", 1, 1), 200, "ok"));
        final BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(new byte[0]));
        response.setEntity(entity);
        handler.completed(response);
        Assert.assertNull(callback.getResponse());
        Assert.assertNotNull(callback.getThrowable());
        Assert.assertTrue(((callback.getThrowable()) instanceof IOException));
        // verify latch is released
        try {
            handler.getResult();
            Assert.fail();
        } catch (ExecutionException expected) {
            // expected
        }
    }

    @Test
    public void shouldReportOAuthException() throws Exception {
        handler = new OAuthAsyncCompletionHandler(callback, OAuthAsyncCompletionHandlerTest.OAUTH_EXCEPTION_RESPONSE_CONVERTER);
        final HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("4", 1, 1), 200, "ok"));
        final BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(new byte[0]));
        response.setEntity(entity);
        handler.completed(response);
        Assert.assertNull(callback.getResponse());
        Assert.assertNotNull(callback.getThrowable());
        Assert.assertTrue(((callback.getThrowable()) instanceof OAuthException));
        // verify latch is released
        try {
            handler.getResult();
            Assert.fail();
        } catch (ExecutionException expected) {
            // expected
        }
    }

    @Test
    public void shouldReleaseLatchOnCancel() throws Exception {
        handler = new OAuthAsyncCompletionHandler(callback, OAuthAsyncCompletionHandlerTest.ALL_GOOD_RESPONSE_CONVERTER);
        final HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("4", 1, 1), 200, "ok"));
        final BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(new byte[0]));
        response.setEntity(entity);
        handler.cancelled();
        Assert.assertNull(callback.getResponse());
        Assert.assertNotNull(callback.getThrowable());
        Assert.assertTrue(((callback.getThrowable()) instanceof CancellationException));
        // verify latch is released
        try {
            handler.getResult();
            Assert.fail();
        } catch (ExecutionException expected) {
            // expected
        }
    }

    @Test
    public void shouldReleaseLatchOnFailure() throws Exception {
        handler = new OAuthAsyncCompletionHandler(callback, OAuthAsyncCompletionHandlerTest.ALL_GOOD_RESPONSE_CONVERTER);
        final HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("4", 1, 1), 200, "ok"));
        final BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(new byte[0]));
        response.setEntity(entity);
        handler.failed(new RuntimeException());
        Assert.assertNull(callback.getResponse());
        Assert.assertNotNull(callback.getThrowable());
        Assert.assertTrue(((callback.getThrowable()) instanceof RuntimeException));
        // verify latch is released
        try {
            handler.getResult();
            Assert.fail();
        } catch (ExecutionException expected) {
            // expected
        }
    }

    private static class AllGoodResponseConverter implements OAuthRequest.ResponseConverter<String> {
        @Override
        public String convert(Response response) throws IOException {
            return "All good";
        }
    }

    private static class ExceptionResponseConverter implements OAuthRequest.ResponseConverter<String> {
        @Override
        public String convert(Response response) throws IOException {
            throw new IOException("Failed to convert");
        }
    }

    private static class OAuthExceptionResponseConverter implements OAuthRequest.ResponseConverter<String> {
        @Override
        public String convert(Response response) throws IOException {
            throw new OAuthException("bad oauth");
        }
    }
}

