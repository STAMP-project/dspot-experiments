/**
 * Copyright 2008-2017 the original author or authors.
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
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;


/**
 * Tests for {@link MultiResourceItemWriter} delegating to
 * {@link FlatFileItemWriter}.
 */
public class MultiResourceItemWriterFlatFileTests extends AbstractMultiResourceItemWriterTests {
    /**
     *
     *
     * @author dsyer
     */
    private final class WriterCallback implements TransactionCallback<Void> {
        private List<? extends String> list;

        public WriterCallback(List<? extends String> list) {
            super();
            this.list = list;
        }

        @Override
        public Void doInTransaction(TransactionStatus status) {
            try {
                tested.write(list);
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected");
            }
            return null;
        }
    }

    private FlatFileItemWriter<String> delegate;

    @Test
    public void testBasicMultiResourceWriteScenario() throws Exception {
        super.setUp(delegate);
        tested.open(executionContext);
        tested.write(Arrays.asList("1", "2", "3"));
        File part1 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        Assert.assertEquals("123", readFile(part1));
        tested.write(Arrays.asList("4"));
        File part2 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        Assert.assertEquals("4", readFile(part2));
        tested.write(Arrays.asList("5"));
        Assert.assertEquals("45", readFile(part2));
        tested.write(Arrays.asList("6", "7", "8", "9"));
        File part3 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(3))));
        Assert.assertTrue(part3.exists());
        Assert.assertEquals("6789", readFile(part3));
    }

    @Test
    public void testUpdateAfterDelegateClose() throws Exception {
        super.setUp(delegate);
        tested.open(executionContext);
        tested.update(executionContext);
        Assert.assertEquals(0, executionContext.getInt(tested.getExecutionContextKey("resource.item.count")));
        Assert.assertEquals(1, executionContext.getInt(tested.getExecutionContextKey("resource.index")));
        tested.write(Arrays.asList("1", "2", "3"));
        tested.update(executionContext);
        Assert.assertEquals(0, executionContext.getInt(tested.getExecutionContextKey("resource.item.count")));
        Assert.assertEquals(2, executionContext.getInt(tested.getExecutionContextKey("resource.index")));
    }

    @Test
    public void testMultiResourceWriteScenarioWithFooter() throws Exception {
        delegate.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("f");
            }
        });
        super.setUp(delegate);
        tested.open(executionContext);
        tested.write(Arrays.asList("1", "2", "3"));
        File part1 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        tested.write(Arrays.asList("4"));
        File part2 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        tested.close();
        Assert.assertEquals("123f", readFile(part1));
        Assert.assertEquals("4f", readFile(part2));
    }

    @Test
    public void testTransactionalMultiResourceWriteScenarioWithFooter() throws Exception {
        delegate.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("f");
            }
        });
        super.setUp(delegate);
        tested.open(executionContext);
        ResourcelessTransactionManager transactionManager = new ResourcelessTransactionManager();
        execute(new MultiResourceItemWriterFlatFileTests.WriterCallback(Arrays.asList("1", "2", "3")));
        File part1 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        execute(new MultiResourceItemWriterFlatFileTests.WriterCallback(Arrays.asList("4")));
        File part2 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        tested.close();
        Assert.assertEquals("123f", readFile(part1));
        Assert.assertEquals("4f", readFile(part2));
    }

    @Test
    public void testRestart() throws Exception {
        super.setUp(delegate);
        tested.open(executionContext);
        tested.write(Arrays.asList("1", "2", "3"));
        File part1 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        Assert.assertEquals("123", readFile(part1));
        tested.write(Arrays.asList("4"));
        File part2 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        Assert.assertEquals("4", readFile(part2));
        tested.update(executionContext);
        tested.close();
        tested.open(executionContext);
        tested.write(Arrays.asList("5"));
        Assert.assertEquals("45", readFile(part2));
        tested.write(Arrays.asList("6", "7", "8", "9"));
        File part3 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(3))));
        Assert.assertTrue(part3.exists());
        Assert.assertEquals("6789", readFile(part3));
    }

    @Test
    public void testRestartWithFooter() throws Exception {
        delegate.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("f");
            }
        });
        super.setUp(delegate);
        tested.open(executionContext);
        tested.write(Arrays.asList("1", "2", "3"));
        File part1 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        Assert.assertEquals("123f", readFile(part1));
        tested.write(Arrays.asList("4"));
        File part2 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        Assert.assertEquals("4", readFile(part2));
        tested.update(executionContext);
        tested.close();
        tested.open(executionContext);
        tested.write(Arrays.asList("5"));
        Assert.assertEquals("45f", readFile(part2));
        tested.write(Arrays.asList("6", "7", "8", "9"));
        File part3 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(3))));
        Assert.assertTrue(part3.exists());
        Assert.assertEquals("6789f", readFile(part3));
    }

    @Test
    public void testTransactionalRestartWithFooter() throws Exception {
        delegate.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("f");
            }
        });
        super.setUp(delegate);
        tested.open(executionContext);
        ResourcelessTransactionManager transactionManager = new ResourcelessTransactionManager();
        execute(new MultiResourceItemWriterFlatFileTests.WriterCallback(Arrays.asList("1", "2", "3")));
        File part1 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(1))));
        Assert.assertTrue(part1.exists());
        Assert.assertEquals("123f", readFile(part1));
        execute(new MultiResourceItemWriterFlatFileTests.WriterCallback(Arrays.asList("4")));
        File part2 = new File(((file.getAbsolutePath()) + (suffixCreator.getSuffix(2))));
        Assert.assertTrue(part2.exists());
        Assert.assertEquals("4", readFile(part2));
        tested.update(executionContext);
        tested.close();
        tested.open(executionContext);
        execute(new MultiResourceItemWriterFlatFileTests.WriterCallback(Arrays.asList("5")));
        Assert.assertEquals("45f", readFile(part2));
    }
}

