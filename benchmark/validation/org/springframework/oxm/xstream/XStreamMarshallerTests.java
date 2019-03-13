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
package org.springframework.oxm.xstream;


import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.util.xml.StaxUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import static com.thoughtworks.xstream.io.json.JsonWriter.Format.COMPACT_EMPTY_ELEMENT;
import static com.thoughtworks.xstream.io.json.JsonWriter.Format.SPACE_AFTER_LABEL;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Sam Brannen
 */
public class XStreamMarshallerTests {
    private static final String EXPECTED_STRING = "<flight><flightNumber>42</flightNumber></flight>";

    private XStreamMarshaller marshaller;

    private Flight flight;

    @Test
    public void marshalDOMResult() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.newDocument();
        DOMResult domResult = new DOMResult(document);
        marshaller.marshal(flight, domResult);
        Document expected = builder.newDocument();
        Element flightElement = expected.createElement("flight");
        expected.appendChild(flightElement);
        Element numberElement = expected.createElement("flightNumber");
        flightElement.appendChild(numberElement);
        Text text = expected.createTextNode("42");
        numberElement.appendChild(text);
        Assert.assertThat("Marshaller writes invalid DOMResult", document, isSimilarTo(expected));
    }

    // see SWS-392
    @Test
    public void marshalDOMResultToExistentDocument() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document existent = builder.newDocument();
        Element rootElement = existent.createElement("root");
        Element flightsElement = existent.createElement("flights");
        rootElement.appendChild(flightsElement);
        existent.appendChild(rootElement);
        // marshall into the existent document
        DOMResult domResult = new DOMResult(flightsElement);
        marshaller.marshal(flight, domResult);
        Document expected = builder.newDocument();
        Element eRootElement = expected.createElement("root");
        Element eFlightsElement = expected.createElement("flights");
        Element eFlightElement = expected.createElement("flight");
        eRootElement.appendChild(eFlightsElement);
        eFlightsElement.appendChild(eFlightElement);
        expected.appendChild(eRootElement);
        Element eNumberElement = expected.createElement("flightNumber");
        eFlightElement.appendChild(eNumberElement);
        Text text = expected.createTextNode("42");
        eNumberElement.appendChild(text);
        Assert.assertThat("Marshaller writes invalid DOMResult", existent, isSimilarTo(expected));
    }

    @Test
    public void marshalStreamResultWriter() throws Exception {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        marshaller.marshal(flight, result);
        Assert.assertThat("Marshaller writes invalid StreamResult", writer.toString(), isSimilarTo(XStreamMarshallerTests.EXPECTED_STRING));
    }

    @Test
    public void marshalStreamResultOutputStream() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(os);
        marshaller.marshal(flight, result);
        String s = new String(os.toByteArray(), "UTF-8");
        Assert.assertThat("Marshaller writes invalid StreamResult", s, isSimilarTo(XStreamMarshallerTests.EXPECTED_STRING));
    }

    @Test
    public void marshalSaxResult() throws Exception {
        ContentHandler contentHandler = Mockito.mock(ContentHandler.class);
        SAXResult result = new SAXResult(contentHandler);
        marshaller.marshal(flight, result);
        InOrder ordered = Mockito.inOrder(contentHandler);
        ordered.verify(contentHandler).startDocument();
        ordered.verify(contentHandler).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("flight"), ArgumentMatchers.eq("flight"), ArgumentMatchers.isA(Attributes.class));
        ordered.verify(contentHandler).startElement(ArgumentMatchers.eq(""), ArgumentMatchers.eq("flightNumber"), ArgumentMatchers.eq("flightNumber"), ArgumentMatchers.isA(Attributes.class));
        ordered.verify(contentHandler).characters(ArgumentMatchers.isA(char[].class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(2));
        ordered.verify(contentHandler).endElement("", "flightNumber", "flightNumber");
        ordered.verify(contentHandler).endElement("", "flight", "flight");
        ordered.verify(contentHandler).endDocument();
    }

    @Test
    public void marshalStaxResultXMLStreamWriter() throws Exception {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        StringWriter writer = new StringWriter();
        XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(writer);
        Result result = StaxUtils.createStaxResult(streamWriter);
        marshaller.marshal(flight, result);
        Assert.assertThat("Marshaller writes invalid StreamResult", writer.toString(), isSimilarTo(XStreamMarshallerTests.EXPECTED_STRING));
    }

    @Test
    public void marshalStaxResultXMLEventWriter() throws Exception {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        StringWriter writer = new StringWriter();
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(writer);
        Result result = StaxUtils.createStaxResult(eventWriter);
        marshaller.marshal(flight, result);
        Assert.assertThat("Marshaller writes invalid StreamResult", writer.toString(), isSimilarTo(XStreamMarshallerTests.EXPECTED_STRING));
    }

    @Test
    public void converters() throws Exception {
        marshaller.setConverters(new EncodedByteArrayConverter());
        byte[] buf = new byte[]{ 1, 2 };
        Writer writer = new StringWriter();
        marshaller.marshal(buf, new StreamResult(writer));
        Assert.assertThat(writer.toString(), isSimilarTo("<byte-array>AQI=</byte-array>"));
        Reader reader = new StringReader(writer.toString());
        byte[] bufResult = ((byte[]) (marshaller.unmarshal(new StreamSource(reader))));
        Assert.assertTrue("Invalid result", Arrays.equals(buf, bufResult));
    }

    @Test
    public void useAttributesFor() throws Exception {
        marshaller.setUseAttributeForTypes(Long.TYPE);
        Writer writer = new StringWriter();
        marshaller.marshal(flight, new StreamResult(writer));
        String expected = "<flight flightNumber=\"42\" />";
        Assert.assertThat("Marshaller does not use attributes", writer.toString(), isSimilarTo(expected));
    }

    @Test
    public void useAttributesForStringClassMap() throws Exception {
        marshaller.setUseAttributeFor(Collections.singletonMap("flightNumber", Long.TYPE));
        Writer writer = new StringWriter();
        marshaller.marshal(flight, new StreamResult(writer));
        String expected = "<flight flightNumber=\"42\" />";
        Assert.assertThat("Marshaller does not use attributes", writer.toString(), isSimilarTo(expected));
    }

    @Test
    public void useAttributesForClassStringMap() throws Exception {
        marshaller.setUseAttributeFor(Collections.singletonMap(Flight.class, "flightNumber"));
        Writer writer = new StringWriter();
        marshaller.marshal(flight, new StreamResult(writer));
        String expected = "<flight flightNumber=\"42\" />";
        Assert.assertThat("Marshaller does not use attributes", writer.toString(), isSimilarTo(expected));
    }

    @Test
    public void useAttributesForClassStringListMap() throws Exception {
        marshaller.setUseAttributeFor(Collections.singletonMap(Flight.class, Collections.singletonList("flightNumber")));
        Writer writer = new StringWriter();
        marshaller.marshal(flight, new StreamResult(writer));
        String expected = "<flight flightNumber=\"42\" />";
        Assert.assertThat("Marshaller does not use attributes", writer.toString(), isSimilarTo(expected));
    }

    @Test
    public void fieldAliases() throws Exception {
        marshaller.setFieldAliases(Collections.singletonMap("org.springframework.oxm.xstream.Flight.flightNumber", "flightNo"));
        Writer writer = new StringWriter();
        marshaller.marshal(flight, new StreamResult(writer));
        String expected = "<flight><flightNo>42</flightNo></flight>";
        Assert.assertThat("Marshaller does not use aliases", writer.toString(), isSimilarTo(expected));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void omitFields() throws Exception {
        Map omittedFieldsMap = Collections.singletonMap(Flight.class, "flightNumber");
        marshaller.setOmittedFields(omittedFieldsMap);
        Writer writer = new StringWriter();
        marshaller.marshal(flight, new StreamResult(writer));
        XStreamMarshallerTests.assertXpathNotExists("/flight/flightNumber", writer.toString());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void implicitCollections() throws Exception {
        Flights flights = new Flights();
        flights.getFlights().add(flight);
        flights.getStrings().add("42");
        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("flight", Flight.class);
        aliases.put("flights", Flights.class);
        marshaller.setAliases(aliases);
        Map implicitCollections = Collections.singletonMap(Flights.class, "flights,strings");
        marshaller.setImplicitCollections(implicitCollections);
        Writer writer = new StringWriter();
        marshaller.marshal(flights, new StreamResult(writer));
        String result = writer.toString();
        XStreamMarshallerTests.assertXpathNotExists("/flights/flights", result);
        XStreamMarshallerTests.assertXpathExists("/flights/flight", result);
        XStreamMarshallerTests.assertXpathNotExists("/flights/strings", result);
        XStreamMarshallerTests.assertXpathExists("/flights/string", result);
    }

    @Test
    public void jettisonDriver() throws Exception {
        marshaller.setStreamDriver(new JettisonMappedXmlDriver());
        Writer writer = new StringWriter();
        marshaller.marshal(flight, new StreamResult(writer));
        Assert.assertEquals("Invalid result", "{\"flight\":{\"flightNumber\":42}}", writer.toString());
        Object o = marshaller.unmarshal(new StreamSource(new StringReader(writer.toString())));
        Assert.assertTrue("Unmarshalled object is not Flights", (o instanceof Flight));
        Flight unflight = ((Flight) (o));
        Assert.assertNotNull("Flight is null", unflight);
        Assert.assertEquals("Number is invalid", 42L, unflight.getFlightNumber());
    }

    @Test
    public void jsonDriver() throws Exception {
        marshaller.setStreamDriver(new JsonHierarchicalStreamDriver() {
            @Override
            public HierarchicalStreamWriter createWriter(Writer writer) {
                return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE, new JsonWriter.Format(new char[0], new char[0], ((SPACE_AFTER_LABEL) | (COMPACT_EMPTY_ELEMENT))));
            }
        });
        Writer writer = new StringWriter();
        marshaller.marshal(flight, new StreamResult(writer));
        Assert.assertEquals("Invalid result", "{\"flightNumber\": 42}", writer.toString());
    }

    @Test
    public void annotatedMarshalStreamResultWriter() throws Exception {
        marshaller.setAnnotatedClasses(Flight.class);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        Flight flight = new Flight();
        flight.setFlightNumber(42);
        marshaller.marshal(flight, result);
        String expected = "<flight><number>42</number></flight>";
        Assert.assertThat("Marshaller writes invalid StreamResult", writer.toString(), isSimilarTo(expected));
    }
}

