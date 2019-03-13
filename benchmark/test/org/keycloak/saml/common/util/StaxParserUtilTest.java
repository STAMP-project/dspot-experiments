/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.saml.common.util;


import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.keycloak.saml.common.exceptions.ParsingException;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 *
 *
 * @author hmlnarik
 */
public class StaxParserUtilTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testBypassElementBlock() throws XMLStreamException, ParsingException {
        String xml = "<a><b><c>test</c>" + "<d>aa</d></b></a>";
        XMLEventReader reader = StaxParserUtil.getXMLEventReader(IOUtils.toInputStream(xml, Charset.defaultCharset()));
        Assert.assertThat(reader.nextEvent(), CoreMatchers.instanceOf(StartDocument.class));
        assertStartTag(reader.nextEvent(), "a");
        assertStartTag(reader.nextEvent(), "b");
        assertStartTag(reader.nextEvent(), "c");
        assertCharacters(reader.nextEvent(), CoreMatchers.is("test"));
        assertEndTag(reader.nextEvent(), "c");
        StaxParserUtil.bypassElementBlock(reader, "d");
        assertEndTag(reader.nextEvent(), "b");
        assertEndTag(reader.nextEvent(), "a");
    }

    @Test
    public void testBypassElementBlockAnon() throws XMLStreamException, ParsingException {
        String xml = "<a><b><c>test</c>" + "<d>aa</d></b></a>";
        XMLEventReader reader = StaxParserUtil.getXMLEventReader(IOUtils.toInputStream(xml, Charset.defaultCharset()));
        Assert.assertThat(reader.nextEvent(), CoreMatchers.instanceOf(StartDocument.class));
        assertStartTag(reader.nextEvent(), "a");
        assertStartTag(reader.nextEvent(), "b");
        assertStartTag(reader.nextEvent(), "c");
        assertCharacters(reader.nextEvent(), CoreMatchers.is("test"));
        assertEndTag(reader.nextEvent(), "c");
        StaxParserUtil.bypassElementBlock(reader);
        assertEndTag(reader.nextEvent(), "b");
        assertEndTag(reader.nextEvent(), "a");
    }

    @Test
    public void testBypassElementBlockNested() throws XMLStreamException, ParsingException {
        String xml = "<a><b><c>test</c>" + "<d>aa<d>nestedD</d></d></b></a>";
        XMLEventReader reader = StaxParserUtil.getXMLEventReader(IOUtils.toInputStream(xml, Charset.defaultCharset()));
        Assert.assertThat(reader.nextEvent(), CoreMatchers.instanceOf(StartDocument.class));
        assertStartTag(reader.nextEvent(), "a");
        assertStartTag(reader.nextEvent(), "b");
        assertStartTag(reader.nextEvent(), "c");
        assertCharacters(reader.nextEvent(), CoreMatchers.is("test"));
        assertEndTag(reader.nextEvent(), "c");
        StaxParserUtil.bypassElementBlock(reader, "d");
        assertEndTag(reader.nextEvent(), "b");
        assertEndTag(reader.nextEvent(), "a");
    }

    @Test
    public void testBypassElementBlockNestedAnon() throws XMLStreamException, ParsingException {
        String xml = "<a><b><c>test</c>" + "<d>aa<d>nestedD</d></d></b></a>";
        XMLEventReader reader = StaxParserUtil.getXMLEventReader(IOUtils.toInputStream(xml, Charset.defaultCharset()));
        Assert.assertThat(reader.nextEvent(), CoreMatchers.instanceOf(StartDocument.class));
        assertStartTag(reader.nextEvent(), "a");
        assertStartTag(reader.nextEvent(), "b");
        assertStartTag(reader.nextEvent(), "c");
        assertCharacters(reader.nextEvent(), CoreMatchers.is("test"));
        assertEndTag(reader.nextEvent(), "c");
        StaxParserUtil.bypassElementBlock(reader);
        assertEndTag(reader.nextEvent(), "b");
        assertEndTag(reader.nextEvent(), "a");
    }

    @Test
    public void testBypassElementBlockWrongPairing() throws XMLStreamException, ParsingException {
        String xml = "<a><b><c>test</c>" + "<d><b>aa</d><d>nestedD</d></d></b></a>";
        XMLEventReader reader = StaxParserUtil.getXMLEventReader(IOUtils.toInputStream(xml, Charset.defaultCharset()));
        Assert.assertThat(reader.nextEvent(), CoreMatchers.instanceOf(StartDocument.class));
        assertStartTag(reader.nextEvent(), "a");
        assertStartTag(reader.nextEvent(), "b");
        assertStartTag(reader.nextEvent(), "c");
        assertCharacters(reader.nextEvent(), CoreMatchers.is("test"));
        assertEndTag(reader.nextEvent(), "c");
        expectedException.expect(ParsingException.class);
        StaxParserUtil.bypassElementBlock(reader, "d");
    }

    @Test
    public void testBypassElementBlockNestedPrematureEnd() throws XMLStreamException, ParsingException {
        String xml = "<a><b><c>test</c>" + "<d>aa<d>nestedD</d></d>";
        XMLEventReader reader = StaxParserUtil.getXMLEventReader(IOUtils.toInputStream(xml, Charset.defaultCharset()));
        Assert.assertThat(reader.nextEvent(), CoreMatchers.instanceOf(StartDocument.class));
        assertStartTag(reader.nextEvent(), "a");
        assertStartTag(reader.nextEvent(), "b");
        assertStartTag(reader.nextEvent(), "c");
        assertCharacters(reader.nextEvent(), CoreMatchers.is("test"));
        assertEndTag(reader.nextEvent(), "c");
        StaxParserUtil.bypassElementBlock(reader, "d");
        expectedException.expect(XMLStreamException.class);
        reader.nextEvent();
    }

    @Test
    public void testGetDOMElementSameElements() throws XMLStreamException, ParsingException {
        String xml = "<root><test><test><a>b</a></test></test></root>";
        XMLEventReader reader = StaxParserUtil.getXMLEventReader(IOUtils.toInputStream(xml, Charset.defaultCharset()));
        Assert.assertThat(reader.nextEvent(), CoreMatchers.instanceOf(StartDocument.class));
        assertStartTag(reader.nextEvent(), "root");
        Element element = StaxParserUtil.getDOMElement(reader);
        Assert.assertThat(element.getNodeName(), CoreMatchers.is("test"));
        Assert.assertThat(element.getChildNodes().getLength(), CoreMatchers.is(1));
        Assert.assertThat(element.getChildNodes().item(0), CoreMatchers.instanceOf(Element.class));
        Element e = ((Element) (element.getChildNodes().item(0)));
        Assert.assertThat(e.getNodeName(), CoreMatchers.is("test"));
        Assert.assertThat(e.getChildNodes().getLength(), CoreMatchers.is(1));
        Assert.assertThat(e.getChildNodes().item(0), CoreMatchers.instanceOf(Element.class));
        Element e1 = ((Element) (e.getChildNodes().item(0)));
        Assert.assertThat(e1.getNodeName(), CoreMatchers.is("a"));
        Assert.assertThat(e1.getChildNodes().getLength(), CoreMatchers.is(1));
        Assert.assertThat(e1.getChildNodes().item(0), CoreMatchers.instanceOf(Text.class));
        Assert.assertThat(((Text) (e1.getChildNodes().item(0))).getWholeText(), CoreMatchers.is("b"));
        assertEndTag(reader.nextEvent(), "root");
        Assert.assertThat(reader.nextEvent(), CoreMatchers.instanceOf(EndDocument.class));
        expectedException.expect(NoSuchElementException.class);
        Assert.fail(String.valueOf(reader.nextEvent()));
    }
}

