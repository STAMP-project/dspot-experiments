package org.embulk.spi;


import FileOutputPlugin.Control;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.config.TaskReport;
import org.embulk.config.TaskSource;
import org.embulk.spi.time.Timestamp;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.msgpack.value.ImmutableMapValue;


public class TestFileOutputRunner {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    public interface PluginTask extends Task {}

    private static class MockFileOutputPlugin implements FileOutputPlugin {
        Boolean transactionCompleted = null;

        @Override
        public ConfigDiff transaction(ConfigSource config, int taskCount, FileOutputPlugin.Control control) {
            TestFileOutputRunner.PluginTask task = config.loadConfig(TestFileOutputRunner.PluginTask.class);
            control.run(dump());
            return Exec.newConfigDiff();
        }

        @Override
        public ConfigDiff resume(TaskSource taskSource, int taskCount, FileOutputPlugin.Control control) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void cleanup(TaskSource taskSource, int taskCount, List<TaskReport> successTaskReports) {
        }

        @Override
        public TransactionalFileOutput open(TaskSource taskSource, final int taskIndex) {
            return new TransactionalFileOutput() {
                @Override
                public void nextFile() {
                }

                @Override
                public void add(Buffer buffer) {
                }

                @Override
                public void finish() {
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
                    return Exec.newTaskReport();
                }
            };
        }
    }

    @Test
    public void testMockFormatterIteration() {
        TestFileOutputRunner.MockFileOutputPlugin fileOutputPlugin = new TestFileOutputRunner.MockFileOutputPlugin();
        final FileOutputRunner runner = new FileOutputRunner(fileOutputPlugin);
        ImmutableList<ImmutableMap<String, Object>> columns = ImmutableList.of(ImmutableMap.<String, Object>of("name", "col1", "type", "boolean", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col2", "type", "long", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col3", "type", "double", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col4", "type", "string", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col5", "type", "timestamp", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col6", "type", "json", "option", ImmutableMap.of()));
        ConfigSource config = Exec.newConfigSource().set("type", "unused?").set("formatter", ImmutableMap.of("type", "mock", "columns", columns));
        final Schema schema = config.getNested("formatter").loadConfig(MockParserPlugin.PluginTask.class).getSchemaConfig().toSchema();
        runner.transaction(config, schema, 1, new OutputPlugin.Control() {
            public List<TaskReport> run(final TaskSource outputTask) {
                TransactionalPageOutput tran = runner.open(outputTask, schema, 1);
                boolean committed = false;
                try {
                    ImmutableMapValue jsonValue = newMap(newString("_c1"), newBoolean(true), newString("_c2"), newInteger(10), newString("_c3"), newString("embulk"), newString("_c4"), newMap(newString("k"), newString("v")));
                    for (Page page : PageTestUtils.buildPage(runtime.getBufferAllocator(), schema, true, 2L, 3.0, "45", Timestamp.ofEpochMilli(678L), jsonValue, true, 2L, 3.0, "45", Timestamp.ofEpochMilli(678L), jsonValue)) {
                        tran.add(page);
                    }
                    tran.commit();
                    committed = true;
                } finally {
                    if (!committed) {
                        tran.abort();
                    }
                    tran.close();
                }
                return new ArrayList<TaskReport>();
            }
        });
        Assert.assertEquals(true, fileOutputPlugin.transactionCompleted);
        Assert.assertEquals(2, MockFormatterPlugin.records.size());
        for (List<Object> record : MockFormatterPlugin.records) {
            Assert.assertEquals(Boolean.TRUE, record.get(0));
            Assert.assertEquals(2L, record.get(1));
            Assert.assertEquals(3.0, ((Double) (record.get(2))), 0.1);
            Assert.assertEquals("45", record.get(3));
            Assert.assertEquals(678L, toEpochMilli());
            Assert.assertEquals("{\"_c1\":true,\"_c2\":10,\"_c3\":\"embulk\",\"_c4\":{\"k\":\"v\"}}", record.get(5).toString());
        }
    }

    @Test
    public void testTransactionAborted() {
        TestFileOutputRunner.MockFileOutputPlugin fileOutputPlugin = new TestFileOutputRunner.MockFileOutputPlugin();
        final FileOutputRunner runner = new FileOutputRunner(fileOutputPlugin);
        ImmutableList<ImmutableMap<String, Object>> columns = ImmutableList.of(ImmutableMap.<String, Object>of("name", "col1", "type", "boolean", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col2", "type", "long", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col3", "type", "double", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col4", "type", "string", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col5", "type", "timestamp", "option", ImmutableMap.of()), ImmutableMap.<String, Object>of("name", "col6", "type", "json", "option", ImmutableMap.of()));
        ConfigSource config = Exec.newConfigSource().set("type", "unused?").set("formatter", ImmutableMap.of("type", "mock", "columns", columns));
        final Schema schema = config.getNested("formatter").loadConfig(MockParserPlugin.PluginTask.class).getSchemaConfig().toSchema();
        try {
            runner.transaction(config, schema, 1, new OutputPlugin.Control() {
                public List<TaskReport> run(final TaskSource outputTask) {
                    TransactionalPageOutput tran = runner.open(outputTask, schema, 1);
                    boolean committed = false;
                    try {
                        tran.add(null);
                        tran.commit();
                        committed = true;
                    } finally {
                        if (!committed) {
                            tran.abort();
                        }
                        tran.close();
                    }
                    return new ArrayList<TaskReport>();
                }
            });
        } catch (NullPointerException npe) {
            // Just passing through.
        }
        Assert.assertEquals(false, fileOutputPlugin.transactionCompleted);
    }
}

