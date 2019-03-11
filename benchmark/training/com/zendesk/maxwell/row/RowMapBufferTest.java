package com.zendesk.maxwell.row;


import com.zendesk.maxwell.TestWithNameLogging;
import com.zendesk.maxwell.replication.BinlogPosition;
import java.io.IOException;
import java.util.ArrayList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class RowMapBufferTest extends TestWithNameLogging {
    @Test
    public void TestOverflowToDisk() throws Exception {
        RowMapBuffer buffer = new RowMapBuffer(2, 250);// allow about 250 bytes of memory to be used

        RowMap r;
        buffer.add(new RowMap("insert", "foo", "bar", 1000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        buffer.add(new RowMap("insert", "foo", "bar", 2000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        buffer.add(new RowMap("insert", "foo", "bar", 3000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        MatcherAssert.assertThat(buffer.size(), CoreMatchers.is(3L));
        MatcherAssert.assertThat(buffer.inMemorySize(), CoreMatchers.is(2L));
        MatcherAssert.assertThat(buffer.removeFirst().getTimestamp(), CoreMatchers.is(1L));
        MatcherAssert.assertThat(buffer.removeFirst().getTimestamp(), CoreMatchers.is(2L));
        MatcherAssert.assertThat(buffer.removeFirst().getTimestamp(), CoreMatchers.is(3L));
    }

    @Test
    public void TestXOffsetIncrement() throws IOException, ClassNotFoundException {
        RowMapBuffer buffer = new RowMapBuffer(100);
        buffer.add(new RowMap("insert", "foo", "bar", 1000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        buffer.add(new RowMap("insert", "foo", "bar", 2000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        buffer.add(new RowMap("insert", "foo", "bar", 3000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        assert (buffer.removeFirst().getXoffset()) == 0;
        assert (buffer.removeFirst().getXoffset()) == 1;
        assert (buffer.removeFirst().getXoffset()) == 2;
        assert buffer.isEmpty();
        buffer.add(new RowMap("insert", "foo", "bar", 3000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        assert (buffer.removeFirst().getXoffset()) == 3;
        buffer = new RowMapBuffer(100);
        buffer.add(new RowMap("insert", "foo", "bar", 1000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        assert (buffer.removeFirst().getXoffset()) == 0;
    }

    // https://github.com/zendesk/maxwell/issues/996
    @Test
    public void TestOverflowToDiskWithJson() throws Exception {
        RowMapBuffer buffer = new RowMapBuffer(2, 250);// allow about 250 bytes of memory to be used

        RowMap r = new RowMap("insert", "foo", "bar", 1000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L));
        r.putData("json", new RawJSONString("1"));
        buffer.add(r);
        buffer.add(new RowMap("insert", "foo", "bar", 2000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        buffer.add(new RowMap("insert", "foo", "bar", 3000L, new ArrayList<String>(), new com.zendesk.maxwell.replication.Position(new BinlogPosition(3, "mysql.1"), 0L)));
        MatcherAssert.assertThat(buffer.size(), CoreMatchers.is(3L));
        MatcherAssert.assertThat(buffer.inMemorySize(), CoreMatchers.is(2L));
        MatcherAssert.assertThat(buffer.removeFirst().getTimestamp(), CoreMatchers.is(1L));
        MatcherAssert.assertThat(buffer.removeFirst().getTimestamp(), CoreMatchers.is(2L));
        MatcherAssert.assertThat(buffer.removeFirst().getTimestamp(), CoreMatchers.is(3L));
    }
}

