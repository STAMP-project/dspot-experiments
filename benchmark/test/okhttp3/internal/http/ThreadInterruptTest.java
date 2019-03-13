/**
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.internal.http;


import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClientTestRule;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.BufferedSink;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public final class ThreadInterruptTest {
    @Rule
    public final OkHttpClientTestRule clientTestRule = new OkHttpClientTestRule();

    // The size of the socket buffers in bytes.
    private static final int SOCKET_BUFFER_SIZE = 256 * 1024;

    private MockWebServer server;

    private OkHttpClient client = clientTestRule.client;

    @Test
    public void interruptWritingRequestBody() throws Exception {
        server.enqueue(new MockResponse());
        server.start();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).post(new RequestBody() {
            @Override
            @Nullable
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                for (int i = 0; i < 10; i++) {
                    sink.writeByte(0);
                    sink.flush();
                    sleep(100);
                }
                Assert.fail("Expected connection to be closed");
            }
        }).build());
        interruptLater(500);
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void interruptReadingResponseBody() throws Exception {
        int responseBodySize = (8 * 1024) * 1024;// 8 MiB.

        server.enqueue(new MockResponse().setBody(new Buffer().write(new byte[responseBodySize])).throttleBody((64 * 1024), 125, TimeUnit.MILLISECONDS));// 500 Kbps

        server.start();
        Call call = client.newCall(new Request.Builder().url(server.url("/")).build());
        Response response = call.execute();
        interruptLater(500);
        InputStream responseBody = response.body().byteStream();
        byte[] buffer = new byte[1024];
        try {
            while ((responseBody.read(buffer)) != (-1)) {
            } 
            Assert.fail("Expected connection to be interrupted");
        } catch (IOException expected) {
        }
        responseBody.close();
    }
}

