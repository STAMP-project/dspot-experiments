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
package org.apache.camel.component.cxf.converter;


import java.io.IOException;
import java.io.StringReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.TypeConversionException;
import org.apache.camel.test.junit4.ExchangeTestSupport;
import org.apache.cxf.staxutils.StaxSource;
import org.apache.cxf.staxutils.StaxUtils;
import org.junit.Test;


public class CachedCxfPayloadTest extends ExchangeTestSupport {
    private static final String PAYLOAD = "<foo>bar<![CDATA[ & a cdata section ]]></foo>";

    private static final String PAYLOAD_AMPED = "<foo>bar &amp; a cdata section </foo>";

    @Test
    public void testCachedCxfPayloadSAXSource() throws IOException, NoTypeConversionAvailableException, TypeConversionException {
        SAXSource source = context.getTypeConverter().mandatoryConvertTo(SAXSource.class, CachedCxfPayloadTest.PAYLOAD);
        // this conversion uses org.apache.camel.converter.jaxp.XmlConverter.toDOMNodeFromSAX which uses Transformer
        // to convert SAXSource to DOM. This conversion preserves the content but loses its original representation.
        doTest(source, CachedCxfPayloadTest.PAYLOAD_AMPED);
    }

    @Test
    public void testCachedCxfPayloadStAXSource() throws IOException, NoTypeConversionAvailableException, TypeConversionException {
        StAXSource source = context.getTypeConverter().mandatoryConvertTo(StAXSource.class, CachedCxfPayloadTest.PAYLOAD);
        doTest(source, CachedCxfPayloadTest.PAYLOAD);
    }

    @Test
    public void testCachedCxfPayloadStaxSource() throws IOException, NoTypeConversionAvailableException, TypeConversionException {
        XMLStreamReader streamReader = StaxUtils.createXMLStreamReader(new StreamSource(new StringReader(CachedCxfPayloadTest.PAYLOAD)));
        StaxSource source = new StaxSource(streamReader);
        doTest(source, CachedCxfPayloadTest.PAYLOAD);
    }

    @Test
    public void testCachedCxfPayloadDOMSource() throws IOException, NoTypeConversionAvailableException, TypeConversionException {
        DOMSource source = context.getTypeConverter().mandatoryConvertTo(DOMSource.class, CachedCxfPayloadTest.PAYLOAD);
        doTest(source, CachedCxfPayloadTest.PAYLOAD);
    }

    @Test
    public void testCachedCxfPayloadStreamSource() throws IOException, NoTypeConversionAvailableException, TypeConversionException {
        StreamSource source = context.getTypeConverter().mandatoryConvertTo(StreamSource.class, CachedCxfPayloadTest.PAYLOAD);
        doTest(source, CachedCxfPayloadTest.PAYLOAD);
    }
}

