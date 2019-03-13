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


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Michael Minella
 * @author Mahmoud Ben Hassine
 * @author Drummond Dawson
 */
public class FlatFileItemWriterBuilderTests {
    // reads the output file to check the result
    private BufferedReader reader;

    @Test(expected = IllegalArgumentException.class)
    public void testMissingLineAggregator() {
        new FlatFileItemWriterBuilder<FlatFileItemWriterBuilderTests.Foo>().build();
    }

    @Test(expected = IllegalStateException.class)
    public void testMultipleLineAggregators() throws IOException {
        Resource output = new FileSystemResource(File.createTempFile("foo", "txt"));
        new FlatFileItemWriterBuilder<FlatFileItemWriterBuilderTests.Foo>().name("itemWriter").resource(output).delimited().delimiter(";").names("foo", "bar").formatted().format("%2s%2s").names("foo", "bar").build();
    }

    @Test
    public void test() throws Exception {
        Resource output = new FileSystemResource(File.createTempFile("foo", "txt"));
        FlatFileItemWriter<FlatFileItemWriterBuilderTests.Foo> writer = new FlatFileItemWriterBuilder<FlatFileItemWriterBuilderTests.Foo>().name("foo").resource(output).lineSeparator("$").lineAggregator(new org.springframework.batch.item.file.transform.PassThroughLineAggregator()).encoding("UTF-16LE").headerCallback(( writer1) -> writer1.append("HEADER")).footerCallback(( writer12) -> writer12.append("FOOTER")).build();
        ExecutionContext executionContext = new ExecutionContext();
        writer.open(executionContext);
        writer.write(Arrays.asList(new FlatFileItemWriterBuilderTests.Foo(1, 2, "3"), new FlatFileItemWriterBuilderTests.Foo(4, 5, "6")));
        writer.close();
        Assert.assertEquals("HEADER$Foo{first=1, second=2, third='3'}$Foo{first=4, second=5, third='6'}$FOOTER", readLine("UTF-16LE", output));
    }

    @Test
    public void testDelimitedOutputWithDefaultDelimiter() throws Exception {
        Resource output = new FileSystemResource(File.createTempFile("foo", "txt"));
        FlatFileItemWriter<FlatFileItemWriterBuilderTests.Foo> writer = new FlatFileItemWriterBuilder<FlatFileItemWriterBuilderTests.Foo>().name("foo").resource(output).lineSeparator("$").delimited().names("first", "second", "third").encoding("UTF-16LE").headerCallback(( writer1) -> writer1.append("HEADER")).footerCallback(( writer12) -> writer12.append("FOOTER")).build();
        ExecutionContext executionContext = new ExecutionContext();
        writer.open(executionContext);
        writer.write(Arrays.asList(new FlatFileItemWriterBuilderTests.Foo(1, 2, "3"), new FlatFileItemWriterBuilderTests.Foo(4, 5, "6")));
        writer.close();
        Assert.assertEquals("HEADER$1,2,3$4,5,6$FOOTER", readLine("UTF-16LE", output));
    }

    @Test
    public void testDelimitedOutputWithDefaultFieldExtractor() throws Exception {
        Resource output = new FileSystemResource(File.createTempFile("foo", "txt"));
        FlatFileItemWriter<FlatFileItemWriterBuilderTests.Foo> writer = new FlatFileItemWriterBuilder<FlatFileItemWriterBuilderTests.Foo>().name("foo").resource(output).lineSeparator("$").delimited().delimiter(";").names("first", "second", "third").encoding("UTF-16LE").headerCallback(( writer1) -> writer1.append("HEADER")).footerCallback(( writer12) -> writer12.append("FOOTER")).build();
        ExecutionContext executionContext = new ExecutionContext();
        writer.open(executionContext);
        writer.write(Arrays.asList(new FlatFileItemWriterBuilderTests.Foo(1, 2, "3"), new FlatFileItemWriterBuilderTests.Foo(4, 5, "6")));
        writer.close();
        Assert.assertEquals("HEADER$1;2;3$4;5;6$FOOTER", readLine("UTF-16LE", output));
    }

    @Test
    public void testDelimitedOutputWithCustomFieldExtractor() throws Exception {
        Resource output = new FileSystemResource(File.createTempFile("foo", "txt"));
        FlatFileItemWriter<FlatFileItemWriterBuilderTests.Foo> writer = new FlatFileItemWriterBuilder<FlatFileItemWriterBuilderTests.Foo>().name("foo").resource(output).lineSeparator("$").delimited().delimiter(" ").fieldExtractor(( item) -> new Object[]{ item.getFirst(), item.getThird() }).encoding("UTF-16LE").headerCallback(( writer1) -> writer1.append("HEADER")).footerCallback(( writer12) -> writer12.append("FOOTER")).build();
        ExecutionContext executionContext = new ExecutionContext();
        writer.open(executionContext);
        writer.write(Arrays.asList(new FlatFileItemWriterBuilderTests.Foo(1, 2, "3"), new FlatFileItemWriterBuilderTests.Foo(4, 5, "6")));
        writer.close();
        Assert.assertEquals("HEADER$1 3$4 6$FOOTER", readLine("UTF-16LE", output));
    }

    @Test
    public void testFormattedOutputWithDefaultFieldExtractor() throws Exception {
        Resource output = new FileSystemResource(File.createTempFile("foo", "txt"));
        FlatFileItemWriter<FlatFileItemWriterBuilderTests.Foo> writer = new FlatFileItemWriterBuilder<FlatFileItemWriterBuilderTests.Foo>().name("foo").resource(output).lineSeparator("$").formatted().format("%2s%2s%2s").names("first", "second", "third").encoding("UTF-16LE").headerCallback(( writer1) -> writer1.append("HEADER")).footerCallback(( writer12) -> writer12.append("FOOTER")).build();
        ExecutionContext executionContext = new ExecutionContext();
        writer.open(executionContext);
        writer.write(Arrays.asList(new FlatFileItemWriterBuilderTests.Foo(1, 2, "3"), new FlatFileItemWriterBuilderTests.Foo(4, 5, "6")));
        writer.close();
        Assert.assertEquals("HEADER$ 1 2 3$ 4 5 6$FOOTER", readLine("UTF-16LE", output));
    }

    @Test
    public void testFormattedOutputWithCustomFieldExtractor() throws Exception {
        Resource output = new FileSystemResource(File.createTempFile("foo", "txt"));
        FlatFileItemWriter<FlatFileItemWriterBuilderTests.Foo> writer = new FlatFileItemWriterBuilder<FlatFileItemWriterBuilderTests.Foo>().name("foo").resource(output).lineSeparator("$").formatted().format("%3s%3s").fieldExtractor(( item) -> new Object[]{ item.getFirst(), item.getThird() }).encoding("UTF-16LE").headerCallback(( writer1) -> writer1.append("HEADER")).footerCallback(( writer12) -> writer12.append("FOOTER")).build();
        ExecutionContext executionContext = new ExecutionContext();
        writer.open(executionContext);
        writer.write(Arrays.asList(new FlatFileItemWriterBuilderTests.Foo(1, 2, "3"), new FlatFileItemWriterBuilderTests.Foo(4, 5, "6")));
        writer.close();
        Assert.assertEquals("HEADER$  1  3$  4  6$FOOTER", readLine("UTF-16LE", output));
    }

    @Test
    public void testFlags() throws Exception {
        Resource output = new FileSystemResource(File.createTempFile("foo", "txt"));
        FlatFileItemWriter<FlatFileItemWriterBuilderTests.Foo> writer = new FlatFileItemWriterBuilder<FlatFileItemWriterBuilderTests.Foo>().name("foo").resource(output).shouldDeleteIfEmpty(true).shouldDeleteIfExists(false).saveState(false).forceSync(true).append(true).transactional(false).lineAggregator(new org.springframework.batch.item.file.transform.PassThroughLineAggregator()).build();
        Assert.assertFalse(((Boolean) (ReflectionTestUtils.getField(writer, "saveState"))));
        Assert.assertTrue(((Boolean) (ReflectionTestUtils.getField(writer, "append"))));
        Assert.assertFalse(((Boolean) (ReflectionTestUtils.getField(writer, "transactional"))));
        Assert.assertTrue(((Boolean) (ReflectionTestUtils.getField(writer, "shouldDeleteIfEmpty"))));
        Assert.assertFalse(((Boolean) (ReflectionTestUtils.getField(writer, "shouldDeleteIfExists"))));
        Assert.assertTrue(((Boolean) (ReflectionTestUtils.getField(writer, "forceSync"))));
    }

    public static class Foo {
        private int first;

        private int second;

        private String third;

        public Foo(int first, int second, String third) {
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

        @Override
        public String toString() {
            return ((((((("Foo{" + "first=") + (first)) + ", second=") + (second)) + ", third='") + (third)) + '\'') + '}';
        }
    }
}

