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


import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.events.XMLEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link UnclosedElementCollectingEventWriter}
 *
 * @author Jimmy Praet
 */
public class UnclosedElementCollectingEventWriterTests {
    private UnclosedElementCollectingEventWriter writer;

    private XMLEventWriter wrappedWriter;

    private XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private QName elementA = new QName("elementA");

    private QName elementB = new QName("elementB");

    private QName elementC = new QName("elementC");

    @Test
    public void testNoUnclosedElements() throws Exception {
        writer.add(eventFactory.createStartElement(elementA, null, null));
        writer.add(eventFactory.createEndElement(elementA, null));
        Assert.assertEquals(0, writer.getUnclosedElements().size());
        Mockito.verify(wrappedWriter, Mockito.times(2)).add(Mockito.any(XMLEvent.class));
    }

    @Test
    public void testSingleUnclosedElement() throws Exception {
        writer.add(eventFactory.createStartElement(elementA, null, null));
        writer.add(eventFactory.createEndElement(elementA, null));
        writer.add(eventFactory.createStartElement(elementB, null, null));
        Assert.assertEquals(1, writer.getUnclosedElements().size());
        Assert.assertEquals(elementB, writer.getUnclosedElements().get(0));
        Mockito.verify(wrappedWriter, Mockito.times(3)).add(Mockito.any(XMLEvent.class));
    }

    @Test
    public void testMultipleUnclosedElements() throws Exception {
        writer.add(eventFactory.createStartElement(elementA, null, null));
        writer.add(eventFactory.createStartElement(elementB, null, null));
        writer.add(eventFactory.createStartElement(elementC, null, null));
        writer.add(eventFactory.createEndElement(elementC, null));
        Assert.assertEquals(2, writer.getUnclosedElements().size());
        Assert.assertEquals(elementA, writer.getUnclosedElements().get(0));
        Assert.assertEquals(elementB, writer.getUnclosedElements().get(1));
        Mockito.verify(wrappedWriter, Mockito.times(4)).add(Mockito.any(XMLEvent.class));
    }

    @Test
    public void testMultipleIdenticalUnclosedElement() throws Exception {
        writer.add(eventFactory.createStartElement(elementA, null, null));
        writer.add(eventFactory.createStartElement(elementA, null, null));
        Assert.assertEquals(2, writer.getUnclosedElements().size());
        Assert.assertEquals(elementA, writer.getUnclosedElements().get(0));
        Assert.assertEquals(elementA, writer.getUnclosedElements().get(1));
        Mockito.verify(wrappedWriter, Mockito.times(2)).add(Mockito.any(XMLEvent.class));
    }
}

