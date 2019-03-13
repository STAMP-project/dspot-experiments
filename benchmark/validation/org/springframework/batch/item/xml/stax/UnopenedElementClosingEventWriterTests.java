/**
 * Copyright 2014 the original author or authors.
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
package org.springframework.batch.item.xml.stax;


import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessResourceFailureException;


/**
 * Tests for {@link UnopenedElementClosingEventWriter}
 *
 * @author Jimmy Praet
 */
public class UnopenedElementClosingEventWriterTests {
    private UnopenedElementClosingEventWriter writer;

    private XMLEventWriter wrappedWriter;

    private Writer ioWriter;

    private XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private List<QName> unopenedElements = new LinkedList<>();

    private QName unopenedA = new QName("http://test", "unopened-a", "t");

    private QName unopenedB = new QName("", "unopened-b", "");

    private QName other = new QName("http://test", "other", "t");

    @Test
    public void testEndUnopenedElements() throws Exception {
        EndElement endElementB = eventFactory.createEndElement(unopenedB, null);
        writer.add(endElementB);
        EndElement endElementA = eventFactory.createEndElement(unopenedA, null);
        writer.add(endElementA);
        Mockito.verify(wrappedWriter, Mockito.never()).add(endElementB);
        Mockito.verify(wrappedWriter, Mockito.never()).add(endElementA);
        Mockito.verify(wrappedWriter, Mockito.times(2)).flush();
        Mockito.verify(ioWriter).write("</unopened-b>");
        Mockito.verify(ioWriter).write("</t:unopened-a>");
        Mockito.verify(ioWriter, Mockito.times(2)).flush();
    }

    @Test
    public void testEndUnopenedElementRemovesFromList() throws Exception {
        EndElement endElement = eventFactory.createEndElement(unopenedB, null);
        writer.add(endElement);
        Mockito.verify(wrappedWriter, Mockito.never()).add(endElement);
        Mockito.verify(wrappedWriter).flush();
        Mockito.verify(ioWriter).write("</unopened-b>");
        Mockito.verify(ioWriter).flush();
        StartElement startElement = eventFactory.createStartElement(unopenedB, null, null);
        writer.add(startElement);
        endElement = eventFactory.createEndElement(unopenedB, null);
        writer.add(endElement);
        Mockito.verify(wrappedWriter).add(startElement);
        Mockito.verify(wrappedWriter).add(endElement);
        // only internal list should be modified
        Assert.assertEquals(2, unopenedElements.size());
    }

    @Test
    public void testOtherEndElement() throws Exception {
        EndElement endElement = eventFactory.createEndElement(other, null);
        writer.add(endElement);
        Mockito.verify(wrappedWriter).add(endElement);
    }

    @Test
    public void testOtherEvent() throws Exception {
        XMLEvent event = eventFactory.createCharacters("foo");
        writer.add(event);
        Mockito.verify(wrappedWriter).add(event);
    }

    @Test(expected = DataAccessResourceFailureException.class)
    public void testIOException() throws Exception {
        EndElement endElementB = eventFactory.createEndElement(unopenedB, null);
        Mockito.doThrow(new IOException("Simulated IOException")).when(ioWriter).write("</unopened-b>");
        writer.add(endElementB);
    }
}

