/**
 * Copyright 2013-2014 the original author or authors.
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
package org.springframework.batch.item.data;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.gemfire.GemfireTemplate;


@SuppressWarnings("serial")
public class GemfireItemWriterTests {
    private GemfireItemWriter<String, GemfireItemWriterTests.Foo> writer;

    @Mock
    private GemfireTemplate template;

    @Test
    public void testAfterPropertiesSet() throws Exception {
        writer = new GemfireItemWriter();
        try {
            writer.afterPropertiesSet();
            Assert.fail("Expected exception was not thrown");
        } catch (IllegalArgumentException iae) {
        }
        writer.setTemplate(template);
        try {
            writer.afterPropertiesSet();
            Assert.fail("Expected exception was not thrown");
        } catch (IllegalArgumentException iae) {
        }
        writer.setItemKeyMapper(new org.springframework.batch.item.SpELItemKeyMapper("foo"));
        writer.afterPropertiesSet();
    }

    @Test
    public void testBasicWrite() throws Exception {
        List<GemfireItemWriterTests.Foo> items = new ArrayList<GemfireItemWriterTests.Foo>() {
            {
                add(new GemfireItemWriterTests.Foo(new GemfireItemWriterTests.Bar("val1")));
                add(new GemfireItemWriterTests.Foo(new GemfireItemWriterTests.Bar("val2")));
            }
        };
        writer.write(items);
        Mockito.verify(template).put("val1", items.get(0));
        Mockito.verify(template).put("val2", items.get(1));
    }

    @Test
    public void testBasicDelete() throws Exception {
        List<GemfireItemWriterTests.Foo> items = new ArrayList<GemfireItemWriterTests.Foo>() {
            {
                add(new GemfireItemWriterTests.Foo(new GemfireItemWriterTests.Bar("val1")));
                add(new GemfireItemWriterTests.Foo(new GemfireItemWriterTests.Bar("val2")));
            }
        };
        writer.setDelete(true);
        writer.write(items);
        Mockito.verify(template).remove("val1");
        Mockito.verify(template).remove("val2");
    }

    @Test
    public void testWriteWithCustomItemKeyMapper() throws Exception {
        List<GemfireItemWriterTests.Foo> items = new ArrayList<GemfireItemWriterTests.Foo>() {
            {
                add(new GemfireItemWriterTests.Foo(new GemfireItemWriterTests.Bar("val1")));
                add(new GemfireItemWriterTests.Foo(new GemfireItemWriterTests.Bar("val2")));
            }
        };
        writer = new GemfireItemWriter();
        writer.setTemplate(template);
        writer.setItemKeyMapper(new org.springframework.core.convert.converter.Converter<GemfireItemWriterTests.Foo, String>() {
            @Override
            public String convert(GemfireItemWriterTests.Foo item) {
                String index = item.bar.val.replaceAll("val", "");
                return "item" + index;
            }
        });
        writer.afterPropertiesSet();
        writer.write(items);
        Mockito.verify(template).put("item1", items.get(0));
        Mockito.verify(template).put("item2", items.get(1));
    }

    @Test
    public void testWriteNoTransactionNoItems() throws Exception {
        writer.write(null);
        Mockito.verifyZeroInteractions(template);
    }

    static class Foo {
        public GemfireItemWriterTests.Bar bar;

        public Foo(GemfireItemWriterTests.Bar bar) {
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

