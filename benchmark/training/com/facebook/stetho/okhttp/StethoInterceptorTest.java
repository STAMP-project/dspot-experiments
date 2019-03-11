/**
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.stetho.okhttp;


import Build.VERSION_CODES;
import NetworkEventReporter.InspectorRequest;
import NetworkEventReporter.InspectorResponse;
import Protocol.HTTP_1_1;
import android.net.Uri;
import com.facebook.stetho.inspector.network.NetworkEventReporter;
import com.facebook.stetho.inspector.network.NetworkEventReporterImpl;
import com.squareup.okhttp.Connection;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(emulateSdk = VERSION_CODES.JELLY_BEAN)
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*", "javax.net.ssl.*" })
@PrepareForTest(NetworkEventReporterImpl.class)
public class StethoInterceptorTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private NetworkEventReporter mMockEventReporter;

    private StethoInterceptor mInterceptor;

    private OkHttpClient mClientWithInterceptor;

    @Test
    public void testHappyPath() throws IOException {
        InOrder inOrder = Mockito.inOrder(mMockEventReporter);
        StethoInterceptorTest.hookAlmostRealRequestWillBeSent(mMockEventReporter);
        ByteArrayOutputStream capturedOutput = StethoInterceptorTest.hookAlmostRealInterpretResponseStream(mMockEventReporter);
        Uri requestUri = Uri.parse("http://www.facebook.com/nowhere");
        String requestText = "Test input";
        Request request = new Request.Builder().url(requestUri.toString()).method("POST", RequestBody.create(MediaType.parse("text/plain"), requestText)).build();
        String originalBodyData = "Success!";
        Response reply = new Response.Builder().request(request).protocol(HTTP_1_1).code(200).body(ResponseBody.create(MediaType.parse("text/plain"), originalBodyData)).build();
        Response filteredResponse = mInterceptor.intercept(new StethoInterceptorTest.SimpleTestChain(request, reply, Mockito.mock(Connection.class)));
        inOrder.verify(mMockEventReporter).isEnabled();
        inOrder.verify(mMockEventReporter).requestWillBeSent(ArgumentMatchers.any(InspectorRequest.class));
        inOrder.verify(mMockEventReporter).dataSent(ArgumentMatchers.anyString(), ArgumentMatchers.eq(requestText.length()), ArgumentMatchers.eq(requestText.length()));
        inOrder.verify(mMockEventReporter).responseHeadersReceived(ArgumentMatchers.any(InspectorResponse.class));
        String filteredResponseString = filteredResponse.body().string();
        String interceptedOutput = capturedOutput.toString();
        inOrder.verify(mMockEventReporter).dataReceived(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        inOrder.verify(mMockEventReporter).responseReadFinished(ArgumentMatchers.anyString());
        Assert.assertEquals(originalBodyData, filteredResponseString);
        Assert.assertEquals(originalBodyData, interceptedOutput);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testWithRequestCompression() throws IOException {
        AtomicReference<NetworkEventReporter.InspectorRequest> capturedRequest = StethoInterceptorTest.hookAlmostRealRequestWillBeSent(mMockEventReporter);
        MockWebServer server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse().setBody("Success!"));
        final byte[] decompressed = "Request text".getBytes();
        final byte[] compressed = StethoInterceptorTest.compress(decompressed);
        Assert.assertNotEquals("Bogus test: decompressed and compressed lengths match", compressed.length, decompressed.length);
        RequestBody compressedBody = RequestBody.create(MediaType.parse("text/plain"), StethoInterceptorTest.compress(decompressed));
        Request request = new Request.Builder().url(server.getUrl("/")).addHeader("Content-Encoding", "gzip").post(compressedBody).build();
        Response response = mClientWithInterceptor.newCall(request).execute();
        // Force a read to complete the flow.
        response.body().string();
        Assert.assertArrayEquals(decompressed, capturedRequest.get().body());
        Mockito.verify(mMockEventReporter).dataSent(ArgumentMatchers.anyString(), ArgumentMatchers.eq(decompressed.length), ArgumentMatchers.eq(compressed.length));
        server.shutdown();
    }

    @Test
    public void testWithResponseCompression() throws IOException {
        ByteArrayOutputStream capturedOutput = StethoInterceptorTest.hookAlmostRealInterpretResponseStream(mMockEventReporter);
        byte[] uncompressedData = StethoInterceptorTest.repeat(".", 1024).getBytes();
        byte[] compressedData = StethoInterceptorTest.compress(uncompressedData);
        MockWebServer server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse().setBody(new Buffer().write(compressedData)).addHeader("Content-Encoding: gzip"));
        Request request = new Request.Builder().url(server.url("/")).build();
        Response response = mClientWithInterceptor.newCall(request).execute();
        // Verify that the final output and the caller both saw the uncompressed stream.
        Assert.assertArrayEquals(uncompressedData, response.body().bytes());
        Assert.assertArrayEquals(uncompressedData, capturedOutput.toByteArray());
        // And verify that the StethoInterceptor was able to see both.
        Mockito.verify(mMockEventReporter).dataReceived(ArgumentMatchers.anyString(), ArgumentMatchers.eq(compressedData.length), ArgumentMatchers.eq(uncompressedData.length));
        server.shutdown();
    }

    private static class SimpleTestChain implements Interceptor.Chain {
        private final Request mRequest;

        private final Response mResponse;

        @Nullable
        private final Connection mConnection;

        public SimpleTestChain(Request request, Response response, @Nullable
        Connection connection) {
            mRequest = request;
            mResponse = response;
            mConnection = connection;
        }

        @Override
        public Request request() {
            return mRequest;
        }

        @Override
        public Response proceed(Request request) throws IOException {
            if ((mRequest) != request) {
                throw new IllegalArgumentException(((("Expected " + (System.identityHashCode(mRequest))) + "; got ") + (System.identityHashCode(request))));
            }
            return mResponse;
        }

        @Override
        public Connection connection() {
            return mConnection;
        }
    }
}

