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
package org.springframework.batch.item.file.builder;


import java.io.File;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.batch.item.file.SimpleResourceSuffixCreator;
import org.springframework.core.io.FileSystemResource;


/**
 *
 *
 * @author Glenn Renfro
 */
public class MultiResourceItemWriterBuilderTests {
    private MultiResourceItemWriter<String> writer;

    private File file;

    private ResourceSuffixCreator suffixCreator = new ResourceSuffixCreator() {
        @Override
        public String getSuffix(int index) {
            return "A" + index;
        }
    };

    private ExecutionContext executionContext = new ExecutionContext();

    private FlatFileItemWriter<String> delegate;

    @Test
    public void testBasicMultiResourceWriteScenario() throws Exception {
        this.writer = new MultiResourceItemWriterBuilder<String>().delegate(this.delegate).resource(new FileSystemResource(this.file)).resourceSuffixCreator(this.suffixCreator).itemCountLimitPerResource(2).saveState(true).name("foo").build();
        this.writer.open(this.executionContext);
        this.writer.write(Arrays.asList("1", "2", "3"));
        File part1 = new File(((this.file.getAbsolutePath()) + (this.suffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        Assert.assertEquals("123", readFile(part1));
        this.writer.write(Arrays.asList("4"));
        File part2 = new File(((this.file.getAbsolutePath()) + (this.suffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        Assert.assertEquals("4", readFile(part2));
        this.writer.write(Arrays.asList("5"));
        Assert.assertEquals("45", readFile(part2));
        this.writer.write(Arrays.asList("6", "7", "8", "9"));
        File part3 = new File(((this.file.getAbsolutePath()) + (this.suffixCreator.getSuffix(3))));
        Assert.assertTrue(part3.exists());
        Assert.assertEquals("6789", readFile(part3));
    }

    @Test
    public void testBasicDefaultSuffixCreator() throws Exception {
        SimpleResourceSuffixCreator simpleResourceSuffixCreator = new SimpleResourceSuffixCreator();
        this.writer = new MultiResourceItemWriterBuilder<String>().delegate(this.delegate).resource(new FileSystemResource(this.file)).itemCountLimitPerResource(2).saveState(true).name("foo").build();
        this.writer.open(this.executionContext);
        this.writer.write(Arrays.asList("1", "2", "3"));
        File part1 = new File(((this.file.getAbsolutePath()) + (simpleResourceSuffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        Assert.assertEquals("123", readFile(part1));
        this.writer.write(Arrays.asList("4"));
        File part2 = new File(((this.file.getAbsolutePath()) + (simpleResourceSuffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        Assert.assertEquals("4", readFile(part2));
    }

    @Test
    public void testUpdateAfterDelegateClose() throws Exception {
        this.writer = new MultiResourceItemWriterBuilder<String>().delegate(this.delegate).resource(new FileSystemResource(this.file)).resourceSuffixCreator(this.suffixCreator).itemCountLimitPerResource(2).saveState(true).name("foo").build();
        this.writer.update(this.executionContext);
        Assert.assertEquals(0, this.executionContext.getInt(this.writer.getExecutionContextKey("resource.item.count")));
        Assert.assertEquals(1, this.executionContext.getInt(this.writer.getExecutionContextKey("resource.index")));
        this.writer.write(Arrays.asList("1", "2", "3"));
        this.writer.update(this.executionContext);
        Assert.assertEquals(0, this.executionContext.getInt(this.writer.getExecutionContextKey("resource.item.count")));
        Assert.assertEquals(2, this.executionContext.getInt(this.writer.getExecutionContextKey("resource.index")));
    }

    @Test
    public void testRestart() throws Exception {
        this.writer = new MultiResourceItemWriterBuilder<String>().delegate(this.delegate).resource(new FileSystemResource(this.file)).resourceSuffixCreator(this.suffixCreator).itemCountLimitPerResource(2).saveState(true).name("foo").build();
        this.writer.write(Arrays.asList("1", "2", "3"));
        File part1 = new File(((this.file.getAbsolutePath()) + (this.suffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        Assert.assertEquals("123", readFile(part1));
        this.writer.write(Arrays.asList("4"));
        File part2 = new File(((this.file.getAbsolutePath()) + (this.suffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        Assert.assertEquals("4", readFile(part2));
        this.writer.update(this.executionContext);
        this.writer.close();
        this.writer.open(this.executionContext);
        this.writer.write(Arrays.asList("5"));
        Assert.assertEquals("45", readFile(part2));
        this.writer.write(Arrays.asList("6", "7", "8", "9"));
        File part3 = new File(((this.file.getAbsolutePath()) + (this.suffixCreator.getSuffix(3))));
        Assert.assertTrue(part3.exists());
        Assert.assertEquals("6789", readFile(part3));
    }

    @Test
    public void testRestartNoSaveState() throws Exception {
        this.writer = new MultiResourceItemWriterBuilder<String>().delegate(this.delegate).resource(new FileSystemResource(this.file)).resourceSuffixCreator(this.suffixCreator).itemCountLimitPerResource(2).saveState(false).name("foo").build();
        this.writer.write(Arrays.asList("1", "2", "3"));
        File part1 = new File(((this.file.getAbsolutePath()) + (this.suffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        Assert.assertEquals("123", readFile(part1));
        this.writer.write(Arrays.asList("4"));
        File part2 = new File(((this.file.getAbsolutePath()) + (this.suffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        Assert.assertEquals("4", readFile(part2));
        this.writer.update(this.executionContext);
        this.writer.close();
        this.writer.open(this.executionContext);
        this.writer.write(Arrays.asList("5"));
        Assert.assertEquals("4", readFile(part2));
        this.writer.write(Arrays.asList("6", "7", "8", "9"));
        File part3 = new File(((this.file.getAbsolutePath()) + (this.suffixCreator.getSuffix(1))));
        Assert.assertTrue(part3.exists());
        Assert.assertEquals("56789", readFile(part3));
    }

    @Test
    public void testSaveStateNoName() {
        try {
            new MultiResourceItemWriterBuilder<String>().delegate(this.delegate).resource(new FileSystemResource(this.file)).resourceSuffixCreator(this.suffixCreator).itemCountLimitPerResource(2).saveState(true).build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "A name is required when saveState is true.", ise.getMessage());
        }
    }

    @Test
    public void testNoResource() throws Exception {
        try {
            new MultiResourceItemWriterBuilder<String>().delegate(this.delegate).resourceSuffixCreator(this.suffixCreator).itemCountLimitPerResource(2).build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "resource is required.", ise.getMessage());
        }
    }

    @Test
    public void testNoDelegateNoName() {
        try {
            new MultiResourceItemWriterBuilder<String>().resource(new FileSystemResource(this.file)).resourceSuffixCreator(this.suffixCreator).itemCountLimitPerResource(2).saveState(false).build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "delegate is required.", ise.getMessage());
        }
    }
}

