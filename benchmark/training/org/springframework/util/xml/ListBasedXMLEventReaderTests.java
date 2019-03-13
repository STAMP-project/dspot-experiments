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
package org.springframework.util.xml;


import java.io.StringWriter;
import java.util.List;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Andrzej Ho?owko
 */
public class ListBasedXMLEventReaderTests {
    private final XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    private final XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();

    @Test
    public void standard() throws Exception {
        String xml = "<foo><bar>baz</bar></foo>";
        List<XMLEvent> events = readEvents(xml);
        ListBasedXMLEventReader reader = new ListBasedXMLEventReader(events);
        StringWriter resultWriter = new StringWriter();
        XMLEventWriter writer = this.outputFactory.createXMLEventWriter(resultWriter);
        writer.add(reader);
        Assert.assertThat(resultWriter.toString(), isSimilarTo(xml));
    }

    @Test
    public void testGetElementText() throws Exception {
        String xml = "<foo><bar>baz</bar></foo>";
        List<XMLEvent> events = readEvents(xml);
        ListBasedXMLEventReader reader = new ListBasedXMLEventReader(events);
        Assert.assertEquals(XMLStreamConstants.START_DOCUMENT, reader.nextEvent().getEventType());
        Assert.assertEquals(XMLStreamConstants.START_ELEMENT, reader.nextEvent().getEventType());
        Assert.assertEquals(XMLStreamConstants.START_ELEMENT, reader.nextEvent().getEventType());
        Assert.assertEquals("baz", reader.getElementText());
        Assert.assertEquals(XMLStreamConstants.END_ELEMENT, reader.nextEvent().getEventType());
        Assert.assertEquals(XMLStreamConstants.END_DOCUMENT, reader.nextEvent().getEventType());
    }

    @Test
    public void testGetElementTextThrowsExceptionAtWrongPosition() throws Exception {
        String xml = "<foo><bar>baz</bar></foo>";
        List<XMLEvent> events = readEvents(xml);
        ListBasedXMLEventReader reader = new ListBasedXMLEventReader(events);
        Assert.assertEquals(XMLStreamConstants.START_DOCUMENT, reader.nextEvent().getEventType());
        try {
            reader.getElementText();
            Assert.fail("Should have thrown XMLStreamException");
        } catch (XMLStreamException ex) {
            // expected
            Assert.assertTrue(ex.getMessage().startsWith("Not at START_ELEMENT"));
        }
    }
}

