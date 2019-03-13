/**
 * Copyright 2002-2010 the original author or authors.
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
import java.io.StringWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class StaxUtilsTests {
    @Test
    public void isStaxSourceInvalid() throws Exception {
        Assert.assertFalse("A StAX Source", StaxUtils.isStaxSource(new DOMSource()));
        Assert.assertFalse("A StAX Source", StaxUtils.isStaxSource(new SAXSource()));
        Assert.assertFalse("A StAX Source", StaxUtils.isStaxSource(new StreamSource()));
    }

    @Test
    public void isStaxSource() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        String expected = "<element/>";
        XMLStreamReader streamReader = inputFactory.createXMLStreamReader(new StringReader(expected));
        Source source = StaxUtils.createCustomStaxSource(streamReader);
        Assert.assertTrue("Not a StAX Source", StaxUtils.isStaxSource(source));
    }

    @Test
    public void isStaxSourceJaxp14() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        String expected = "<element/>";
        XMLStreamReader streamReader = inputFactory.createXMLStreamReader(new StringReader(expected));
        StAXSource source = new StAXSource(streamReader);
        Assert.assertTrue("Not a StAX Source", StaxUtils.isStaxSource(source));
    }

    @Test
    public void isStaxResultInvalid() throws Exception {
        Assert.assertFalse("A StAX Result", StaxUtils.isStaxResult(new DOMResult()));
        Assert.assertFalse("A StAX Result", StaxUtils.isStaxResult(new SAXResult()));
        Assert.assertFalse("A StAX Result", StaxUtils.isStaxResult(new StreamResult()));
    }

    @Test
    public void isStaxResult() throws Exception {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(new StringWriter());
        Result result = StaxUtils.createCustomStaxResult(streamWriter);
        Assert.assertTrue("Not a StAX Result", StaxUtils.isStaxResult(result));
    }

    @Test
    public void isStaxResultJaxp14() throws Exception {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(new StringWriter());
        StAXResult result = new StAXResult(streamWriter);
        Assert.assertTrue("Not a StAX Result", StaxUtils.isStaxResult(result));
    }
}

