/**
 * Copyright 2006-2007 the original author or authors.
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


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.ClassUtils;


/**
 * Tests of regular usage for {@link FlatFileItemWriter} Exception cases will be in separate TestCase classes with
 * different <code>setUp</code> and <code>tearDown</code> methods
 *
 * @author Robert Kasanicky
 * @author Dave Syer
 */
public class FlatFileItemWriterTests {
    // object under test
    private FlatFileItemWriter<String> writer = new FlatFileItemWriter();

    // String to be written into file by the FlatFileInputTemplate
    private static final String TEST_STRING = "FlatFileOutputTemplateTest-OutputData";

    // temporary output file
    private File outputFile;

    // reads the output file to check the result
    private BufferedReader reader;

    private ExecutionContext executionContext;

    @Test
    public void testWriteWithMultipleOpen() throws Exception {
        writer.open(executionContext);
        writer.write(Collections.singletonList("test1"));
        writer.open(executionContext);
        writer.write(Collections.singletonList("test2"));
        Assert.assertEquals("test1", readLine());
        Assert.assertEquals("test2", readLine());
    }

    @Test
    public void testWriteWithDelete() throws Exception {
        writer.open(executionContext);
        writer.write(Collections.singletonList("test1"));
        writer.close();
        Assert.assertEquals("test1", readLine());
        closeReader();
        writer.setShouldDeleteIfExists(true);
        writer.open(executionContext);
        writer.write(Collections.singletonList("test2"));
        Assert.assertEquals("test2", readLine());
    }

    @Test
    public void testWriteWithAppend() throws Exception {
        writer.setAppendAllowed(true);
        writer.open(executionContext);
        writer.write(Collections.singletonList("test1"));
        writer.close();
        Assert.assertEquals("test1", readLine());
        closeReader();
        writer.open(executionContext);
        writer.write(Collections.singletonList("test2"));
        Assert.assertEquals("test1", readLine());
        Assert.assertEquals("test2", readLine());
    }

    @Test
    public void testWriteWithAppendRestartOnSecondChunk() throws Exception {
        // This should be overridden via the writer#setAppendAllowed(true)
        writer.setShouldDeleteIfExists(true);
        writer.setAppendAllowed(true);
        writer.open(executionContext);
        writer.write(Collections.singletonList("test1"));
        writer.close();
        Assert.assertEquals("test1", readLine());
        closeReader();
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.update(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        Assert.assertEquals("test1", readLine());
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, readLine());
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, readLine());
        Assert.assertEquals(null, readLine());
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        closeReader();
        Assert.assertEquals("test1", readLine());
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, readLine());
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, readLine());
        Assert.assertEquals(null, readLine());
    }

    @Test
    public void testOpenTwice() {
        // opening the writer twice should cause no issues
        writer.open(executionContext);
        writer.open(executionContext);
    }

    /**
     * Regular usage of <code>write(String)</code> method
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWriteString() throws Exception {
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        String lineFromFile = readLine();
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, lineFromFile);
    }

    @Test
    public void testForcedWriteString() throws Exception {
        writer.setForceSync(true);
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        String lineFromFile = readLine();
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, lineFromFile);
    }

    /**
     * Regular usage of <code>write(String)</code> method
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWriteWithConverter() throws Exception {
        writer.setLineAggregator(new org.springframework.batch.item.file.transform.LineAggregator<String>() {
            @Override
            public String aggregate(String item) {
                return "FOO:" + item;
            }
        });
        String data = "string";
        writer.open(executionContext);
        writer.write(Collections.singletonList(data));
        String lineFromFile = readLine();
        // converter not used if input is String
        Assert.assertEquals(("FOO:" + data), lineFromFile);
    }

    /**
     * Regular usage of <code>write(String)</code> method
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWriteWithConverterAndString() throws Exception {
        writer.setLineAggregator(new org.springframework.batch.item.file.transform.LineAggregator<String>() {
            @Override
            public String aggregate(String item) {
                return "FOO:" + item;
            }
        });
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        String lineFromFile = readLine();
        Assert.assertEquals(("FOO:" + (FlatFileItemWriterTests.TEST_STRING)), lineFromFile);
    }

    /**
     * Regular usage of <code>write(String[], LineDescriptor)</code> method
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWriteRecord() throws Exception {
        writer.open(executionContext);
        writer.write(Collections.singletonList("1"));
        String lineFromFile = readLine();
        Assert.assertEquals("1", lineFromFile);
    }

    @Test
    public void testWriteRecordWithrecordSeparator() throws Exception {
        writer.setLineSeparator("|");
        writer.open(executionContext);
        writer.write(Arrays.asList(new String[]{ "1", "2" }));
        String lineFromFile = readLine();
        Assert.assertEquals("1|2|", lineFromFile);
    }

    @Test
    public void testRestart() throws Exception {
        writer.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("footer");
            }
        });
        writer.open(executionContext);
        // write some lines
        writer.write(Arrays.asList(new String[]{ "testLine1", "testLine2", "testLine3" }));
        // write more lines
        writer.write(Arrays.asList(new String[]{ "testLine4", "testLine5" }));
        // get restart data
        writer.update(executionContext);
        // close template
        writer.close();
        // init with correct data
        writer.open(executionContext);
        // write more lines
        writer.write(Arrays.asList(new String[]{ "testLine6", "testLine7", "testLine8" }));
        // get statistics
        writer.update(executionContext);
        // close template
        writer.close();
        // verify what was written to the file
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals(("testLine" + i), readLine());
        }
        Assert.assertEquals("footer", readLine());
        // 8 lines were written to the file in total
        Assert.assertEquals(8, executionContext.getLong(((ClassUtils.getShortName(FlatFileItemWriter.class)) + ".written")));
    }

    @Test
    public void testWriteStringTransactional() throws Exception {
        writeStringTransactionCheck(null);
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, readLine());
    }

    @Test
    public void testWriteStringNotTransactional() throws Exception {
        writer.setTransactional(false);
        writeStringTransactionCheck(FlatFileItemWriterTests.TEST_STRING);
    }

    @Test
    public void testTransactionalRestart() throws Exception {
        writer.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("footer");
            }
        });
        writer.open(executionContext);
        PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
        new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    // write some lines
                    writer.write(Arrays.asList(new String[]{ "testLine1", "testLine2", "testLine3" }));
                    // write more lines
                    writer.write(Arrays.asList(new String[]{ "testLine4", "testLine5" }));
                } catch (Exception e) {
                    throw new UnexpectedInputException("Could not write data", e);
                }
                // get restart data
                writer.update(executionContext);
                return null;
            }
        });
        // close template
        writer.close();
        // init with correct data
        writer.open(executionContext);
        execute(new org.springframework.transaction.support.TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    // write more lines
                    writer.write(Arrays.asList(new String[]{ "testLine6", "testLine7", "testLine8" }));
                } catch (Exception e) {
                    throw new UnexpectedInputException("Could not write data", e);
                }
                // get restart data
                writer.update(executionContext);
                return null;
            }
        });
        // close template
        writer.close();
        // verify what was written to the file
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals(("testLine" + i), readLine());
        }
        Assert.assertEquals("footer", readLine());
        // 8 lines were written to the file in total
        Assert.assertEquals(8, executionContext.getLong(((ClassUtils.getShortName(FlatFileItemWriter.class)) + ".written")));
    }

    // BATCH-1959
    @Test
    public void testTransactionalRestartWithMultiByteCharacterUTF8() throws Exception {
        testTransactionalRestartWithMultiByteCharacter("UTF-8");
    }

    // BATCH-1959
    @Test
    public void testTransactionalRestartWithMultiByteCharacterUTF16BE() throws Exception {
        testTransactionalRestartWithMultiByteCharacter("UTF-16BE");
    }

    @Test
    public void testOpenWithNonWritableFile() throws Exception {
        writer = new FlatFileItemWriter();
        writer.setLineAggregator(new org.springframework.batch.item.file.transform.PassThroughLineAggregator());
        FileSystemResource file = new FileSystemResource("build/no-such-file.foo");
        writer.setResource(file);
        new File(file.getFile().getParent()).mkdirs();
        file.getFile().createNewFile();
        Assert.assertTrue(("Test file must exist: " + file), file.exists());
        Assert.assertTrue(("Test file set to read-only: " + file), file.getFile().setReadOnly());
        Assert.assertFalse(("Should be readonly file: " + file), file.getFile().canWrite());
        writer.afterPropertiesSet();
        try {
            writer.open(executionContext);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Message does not contain 'writable': " + message), ((message.indexOf("writable")) >= 0));
        }
    }

    @Test
    public void testAfterPropertiesSetChecksMandatory() throws Exception {
        writer = new FlatFileItemWriter();
        try {
            writer.afterPropertiesSet();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testDefaultStreamContext() throws Exception {
        writer = new FlatFileItemWriter();
        writer.setResource(new FileSystemResource(outputFile));
        writer.setLineAggregator(new org.springframework.batch.item.file.transform.PassThroughLineAggregator());
        writer.afterPropertiesSet();
        writer.setSaveState(true);
        writer.open(executionContext);
        writer.update(executionContext);
        Assert.assertNotNull(executionContext);
        Assert.assertEquals(2, executionContext.entrySet().size());
        Assert.assertEquals(0, executionContext.getLong(((ClassUtils.getShortName(FlatFileItemWriter.class)) + ".current.count")));
    }

    @Test
    public void testWriteStringWithBogusEncoding() throws Exception {
        writer.setTransactional(false);
        writer.setEncoding("BOGUS");
        // writer.setShouldDeleteIfEmpty(true);
        try {
            writer.open(executionContext);
            Assert.fail("Expected ItemStreamException");
        } catch (ItemStreamException e) {
            Assert.assertTrue(((e.getCause()) instanceof UnsupportedCharsetException));
        }
        writer.close();
        // Try and write after the exception on open:
        writer.setEncoding("UTF-8");
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
    }

    @Test
    public void testWriteStringWithEncodingAfterClose() throws Exception {
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        writer.setEncoding("UTF-8");
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        String lineFromFile = readLine();
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, lineFromFile);
    }

    @Test
    public void testWriteFooter() throws Exception {
        writer.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("a\nb");
            }
        });
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, readLine());
        Assert.assertEquals("a", readLine());
        Assert.assertEquals("b", readLine());
    }

    @Test
    public void testWriteHeader() throws Exception {
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("a\nb");
            }
        });
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        String lineFromFile = readLine();
        Assert.assertEquals("a", lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals("b", lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, lineFromFile);
    }

    @Test
    public void testWriteWithAppendAfterHeaders() throws Exception {
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("a\nb");
            }
        });
        writer.setAppendAllowed(true);
        writer.open(executionContext);
        writer.write(Collections.singletonList("test1"));
        writer.close();
        Assert.assertEquals("a", readLine());
        Assert.assertEquals("b", readLine());
        Assert.assertEquals("test1", readLine());
        closeReader();
        writer.open(executionContext);
        writer.write(Collections.singletonList("test2"));
        Assert.assertEquals("a", readLine());
        Assert.assertEquals("b", readLine());
        Assert.assertEquals("test1", readLine());
        Assert.assertEquals("test2", readLine());
    }

    @Test
    public void testWriteHeaderAndDeleteOnExit() throws Exception {
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("a\nb");
            }
        });
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        Assert.assertTrue(outputFile.exists());
        writer.close();
        Assert.assertFalse(outputFile.exists());
    }

    @Test
    public void testDeleteOnExitReopen() throws Exception {
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        writer.update(executionContext);
        Assert.assertTrue(outputFile.exists());
        writer.close();
        Assert.assertFalse(outputFile.exists());
        writer.open(executionContext);
        writer.write(Collections.singletonList("test2"));
        Assert.assertEquals("test2", readLine());
    }

    @Test
    public void testWriteHeaderAndDeleteOnExitReopen() throws Exception {
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("a\nb");
            }
        });
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        writer.update(executionContext);
        Assert.assertTrue(outputFile.exists());
        writer.close();
        Assert.assertFalse(outputFile.exists());
        writer.open(executionContext);
        writer.write(Collections.singletonList("test2"));
        Assert.assertEquals("a", readLine());
        Assert.assertEquals("b", readLine());
        Assert.assertEquals("test2", readLine());
    }

    @Test
    public void testDeleteOnExitNoRecordsWrittenAfterRestart() throws Exception {
        writer.setShouldDeleteIfEmpty(true);
        writer.open(executionContext);
        writer.write(Collections.singletonList("test2"));
        writer.update(executionContext);
        writer.close();
        Assert.assertTrue(outputFile.exists());
        writer.open(executionContext);
        writer.close();
        Assert.assertTrue(outputFile.exists());
    }

    @Test
    public void testWriteHeaderAfterRestartOnFirstChunk() throws Exception {
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("a\nb");
            }
        });
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        String lineFromFile = readLine();
        Assert.assertEquals("a", lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals("b", lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals(null, lineFromFile);
    }

    @Test
    public void testWriteHeaderAfterRestartOnSecondChunk() throws Exception {
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("a\nb");
            }
        });
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.update(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        String lineFromFile = readLine();
        Assert.assertEquals("a", lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals("b", lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, lineFromFile);
        writer.open(executionContext);
        writer.write(Collections.singletonList(FlatFileItemWriterTests.TEST_STRING));
        writer.close();
        closeReader();
        lineFromFile = readLine();
        Assert.assertEquals("a", lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals("b", lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, lineFromFile);
        lineFromFile = readLine();
        Assert.assertEquals(FlatFileItemWriterTests.TEST_STRING, lineFromFile);
    }

    /* Nothing gets written to file if line aggregation fails. */
    @Test
    public void testLineAggregatorFailure() throws Exception {
        writer.setLineAggregator(new org.springframework.batch.item.file.transform.LineAggregator<String>() {
            @Override
            public String aggregate(String item) {
                if (item.equals("2")) {
                    throw new RuntimeException(("aggregation failed on " + item));
                }
                return item;
            }
        });
        @SuppressWarnings("serial")
        List<String> items = new ArrayList<String>() {
            {
                add("1");
                add("2");
                add("3");
            }
        };
        writer.open(executionContext);
        try {
            writer.write(items);
            Assert.fail();
        } catch (RuntimeException expected) {
            Assert.assertEquals("aggregation failed on 2", expected.getMessage());
        }
        // nothing was written to output
        Assert.assertNull(readLine());
    }

    /**
     * If append=true a new output file should still be created on the first run (not restart).
     */
    @Test
    public void testAppendToNotYetExistingFile() throws Exception {
        Resource toBeCreated = new FileSystemResource("build/FlatFileItemWriterTests.out");
        outputFile = toBeCreated.getFile();// enable easy content reading and auto-delete the file

        Assert.assertFalse("output file does not exist yet", toBeCreated.exists());
        writer.setResource(toBeCreated);
        writer.setAppendAllowed(true);
        writer.afterPropertiesSet();
        writer.open(executionContext);
        Assert.assertTrue("output file was created", toBeCreated.exists());
        writer.write(Collections.singletonList("test1"));
        writer.close();
        Assert.assertEquals("test1", readLine());
    }
}

