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


import HttpStatus.OK;
import MediaType.MULTIPART_FORM_DATA;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Sebastien Deleuze
 */
public class MultipartIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    @Test
    public void getFormParts() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<MultiValueMap<String, Object>> request = RequestEntity.post(new URI((("http://localhost:" + (port)) + "/form-parts"))).contentType(MULTIPART_FORM_DATA).body(generateBody());
        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
        Assert.assertEquals(OK, response.getStatusCode());
    }

    public static class CheckRequestHandler implements WebHandler {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange) {
            if (exchange.getRequest().getURI().getPath().equals("/form-parts")) {
                return assertGetFormParts(exchange);
            }
            return Mono.error(new AssertionError());
        }

        private Mono<Void> assertGetFormParts(ServerWebExchange exchange) {
            return exchange.getMultipartData().doOnNext(( parts) -> {
                assertEquals(2, parts.size());
                assertTrue(parts.containsKey("fooPart"));
                assertFooPart(parts.getFirst("fooPart"));
                assertTrue(parts.containsKey("barPart"));
                assertBarPart(parts.getFirst("barPart"));
            }).then();
        }

        private void assertFooPart(Part part) {
            Assert.assertEquals("fooPart", part.name());
            Assert.assertTrue((part instanceof FilePart));
            Assert.assertEquals("foo.txt", filename());
            StepVerifier.create(DataBufferUtils.join(part.content())).consumeNextWith(( buffer) -> {
                assertEquals(12, buffer.readableByteCount());
                byte[] byteContent = new byte[12];
                buffer.read(byteContent);
                assertEquals("Lorem Ipsum.", new String(byteContent));
            }).verifyComplete();
        }

        private void assertBarPart(Part part) {
            Assert.assertEquals("barPart", part.name());
            Assert.assertTrue((part instanceof FormFieldPart));
            Assert.assertEquals("bar", value());
        }
    }
}

