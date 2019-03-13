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
package org.springframework.http.codec.xml;


import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.codec.AbstractEncoderTestCase;
import org.springframework.http.codec.Pojo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Sebastien Deleuze
 * @author Arjen Poutsma
 */
public class Jaxb2XmlEncoderTests extends AbstractEncoderTestCase<Jaxb2XmlEncoder> {
    public Jaxb2XmlEncoderTests() {
        super(new Jaxb2XmlEncoder());
    }

    @Override
    @Test
    public void canEncode() {
        Assert.assertTrue(this.encoder.canEncode(org.springframework.core.ResolvableType.forClass(Pojo.class), MediaType.APPLICATION_XML));
        Assert.assertTrue(this.encoder.canEncode(org.springframework.core.ResolvableType.forClass(Pojo.class), MediaType.TEXT_XML));
        Assert.assertFalse(this.encoder.canEncode(org.springframework.core.ResolvableType.forClass(Pojo.class), MediaType.APPLICATION_JSON));
        Assert.assertTrue(this.encoder.canEncode(org.springframework.core.ResolvableType.forClass(Jaxb2XmlDecoderTests.TypePojo.class), MediaType.APPLICATION_XML));
        Assert.assertFalse(this.encoder.canEncode(org.springframework.core.ResolvableType.forClass(getClass()), MediaType.APPLICATION_XML));
        // SPR-15464
        Assert.assertFalse(this.encoder.canEncode(ResolvableType.NONE, null));
    }

    @Override
    @Test
    public void encode() {
        Mono<Pojo> input = Mono.just(new Pojo("foofoo", "barbar"));
        testEncode(input, Pojo.class, ( step) -> step.consumeNextWith(expectXml(("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" + "<pojo><bar>barbar</bar><foo>foofoo</foo></pojo>"))).verifyComplete());
    }

    @Test
    public void encodeError() {
        Flux<Pojo> input = Flux.error(RuntimeException::new);
        testEncode(input, Pojo.class, ( step) -> step.expectError(.class).verify());
    }

    @Test
    public void encodeElementsWithCommonType() {
        Mono<Jaxb2XmlEncoderTests.Container> input = Mono.just(new Jaxb2XmlEncoderTests.Container());
        testEncode(input, Pojo.class, ( step) -> step.consumeNextWith(expectXml(("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" + "<container><foo><name>name1</name></foo><bar><title>title1</title></bar></container>"))).verifyComplete());
    }

    public static class Model {}

    public static class Foo extends Jaxb2XmlEncoderTests.Model {
        private String name;

        public Foo(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Bar extends Jaxb2XmlEncoderTests.Model {
        private String title;

        public Bar(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    @XmlRootElement
    public static class Container {
        @XmlElements({ @XmlElement(name = "foo", type = Jaxb2XmlEncoderTests.Foo.class), @XmlElement(name = "bar", type = Jaxb2XmlEncoderTests.Bar.class) })
        public List<Jaxb2XmlEncoderTests.Model> getElements() {
            return Arrays.asList(new Jaxb2XmlEncoderTests.Foo("name1"), new Jaxb2XmlEncoderTests.Bar("title1"));
        }
    }
}

