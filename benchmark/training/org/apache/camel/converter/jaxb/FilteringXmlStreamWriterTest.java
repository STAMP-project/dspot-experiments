/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.converter.jaxb;


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FilteringXmlStreamWriterTest {
    private FilteringXmlStreamWriter filteringXmlStreamWriter;

    @Mock
    private NonXmlCharFilterer nonXmlCharFiltererMock;

    @Mock
    private XMLStreamWriter xmlStreamWriterMock;

    @Test
    public void testWriteAttribute2Args() throws XMLStreamException {
        filteringXmlStreamWriter.writeAttribute("localName", "value");
        Mockito.verify(xmlStreamWriterMock).writeAttribute("localName", "filteredValue");
    }

    @Test
    public void testWriteAttribute3Args() throws XMLStreamException {
        filteringXmlStreamWriter.writeAttribute("namespaceURI", "localName", "value");
        Mockito.verify(xmlStreamWriterMock).writeAttribute("namespaceURI", "localName", "filteredValue");
    }

    @Test
    public void testWriteAttribute4Args() throws XMLStreamException {
        filteringXmlStreamWriter.writeAttribute("prefix", "namespaceURI", "localName", "value");
        Mockito.verify(xmlStreamWriterMock).writeAttribute("prefix", "namespaceURI", "localName", "filteredValue");
    }

    @Test
    public void testWriteCData() throws XMLStreamException {
        filteringXmlStreamWriter.writeCData("value");
        Mockito.verify(xmlStreamWriterMock).writeCData("filteredValue");
    }

    @Test
    public void testWriteCharacters1Arg() throws XMLStreamException {
        filteringXmlStreamWriter.writeCharacters("value");
        Mockito.verify(xmlStreamWriterMock).writeCharacters("filteredValue");
    }

    @Test
    public void testWriteComment() throws XMLStreamException {
        filteringXmlStreamWriter.writeComment("value");
        Mockito.verify(xmlStreamWriterMock).writeComment("filteredValue");
    }

    @Test
    public void testWriteCharacters3Args() throws XMLStreamException {
        char[] buffer = new char[]{ 'a', 'b', 'c' };
        filteringXmlStreamWriter.writeCharacters(buffer, 2, 3);
        Mockito.verify(nonXmlCharFiltererMock).filter(ArgumentMatchers.same(buffer), ArgumentMatchers.eq(2), ArgumentMatchers.eq(3));
        Mockito.verify(xmlStreamWriterMock).writeCharacters(ArgumentMatchers.same(buffer), ArgumentMatchers.eq(2), ArgumentMatchers.eq(3));
    }
}

