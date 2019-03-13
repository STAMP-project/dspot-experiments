/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import ParseHttpRequest.Method;
import bolts.Task;
import com.parse.http.ParseHttpBody;
import com.parse.http.ParseHttpRequest;
import com.parse.http.ParseHttpResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ParseRequestTest {
    private static byte[] data;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testRetryLogic() throws Exception {
        ParseHttpClient mockHttpClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(mockHttpClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenThrow(new IOException());
        ParseRequestTest.TestParseRequest request = new ParseRequestTest.TestParseRequest(Method.GET, "http://parse.com");
        Task<String> task = request.executeAsync(mockHttpClient);
        task.waitForCompletion();
        Mockito.verify(mockHttpClient, Mockito.times(5)).execute(ArgumentMatchers.any(ParseHttpRequest.class));
    }

    // TODO(grantland): Move to ParseFileRequestTest or ParseCountingByteArrayHttpBodyTest
    @Test
    public void testDownloadProgress() throws Exception {
        ParseHttpResponse mockResponse = new ParseHttpResponse.Builder().setStatusCode(200).setTotalSize(((long) (ParseRequestTest.data.length))).setContent(new ByteArrayInputStream(ParseRequestTest.data)).build();
        ParseHttpClient mockHttpClient = Mockito.mock(ParseHttpClient.class);
        Mockito.when(mockHttpClient.execute(ArgumentMatchers.any(ParseHttpRequest.class))).thenReturn(mockResponse);
        File tempFile = temporaryFolder.newFile("test");
        ParseFileRequest request = new ParseFileRequest(Method.GET, "localhost", tempFile);
        ParseRequestTest.TestProgressCallback downloadProgressCallback = new ParseRequestTest.TestProgressCallback();
        Task<Void> task = request.executeAsync(mockHttpClient, null, downloadProgressCallback);
        task.waitForCompletion();
        Assert.assertFalse(("Download failed: " + (task.getError())), task.isFaulted());
        Assert.assertEquals(ParseRequestTest.data.length, ParseFileUtils.readFileToByteArray(tempFile).length);
        ParseRequestTest.assertProgressCompletedSuccessfully(downloadProgressCallback);
    }

    private static class TestProgressCallback implements ProgressCallback {
        List<Integer> history = new LinkedList<>();

        @Override
        public void done(Integer percentDone) {
            history.add(percentDone);
        }
    }

    private static class TestParseRequest extends ParseRequest<String> {
        byte[] data;

        public TestParseRequest(ParseHttpRequest.Method method, String url) {
            super(method, url);
        }

        @Override
        protected Task<String> onResponseAsync(ParseHttpResponse response, ProgressCallback downloadProgressCallback) {
            return Task.forResult(null);
        }

        @Override
        protected ParseHttpBody newBody(ProgressCallback uploadProgressCallback) {
            if (uploadProgressCallback != null) {
                return new ParseCountingByteArrayHttpBody(data, null, uploadProgressCallback);
            }
            return super.newBody(null);
        }
    }
}

