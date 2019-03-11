/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.xml;


import io.netty.channel.embedded.EmbeddedChannel;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.Test;


/**
 * Verifies the basic functionality of the {@link XmlDecoder}.
 * XML borrowed from <a href="http://www.studytrails.com/java/xml/woodstox/java-xml-woodstox-validation-xml-schema.jsp">
 * Woodstox : Validate against XML Schema</a>
 */
public class XmlDecoderTest {
    private static final String XML1 = "<?xml version=\"1.0\"?>" + ((((("<!DOCTYPE employee SYSTEM \"employee.dtd\">" + "<?xml-stylesheet type=\"text/css\" href=\"netty.css\"?>") + "<?xml-test ?>") + "<employee xmlns:nettya=\"http://netty.io/netty/a\">") + "<nettya:id>&plusmn;1</nettya:id>\n") + "<name ");

    private static final String XML2 = "type=\"given\">Alba</name><![CDATA[ <some data &gt;/> ]]>" + ("   <!-- namespaced --><nettyb:salary xmlns:nettyb=\"http://netty.io/netty/b\" nettyb:period=\"weekly\">" + "100</nettyb:salary><last/></employee>");

    private static final String XML3 = "<?xml version=\"1.1\" encoding=\"UTf-8\" standalone=\"yes\"?><netty></netty>";

    private static final String XML4 = "<netty></netty>";

    private EmbeddedChannel channel;

    /**
     * This test feeds basic XML and verifies the resulting messages
     */
    @Test
    public void shouldDecodeRequestWithSimpleXml() {
        Object temp;
        write(XmlDecoderTest.XML1);
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlDocumentStart.class));
        MatcherAssert.assertThat(version(), CoreMatchers.is("1.0"));
        MatcherAssert.assertThat(encoding(), CoreMatchers.is("UTF-8"));
        MatcherAssert.assertThat(standalone(), CoreMatchers.is(false));
        MatcherAssert.assertThat(encodingScheme(), CoreMatchers.is(IsNull.nullValue()));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlDTD.class));
        MatcherAssert.assertThat(text(), CoreMatchers.is("employee.dtd"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlProcessingInstruction.class));
        MatcherAssert.assertThat(target(), CoreMatchers.is("xml-stylesheet"));
        MatcherAssert.assertThat(data(), CoreMatchers.is("type=\"text/css\" href=\"netty.css\""));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlProcessingInstruction.class));
        MatcherAssert.assertThat(target(), CoreMatchers.is("xml-test"));
        MatcherAssert.assertThat(data(), CoreMatchers.is(""));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementStart.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("employee"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(attributes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is("nettya"));
        MatcherAssert.assertThat(namespaces().get(0).uri(), CoreMatchers.is("http://netty.io/netty/a"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementStart.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("id"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is("nettya"));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is("http://netty.io/netty/a"));
        MatcherAssert.assertThat(attributes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(0));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlEntityReference.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("plusmn"));
        MatcherAssert.assertThat(text(), CoreMatchers.is(""));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlCharacters.class));
        MatcherAssert.assertThat(data(), CoreMatchers.is("1"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementEnd.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("id"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is("nettya"));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is("http://netty.io/netty/a"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlCharacters.class));
        MatcherAssert.assertThat(data(), CoreMatchers.is("\n"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, IsNull.nullValue());
        write(XmlDecoderTest.XML2);
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementStart.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("name"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(attributes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(name(), CoreMatchers.is("type"));
        MatcherAssert.assertThat(attributes().get(0).value(), CoreMatchers.is("given"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(0));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlCharacters.class));
        MatcherAssert.assertThat(data(), CoreMatchers.is("Alba"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementEnd.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("name"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlCdata.class));
        MatcherAssert.assertThat(data(), CoreMatchers.is(" <some data &gt;/> "));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlCharacters.class));
        MatcherAssert.assertThat(data(), CoreMatchers.is("   "));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlComment.class));
        MatcherAssert.assertThat(data(), CoreMatchers.is(" namespaced "));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementStart.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("salary"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is("nettyb"));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is("http://netty.io/netty/b"));
        MatcherAssert.assertThat(attributes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(name(), CoreMatchers.is("period"));
        MatcherAssert.assertThat(attributes().get(0).value(), CoreMatchers.is("weekly"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is("nettyb"));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is("http://netty.io/netty/b"));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is("nettyb"));
        MatcherAssert.assertThat(namespaces().get(0).uri(), CoreMatchers.is("http://netty.io/netty/b"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlCharacters.class));
        MatcherAssert.assertThat(data(), CoreMatchers.is("100"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementEnd.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("salary"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is("nettyb"));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is("http://netty.io/netty/b"));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is("nettyb"));
        MatcherAssert.assertThat(namespaces().get(0).uri(), CoreMatchers.is("http://netty.io/netty/b"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementStart.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("last"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(attributes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(0));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementEnd.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("last"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(0));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementEnd.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("employee"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is("nettya"));
        MatcherAssert.assertThat(namespaces().get(0).uri(), CoreMatchers.is("http://netty.io/netty/a"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, IsNull.nullValue());
    }

    /**
     * This test checks for different attributes in XML header
     */
    @Test
    public void shouldDecodeXmlHeader() {
        Object temp;
        write(XmlDecoderTest.XML3);
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlDocumentStart.class));
        MatcherAssert.assertThat(version(), CoreMatchers.is("1.1"));
        MatcherAssert.assertThat(encoding(), CoreMatchers.is("UTF-8"));
        MatcherAssert.assertThat(standalone(), CoreMatchers.is(true));
        MatcherAssert.assertThat(encodingScheme(), CoreMatchers.is("UTF-8"));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementStart.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("netty"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(attributes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(0));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementEnd.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("netty"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(0));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, IsNull.nullValue());
    }

    /**
     * This test checks for no XML header
     */
    @Test
    public void shouldDecodeWithoutHeader() {
        Object temp;
        write(XmlDecoderTest.XML4);
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlDocumentStart.class));
        MatcherAssert.assertThat(version(), CoreMatchers.is(IsNull.nullValue()));
        MatcherAssert.assertThat(encoding(), CoreMatchers.is("UTF-8"));
        MatcherAssert.assertThat(standalone(), CoreMatchers.is(false));
        MatcherAssert.assertThat(encodingScheme(), CoreMatchers.is(IsNull.nullValue()));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementStart.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("netty"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(attributes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(0));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, CoreMatchers.instanceOf(XmlElementEnd.class));
        MatcherAssert.assertThat(name(), CoreMatchers.is("netty"));
        MatcherAssert.assertThat(prefix(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespace(), CoreMatchers.is(""));
        MatcherAssert.assertThat(namespaces().size(), CoreMatchers.is(0));
        temp = channel.readInbound();
        MatcherAssert.assertThat(temp, IsNull.nullValue());
    }
}

