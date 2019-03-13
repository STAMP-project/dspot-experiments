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
package org.springframework.http.codec;


import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.MULTIPART_FORM_DATA;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.AbstractLeakCheckingTestCase;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Sebastien Deleuze
 */
public class FormHttpMessageReaderTests extends AbstractLeakCheckingTestCase {
    private final FormHttpMessageReader reader = new FormHttpMessageReader();

    @Test
    public void canRead() {
        Assert.assertTrue(this.reader.canRead(ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class), APPLICATION_FORM_URLENCODED));
        Assert.assertTrue(this.reader.canRead(ResolvableType.forInstance(new org.springframework.util.LinkedMultiValueMap<String, String>()), APPLICATION_FORM_URLENCODED));
        Assert.assertFalse(this.reader.canRead(ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Object.class), APPLICATION_FORM_URLENCODED));
        Assert.assertFalse(this.reader.canRead(ResolvableType.forClassWithGenerics(MultiValueMap.class, Object.class, String.class), APPLICATION_FORM_URLENCODED));
        Assert.assertFalse(this.reader.canRead(ResolvableType.forClassWithGenerics(Map.class, String.class, String.class), APPLICATION_FORM_URLENCODED));
        Assert.assertFalse(this.reader.canRead(ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class), MULTIPART_FORM_DATA));
    }

    @Test
    public void readFormAsMono() {
        String body = "name+1=value+1&name+2=value+2%2B1&name+2=value+2%2B2&name+3";
        MockServerHttpRequest request = request(body);
        MultiValueMap<String, String> result = this.reader.readMono(null, request, null).block();
        Assert.assertEquals("Invalid result", 3, result.size());
        Assert.assertEquals("Invalid result", "value 1", result.getFirst("name 1"));
        List<String> values = result.get("name 2");
        Assert.assertEquals("Invalid result", 2, values.size());
        Assert.assertEquals("Invalid result", "value 2+1", values.get(0));
        Assert.assertEquals("Invalid result", "value 2+2", values.get(1));
        Assert.assertNull("Invalid result", result.getFirst("name 3"));
    }

    @Test
    public void readFormAsFlux() {
        String body = "name+1=value+1&name+2=value+2%2B1&name+2=value+2%2B2&name+3";
        MockServerHttpRequest request = request(body);
        MultiValueMap<String, String> result = this.reader.read(null, request, null).single().block();
        Assert.assertEquals("Invalid result", 3, result.size());
        Assert.assertEquals("Invalid result", "value 1", result.getFirst("name 1"));
        List<String> values = result.get("name 2");
        Assert.assertEquals("Invalid result", 2, values.size());
        Assert.assertEquals("Invalid result", "value 2+1", values.get(0));
        Assert.assertEquals("Invalid result", "value 2+2", values.get(1));
        Assert.assertNull("Invalid result", result.getFirst("name 3"));
    }

    @Test
    public void readFormError() {
        DataBuffer fooBuffer = stringBuffer("name=value");
        Flux<DataBuffer> body = Flux.just(fooBuffer).concatWith(Flux.error(new RuntimeException()));
        MockServerHttpRequest request = request(body);
        Flux<MultiValueMap<String, String>> result = this.reader.read(null, request, null);
        StepVerifier.create(result).expectError().verify();
    }
}

