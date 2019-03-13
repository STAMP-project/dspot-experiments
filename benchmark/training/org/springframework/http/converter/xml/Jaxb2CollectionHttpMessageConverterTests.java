/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.http.converter.xml;


import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLInputFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;


/**
 * Test fixture for {@link Jaxb2CollectionHttpMessageConverter}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class Jaxb2CollectionHttpMessageConverterTests {
    private Jaxb2CollectionHttpMessageConverter<?> converter;

    private Type rootElementListType;

    private Type rootElementSetType;

    private Type typeListType;

    private Type typeSetType;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canRead() {
        Assert.assertTrue(converter.canRead(rootElementListType, null, null));
        Assert.assertTrue(converter.canRead(rootElementSetType, null, null));
        Assert.assertTrue(converter.canRead(typeSetType, null, null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readXmlRootElementList() throws Exception {
        String content = "<list><rootElement><type s=\"1\"/></rootElement><rootElement><type s=\"2\"/></rootElement></list>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        List<Jaxb2CollectionHttpMessageConverterTests.RootElement> result = ((List<Jaxb2CollectionHttpMessageConverterTests.RootElement>) (converter.read(rootElementListType, null, inputMessage)));
        Assert.assertEquals("Invalid result", 2, result.size());
        Assert.assertEquals("Invalid result", "1", result.get(0).type.s);
        Assert.assertEquals("Invalid result", "2", result.get(1).type.s);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readXmlRootElementSet() throws Exception {
        String content = "<set><rootElement><type s=\"1\"/></rootElement><rootElement><type s=\"2\"/></rootElement></set>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        Set<Jaxb2CollectionHttpMessageConverterTests.RootElement> result = ((Set<Jaxb2CollectionHttpMessageConverterTests.RootElement>) (converter.read(rootElementSetType, null, inputMessage)));
        Assert.assertEquals("Invalid result", 2, result.size());
        Assert.assertTrue("Invalid result", result.contains(new Jaxb2CollectionHttpMessageConverterTests.RootElement("1")));
        Assert.assertTrue("Invalid result", result.contains(new Jaxb2CollectionHttpMessageConverterTests.RootElement("2")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readXmlTypeList() throws Exception {
        String content = "<list><foo s=\"1\"/><bar s=\"2\"/></list>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        List<Jaxb2CollectionHttpMessageConverterTests.TestType> result = ((List<Jaxb2CollectionHttpMessageConverterTests.TestType>) (converter.read(typeListType, null, inputMessage)));
        Assert.assertEquals("Invalid result", 2, result.size());
        Assert.assertEquals("Invalid result", "1", result.get(0).s);
        Assert.assertEquals("Invalid result", "2", result.get(1).s);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readXmlTypeSet() throws Exception {
        String content = "<set><foo s=\"1\"/><bar s=\"2\"/></set>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        Set<Jaxb2CollectionHttpMessageConverterTests.TestType> result = ((Set<Jaxb2CollectionHttpMessageConverterTests.TestType>) (converter.read(typeSetType, null, inputMessage)));
        Assert.assertEquals("Invalid result", 2, result.size());
        Assert.assertTrue("Invalid result", result.contains(new Jaxb2CollectionHttpMessageConverterTests.TestType("1")));
        Assert.assertTrue("Invalid result", result.contains(new Jaxb2CollectionHttpMessageConverterTests.TestType("2")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readXmlRootElementExternalEntityDisabled() throws Exception {
        Resource external = new ClassPathResource("external.txt", getClass());
        String content = ((("<!DOCTYPE root [" + ("  <!ELEMENT external ANY >\n" + "  <!ENTITY ext SYSTEM \"")) + (external.getURI())) + "\" >]>") + "  <list><rootElement><type s=\"1\"/><external>&ext;</external></rootElement></list>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        converter = new Jaxb2CollectionHttpMessageConverter<java.util.Collection<Object>>() {
            @Override
            protected XMLInputFactory createXmlInputFactory() {
                XMLInputFactory inputFactory = super.createXmlInputFactory();
                inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, true);
                return inputFactory;
            }
        };
        try {
            java.util.Collection<Jaxb2CollectionHttpMessageConverterTests.RootElement> result = converter.read(rootElementListType, null, inputMessage);
            Assert.assertEquals(1, result.size());
            Assert.assertEquals("", result.iterator().next().external);
        } catch (HttpMessageNotReadableException ex) {
            // Some parsers raise an exception
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readXmlRootElementExternalEntityEnabled() throws Exception {
        Resource external = new ClassPathResource("external.txt", getClass());
        String content = ((("<!DOCTYPE root [" + ("  <!ELEMENT external ANY >\n" + "  <!ENTITY ext SYSTEM \"")) + (external.getURI())) + "\" >]>") + "  <list><rootElement><type s=\"1\"/><external>&ext;</external></rootElement></list>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        Jaxb2CollectionHttpMessageConverter<?> c = new Jaxb2CollectionHttpMessageConverter<java.util.Collection<Object>>() {
            @Override
            protected XMLInputFactory createXmlInputFactory() {
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true);
                return inputFactory;
            }
        };
        java.util.Collection<Jaxb2CollectionHttpMessageConverterTests.RootElement> result = c.read(rootElementListType, null, inputMessage);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("Foo Bar", result.iterator().next().external);
    }

    @Test
    public void testXmlBomb() throws Exception {
        // https://en.wikipedia.org/wiki/Billion_laughs
        // https://msdn.microsoft.com/en-us/magazine/ee335713.aspx
        String content = "<?xml version=\"1.0\"?>\n" + ((((((((((((("<!DOCTYPE lolz [\n" + " <!ENTITY lol \"lol\">\n") + " <!ELEMENT lolz (#PCDATA)>\n") + " <!ENTITY lol1 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">\n") + " <!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">\n") + " <!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">\n") + " <!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">\n") + " <!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">\n") + " <!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">\n") + " <!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">\n") + " <!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">\n") + " <!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">\n") + "]>\n") + "<list><rootElement><external>&lol9;</external></rootElement></list>");
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        this.thrown.expect(HttpMessageNotReadableException.class);
        this.thrown.expectMessage("\"lol9\"");
        this.converter.read(this.rootElementListType, null, inputMessage);
    }

    @XmlRootElement
    public static class RootElement {
        public RootElement() {
        }

        public RootElement(String s) {
            this.type = new Jaxb2CollectionHttpMessageConverterTests.TestType(s);
        }

        @XmlElement
        public Jaxb2CollectionHttpMessageConverterTests.TestType type = new Jaxb2CollectionHttpMessageConverterTests.TestType();

        @XmlElement(required = false)
        public String external;

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (o instanceof Jaxb2CollectionHttpMessageConverterTests.RootElement) {
                Jaxb2CollectionHttpMessageConverterTests.RootElement other = ((Jaxb2CollectionHttpMessageConverterTests.RootElement) (o));
                return this.type.equals(other.type);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }
    }

    @XmlType
    public static class TestType {
        public TestType() {
        }

        public TestType(String s) {
            this.s = s;
        }

        @XmlAttribute
        public String s = "Hello World";

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (o instanceof Jaxb2CollectionHttpMessageConverterTests.TestType) {
                Jaxb2CollectionHttpMessageConverterTests.TestType other = ((Jaxb2CollectionHttpMessageConverterTests.TestType) (o));
                return this.s.equals(other.s);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return s.hashCode();
        }
    }
}

