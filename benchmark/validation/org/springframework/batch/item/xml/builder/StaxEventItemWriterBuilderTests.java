/**
 * Copyright 2017 the original author or authors.
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
package org.springframework.batch.item.xml.builder;


import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.support.transaction.TransactionAwareBufferedWriter;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Michael Minella
 */
public class StaxEventItemWriterBuilderTests {
    private Resource resource;

    private List<StaxEventItemWriterBuilderTests.Foo> items;

    private Marshaller marshaller;

    private static final String FULL_OUTPUT = "<?xml version='1.1' encoding='UTF-16'?>" + (((((("<foobarred baz=\"quix\">\ufeff<ns:group><ns2:item xmlns:ns2=\"http://www.springframework.org/test\">" + "<first>1</first><second>two</second><third>three</third></ns2:item>\ufeff") + "<ns2:item xmlns:ns2=\"http://www.springframework.org/test\"><first>4</first>") + "<second>five</second><third>six</third></ns2:item>\ufeff") + "<ns2:item xmlns:ns2=\"http://www.springframework.org/test\"><first>7</first>") + "<second>eight</second><third>nine</third></ns2:item>\ufeff</ns:group>\ufeff") + "</foobarred>");

    private static final String SIMPLE_OUTPUT = "<root><ns2:item xmlns:ns2=\"http://www.springframework.org/test\">" + (((("<first>1</first><second>two</second><third>three</third></ns2:item>" + "<ns2:item xmlns:ns2=\"http://www.springframework.org/test\"><first>4</first>") + "<second>five</second><third>six</third></ns2:item>") + "<ns2:item xmlns:ns2=\"http://www.springframework.org/test\"><first>7</first>") + "<second>eight</second><third>nine</third></ns2:item></root>");

    @Test(expected = ItemStreamException.class)
    public void testOverwriteOutput() throws Exception {
        StaxEventItemWriter<StaxEventItemWriterBuilderTests.Foo> staxEventItemWriter = new StaxEventItemWriterBuilder<StaxEventItemWriterBuilderTests.Foo>().name("fooWriter").marshaller(marshaller).resource(this.resource).overwriteOutput(false).build();
        staxEventItemWriter.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        staxEventItemWriter.open(executionContext);
        staxEventItemWriter.write(this.items);
        staxEventItemWriter.update(executionContext);
        staxEventItemWriter.close();
        File output = this.resource.getFile();
        Assert.assertTrue(output.exists());
        executionContext = new ExecutionContext();
        staxEventItemWriter.open(executionContext);
    }

    @Test
    public void testDeleteIfEmpty() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        StaxEventItemWriter<StaxEventItemWriterBuilderTests.Foo> staxEventItemWriter = new StaxEventItemWriterBuilder<StaxEventItemWriterBuilderTests.Foo>().name("fooWriter").resource(this.resource).marshaller(this.marshaller).shouldDeleteIfEmpty(true).build();
        staxEventItemWriter.afterPropertiesSet();
        staxEventItemWriter.open(executionContext);
        staxEventItemWriter.write(Collections.emptyList());
        staxEventItemWriter.update(executionContext);
        staxEventItemWriter.close();
        File file = this.resource.getFile();
        Assert.assertFalse(file.exists());
    }

    @Test
    public void testTransactional() {
        StaxEventItemWriter<StaxEventItemWriterBuilderTests.Foo> staxEventItemWriter = new StaxEventItemWriterBuilder<StaxEventItemWriterBuilderTests.Foo>().name("fooWriter").resource(this.resource).marshaller(this.marshaller).transactional(true).forceSync(true).build();
        ExecutionContext executionContext = new ExecutionContext();
        staxEventItemWriter.open(executionContext);
        Object writer = ReflectionTestUtils.getField(staxEventItemWriter, "bufferedWriter");
        Assert.assertTrue((writer instanceof TransactionAwareBufferedWriter));
        Assert.assertTrue(((Boolean) (ReflectionTestUtils.getField(writer, "forceSync"))));
    }

    @Test
    public void testConfiguration() throws Exception {
        Map<String, String> rootElementAttributes = new HashMap<>();
        rootElementAttributes.put("baz", "quix");
        StaxEventItemWriter<StaxEventItemWriterBuilderTests.Foo> staxEventItemWriter = new StaxEventItemWriterBuilder<StaxEventItemWriterBuilderTests.Foo>().name("fooWriter").marshaller(marshaller).encoding("UTF-16").footerCallback(( writer) -> {
            javax.xml.stream.XMLEventFactory factory = javax.xml.stream.XMLEventFactory.newInstance();
            try {
                writer.add(factory.createEndElement("ns", "http://www.springframework.org/test", "group"));
            } catch ( e) {
                throw new <e>RuntimeException();
            }
        }).headerCallback(( writer) -> {
            javax.xml.stream.XMLEventFactory factory = javax.xml.stream.XMLEventFactory.newInstance();
            try {
                writer.add(factory.createStartElement("ns", "http://www.springframework.org/test", "group"));
            } catch ( e) {
                throw new <e>RuntimeException();
            }
        }).resource(this.resource).rootTagName("foobarred").rootElementAttributes(rootElementAttributes).saveState(false).version("1.1").build();
        staxEventItemWriter.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        staxEventItemWriter.open(executionContext);
        staxEventItemWriter.write(this.items);
        staxEventItemWriter.update(executionContext);
        staxEventItemWriter.close();
        Assert.assertEquals(StaxEventItemWriterBuilderTests.FULL_OUTPUT, getOutputFileContent("UTF-16"));
        Assert.assertEquals(0, executionContext.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingMarshallerValidation() {
        new StaxEventItemWriterBuilder<StaxEventItemWriterBuilderTests.Foo>().name("fooWriter").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingNameValidation() {
        new StaxEventItemWriterBuilder<StaxEventItemWriterBuilderTests.Foo>().marshaller(new Jaxb2Marshaller()).build();
    }

    @XmlRootElement(name = "item", namespace = "http://www.springframework.org/test")
    public static class Foo {
        private int first;

        private String second;

        private String third;

        public Foo() {
        }

        public Foo(int first, String second, String third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public int getFirst() {
            return first;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }

        public String getThird() {
            return third;
        }

        public void setThird(String third) {
            this.third = third;
        }
    }
}

