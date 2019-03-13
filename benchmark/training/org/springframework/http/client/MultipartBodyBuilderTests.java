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
package org.springframework.http.client;


import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder.PublisherEntity;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class MultipartBodyBuilderTests {
    @Test
    public void builder() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        MultiValueMap<String, String> multipartData = new org.springframework.util.LinkedMultiValueMap();
        multipartData.add("form field", "form value");
        builder.part("key", multipartData).header("foo", "bar");
        Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
        builder.part("logo", logo).header("baz", "qux");
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.add("foo", "bar");
        HttpEntity<String> entity = new HttpEntity("body", entityHeaders);
        builder.part("entity", entity).header("baz", "qux");
        Publisher<String> publisher = Flux.just("foo", "bar", "baz");
        builder.asyncPart("publisherClass", publisher, String.class).header("baz", "qux");
        builder.asyncPart("publisherPtr", publisher, new org.springframework.core.ParameterizedTypeReference<String>() {}).header("baz", "qux");
        MultiValueMap<String, HttpEntity<?>> result = builder.build();
        Assert.assertEquals(5, result.size());
        HttpEntity<?> resultEntity = result.getFirst("key");
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(multipartData, resultEntity.getBody());
        Assert.assertEquals("bar", resultEntity.getHeaders().getFirst("foo"));
        resultEntity = result.getFirst("logo");
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(logo, resultEntity.getBody());
        Assert.assertEquals("qux", resultEntity.getHeaders().getFirst("baz"));
        resultEntity = result.getFirst("entity");
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals("body", resultEntity.getBody());
        Assert.assertEquals("bar", resultEntity.getHeaders().getFirst("foo"));
        Assert.assertEquals("qux", resultEntity.getHeaders().getFirst("baz"));
        resultEntity = result.getFirst("publisherClass");
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(publisher, resultEntity.getBody());
        Assert.assertEquals(ResolvableType.forClass(String.class), ((PublisherEntity<?, ?>) (resultEntity)).getResolvableType());
        Assert.assertEquals("qux", resultEntity.getHeaders().getFirst("baz"));
        resultEntity = result.getFirst("publisherPtr");
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(publisher, resultEntity.getBody());
        Assert.assertEquals(ResolvableType.forClass(String.class), ((PublisherEntity<?, ?>) (resultEntity)).getResolvableType());
        Assert.assertEquals("qux", resultEntity.getHeaders().getFirst("baz"));
    }

    // SPR-16601
    @Test
    public void publisherEntityAcceptedAsInput() {
        Publisher<String> publisher = Flux.just("foo", "bar", "baz");
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.asyncPart("publisherClass", publisher, String.class).header("baz", "qux");
        HttpEntity<?> entity = builder.build().getFirst("publisherClass");
        Assert.assertNotNull(entity);
        Assert.assertEquals(PublisherEntity.class, entity.getClass());
        // Now build a new MultipartBodyBuilder, as BodyInserters.fromMultipartData would do...
        builder = new MultipartBodyBuilder();
        builder.part("publisherClass", entity);
        entity = builder.build().getFirst("publisherClass");
        Assert.assertNotNull(entity);
        Assert.assertEquals(PublisherEntity.class, entity.getClass());
    }
}

