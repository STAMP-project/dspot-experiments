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
package org.springframework.util.xml;


import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class StaxResultTests {
    private static final String XML = "<root xmlns='namespace'><child/></root>";

    private Transformer transformer;

    private XMLOutputFactory inputFactory;

    @Test
    public void streamWriterSource() throws Exception {
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter streamWriter = inputFactory.createXMLStreamWriter(stringWriter);
        Reader reader = new StringReader(StaxResultTests.XML);
        Source source = new StreamSource(reader);
        StaxResult result = new StaxResult(streamWriter);
        Assert.assertEquals("Invalid streamWriter returned", streamWriter, result.getXMLStreamWriter());
        Assert.assertNull("EventWriter returned", result.getXMLEventWriter());
        transformer.transform(source, result);
        Assert.assertThat("Invalid result", stringWriter.toString(), isSimilarTo(StaxResultTests.XML));
    }

    @Test
    public void eventWriterSource() throws Exception {
        StringWriter stringWriter = new StringWriter();
        XMLEventWriter eventWriter = inputFactory.createXMLEventWriter(stringWriter);
        Reader reader = new StringReader(StaxResultTests.XML);
        Source source = new StreamSource(reader);
        StaxResult result = new StaxResult(eventWriter);
        Assert.assertEquals("Invalid eventWriter returned", eventWriter, result.getXMLEventWriter());
        Assert.assertNull("StreamWriter returned", result.getXMLStreamWriter());
        transformer.transform(source, result);
        Assert.assertThat("Invalid result", stringWriter.toString(), isSimilarTo(StaxResultTests.XML));
    }
}

