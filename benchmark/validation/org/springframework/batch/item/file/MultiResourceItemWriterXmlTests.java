/**
 * Copyright 2009-2017 the original author or authors.
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
package org.springframework.batch.item.file;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.transform.Result;
import org.junit.Test;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.StaxTestUtils;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.util.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Tests for {@link MultiResourceItemWriter} delegating to
 * {@link StaxEventItemWriter}.
 */
public class MultiResourceItemWriterXmlTests extends AbstractMultiResourceItemWriterTests {
    private static final String xmlDocStart = "<root>";

    private static final String xmlDocEnd = "</root>";

    private StaxEventItemWriter<String> delegate;

    /**
     * Writes object's toString representation as tag.
     */
    private static class SimpleMarshaller implements Marshaller {
        @Override
        public void marshal(Object graph, Result result) throws IOException, XmlMappingException {
            Assert.isInstanceOf(Result.class, result);
            try {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                XMLEventWriter writer = StaxTestUtils.getXmlEventWriter(result);
                writer.add(factory.createStartDocument("UTF-8"));
                writer.add(factory.createStartElement("prefix", "namespace", graph.toString()));
                writer.add(factory.createEndElement("prefix", "namespace", graph.toString()));
                writer.add(factory.createEndDocument());
            } catch (Exception e) {
                throw new RuntimeException("Exception while writing to output file", e);
            }
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return true;
        }
    }

    @Test
    public void multiResourceWritingWithRestart() throws Exception {
        super.setUp(delegate);
        tested.open(executionContext);
        tested.write(Arrays.asList("1", "2", "3"));
        File part1 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(1))));
        assertTrue(part1.exists());
        tested.write(Arrays.asList("4"));
        File part2 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(2))));
        assertTrue(part2.exists());
        tested.update(executionContext);
        tested.close();
        assertEquals((((MultiResourceItemWriterXmlTests.xmlDocStart) + "<prefix:4/>") + (MultiResourceItemWriterXmlTests.xmlDocEnd)), readFile(part2));
        assertEquals((((MultiResourceItemWriterXmlTests.xmlDocStart) + "<prefix:1/><prefix:2/><prefix:3/>") + (MultiResourceItemWriterXmlTests.xmlDocEnd)), readFile(part1));
        tested.open(executionContext);
        tested.write(Arrays.asList("5"));
        tested.write(Arrays.asList("6", "7", "8", "9"));
        File part3 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(3))));
        assertTrue(part3.exists());
        tested.close();
        assertEquals((((MultiResourceItemWriterXmlTests.xmlDocStart) + "<prefix:4/><prefix:5/>") + (MultiResourceItemWriterXmlTests.xmlDocEnd)), readFile(part2));
        assertEquals((((MultiResourceItemWriterXmlTests.xmlDocStart) + "<prefix:6/><prefix:7/><prefix:8/><prefix:9/>") + (MultiResourceItemWriterXmlTests.xmlDocEnd)), readFile(part3));
    }
}

