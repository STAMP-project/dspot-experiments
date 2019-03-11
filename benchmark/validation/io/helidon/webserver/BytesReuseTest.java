/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.webserver;


import Http.Method.POST;
import io.helidon.common.http.DataChunk;
import io.helidon.webserver.utils.SocketHttpClient;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringEndsWith;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;


/**
 * The BytesReuseTest verifies whether the {@link DataChunk} instances get released properly.
 * <p>
 * Note that with {@link DataChunk#finalize()} which calls {@link DataChunk#release()},
 * we no longer experience {@link OutOfMemoryError} exceptions in case the chunks aren't freed
 * as long as no references to the {@link DataChunk} instances are kept.
 */
public class BytesReuseTest {
    private static final Logger LOGGER = Logger.getLogger(PlainTest.class.getName());

    private static WebServer webServer;

    private static Queue<DataChunk> chunkReference = new ConcurrentLinkedQueue<>();

    private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(( r) -> new Thread(r) {
        {
            setDaemon(true);
        }
    });

    @Test
    public void requestChunkDataRemainsWhenNotReleased() throws Exception {
        doSubscriberPostRequest(false);
        for (DataChunk chunk : BytesReuseTest.chunkReference) {
            MatcherAssert.assertThat(("The chunk was released: ID " + (chunk.id())), chunk.isReleased(), CoreMatchers.is(false));
        }
        MatcherAssert.assertThat(new String(BytesReuseTest.chunkReference.peek().bytes()), StringStartsWith.startsWith("myData"));
    }

    @Test
    public void requestChunkDataGetReusedWhenReleased() throws Exception {
        doSubscriberPostRequest(true);
        assertChunkReferencesAreReleased();
    }

    @Test
    public void toStringConverterFreesTheRequestChunks() throws Exception {
        try (SocketHttpClient s = new SocketHttpClient(BytesReuseTest.webServer)) {
            s.request(POST, "/string?test=myData", ("myData" + (SocketHttpClient.longData(100000).toString())));
            MatcherAssert.assertThat(s.receive(), StringEndsWith.endsWith("\nFinished\n0\n\n"));
        }
        assertChunkReferencesAreReleased();
    }

    @Test
    public void toByteArrayConverterFreesTheRequestChunks() throws Exception {
        try (SocketHttpClient s = new SocketHttpClient(BytesReuseTest.webServer)) {
            s.request(POST, "/bytes?test=myData", ("myData" + (SocketHttpClient.longData(100000).toString())));
            MatcherAssert.assertThat(s.receive(), StringEndsWith.endsWith("\nFinished\n0\n\n"));
        }
        assertChunkReferencesAreReleased();
    }

    @Test
    public void toByteArrayDeferredConverterFreesTheRequestChunks() throws Exception {
        try (SocketHttpClient s = new SocketHttpClient(BytesReuseTest.webServer)) {
            s.request(POST, "/bytes_deferred?test=myData", ("myData" + (SocketHttpClient.longData(100000).toString())));
            MatcherAssert.assertThat(s.receive(), StringEndsWith.endsWith("\nFinished\n0\n\n"));
        }
        assertChunkReferencesAreReleased();
    }

    @Test
    public void toInputStreamConverterFreesTheRequestChunks() throws Exception {
        try (SocketHttpClient s = new SocketHttpClient(BytesReuseTest.webServer)) {
            s.request(POST, "/input_stream?test=myData", ("myData" + (SocketHttpClient.longData(100000).toString())));
            MatcherAssert.assertThat(s.receive(), StringEndsWith.endsWith("\nFinished\n0\n\n"));
        }
        assertChunkReferencesAreReleased();
    }

    @Test
    public void notFoundPostRequestPayloadGetsReleased() throws Exception {
        try (SocketHttpClient s = new SocketHttpClient(BytesReuseTest.webServer)) {
            s.request(POST, "/non_existent?test=myData", ("myData" + (SocketHttpClient.longData(100000).toString())));
            MatcherAssert.assertThat(s.receive(), StringStartsWith.startsWith("HTTP/1.1 404 Not Found\n"));
        }
        assertChunkReferencesAreReleased();
    }

    @Test
    public void unconsumedPostRequestPayloadGetsReleased() throws Exception {
        try (SocketHttpClient s = new SocketHttpClient(BytesReuseTest.webServer)) {
            s.request(POST, "/unconsumed?test=myData", ("myData" + (SocketHttpClient.longData(100000).toString())));
            MatcherAssert.assertThat(s.receive(), StringEndsWith.endsWith("Nothing consumed!\n0\n\n"));
        }
        assertChunkReferencesAreReleased();
    }

    private static class ChunkedSocketHttpClient extends SocketHttpClient {
        private final long limit;

        private final AtomicLong sentData = new AtomicLong();

        public ChunkedSocketHttpClient(WebServer webServer, long limit) throws IOException {
            super(webServer);
            this.limit = limit;
        }

        @Override
        protected void sendPayload(PrintWriter pw, String payload) {
            pw.println("transfer-encoding: chunked");
            pw.println("");
            pw.println("9");
            pw.println("unlimited");
            ScheduledFuture<?> future = startMeasuring();
            try {
                String data = SocketHttpClient.longData(1000000).toString();
                long i = 0;
                for (; (!(pw.checkError())) && (((limit) == 0) || (i < (limit))); ++i) {
                    pw.println(Integer.toHexString(data.length()));
                    pw.println(data);
                    pw.flush();
                    sentData.addAndGet(data.length());
                }
                BytesReuseTest.LOGGER.info(("Published chunks: " + i));
            } finally {
                future.cancel(true);
            }
        }

        ScheduledFuture<?> startMeasuring() {
            long startTime = System.nanoTime();
            Queue<Long> receivedDataShort = new LinkedList<>();
            return BytesReuseTest.service.scheduleAtFixedRate(() -> {
                long l = (sentData.get()) / 1000000;
                receivedDataShort.add(l);
                long previous = l - ((receivedDataShort.size()) > 10 ? receivedDataShort.remove() : 0);
                long time = TimeUnit.SECONDS.convert(((System.nanoTime()) - startTime), TimeUnit.NANOSECONDS);
                System.out.println((("Sent bytes: " + ((sentData.get()) / 1000000)) + " MB"));
                System.out.println((("SPEED: " + (l / time)) + " MB/s"));
                System.out.println((("SHORT SPEED: " + (previous / (time > 10 ? 10 : time))) + " MB/s"));
                System.out.println("====");
            }, 1, 1, TimeUnit.SECONDS);
        }
    }
}

