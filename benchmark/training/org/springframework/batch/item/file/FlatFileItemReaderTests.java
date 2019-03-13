/**
 * Copyright 2008-2019 the original author or authors.
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


import java.io.IOException;
import java.io.InputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemCountAware;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;


/**
 * Tests for {@link FlatFileItemReader}.
 */
public class FlatFileItemReaderTests {
    // common value used for writing to a file
    private String TEST_STRING = "FlatFileInputTemplate-TestData";

    private FlatFileItemReader<String> reader = new FlatFileItemReader();

    private FlatFileItemReader<FlatFileItemReaderTests.Item> itemReader = new FlatFileItemReader();

    private ExecutionContext executionContext = new ExecutionContext();

    private Resource inputResource2 = getInputResource("testLine1\ntestLine2\ntestLine3\ntestLine4\ntestLine5\ntestLine6");

    private Resource inputResource1 = getInputResource("testLine1\ntestLine2\ntestLine3\ntestLine4\ntestLine5\ntestLine6");

    @Test
    public void testRestartWithCustomRecordSeparatorPolicy() throws Exception {
        reader.setRecordSeparatorPolicy(new RecordSeparatorPolicy() {
            // 1 record = 2 lines
            boolean pair = true;

            @Override
            public boolean isEndOfRecord(String line) {
                pair = !(pair);
                return pair;
            }

            @Override
            public String postProcess(String record) {
                return record;
            }

            @Override
            public String preProcess(String record) {
                return record;
            }
        });
        reader.open(executionContext);
        Assert.assertEquals("testLine1testLine2", reader.read());
        Assert.assertEquals("testLine3testLine4", reader.read());
        reader.update(executionContext);
        reader.close();
        reader.open(executionContext);
        Assert.assertEquals("testLine5testLine6", reader.read());
    }

    @Test
    public void testCustomRecordSeparatorPolicyEndOfFile() throws Exception {
        reader.setRecordSeparatorPolicy(new RecordSeparatorPolicy() {
            // 1 record = 2 lines
            boolean pair = true;

            @Override
            public boolean isEndOfRecord(String line) {
                pair = !(pair);
                return pair;
            }

            @Override
            public String postProcess(String record) {
                return record;
            }

            @Override
            public String preProcess(String record) {
                return record;
            }
        });
        reader.setResource(getInputResource("testLine1\ntestLine2\ntestLine3\n"));
        reader.open(executionContext);
        Assert.assertEquals("testLine1testLine2", reader.read());
        try {
            reader.read();
            Assert.fail("Expected Exception");
        } catch (FlatFileParseException e) {
            // File ends in the middle of a record
            Assert.assertEquals(3, e.getLineNumber());
            Assert.assertEquals("testLine3", e.getInput());
        }
    }

    @Test
    public void testCustomRecordSeparatorBlankLine() throws Exception {
        reader.setRecordSeparatorPolicy(new RecordSeparatorPolicy() {
            @Override
            public boolean isEndOfRecord(String line) {
                return StringUtils.hasText(line);
            }

            @Override
            public String postProcess(String record) {
                return StringUtils.hasText(record) ? record : null;
            }

            @Override
            public String preProcess(String record) {
                return record;
            }
        });
        reader.setResource(getInputResource("testLine1\ntestLine2\ntestLine3\n\n"));
        reader.open(executionContext);
        Assert.assertEquals("testLine1", reader.read());
        Assert.assertEquals("testLine2", reader.read());
        Assert.assertEquals("testLine3", reader.read());
        Assert.assertEquals(null, reader.read());
    }

    @Test
    public void testCustomRecordSeparatorMultilineBlankLineAfterEnd() throws Exception {
        reader.setRecordSeparatorPolicy(new RecordSeparatorPolicy() {
            // 1 record = 2 lines
            boolean pair = true;

            @Override
            public boolean isEndOfRecord(String line) {
                if (StringUtils.hasText(line)) {
                    pair = !(pair);
                }
                return pair;
            }

            @Override
            public String postProcess(String record) {
                return StringUtils.hasText(record) ? record : null;
            }

            @Override
            public String preProcess(String record) {
                return record;
            }
        });
        reader.setResource(getInputResource("testLine1\ntestLine2\n\n"));
        reader.open(executionContext);
        Assert.assertEquals("testLine1testLine2", reader.read());
        Assert.assertEquals(null, reader.read());
    }

    @Test
    public void testRestartWithSkippedLines() throws Exception {
        reader.setLinesToSkip(2);
        reader.open(executionContext);
        // read some records
        reader.read();
        reader.read();
        // get restart data
        reader.update(executionContext);
        // read next two records
        reader.read();
        reader.read();
        Assert.assertEquals(2, executionContext.getInt(((ClassUtils.getShortName(FlatFileItemReader.class)) + ".read.count")));
        // close input
        reader.close();
        reader.setResource(getInputResource("header\nignoreme\ntestLine1\ntestLine2\ntestLine3\ntestLine4\ntestLine5\ntestLine6"));
        // init for restart
        reader.open(executionContext);
        // read remaining records
        Assert.assertEquals("testLine3", reader.read());
        Assert.assertEquals("testLine4", reader.read());
        reader.update(executionContext);
        Assert.assertEquals(4, executionContext.getInt(((ClassUtils.getShortName(FlatFileItemReader.class)) + ".read.count")));
    }

    @Test
    public void testCurrentItemCount() throws Exception {
        reader.setCurrentItemCount(2);
        reader.open(executionContext);
        // read some records
        reader.read();
        reader.read();
        // get restart data
        reader.update(executionContext);
        Assert.assertEquals(4, executionContext.getInt(((ClassUtils.getShortName(FlatFileItemReader.class)) + ".read.count")));
        // close input
        reader.close();
    }

    @Test
    public void testMaxItemCount() throws Exception {
        reader.setMaxItemCount(2);
        reader.open(executionContext);
        // read some records
        reader.read();
        reader.read();
        // get restart data
        reader.update(executionContext);
        Assert.assertNull(reader.read());
        Assert.assertEquals(2, executionContext.getInt(((ClassUtils.getShortName(FlatFileItemReader.class)) + ".read.count")));
        // close input
        reader.close();
    }

    @Test
    public void testMaxItemCountFromContext() throws Exception {
        reader.setMaxItemCount(2);
        executionContext.putInt(((reader.getClass().getSimpleName()) + ".read.count.max"), Integer.MAX_VALUE);
        reader.open(executionContext);
        // read some records
        reader.read();
        reader.read();
        Assert.assertNotNull(reader.read());
        // close input
        reader.close();
    }

    @Test
    public void testCurrentItemCountFromContext() throws Exception {
        reader.setCurrentItemCount(2);
        executionContext.putInt(((reader.getClass().getSimpleName()) + ".read.count"), 3);
        reader.open(executionContext);
        // read some records
        Assert.assertEquals("testLine4", reader.read());
        // close input
        reader.close();
    }

    @Test
    public void testMaxAndCurrentItemCount() throws Exception {
        reader.setMaxItemCount(2);
        reader.setCurrentItemCount(2);
        reader.open(executionContext);
        // read some records
        Assert.assertNull(reader.read());
        // close input
        reader.close();
    }

    @Test
    public void testNonExistentResource() throws Exception {
        Resource resource = new FlatFileItemReaderTests.NonExistentResource();
        reader.setResource(resource);
        // afterPropertiesSet should only throw an exception if the Resource is
        // null
        reader.afterPropertiesSet();
        reader.setStrict(false);
        reader.open(executionContext);
        Assert.assertNull(reader.read());
        reader.close();
    }

    @Test
    public void testOpenBadIOInput() throws Exception {
        reader.setResource(new AbstractResource() {
            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                throw new IOException();
            }

            @Override
            public boolean exists() {
                return true;
            }
        });
        try {
            reader.open(executionContext);
            Assert.fail();
        } catch (ItemStreamException ex) {
            // expected
        }
        // read() should then return a null
        Assert.assertNull(reader.read());
        reader.close();
    }

    @Test
    public void testDirectoryResource() throws Exception {
        FileSystemResource resource = new FileSystemResource("build/data");
        resource.getFile().mkdirs();
        Assert.assertTrue(resource.getFile().isDirectory());
        reader.setResource(resource);
        reader.afterPropertiesSet();
        reader.setStrict(false);
        reader.open(executionContext);
        Assert.assertNull(reader.read());
    }

    @Test
    public void testRuntimeFileCreation() throws Exception {
        Resource resource = new FlatFileItemReaderTests.NonExistentResource();
        reader.setResource(resource);
        // afterPropertiesSet should only throw an exception if the Resource is
        // null
        reader.afterPropertiesSet();
        // replace the resource to simulate runtime resource creation
        reader.setResource(getInputResource(TEST_STRING));
        reader.open(executionContext);
        Assert.assertEquals(TEST_STRING, reader.read());
    }

    /**
     * In strict mode, resource must exist at the time reader is opened.
     */
    @Test(expected = ItemStreamException.class)
    public void testStrictness() throws Exception {
        Resource resource = new FlatFileItemReaderTests.NonExistentResource();
        reader.setResource(resource);
        reader.setStrict(true);
        reader.afterPropertiesSet();
        reader.open(executionContext);
    }

    /**
     * Exceptions from {@link LineMapper} are wrapped as {@link FlatFileParseException} containing contextual info about
     * the problematic line and its line number.
     */
    @Test
    public void testMappingExceptionWrapping() throws Exception {
        LineMapper<String> exceptionLineMapper = new LineMapper<String>() {
            @Override
            public String mapLine(String line, int lineNumber) throws Exception {
                if (lineNumber == 2) {
                    throw new Exception("Couldn't map line 2");
                }
                return line;
            }
        };
        reader.setLineMapper(exceptionLineMapper);
        reader.afterPropertiesSet();
        reader.open(executionContext);
        Assert.assertNotNull(reader.read());
        try {
            reader.read();
            Assert.fail();
        } catch (FlatFileParseException expected) {
            Assert.assertEquals(2, expected.getLineNumber());
            Assert.assertEquals("testLine2", expected.getInput());
            Assert.assertEquals("Couldn't map line 2", expected.getCause().getMessage());
            Assert.assertThat(expected.getMessage(), Matchers.startsWith("Parsing error at line: 2 in resource=["));
            Assert.assertThat(expected.getMessage(), Matchers.endsWith("], input=[testLine2]"));
        }
    }

    @Test
    public void testItemCountAware() throws Exception {
        itemReader.open(executionContext);
        FlatFileItemReaderTests.Item item1 = itemReader.read();
        Assert.assertEquals("testLine1", item1.getValue());
        Assert.assertEquals(1, item1.getItemCount());
        FlatFileItemReaderTests.Item item2 = itemReader.read();
        Assert.assertEquals("testLine2", item2.getValue());
        Assert.assertEquals(2, item2.getItemCount());
        itemReader.update(executionContext);
        itemReader.close();
        itemReader.open(executionContext);
        FlatFileItemReaderTests.Item item3 = itemReader.read();
        Assert.assertEquals("testLine3", item3.getValue());
        Assert.assertEquals(3, item3.getItemCount());
    }

    @Test
    public void testItemCountAwareMultiLine() throws Exception {
        itemReader.setRecordSeparatorPolicy(new RecordSeparatorPolicy() {
            // 1 record = 2 lines
            boolean pair = true;

            @Override
            public boolean isEndOfRecord(String line) {
                if (StringUtils.hasText(line)) {
                    pair = !(pair);
                }
                return pair;
            }

            @Override
            public String postProcess(String record) {
                return StringUtils.hasText(record) ? record : null;
            }

            @Override
            public String preProcess(String record) {
                return record;
            }
        });
        itemReader.open(executionContext);
        FlatFileItemReaderTests.Item item1 = itemReader.read();
        Assert.assertEquals("testLine1testLine2", item1.getValue());
        Assert.assertEquals(1, item1.getItemCount());
        FlatFileItemReaderTests.Item item2 = itemReader.read();
        Assert.assertEquals("testLine3testLine4", item2.getValue());
        Assert.assertEquals(2, item2.getItemCount());
        itemReader.update(executionContext);
        itemReader.close();
        itemReader.open(executionContext);
        FlatFileItemReaderTests.Item item3 = itemReader.read();
        Assert.assertEquals("testLine5testLine6", item3.getValue());
        Assert.assertEquals(3, item3.getItemCount());
    }

    private static class NonExistentResource extends AbstractResource {
        public NonExistentResource() {
        }

        @Override
        public boolean exists() {
            return false;
        }

        @Override
        public String getDescription() {
            return "NonExistentResource";
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }
    }

    private static class Item implements ItemCountAware {
        private String value;

        private int itemCount;

        public Item(String value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public void setItemCount(int count) {
            this.itemCount = count;
        }

        public int getItemCount() {
            return itemCount;
        }
    }

    private static final class ItemLineMapper implements LineMapper<FlatFileItemReaderTests.Item> {
        @Override
        public FlatFileItemReaderTests.Item mapLine(String line, int lineNumber) throws Exception {
            return new FlatFileItemReaderTests.Item(line);
        }
    }
}

