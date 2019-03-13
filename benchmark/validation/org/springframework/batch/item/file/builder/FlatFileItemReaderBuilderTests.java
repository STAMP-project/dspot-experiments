/**
 * Copyright 2016-2018 the original author or authors.
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
package org.springframework.batch.item.file.builder;


import DelimitedLineTokenizer.DELIMITER_TAB;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Michael Minella
 * @author Mahmoud Ben Hassine
 * @author Drummond Dawson
 */
public class FlatFileItemReaderBuilderTests {
    @Test
    public void testSimpleFixedLength() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1  2  3")).fixedLength().columns(new Range(1, 3), new Range(4, 6), new Range(7)).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("3", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testSimpleDelimited() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1,2,3")).delimited().names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("3", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testSimpleDelimitedWithWhitespaceCharacter() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1 2 3")).delimited().delimiter(" ").names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("3", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testSimpleDelimitedWithTabCharacter() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1\t2\t3")).delimited().delimiter(DELIMITER_TAB).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("3", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testAdvancedDelimited() throws Exception {
        final List<String> skippedLines = new ArrayList<>();
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1,2,3\n4,5,$1,2,3$\n@this is a comment\n6,7, 8")).delimited().quoteCharacter('$').names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).linesToSkip(1).skippedLinesCallback(skippedLines::add).addComment("@").build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(4, item.getFirst());
        Assert.assertEquals(5, item.getSecond());
        Assert.assertEquals("1,2,3", item.getThird());
        item = reader.read();
        Assert.assertEquals(6, item.getFirst());
        Assert.assertEquals(7, item.getSecond());
        Assert.assertEquals("8", item.getThird());
        reader.update(executionContext);
        Assert.assertNull(reader.read());
        Assert.assertEquals("1,2,3", skippedLines.get(0));
        Assert.assertEquals(1, skippedLines.size());
        Assert.assertEquals(1, executionContext.size());
    }

    @Test
    public void testAdvancedFixedLength() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1 2%\n  3\n4 5%\n  6\n@this is a comment\n7 8%\n  9\n")).fixedLength().columns(new Range(1, 2), new Range(3, 5), new Range(6)).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).recordSeparatorPolicy(new DefaultRecordSeparatorPolicy("\"", "%")).maxItemCount(2).saveState(false).build();
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("3", item.getThird());
        item = reader.read();
        Assert.assertEquals(4, item.getFirst());
        Assert.assertEquals(5, item.getSecond());
        Assert.assertEquals("6", item.getThird());
        reader.update(executionContext);
        Assert.assertNull(reader.read());
        Assert.assertEquals(0, executionContext.size());
    }

    @Test
    public void testStrict() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(new FileSystemResource("this/file/does/not/exist")).delimited().names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).strict(false).build();
        reader.open(new ExecutionContext());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testCustomLineTokenizerFieldSetMapper() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("|1|&|2|&|  3|\n|4|&|5|&|foo|")).lineTokenizer(( line) -> new DefaultFieldSet(line.split("&"))).fieldSetMapper(( fieldSet) -> {
            org.springframework.batch.item.file.builder.Foo item = new org.springframework.batch.item.file.builder.Foo();
            item.setFirst(Integer.valueOf(fieldSet.readString(0).replaceAll("\\|", "")));
            item.setSecond(Integer.valueOf(fieldSet.readString(1).replaceAll("\\|", "")));
            item.setThird(fieldSet.readString(2).replaceAll("\\|", ""));
            return item;
        }).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("  3", item.getThird());
        item = reader.read();
        Assert.assertEquals(4, item.getFirst());
        Assert.assertEquals(5, item.getSecond());
        Assert.assertEquals("foo", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testComments() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1,2,3\n@this is a comment\n+so is this\n4,5,6")).comments("@", "+").delimited().names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("3", item.getThird());
        item = reader.read();
        Assert.assertEquals(4, item.getFirst());
        Assert.assertEquals(5, item.getSecond());
        Assert.assertEquals("6", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testPrototypeBean() throws Exception {
        BeanFactory factory = new AnnotationConfigApplicationContext(FlatFileItemReaderBuilderTests.Beans.class);
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1,2,3")).delimited().names("first", "second", "third").prototypeBeanName("foo").beanFactory(factory).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("3", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testBeanWrapperFieldSetMapperStrict() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1,2,3")).delimited().names("setFirst", "setSecond", "setThird").targetType(FlatFileItemReaderBuilderTests.Foo.class).beanMapperStrict(true).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("3", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testDelimitedIncludedFields() throws Exception {
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1,2,3")).delimited().includedFields(0, 2).addIncludedField(1).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(2, item.getSecond());
        Assert.assertEquals("3", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testDelimitedFieldSetFactory() throws Exception {
        String[] names = new String[]{ "first", "second", "third" };
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1,2,3")).delimited().fieldSetFactory(new FieldSetFactory() {
            private FieldSet fieldSet = new DefaultFieldSet(new String[]{ "1", "3", "foo" }, names);

            @Override
            public FieldSet create(String[] values, String[] names) {
                return fieldSet;
            }

            @Override
            public FieldSet create(String[] values) {
                return fieldSet;
            }
        }).names(names).targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(3, item.getSecond());
        Assert.assertEquals("foo", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testFixedLengthFieldSetFactory() throws Exception {
        String[] names = new String[]{ "first", "second", "third" };
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1  2  3")).fixedLength().fieldSetFactory(new FieldSetFactory() {
            private FieldSet fieldSet = new DefaultFieldSet(new String[]{ "1", "3", "foo" }, names);

            @Override
            public FieldSet create(String[] values, String[] names) {
                return fieldSet;
            }

            @Override
            public FieldSet create(String[] values) {
                return fieldSet;
            }
        }).columns(new Range(1, 3), new Range(4, 6), new Range(7)).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        reader.open(new ExecutionContext());
        FlatFileItemReaderBuilderTests.Foo item = reader.read();
        Assert.assertEquals(1, item.getFirst());
        Assert.assertEquals(3, item.getSecond());
        Assert.assertEquals("foo", item.getThird());
        Assert.assertNull(reader.read());
    }

    @Test
    public void testName() throws Exception {
        try {
            new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().resource(getResource("1  2  3")).fixedLength().columns(new Range(1, 3), new Range(4, 6), new Range(7)).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
            Assert.fail("null name should throw exception");
        } catch (IllegalStateException iae) {
            Assert.assertEquals("A name is required when saveState is set to true.", iae.getMessage());
        }
        try {
            new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().resource(getResource("1  2  3")).fixedLength().columns(new Range(1, 3), new Range(4, 6), new Range(7)).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).name(null).build();
        } catch (IllegalStateException iae) {
            Assert.assertEquals("A name is required when saveState is set to true.", iae.getMessage());
        }
        Assert.assertNotNull("builder should return new instance of FlatFileItemReader", new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().resource(getResource("1  2  3")).fixedLength().columns(new Range(1, 3), new Range(4, 6), new Range(7)).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).saveState(false).build());
        Assert.assertNotNull("builder should return new instance of FlatFileItemReader", new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().resource(getResource("1  2  3")).fixedLength().columns(new Range(1, 3), new Range(4, 6), new Range(7)).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).name("foobar").build());
    }

    @Test
    public void testDefaultEncoding() {
        String encoding = FlatFileItemReader.DEFAULT_CHARSET;
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1,2,3")).delimited().names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        Assert.assertEquals(encoding, ReflectionTestUtils.getField(reader, "encoding"));
    }

    @Test
    public void testCustomEncoding() {
        String encoding = "UTF-8";
        FlatFileItemReader<FlatFileItemReaderBuilderTests.Foo> reader = new FlatFileItemReaderBuilder<FlatFileItemReaderBuilderTests.Foo>().name("fooReader").resource(getResource("1  2  3")).encoding(encoding).fixedLength().columns(new Range(1, 3), new Range(4, 6), new Range(7)).names("first", "second", "third").targetType(FlatFileItemReaderBuilderTests.Foo.class).build();
        Assert.assertEquals(encoding, ReflectionTestUtils.getField(reader, "encoding"));
    }

    public static class Foo {
        private int first;

        private int second;

        private String third;

        public int getFirst() {
            return first;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }

        public String getThird() {
            return third;
        }

        public void setThird(String third) {
            this.third = third;
        }
    }

    @Configuration
    public static class Beans {
        @Bean
        @Scope("prototype")
        public FlatFileItemReaderBuilderTests.Foo foo() {
            return new FlatFileItemReaderBuilderTests.Foo();
        }
    }
}

