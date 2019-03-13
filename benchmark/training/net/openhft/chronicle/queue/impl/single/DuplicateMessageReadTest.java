package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.queue.RollCycles;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static RollCycles.DAILY;


public final class DuplicateMessageReadTest {
    private static final RollCycles QUEUE_CYCLE = DAILY;

    @Test
    public void shouldNotReceiveDuplicateMessages() throws Exception {
        final File location = DirectoryUtils.tempDir(DuplicateMessageReadTest.class.getSimpleName());
        final ChronicleQueue chronicleQueue = SingleChronicleQueueBuilder.binary(location).rollCycle(DuplicateMessageReadTest.QUEUE_CYCLE).build();
        final ExcerptAppender appender = chronicleQueue.acquireAppender();
        appender.pretouch();
        final List<DuplicateMessageReadTest.Data> expected = new ArrayList<>();
        for (int i = 50; i < 60; i++) {
            expected.add(new DuplicateMessageReadTest.Data(i));
        }
        final ExcerptTailer tailer = chronicleQueue.createTailer();
        tailer.toEnd();// move to end of chronicle before writing

        for (final DuplicateMessageReadTest.Data data : expected) {
            DuplicateMessageReadTest.write(appender, data);
        }
        final List<DuplicateMessageReadTest.Data> actual = new ArrayList<>();
        DuplicateMessageReadTest.Data data;
        while ((data = DuplicateMessageReadTest.read(tailer)) != null) {
            actual.add(data);
        } 
        Assert.assertThat(actual, CoreMatchers.is(expected));
    }

    private static final class Data {
        private final int id;

        private Data(final int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            DuplicateMessageReadTest.Data data = ((DuplicateMessageReadTest.Data) (o));
            return (id) == (data.id);
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "" + (id);
        }
    }
}

