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


import java.util.Collections;
import javax.xml.stream.events.XMLEvent;
import org.junit.Test;
import org.springframework.core.io.buffer.AbstractLeakCheckingTestCase;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class XmlEventDecoderTests extends AbstractLeakCheckingTestCase {
    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((("<pojo>" + "<foo>foofoo</foo>") + "<bar>barbar</bar>") + "</pojo>");

    private XmlEventDecoder decoder = new XmlEventDecoder();

    @Test
    public void toXMLEventsAalto() {
        Flux<XMLEvent> events = this.decoder.decode(stringBuffer(XmlEventDecoderTests.XML), null, null, Collections.emptyMap());
        StepVerifier.create(events).consumeNextWith(( e) -> assertTrue(e.isStartDocument())).consumeNextWith(( e) -> assertStartElement(e, "pojo")).consumeNextWith(( e) -> assertStartElement(e, "foo")).consumeNextWith(( e) -> assertCharacters(e, "foofoo")).consumeNextWith(( e) -> assertEndElement(e, "foo")).consumeNextWith(( e) -> assertStartElement(e, "bar")).consumeNextWith(( e) -> assertCharacters(e, "barbar")).consumeNextWith(( e) -> assertEndElement(e, "bar")).consumeNextWith(( e) -> assertEndElement(e, "pojo")).expectComplete().verify();
    }

    @Test
    public void toXMLEventsNonAalto() {
        decoder.useAalto = false;
        Flux<XMLEvent> events = this.decoder.decode(stringBuffer(XmlEventDecoderTests.XML), null, null, Collections.emptyMap());
        StepVerifier.create(events).consumeNextWith(( e) -> assertTrue(e.isStartDocument())).consumeNextWith(( e) -> assertStartElement(e, "pojo")).consumeNextWith(( e) -> assertStartElement(e, "foo")).consumeNextWith(( e) -> assertCharacters(e, "foofoo")).consumeNextWith(( e) -> assertEndElement(e, "foo")).consumeNextWith(( e) -> assertStartElement(e, "bar")).consumeNextWith(( e) -> assertCharacters(e, "barbar")).consumeNextWith(( e) -> assertEndElement(e, "bar")).consumeNextWith(( e) -> assertEndElement(e, "pojo")).consumeNextWith(( e) -> assertTrue(e.isEndDocument())).expectComplete().verify();
    }

    @Test
    public void decodeErrorAalto() {
        Flux<DataBuffer> source = Flux.concat(stringBuffer("<pojo>"), Flux.error(new RuntimeException()));
        Flux<XMLEvent> events = this.decoder.decode(source, null, null, Collections.emptyMap());
        StepVerifier.create(events).consumeNextWith(( e) -> assertTrue(e.isStartDocument())).consumeNextWith(( e) -> assertStartElement(e, "pojo")).expectError(RuntimeException.class).verify();
    }

    @Test
    public void decodeErrorNonAalto() {
        decoder.useAalto = false;
        Flux<DataBuffer> source = Flux.concat(stringBuffer("<pojo>"), Flux.error(new RuntimeException()));
        Flux<XMLEvent> events = this.decoder.decode(source, null, null, Collections.emptyMap());
        StepVerifier.create(events).expectError(RuntimeException.class).verify();
    }
}

