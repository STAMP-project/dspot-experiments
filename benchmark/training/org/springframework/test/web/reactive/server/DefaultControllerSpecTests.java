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
package org.springframework.test.web.reactive.server;


import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.accept.RequestedContentTypeResolverBuilder;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.PathMatchConfigurer;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;


/**
 * Unit tests for {@link DefaultControllerSpec}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultControllerSpecTests {
    @Test
    public void controller() {
        new DefaultControllerSpec(new DefaultControllerSpecTests.MyController()).build().get().uri("/").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Success");
    }

    @Test
    public void controllerAdvice() {
        new DefaultControllerSpec(new DefaultControllerSpecTests.MyController()).controllerAdvice(new DefaultControllerSpecTests.MyControllerAdvice()).build().get().uri("/exception").exchange().expectStatus().isBadRequest().expectBody(String.class).isEqualTo("Handled exception");
    }

    @Test
    public void controllerAdviceWithClassArgument() {
        new DefaultControllerSpec(DefaultControllerSpecTests.MyController.class).controllerAdvice(DefaultControllerSpecTests.MyControllerAdvice.class).build().get().uri("/exception").exchange().expectStatus().isBadRequest().expectBody(String.class).isEqualTo("Handled exception");
    }

    @Test
    public void configurerConsumers() {
        DefaultControllerSpecTests.TestConsumer<ArgumentResolverConfigurer> argumentResolverConsumer = new DefaultControllerSpecTests.TestConsumer<>();
        DefaultControllerSpecTests.TestConsumer<RequestedContentTypeResolverBuilder> contenTypeResolverConsumer = new DefaultControllerSpecTests.TestConsumer<>();
        DefaultControllerSpecTests.TestConsumer<CorsRegistry> corsRegistryConsumer = new DefaultControllerSpecTests.TestConsumer<>();
        DefaultControllerSpecTests.TestConsumer<FormatterRegistry> formatterConsumer = new DefaultControllerSpecTests.TestConsumer<>();
        DefaultControllerSpecTests.TestConsumer<ServerCodecConfigurer> codecsConsumer = new DefaultControllerSpecTests.TestConsumer<>();
        DefaultControllerSpecTests.TestConsumer<PathMatchConfigurer> pathMatchingConsumer = new DefaultControllerSpecTests.TestConsumer<>();
        DefaultControllerSpecTests.TestConsumer<ViewResolverRegistry> viewResolverConsumer = new DefaultControllerSpecTests.TestConsumer<>();
        new DefaultControllerSpec(new DefaultControllerSpecTests.MyController()).argumentResolvers(argumentResolverConsumer).contentTypeResolver(contenTypeResolverConsumer).corsMappings(corsRegistryConsumer).formatters(formatterConsumer).httpMessageCodecs(codecsConsumer).pathMatching(pathMatchingConsumer).viewResolvers(viewResolverConsumer).build();
        Assert.assertNotNull(argumentResolverConsumer.getValue());
        Assert.assertNotNull(contenTypeResolverConsumer.getValue());
        Assert.assertNotNull(corsRegistryConsumer.getValue());
        Assert.assertNotNull(formatterConsumer.getValue());
        Assert.assertNotNull(codecsConsumer.getValue());
        Assert.assertNotNull(pathMatchingConsumer.getValue());
        Assert.assertNotNull(viewResolverConsumer.getValue());
    }

    @RestController
    private static class MyController {
        @GetMapping("/")
        public String handle() {
            return "Success";
        }

        @GetMapping("/exception")
        public void handleWithError() {
            throw new IllegalStateException();
        }
    }

    @ControllerAdvice
    private static class MyControllerAdvice {
        @ExceptionHandler
        public ResponseEntity<String> handle(IllegalStateException ex) {
            return ResponseEntity.status(400).body("Handled exception");
        }
    }

    private static class TestConsumer<T> implements Consumer<T> {
        private T value;

        public T getValue() {
            return this.value;
        }

        @Override
        public void accept(T t) {
            this.value = t;
        }
    }
}

