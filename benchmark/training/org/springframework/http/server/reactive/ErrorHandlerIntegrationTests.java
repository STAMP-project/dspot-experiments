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


import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class ErrorHandlerIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    private final ErrorHandlerIntegrationTests.ErrorHandler handler = new ErrorHandlerIntegrationTests.ErrorHandler();

    @Test
    public void responseBodyError() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(ErrorHandlerIntegrationTests.NO_OP_ERROR_HANDLER);
        URI url = new URI((("http://localhost:" + (port)) + "/response-body-error"));
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        Assert.assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void handlingError() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(ErrorHandlerIntegrationTests.NO_OP_ERROR_HANDLER);
        URI url = new URI((("http://localhost:" + (port)) + "/handling-error"));
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        Assert.assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // SPR-15560
    @Test
    public void emptyPathSegments() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(ErrorHandlerIntegrationTests.NO_OP_ERROR_HANDLER);
        URI url = new URI((("http://localhost:" + (port)) + "//"));
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        Assert.assertEquals(OK, response.getStatusCode());
    }

    private static class ErrorHandler implements HttpHandler {
        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            Exception error = new UnsupportedOperationException();
            String path = request.getURI().getPath();
            if (path.endsWith("response-body-error")) {
                return response.writeWith(Mono.error(error));
            } else
                if (path.endsWith("handling-error")) {
                    return Mono.error(error);
                } else {
                    return Mono.empty();
                }

        }
    }

    private static final ResponseErrorHandler NO_OP_ERROR_HANDLER = new ResponseErrorHandler() {
        @Override
        public boolean hasError(ClientHttpResponse response) {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) {
        }
    };
}

