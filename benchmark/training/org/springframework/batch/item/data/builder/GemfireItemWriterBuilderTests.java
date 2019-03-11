/**
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.springframework.batch.item.data.builder;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.item.SpELItemKeyMapper;
import org.springframework.batch.item.data.GemfireItemWriter;
import org.springframework.data.gemfire.GemfireTemplate;


/**
 *
 *
 * @author Glenn Renfro
 */
public class GemfireItemWriterBuilderTests {
    @Mock
    private GemfireTemplate template;

    private SpELItemKeyMapper<String, GemfireItemWriterBuilderTests.Foo> itemKeyMapper;

    private List<GemfireItemWriterBuilderTests.Foo> items;

    @Test
    public void testBasicWrite() throws Exception {
        GemfireItemWriter<String, GemfireItemWriterBuilderTests.Foo> writer = new GemfireItemWriterBuilder<String, GemfireItemWriterBuilderTests.Foo>().template(this.template).itemKeyMapper(this.itemKeyMapper).build();
        writer.write(this.items);
        Mockito.verify(this.template).put("val1", items.get(0));
        Mockito.verify(this.template).put("val2", items.get(1));
        Mockito.verify(this.template, Mockito.never()).remove("val1");
        Mockito.verify(this.template, Mockito.never()).remove("val2");
    }

    @Test
    public void testBasicDelete() throws Exception {
        GemfireItemWriter<String, GemfireItemWriterBuilderTests.Foo> writer = new GemfireItemWriterBuilder<String, GemfireItemWriterBuilderTests.Foo>().template(this.template).delete(true).itemKeyMapper(this.itemKeyMapper).build();
        writer.write(this.items);
        Mockito.verify(this.template).remove("val1");
        Mockito.verify(this.template).remove("val2");
        Mockito.verify(this.template, Mockito.never()).put("val1", items.get(0));
        Mockito.verify(this.template, Mockito.never()).put("val2", items.get(1));
    }

    @Test
    public void testNullTemplate() {
        try {
            new GemfireItemWriterBuilder<String, GemfireItemWriterBuilderTests.Foo>().itemKeyMapper(this.itemKeyMapper).build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "template is required.", iae.getMessage());
        }
    }

    @Test
    public void testNullItemKeyMapper() {
        try {
            new GemfireItemWriterBuilder<String, GemfireItemWriterBuilderTests.Foo>().template(this.template).build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "itemKeyMapper is required.", iae.getMessage());
        }
    }

    static class Foo {
        public GemfireItemWriterBuilderTests.Bar bar;

        public Foo(GemfireItemWriterBuilderTests.Bar bar) {
            this.bar = bar;
        }
    }

    static class Bar {
        public String val;

        public Bar(String b1) {
            this.val = b1;
        }
    }
}

