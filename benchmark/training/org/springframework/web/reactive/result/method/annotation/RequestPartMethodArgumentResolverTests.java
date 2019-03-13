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
package org.springframework.web.reactive.result.method.annotation;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.support.DataBufferTestUtils;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link RequestPartMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestPartMethodArgumentResolverTests {
    private RequestPartMethodArgumentResolver resolver;

    private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

    private MultipartHttpMessageWriter writer;

    @Test
    public void supportsParameter() {
        MethodParameter param;
        param = this.testMethod.annot(requestPart()).arg(RequestPartMethodArgumentResolverTests.Person.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestPart()).arg(Mono.class, RequestPartMethodArgumentResolverTests.Person.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestPart()).arg(Flux.class, RequestPartMethodArgumentResolverTests.Person.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestPart()).arg(Part.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestPart()).arg(Mono.class, Part.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annot(requestPart()).arg(Flux.class, Part.class);
        Assert.assertTrue(this.resolver.supportsParameter(param));
        param = this.testMethod.annotNotPresent(RequestPart.class).arg(RequestPartMethodArgumentResolverTests.Person.class);
        Assert.assertFalse(this.resolver.supportsParameter(param));
    }

    @Test
    public void person() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(RequestPartMethodArgumentResolverTests.Person.class);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("Jones"));
        RequestPartMethodArgumentResolverTests.Person actual = resolveArgument(param, bodyBuilder);
        Assert.assertEquals("Jones", actual.getName());
    }

    @Test
    public void listPerson() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(List.class, RequestPartMethodArgumentResolverTests.Person.class);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("Jones"));
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("James"));
        List<RequestPartMethodArgumentResolverTests.Person> actual = resolveArgument(param, bodyBuilder);
        Assert.assertEquals("Jones", actual.get(0).getName());
        Assert.assertEquals("James", actual.get(1).getName());
    }

    @Test
    public void monoPerson() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(Mono.class, RequestPartMethodArgumentResolverTests.Person.class);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("Jones"));
        Mono<RequestPartMethodArgumentResolverTests.Person> actual = resolveArgument(param, bodyBuilder);
        Assert.assertEquals("Jones", actual.block().getName());
    }

    @Test
    public void fluxPerson() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(Flux.class, RequestPartMethodArgumentResolverTests.Person.class);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("Jones"));
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("James"));
        Flux<RequestPartMethodArgumentResolverTests.Person> actual = resolveArgument(param, bodyBuilder);
        List<RequestPartMethodArgumentResolverTests.Person> persons = actual.collectList().block();
        Assert.assertEquals("Jones", persons.get(0).getName());
        Assert.assertEquals("James", persons.get(1).getName());
    }

    @Test
    public void part() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(Part.class);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("Jones"));
        Part actual = resolveArgument(param, bodyBuilder);
        DataBuffer buffer = DataBufferUtils.join(actual.content()).block();
        Assert.assertEquals("{\"name\":\"Jones\"}", DataBufferTestUtils.dumpString(buffer, StandardCharsets.UTF_8));
    }

    @Test
    public void listPart() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(List.class, Part.class);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("Jones"));
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("James"));
        List<Part> actual = resolveArgument(param, bodyBuilder);
        Assert.assertEquals("{\"name\":\"Jones\"}", partToUtf8String(actual.get(0)));
        Assert.assertEquals("{\"name\":\"James\"}", partToUtf8String(actual.get(1)));
    }

    @Test
    public void monoPart() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(Mono.class, Part.class);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("Jones"));
        Mono<Part> actual = resolveArgument(param, bodyBuilder);
        Part part = actual.block();
        Assert.assertEquals("{\"name\":\"Jones\"}", partToUtf8String(part));
    }

    @Test
    public void fluxPart() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(Flux.class, Part.class);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("Jones"));
        bodyBuilder.part("name", new RequestPartMethodArgumentResolverTests.Person("James"));
        Flux<Part> actual = resolveArgument(param, bodyBuilder);
        List<Part> parts = actual.collectList().block();
        Assert.assertEquals("{\"name\":\"Jones\"}", partToUtf8String(parts.get(0)));
        Assert.assertEquals("{\"name\":\"James\"}", partToUtf8String(parts.get(1)));
    }

    @Test
    public void personRequired() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(RequestPartMethodArgumentResolverTests.Person.class);
        ServerWebExchange exchange = createExchange(new MultipartBodyBuilder());
        Mono<Object> result = this.resolver.resolveArgument(param, new BindingContext(), exchange);
        StepVerifier.create(result).expectError(ServerWebInputException.class).verify();
    }

    @Test
    public void personNotRequired() {
        MethodParameter param = this.testMethod.annot(requestPart().notRequired()).arg(RequestPartMethodArgumentResolverTests.Person.class);
        ServerWebExchange exchange = createExchange(new MultipartBodyBuilder());
        Mono<Object> result = this.resolver.resolveArgument(param, new BindingContext(), exchange);
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    public void partRequired() {
        MethodParameter param = this.testMethod.annot(requestPart()).arg(Part.class);
        ServerWebExchange exchange = createExchange(new MultipartBodyBuilder());
        Mono<Object> result = this.resolver.resolveArgument(param, new BindingContext(), exchange);
        StepVerifier.create(result).expectError(ServerWebInputException.class).verify();
    }

    @Test
    public void partNotRequired() {
        MethodParameter param = this.testMethod.annot(requestPart().notRequired()).arg(Part.class);
        ServerWebExchange exchange = createExchange(new MultipartBodyBuilder());
        Mono<Object> result = this.resolver.resolveArgument(param, new BindingContext(), exchange);
        StepVerifier.create(result).verifyComplete();
    }

    private static class Person {
        private String name;

        @JsonCreator
        public Person(@JsonProperty("name")
        String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

