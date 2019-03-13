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
package org.springframework.oxm.xstream;


import java.io.ByteArrayInputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.junit.Test;
import org.springframework.util.xml.StaxUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class XStreamUnmarshallerTests {
    protected static final String INPUT_STRING = "<flight><flightNumber>42</flightNumber></flight>";

    private XStreamMarshaller unmarshaller;

    @Test
    public void unmarshalDomSource() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(XStreamUnmarshallerTests.INPUT_STRING)));
        DOMSource source = new DOMSource(document);
        Object flight = unmarshaller.unmarshal(source);
        testFlight(flight);
    }

    @Test
    public void unmarshalStaxSourceXmlStreamReader() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader streamReader = inputFactory.createXMLStreamReader(new StringReader(XStreamUnmarshallerTests.INPUT_STRING));
        Source source = StaxUtils.createStaxSource(streamReader);
        Object flights = unmarshaller.unmarshal(source);
        testFlight(flights);
    }

    @Test
    public void unmarshalStreamSourceInputStream() throws Exception {
        StreamSource source = new StreamSource(new ByteArrayInputStream(XStreamUnmarshallerTests.INPUT_STRING.getBytes("UTF-8")));
        Object flights = unmarshaller.unmarshal(source);
        testFlight(flights);
    }

    @Test
    public void unmarshalStreamSourceReader() throws Exception {
        StreamSource source = new StreamSource(new StringReader(XStreamUnmarshallerTests.INPUT_STRING));
        Object flights = unmarshaller.unmarshal(source);
        testFlight(flights);
    }
}

