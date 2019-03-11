package org.embulk.spi;


import java.util.ArrayList;
import java.util.List;
import org.embulk.EmbulkTestRuntime;
import org.embulk.spi.time.Timestamp;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestPageBuilderReader {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    public static class MockPageOutput implements PageOutput {
        public List<Page> pages;

        public MockPageOutput() {
            this.pages = new ArrayList();
        }

        @Override
        public void add(Page page) {
            pages.add(page);
        }

        @Override
        public void finish() {
        }

        @Override
        public void close() {
        }
    }

    private BufferAllocator bufferAllocator;

    private PageReader reader;

    private PageBuilder builder;

    @Test
    public void testBoolean() {
        check(Schema.builder().add("col1", org.embulk.spi.type.Types.BOOLEAN).build(), false, true, true);
    }

    @Test
    public void testLong() {
        check(Schema.builder().add("col1", org.embulk.spi.type.Types.LONG).build(), 1L, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Test
    public void testDouble() {
        check(Schema.builder().add("col1", org.embulk.spi.type.Types.DOUBLE).build(), 8.1, 3.141592, 4.3);
    }

    @Test
    public void testUniqueStrings() {
        check(Schema.builder().add("col1", org.embulk.spi.type.Types.STRING).build(), "test1", "test2", "test0");
    }

    @Test
    public void testDuplicateStrings() {
        check(Schema.builder().add("col1", org.embulk.spi.type.Types.STRING).build(), "test1", "test1", "test1");
    }

    @Test
    public void testDuplicateStringsMultiColumns() {
        check(Schema.builder().add("col1", org.embulk.spi.type.Types.STRING).add("col1", org.embulk.spi.type.Types.STRING).build(), "test2", "test1", "test1", "test2", "test2", "test0", "test1", "test1");
    }

    @Test
    public void testTimestamp() {
        check(Schema.builder().add("col1", org.embulk.spi.type.Types.TIMESTAMP).build(), Timestamp.ofEpochMilli(0), Timestamp.ofEpochMilli(10));
    }

    @Test
    public void testJson() {
        check(Schema.builder().add("col1", org.embulk.spi.type.Types.JSON).build(), getJsonSampleData());
    }

    @Test
    public void testNull() {
        check(Schema.builder().add("col3", org.embulk.spi.type.Types.DOUBLE).add("col1", org.embulk.spi.type.Types.STRING).add("col3", org.embulk.spi.type.Types.LONG).add("col3", org.embulk.spi.type.Types.BOOLEAN).add("col2", org.embulk.spi.type.Types.TIMESTAMP).add("col4", org.embulk.spi.type.Types.JSON).build(), null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void testMixedTypes() {
        check(Schema.builder().add("col3", org.embulk.spi.type.Types.DOUBLE).add("col1", org.embulk.spi.type.Types.STRING).add("col3", org.embulk.spi.type.Types.LONG).add("col3", org.embulk.spi.type.Types.BOOLEAN).add("col2", org.embulk.spi.type.Types.TIMESTAMP).add("col4", org.embulk.spi.type.Types.JSON).build(), 8122.0, "val1", 3L, false, Timestamp.ofEpochMilli(0), getJsonSampleData(), 140.15, "val2", Long.MAX_VALUE, true, Timestamp.ofEpochMilli(10), getJsonSampleData());
    }

    @Test
    public void testEmptySchema() {
        TestPageBuilderReader.MockPageOutput output = new TestPageBuilderReader.MockPageOutput();
        this.builder = new PageBuilder(bufferAllocator, Schema.builder().build(), output);
        builder.addRecord();
        builder.addRecord();
        builder.flush();
        builder.close();
        this.reader = new PageReader(Schema.builder().build());
        Assert.assertEquals(1, output.pages.size());
        reader.setPage(output.pages.get(0));
        Assert.assertTrue(reader.nextRecord());
        Assert.assertTrue(reader.nextRecord());
        Assert.assertFalse(reader.nextRecord());
    }

    @Test
    public void testRenewPage() {
        this.bufferAllocator = new BufferAllocator() {
            @Override
            public Buffer allocate() {
                return Buffer.allocate(1);
            }

            @Override
            public Buffer allocate(int minimumCapacity) {
                return Buffer.allocate(minimumCapacity);
            }
        };
        Assert.assertEquals(9, buildPages(Schema.builder().add("col1", org.embulk.spi.type.Types.LONG).build(), 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L).size());
    }

    @Test
    public void testRenewPageWithStrings() {
        this.bufferAllocator = new BufferAllocator() {
            @Override
            public Buffer allocate() {
                return Buffer.allocate(1);
            }

            @Override
            public Buffer allocate(int minimumCapacity) {
                return Buffer.allocate(minimumCapacity);
            }
        };
        Assert.assertEquals(3, buildPages(Schema.builder().add("col1", org.embulk.spi.type.Types.LONG).add("col1", org.embulk.spi.type.Types.STRING).build(), 0L, "record0", 1L, "record1", 3L, "record3").size());
    }

    @Test
    public void testDoubleWriteStringsToRow() {
        TestPageBuilderReader.MockPageOutput output = new TestPageBuilderReader.MockPageOutput();
        Schema schema = Schema.builder().add("col0", org.embulk.spi.type.Types.STRING).add("col1", org.embulk.spi.type.Types.STRING).add("col2", org.embulk.spi.type.Types.STRING).build();
        builder = new PageBuilder(bufferAllocator, schema, output);
        builder.setString(0, "v0");
        builder.setString(1, "v1");
        builder.setNull(2);
        builder.setString(0, "v2");// stored to page for col0

        builder.setNull(1);// null is stored to page for col1

        builder.setString(2, "v3");// stored to page for col2

        builder.addRecord();
        builder.finish();
        builder.close();
        reader = new PageReader(schema);
        reader.setPage(output.pages.get(0));
        Assert.assertTrue(reader.nextRecord());
        Assert.assertEquals(reader.getString(0), "v2");
        Assert.assertTrue(reader.isNull(1));
        Assert.assertEquals(reader.getString(2), "v3");
        Assert.assertFalse(reader.nextRecord());
        reader.close();
    }

    @Test
    public void testDoubleWriteJsonsToRow() {
        TestPageBuilderReader.MockPageOutput output = new TestPageBuilderReader.MockPageOutput();
        Schema schema = Schema.builder().add("col0", org.embulk.spi.type.Types.JSON).add("col1", org.embulk.spi.type.Types.JSON).add("col2", org.embulk.spi.type.Types.JSON).build();
        builder = new PageBuilder(bufferAllocator, schema, output);
        builder.setJson(0, newString("v0"));
        builder.setJson(1, newString("v1"));
        builder.setNull(2);
        builder.setJson(0, newString("v2"));// store to page for col0

        builder.setNull(1);
        // null is stored to page for col1
        builder.setJson(2, newString("v3"));// store to page for col2

        builder.addRecord();
        builder.finish();
        builder.close();
        reader = new PageReader(schema);
        reader.setPage(output.pages.get(0));
        Assert.assertTrue(reader.nextRecord());
        Assert.assertEquals(reader.getJson(0), newString("v2"));
        Assert.assertTrue(reader.isNull(1));
        Assert.assertEquals(reader.getJson(2), newString("v3"));
        Assert.assertFalse(reader.nextRecord());
        reader.close();
    }

    @Test
    public void testRepeatableClose() {
        TestPageBuilderReader.MockPageOutput output = new TestPageBuilderReader.MockPageOutput();
        this.builder = new PageBuilder(bufferAllocator, Schema.builder().add("col1", org.embulk.spi.type.Types.STRING).build(), output);
        builder.close();
        builder.close();
    }

    @Test
    public void testRepeatableFlush() {
        TestPageBuilderReader.MockPageOutput output = new TestPageBuilderReader.MockPageOutput();
        this.builder = new PageBuilder(bufferAllocator, Schema.builder().add("col1", org.embulk.spi.type.Types.STRING).build(), output);
        builder.flush();
        builder.flush();
    }
}

