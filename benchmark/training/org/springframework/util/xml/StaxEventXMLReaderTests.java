/**
 * Copyright 2002-2016 the original author or authors.
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


import java.io.StringReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;


public class StaxEventXMLReaderTests extends AbstractStaxXMLReaderTestCase {
    public static final String CONTENT = "<root xmlns='http://springframework.org/spring-ws'><child/></root>";

    @Test
    public void partial() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(new StringReader(StaxEventXMLReaderTests.CONTENT));
        eventReader.nextTag();// skip to root

        StaxEventXMLReader xmlReader = new StaxEventXMLReader(eventReader);
        ContentHandler contentHandler = Mockito.mock(ContentHandler.class);
        xmlReader.setContentHandler(contentHandler);
        xmlReader.parse(new InputSource());
        Mockito.verify(contentHandler).startDocument();
        Mockito.verify(contentHandler).startElement(ArgumentMatchers.eq("http://springframework.org/spring-ws"), ArgumentMatchers.eq("child"), ArgumentMatchers.eq("child"), ArgumentMatchers.any(Attributes.class));
        Mockito.verify(contentHandler).endElement("http://springframework.org/spring-ws", "child", "child");
        Mockito.verify(contentHandler).endDocument();
    }
}

