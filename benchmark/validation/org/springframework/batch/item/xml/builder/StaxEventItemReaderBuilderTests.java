/**
 * Copyright 2017-2019 the original author or authors.
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


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


/**
 *
 *
 * @author Michael Minella
 * @author Mahmoud Ben Hassine
 */
public class StaxEventItemReaderBuilderTests {
    private static final String SIMPLE_XML = "<foos><foo><first>1</first>" + (("<second>two</second><third>three</third></foo><foo><first>4</first>" + "<second>five</second><third>six</third></foo><foo><first>7</first>") + "<second>eight</second><third>nine</third></foo></foos>");

    @Mock
    private Resource resource;

    @Test
    public void testValidation() {
        try {
            new StaxEventItemReaderBuilder<StaxEventItemReaderBuilderTests.Foo>().build();
            Assert.fail("Validation of the missing resource failed");
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new StaxEventItemReaderBuilder<StaxEventItemReaderBuilderTests.Foo>().resource(this.resource).build();
            Assert.fail("saveState == true should require a name");
        } catch (IllegalStateException iae) {
            Assert.assertEquals("A name is required when saveState is set to true.", iae.getMessage());
        }
        try {
            new StaxEventItemReaderBuilder<StaxEventItemReaderBuilderTests.Foo>().resource(this.resource).saveState(false).build();
            Assert.fail("No root tags have been configured");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("At least one fragment root element is required", iae.getMessage());
        }
    }

    @Test
    public void testConfiguration() throws Exception {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(StaxEventItemReaderBuilderTests.Foo.class);
        StaxEventItemReader<StaxEventItemReaderBuilderTests.Foo> reader = new StaxEventItemReaderBuilder<StaxEventItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource(StaxEventItemReaderBuilderTests.SIMPLE_XML)).addFragmentRootElements("foo").currentItemCount(1).maxItemCount(2).unmarshaller(unmarshaller).xmlInputFactory(XMLInputFactory.newInstance()).build();
        reader.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        StaxEventItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertNull(reader.read());
        reader.update(executionContext);
        reader.close();
        Assert.assertEquals(4, item.getFirst());
        Assert.assertEquals("five", item.getSecond());
        Assert.assertEquals("six", item.getThird());
        Assert.assertEquals(2, executionContext.size());
    }

    @Test(expected = ItemStreamException.class)
    public void testStrict() throws Exception {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(StaxEventItemReaderBuilderTests.Foo.class);
        StaxEventItemReader<StaxEventItemReaderBuilderTests.Foo> reader = new StaxEventItemReaderBuilder<StaxEventItemReaderBuilderTests.Foo>().name("fooReader").resource(this.resource).addFragmentRootElements("foo").unmarshaller(unmarshaller).build();
        reader.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
    }

    @Test
    public void testSaveState() throws Exception {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(StaxEventItemReaderBuilderTests.Foo.class);
        StaxEventItemReader<StaxEventItemReaderBuilderTests.Foo> reader = new StaxEventItemReaderBuilder<StaxEventItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource(StaxEventItemReaderBuilderTests.SIMPLE_XML)).addFragmentRootElements("foo").unmarshaller(unmarshaller).saveState(false).build();
        reader.afterPropertiesSet();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        StaxEventItemReaderBuilderTests.Foo item = reader.read();
        item = reader.read();
        item = reader.read();
        Assert.assertNull(reader.read());
        reader.update(executionContext);
        reader.close();
        Assert.assertEquals(7, item.getFirst());
        Assert.assertEquals("eight", item.getSecond());
        Assert.assertEquals("nine", item.getThird());
        Assert.assertEquals(0, executionContext.size());
    }

    @XmlRootElement(name = "foo")
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

        @Override
        public String toString() {
            return String.format("{%s, %s, %s}", this.first, this.second, this.third);
        }
    }
}

