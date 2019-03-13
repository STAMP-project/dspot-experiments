/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.messaging.handler.annotation.support.reactive;


import DestinationPatternsMessageCondition.LOOKUP_DESTINATION_HEADER;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.invocation.reactive.TestEncoderMethodReturnValueHandler;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link MessageMappingMessageHandler}.
 *
 * @author Rossen Stoyanchev
 */
@SuppressWarnings("ALL")
public class MessageMappingMessageHandlerTests {
    private static final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();

    private TestEncoderMethodReturnValueHandler returnValueHandler;

    @Test
    public void handleString() {
        MessageMappingMessageHandler messsageHandler = initMesssageHandler();
        messsageHandler.handleMessage(message("string", "abcdef")).block(Duration.ofSeconds(5));
        verifyOutputContent(Collections.singletonList("abcdef::response"));
    }

    @Test
    public void handleMonoString() {
        MessageMappingMessageHandler messsageHandler = initMesssageHandler();
        messsageHandler.handleMessage(message("monoString", "abcdef")).block(Duration.ofSeconds(5));
        verifyOutputContent(Collections.singletonList("abcdef::response"));
    }

    @Test
    public void handleFluxString() {
        MessageMappingMessageHandler messsageHandler = initMesssageHandler();
        messsageHandler.handleMessage(message("fluxString", "abc\ndef\nghi")).block(Duration.ofSeconds(5));
        verifyOutputContent(Arrays.asList("abc::response", "def::response", "ghi::response"));
    }

    @Test
    public void handleWithPlaceholderInMapping() {
        MessageMappingMessageHandler messsageHandler = initMesssageHandler();
        messsageHandler.handleMessage(message("path123", "abcdef")).block(Duration.ofSeconds(5));
        verifyOutputContent(Collections.singletonList("abcdef::response"));
    }

    @Test
    public void handleException() {
        MessageMappingMessageHandler messsageHandler = initMesssageHandler();
        messsageHandler.handleMessage(message("exception", "abc")).block(Duration.ofSeconds(5));
        verifyOutputContent(Collections.singletonList("rejected::handled"));
    }

    @Test
    public void handleErrorSignal() {
        MessageMappingMessageHandler messsageHandler = initMesssageHandler();
        messsageHandler.handleMessage(message("errorSignal", "abc")).block(Duration.ofSeconds(5));
        verifyOutputContent(Collections.singletonList("rejected::handled"));
    }

    @Test
    public void unhandledExceptionShouldFlowThrough() {
        GenericMessage<?> message = new GenericMessage(new Object(), Collections.singletonMap(LOOKUP_DESTINATION_HEADER, "string"));
        StepVerifier.create(initMesssageHandler().handleMessage(message)).expectErrorSatisfies(( ex) -> assertTrue(("Actual: " + (ex.getMessage())), ex.getMessage().startsWith("Could not resolve method parameter at index 0"))).verify(Duration.ofSeconds(5));
    }

    @Controller
    static class TestController {
        @MessageMapping("string")
        String handleString(String payload) {
            return payload + "::response";
        }

        @MessageMapping("monoString")
        Mono<String> handleMonoString(Mono<String> payload) {
            return payload.map(( s) -> s + "::response").delayElement(Duration.ofMillis(10));
        }

        @MessageMapping("fluxString")
        Flux<String> handleFluxString(Flux<String> payload) {
            return payload.map(( s) -> s + "::response").delayElements(Duration.ofMillis(10));
        }

        @MessageMapping("${path}")
        String handleWithPlaceholder(String payload) {
            return payload + "::response";
        }

        @MessageMapping("exception")
        String handleAndThrow() {
            throw new IllegalArgumentException("rejected");
        }

        @MessageMapping("errorSignal")
        Mono<String> handleAndSignalError() {
            return Mono.delay(Duration.ofMillis(10)).flatMap(( aLong) -> Mono.error(new IllegalArgumentException("rejected")));
        }

        @MessageExceptionHandler
        Mono<String> handleException(IllegalArgumentException ex) {
            return Mono.delay(Duration.ofMillis(10)).map(( aLong) -> (ex.getMessage()) + "::handled");
        }
    }
}

