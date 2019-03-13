/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.oxm.jaxb;


import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Collections;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.AbstractMarshallerTests;
import org.springframework.oxm.UncategorizedMappingException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.test.FlightType;
import org.springframework.oxm.jaxb.test.Flights;
import org.springframework.oxm.mime.MimeContainer;
import org.springframework.util.FileCopyUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xmlunit.diff.DifferenceEvaluator;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Biju Kunjummen
 * @author Sam Brannen
 */
public class Jaxb2MarshallerTests extends AbstractMarshallerTests<Jaxb2Marshaller> {
    private static final String CONTEXT_PATH = "org.springframework.oxm.jaxb.test";

    private Flights flights;

    @Test
    public void marshalSAXResult() throws Exception {
        ContentHandler contentHandler = Mockito.mock(ContentHandler.class);
        SAXResult result = new SAXResult(contentHandler);
        marshaller.marshal(flights, result);
        InOrder ordered = Mockito.inOrder(contentHandler);
        ordered.verify(contentHandler).setDocumentLocator(ArgumentMatchers.isA(Locator.class));
        ordered.verify(contentHandler).startDocument();
        ordered.verify(contentHandler).startPrefixMapping("", "http://samples.springframework.org/flight");
        ordered.verify(contentHandler).startElement(ArgumentMatchers.eq("http://samples.springframework.org/flight"), ArgumentMatchers.eq("flights"), ArgumentMatchers.eq("flights"), ArgumentMatchers.isA(Attributes.class));
        ordered.verify(contentHandler).startElement(ArgumentMatchers.eq("http://samples.springframework.org/flight"), ArgumentMatchers.eq("flight"), ArgumentMatchers.eq("flight"), ArgumentMatchers.isA(Attributes.class));
        ordered.verify(contentHandler).startElement(ArgumentMatchers.eq("http://samples.springframework.org/flight"), ArgumentMatchers.eq("number"), ArgumentMatchers.eq("number"), ArgumentMatchers.isA(Attributes.class));
        ordered.verify(contentHandler).characters(ArgumentMatchers.isA(char[].class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(2));
        ordered.verify(contentHandler).endElement("http://samples.springframework.org/flight", "number", "number");
        ordered.verify(contentHandler).endElement("http://samples.springframework.org/flight", "flight", "flight");
        ordered.verify(contentHandler).endElement("http://samples.springframework.org/flight", "flights", "flights");
        ordered.verify(contentHandler).endPrefixMapping("");
        ordered.verify(contentHandler).endDocument();
    }

    @Test
    public void lazyInit() throws Exception {
        marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(Jaxb2MarshallerTests.CONTEXT_PATH);
        marshaller.setLazyInit(true);
        marshaller.afterPropertiesSet();
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        marshaller.marshal(flights, result);
        DifferenceEvaluator ev = chain(Default, downgradeDifferencesToEqual(XML_STANDALONE));
        Assert.assertThat("Marshaller writes invalid StreamResult", writer.toString(), AbstractMarshallerTests.isSimilarTo(AbstractMarshallerTests.EXPECTED_STRING).withDifferenceEvaluator(ev));
    }

    @Test
    public void properties() throws Exception {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(Jaxb2MarshallerTests.CONTEXT_PATH);
        marshaller.setMarshallerProperties(Collections.<String, Object>singletonMap(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE));
        marshaller.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noContextPathOrClassesToBeBound() throws Exception {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.afterPropertiesSet();
    }

    @Test(expected = UncategorizedMappingException.class)
    public void testInvalidContextPath() throws Exception {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("ab");
        marshaller.afterPropertiesSet();
    }

    @Test(expected = XmlMappingException.class)
    public void marshalInvalidClass() throws Exception {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(FlightType.class);
        marshaller.afterPropertiesSet();
        Result result = new StreamResult(new StringWriter());
        Flights flights = new Flights();
        marshaller.marshal(flights, result);
    }

    @Test
    public void supportsContextPath() throws Exception {
        testSupports();
    }

    @Test
    public void supportsClassesToBeBound() throws Exception {
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Flights.class, FlightType.class);
        marshaller.afterPropertiesSet();
        testSupports();
    }

    @Test
    public void supportsPackagesToScan() throws Exception {
        marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan(Jaxb2MarshallerTests.CONTEXT_PATH);
        marshaller.afterPropertiesSet();
        testSupports();
    }

    @Test
    public void supportsXmlRootElement() throws Exception {
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Jaxb2MarshallerTests.DummyRootElement.class, Jaxb2MarshallerTests.DummyType.class);
        marshaller.afterPropertiesSet();
        Assert.assertTrue("Jaxb2Marshaller does not support XmlRootElement class", marshaller.supports(Jaxb2MarshallerTests.DummyRootElement.class));
        Assert.assertTrue("Jaxb2Marshaller does not support XmlRootElement generic type", marshaller.supports(((Type) (Jaxb2MarshallerTests.DummyRootElement.class))));
        Assert.assertFalse("Jaxb2Marshaller supports DummyType class", marshaller.supports(Jaxb2MarshallerTests.DummyType.class));
        Assert.assertFalse("Jaxb2Marshaller supports DummyType type", marshaller.supports(((Type) (Jaxb2MarshallerTests.DummyType.class))));
    }

    @Test
    public void marshalAttachments() throws Exception {
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(BinaryObject.class);
        marshaller.setMtomEnabled(true);
        marshaller.afterPropertiesSet();
        MimeContainer mimeContainer = Mockito.mock(MimeContainer.class);
        Resource logo = new ClassPathResource("spring-ws.png", getClass());
        DataHandler dataHandler = new DataHandler(new FileDataSource(logo.getFile()));
        BDDMockito.given(mimeContainer.convertToXopPackage()).willReturn(true);
        byte[] bytes = FileCopyUtils.copyToByteArray(logo.getInputStream());
        BinaryObject object = new BinaryObject(bytes, dataHandler);
        StringWriter writer = new StringWriter();
        marshaller.marshal(object, new StreamResult(writer), mimeContainer);
        Assert.assertTrue("No XML written", ((writer.toString().length()) > 0));
        Mockito.verify(mimeContainer, Mockito.times(3)).addAttachment(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(DataHandler.class));
    }

    // SPR-10714
    @Test
    public void marshalAWrappedObjectHoldingAnXmlElementDeclElement() throws Exception {
        marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("org.springframework.oxm.jaxb");
        marshaller.afterPropertiesSet();
        Airplane airplane = new Airplane();
        airplane.setName("test");
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        marshaller.marshal(airplane, result);
        DifferenceEvaluator ev = chain(Default, downgradeDifferencesToEqual(XML_STANDALONE));
        Assert.assertThat("Marshalling should use root Element", writer.toString(), AbstractMarshallerTests.isSimilarTo("<airplane><name>test</name></airplane>").withDifferenceEvaluator(ev));
    }

    // SPR-10806
    @Test
    public void unmarshalStreamSourceWithXmlOptions() throws Exception {
        final Unmarshaller unmarshaller = Mockito.mock(Unmarshaller.class);
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller() {
            @Override
            protected Unmarshaller createUnmarshaller() {
                return unmarshaller;
            }
        };
        // 1. external-general-entities and dtd support disabled (default)
        marshaller.unmarshal(new StreamSource("1"));
        ArgumentCaptor<SAXSource> sourceCaptor = ArgumentCaptor.forClass(SAXSource.class);
        Mockito.verify(unmarshaller).unmarshal(sourceCaptor.capture());
        SAXSource result = sourceCaptor.getValue();
        Assert.assertEquals(true, result.getXMLReader().getFeature("http://apache.org/xml/features/disallow-doctype-decl"));
        Assert.assertEquals(false, result.getXMLReader().getFeature("http://xml.org/sax/features/external-general-entities"));
        // 2. external-general-entities and dtd support enabled
        Mockito.reset(unmarshaller);
        marshaller.setProcessExternalEntities(true);
        marshaller.setSupportDtd(true);
        marshaller.unmarshal(new StreamSource("1"));
        Mockito.verify(unmarshaller).unmarshal(sourceCaptor.capture());
        result = sourceCaptor.getValue();
        Assert.assertEquals(false, result.getXMLReader().getFeature("http://apache.org/xml/features/disallow-doctype-decl"));
        Assert.assertEquals(true, result.getXMLReader().getFeature("http://xml.org/sax/features/external-general-entities"));
    }

    // SPR-10806
    @Test
    public void unmarshalSaxSourceWithXmlOptions() throws Exception {
        final Unmarshaller unmarshaller = Mockito.mock(Unmarshaller.class);
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller() {
            @Override
            protected Unmarshaller createUnmarshaller() {
                return unmarshaller;
            }
        };
        // 1. external-general-entities and dtd support disabled (default)
        marshaller.unmarshal(new SAXSource(new InputSource("1")));
        ArgumentCaptor<SAXSource> sourceCaptor = ArgumentCaptor.forClass(SAXSource.class);
        Mockito.verify(unmarshaller).unmarshal(sourceCaptor.capture());
        SAXSource result = sourceCaptor.getValue();
        Assert.assertEquals(true, result.getXMLReader().getFeature("http://apache.org/xml/features/disallow-doctype-decl"));
        Assert.assertEquals(false, result.getXMLReader().getFeature("http://xml.org/sax/features/external-general-entities"));
        // 2. external-general-entities and dtd support enabled
        Mockito.reset(unmarshaller);
        marshaller.setProcessExternalEntities(true);
        marshaller.setSupportDtd(true);
        marshaller.unmarshal(new SAXSource(new InputSource("1")));
        Mockito.verify(unmarshaller).unmarshal(sourceCaptor.capture());
        result = sourceCaptor.getValue();
        Assert.assertEquals(false, result.getXMLReader().getFeature("http://apache.org/xml/features/disallow-doctype-decl"));
        Assert.assertEquals(true, result.getXMLReader().getFeature("http://xml.org/sax/features/external-general-entities"));
    }

    @XmlRootElement
    @SuppressWarnings("unused")
    public static class DummyRootElement {
        private Jaxb2MarshallerTests.DummyType t = new Jaxb2MarshallerTests.DummyType();
    }

    @XmlType
    @SuppressWarnings("unused")
    public static class DummyType {
        private String s = "Hello";
    }
}

