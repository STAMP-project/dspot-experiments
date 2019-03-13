/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.evtx;


import XmlRootNodeHandler.EVENTS;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class XmlRootNodeHandlerTest {
    @Mock
    XMLStreamWriter xmlStreamWriter;

    @Mock
    XmlBxmlNodeVisitorFactory xmlBxmlNodeVisitorFactory;

    XmlRootNodeHandler xmlRootNodeHandler;

    @Test
    public void testConstructor() throws XMLStreamException {
        Mockito.verify(xmlStreamWriter).writeStartDocument();
        Mockito.verify(xmlStreamWriter).writeStartElement(EVENTS);
    }

    @Test(expected = IOException.class)
    public void testConstructorException() throws IOException, XMLStreamException {
        xmlStreamWriter = Mockito.mock(XMLStreamWriter.class);
        Mockito.doThrow(new XMLStreamException()).when(xmlStreamWriter).writeStartElement(EVENTS);
        new XmlRootNodeHandler(xmlStreamWriter, xmlBxmlNodeVisitorFactory);
    }

    @Test
    public void testClose() throws IOException, XMLStreamException {
        xmlRootNodeHandler.close();
        Mockito.verify(xmlStreamWriter).writeEndElement();
        Mockito.verify(xmlStreamWriter).close();
    }

    @Test(expected = IOException.class)
    public void testCloseException() throws IOException, XMLStreamException {
        Mockito.doThrow(new XMLStreamException()).when(xmlStreamWriter).close();
        xmlRootNodeHandler.close();
    }
}

