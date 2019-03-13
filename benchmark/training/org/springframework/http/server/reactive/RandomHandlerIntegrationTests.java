/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.http.server.reactive;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class RandomHandlerIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    public static final int REQUEST_SIZE = 4096 * 3;

    public static final int RESPONSE_SIZE = 1024 * 4;

    private final Random rnd = new Random();

    private final RandomHandlerIntegrationTests.RandomHandler handler = new RandomHandlerIntegrationTests.RandomHandler();

    private final DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    @Test
    public void random() throws Throwable {
        // TODO: fix Reactor support
        RestTemplate restTemplate = new RestTemplate();
        byte[] body = randomBytes();
        RequestEntity<byte[]> request = RequestEntity.post(new java.net.URI(("http://localhost:" + (port)))).body(body);
        ResponseEntity<byte[]> response = restTemplate.exchange(request, byte[].class);
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals(RandomHandlerIntegrationTests.RESPONSE_SIZE, response.getHeaders().getContentLength());
        Assert.assertEquals(RandomHandlerIntegrationTests.RESPONSE_SIZE, response.getBody().length);
    }

    private class RandomHandler implements HttpHandler {
        public static final int CHUNKS = 16;

        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            Mono<Integer> requestSizeMono = request.getBody().reduce(0, ( integer, dataBuffer) -> integer + (dataBuffer.readableByteCount())).doOnSuccessOrError(( size, throwable) -> {
                assertNull(throwable);
                assertEquals(org.springframework.http.server.reactive.REQUEST_SIZE, ((long) (size)));
            });
            response.getHeaders().setContentLength(RandomHandlerIntegrationTests.RESPONSE_SIZE);
            return requestSizeMono.then(response.writeWith(multipleChunks()));
        }

        private Publisher<DataBuffer> multipleChunks() {
            int chunkSize = (RandomHandlerIntegrationTests.RESPONSE_SIZE) / (RandomHandlerIntegrationTests.RandomHandler.CHUNKS);
            return Flux.range(1, RandomHandlerIntegrationTests.RandomHandler.CHUNKS).map(( integer) -> randomBuffer(chunkSize));
        }

        private DataBuffer randomBuffer(int size) {
            byte[] bytes = new byte[size];
            rnd.nextBytes(bytes);
            DataBuffer buffer = dataBufferFactory.allocateBuffer(size);
            buffer.write(bytes);
            return buffer;
        }
    }
}

