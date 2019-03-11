package org.embulk.spi;


import FileInputPlugin.Control;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigSource;
import org.embulk.config.TaskReport;
import org.embulk.config.TaskSource;
import org.embulk.spi.util.Pages;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestFileInputRunner {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    private static class MockFileInputPlugin implements FileInputPlugin {
        Boolean transactionCompleted = null;

        Queue<Buffer> buffers;

        public MockFileInputPlugin(Queue<Buffer> buffers) {
            this.buffers = buffers;
        }

        @Override
        public ConfigDiff transaction(ConfigSource config, FileInputPlugin.Control control) {
            control.run(Exec.newTaskSource(), 1);
            return null;
        }

        @Override
        public ConfigDiff resume(TaskSource taskSource, int taskCount, FileInputPlugin.Control control) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void cleanup(TaskSource taskSource, int taskCount, List<TaskReport> successTaskReports) {
        }

        public TransactionalFileInput open(TaskSource taskSource, int taskIndex) {
            return new TransactionalFileInput() {
                @Override
                public Buffer poll() {
                    return buffers.poll();
                }

                @Override
                public boolean nextFile() {
                    return !(buffers.isEmpty());
                }

                @Override
                public void close() {
                }

                @Override
                public void abort() {
                    transactionCompleted = false;
                }

                @Override
                public TaskReport commit() {
                    transactionCompleted = true;
                    return null;
                }
            };
        }
    }

    @Test
    public void testMockParserIteration() {
        Buffer[] buffers = new Buffer[]{ runtime.getBufferAllocator().allocate(), runtime.getBufferAllocator().allocate() };
        TestFileInputRunner.MockFileInputPlugin fileInputPlugin = new TestFileInputRunner.MockFileInputPlugin(new LinkedList<Buffer>(Arrays.asList(buffers)));
        final FileInputRunner runner = new FileInputRunner(fileInputPlugin);
        ConfigSource config = Exec.newConfigSource().set("parser", ImmutableMap.of("type", "mock", "columns", ImmutableList.of(ImmutableMap.of("name", "col1", "type", "boolean", "option", ImmutableMap.of()), ImmutableMap.of("name", "col2", "type", "long", "option", ImmutableMap.of()), ImmutableMap.of("name", "col3", "type", "double", "option", ImmutableMap.of()), ImmutableMap.of("name", "col4", "type", "string", "option", ImmutableMap.of()), ImmutableMap.of("name", "col5", "type", "timestamp", "option", ImmutableMap.of()), ImmutableMap.of("name", "col6", "type", "json", "option", ImmutableMap.of()))));
        final TestPageBuilderReader.MockPageOutput output = new TestPageBuilderReader.MockPageOutput();
        runner.transaction(config, new InputPlugin.Control() {
            public List<TaskReport> run(TaskSource inputTaskSource, Schema schema, int taskCount) {
                List<TaskReport> reports = new ArrayList<>();
                reports.add(runner.run(inputTaskSource, schema, 0, output));
                return reports;
            }
        });
        Assert.assertEquals(true, fileInputPlugin.transactionCompleted);
        Assert.assertEquals(1, output.pages.size());
        Schema schema = config.getNested("parser").loadConfig(MockParserPlugin.PluginTask.class).getSchemaConfig().toSchema();
        List<Object[]> records = Pages.toObjects(schema, output.pages);
        Assert.assertEquals(2, records.size());
        for (Object[] record : records) {
            Assert.assertEquals(6, record.length);
            Assert.assertEquals(true, record[0]);
            Assert.assertEquals(2L, record[1]);
            Assert.assertEquals(3.0, ((Double) (record[2])), 0.01);
            Assert.assertEquals("45", record[3]);
            Assert.assertEquals(678L, toEpochMilli());
            Assert.assertEquals("{\"_c2\":10,\"_c1\":true,\"_c4\":{\"k\":\"v\"},\"_c3\":\"embulk\"}", record[5].toString());
        }
    }

    @Test
    public void testTransactionAborted() {
        Buffer[] buffers = new Buffer[]{ runtime.getBufferAllocator().allocate(), runtime.getBufferAllocator().allocate() };
        TestFileInputRunner.MockFileInputPlugin fileInputPlugin = new TestFileInputRunner.MockFileInputPlugin(new LinkedList<Buffer>(Arrays.asList(buffers)));
        final FileInputRunner runner = new FileInputRunner(fileInputPlugin);
        ConfigSource config = Exec.newConfigSource().set("parser", ImmutableMap.of("type", "mock", "columns", ImmutableList.of(ImmutableMap.of("name", "col1", "type", "boolean", "option", ImmutableMap.of()), ImmutableMap.of("name", "col2", "type", "long", "option", ImmutableMap.of()), ImmutableMap.of("name", "col3", "type", "double", "option", ImmutableMap.of()), ImmutableMap.of("name", "col4", "type", "string", "option", ImmutableMap.of()), ImmutableMap.of("name", "col5", "type", "timestamp", "option", ImmutableMap.of()), ImmutableMap.of("name", "col6", "type", "json", "option", ImmutableMap.of()))));
        final TestPageBuilderReader.MockPageOutput output = new TestPageBuilderReader.MockPageOutput();
        MockParserPlugin.raiseException = true;
        try {
            runner.transaction(config, new InputPlugin.Control() {
                public List<TaskReport> run(TaskSource inputTaskSource, Schema schema, int taskCount) {
                    List<TaskReport> reports = new ArrayList<>();
                    reports.add(runner.run(inputTaskSource, schema, 0, output));
                    return reports;
                }
            });
        } catch (RuntimeException re) {
            // Just passing through.
        }
        Assert.assertEquals(false, fileInputPlugin.transactionCompleted);
        Assert.assertEquals(0, output.pages.size());
    }
}

