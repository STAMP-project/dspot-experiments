package org.gridkit.jvmtool.codec.stacktrace;


import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.gridkit.jvmtool.event.CommonEvent;
import org.gridkit.jvmtool.event.Event;
import org.gridkit.jvmtool.event.EventReader;
import org.gridkit.jvmtool.event.SimpleTagCollection;
import org.gridkit.jvmtool.event.TagCollection;
import org.gridkit.jvmtool.stacktrace.CounterCollection;
import org.gridkit.jvmtool.stacktrace.StackFrame;
import org.gridkit.jvmtool.stacktrace.StackFrameList;
import org.junit.Test;


public class EventReadWriteTest {
    @Test
    public void verify_simple_event() throws Exception {
        EventReadWriteTest.TestDataEvent a = new EventReadWriteTest.TestDataEvent();
        a.timestamp(1000);
        a.tag("A", "1");
        a.tag("B", "1");
        a.tag("B", "2");
        a.tag("C", "0");
        a.set("qwerty", 10);
        a.set("QWERTY", ((Long.MAX_VALUE) - (Integer.MAX_VALUE)));
        CommonEvent b = ((CommonEvent) (safeWriteReadEvents(a).iterator().next()));
        assertThat(b).isNotInstanceOf(ThreadSnapshotEvent.class);
        assertThat(b.timestamp()).isEqualTo(1000);
        assertThat(b.counters().getValue("qwerty")).isEqualTo(10);
        assertThat(b.counters().getValue("QWERTY")).isEqualTo(((Long.MAX_VALUE) - (Integer.MAX_VALUE)));
        assertThat(b.tags()).containsOnly("A", "B", "C");
        assertThat(b.tags().tagsFor("A")).containsOnly("1");
        assertThat(b.tags().tagsFor("B")).containsOnly("1", "2");
        assertThat(b.tags().tagsFor("C")).containsOnly("0");
        assertThat(b).is(EventEqualToCondition.eventEquals(a));
    }

    @Test
    public void verify_3_same_events() throws Exception {
        EventReadWriteTest.TestDataEvent a = new EventReadWriteTest.TestDataEvent();
        a.timestamp(1000);
        a.tag("A", "1");
        a.tag("B", "1");
        a.tag("B", "2");
        a.tag("C", "0");
        a.set("qwerty", 10);
        a.set("QWERTY", ((Long.MAX_VALUE) - (Integer.MAX_VALUE)));
        EventReader<Event> reader = safeWriteReadEvents(a, a, a);
        assertThat(((Iterator<Event>) (reader))).is(EventSeqEqualToCondition.exactlyAs(a, a, a));
    }

    @Test
    public void verify_more_simple_events() throws Exception {
        EventReadWriteTest.TestDataEvent a1 = new EventReadWriteTest.TestDataEvent();
        a1.timestamp(1000);
        a1.tag("A", "1");
        a1.tag("B", "1");
        a1.tag("B", "2");
        a1.tag("C", "0");
        a1.set("qwerty", 10);
        a1.set("QWERTY", ((Long.MAX_VALUE) - (Integer.MAX_VALUE)));
        EventReadWriteTest.TestDataEvent a2 = new EventReadWriteTest.TestDataEvent();
        a2.timestamp(2000);
        a2.tag("AA", "1");
        a2.tag("BB", "1");
        a2.tag("BB", "2");
        a2.tag("CC", "0");
        a2.set("Q", 20);
        a2.set("q", ((Long.MAX_VALUE) - (Integer.MAX_VALUE)));
        EventReader<Event> reader = safeWriteReadEvents(a1, a2, a1, a2, a1);
        assertThat(reader.next()).is(EventEqualToCondition.eventEquals(a1));
        assertThat(reader.next()).is(EventEqualToCondition.eventEquals(a2));
        assertThat(reader.next()).is(EventEqualToCondition.eventEquals(a1));
        assertThat(reader.next()).is(EventEqualToCondition.eventEquals(a2));
        assertThat(reader.next()).is(EventEqualToCondition.eventEquals(a1));
        assertThat(reader.hasNext()).isFalse();
        assertThat(reader.hasNext()).isFalse();
    }

    @Test
    public void verify_thread_snapshot() throws IOException {
        EventReadWriteTest.ThreadEvent a = new EventReadWriteTest.ThreadEvent();
        a.timestamp(10000);
        a.threadId(100);
        a.threadName("Abc");
        a.stackTrace(genTrace(10));
        ThreadSnapshotEvent b = ((ThreadSnapshotEvent) (safeWriteReadEvents(a).iterator().next()));
        assertThat(b.timestamp()).isEqualTo(10000);
        assertThat(b.threadId()).isEqualTo(100);
        assertThat(b.threadName()).isEqualTo("Abc");
        assertThat(b.threadState()).isNull();
        assertThat(b.counters()).containsExactly("thread.javaId");
        assertThat(b.tags().toString()).isEqualTo("[thread.javaName:Abc]");
        assertThat(b).is(EventEqualToCondition.eventEquals(a));
    }

    @Test
    public void verify_mixed_event_set() throws IOException {
        EventReadWriteTest.ThreadEvent a1 = new EventReadWriteTest.ThreadEvent();
        a1.timestamp(10000);
        a1.threadId(100);
        a1.threadName("Abc");
        a1.stackTrace(genTrace(10));
        EventReadWriteTest.ThreadEvent a2 = new EventReadWriteTest.ThreadEvent();
        a2.timestamp(20000);
        a2.threadId(101);
        a2.threadName("xYZ");
        a2.stackTrace(genTrace(5));
        a2.set("thread.cpu", 1000);
        EventReadWriteTest.TestDataEvent a3 = new EventReadWriteTest.TestDataEvent();
        a3.timestamp(1000);
        a3.tag("A", "1");
        a3.tag("B", "1");
        a3.tag("B", "2");
        a3.tag("C", "0");
        a3.set("qwerty", 10);
        a3.set("QWERTY", ((Long.MAX_VALUE) - (Integer.MAX_VALUE)));
        EventReadWriteTest.TestDataEvent a4 = new EventReadWriteTest.TestDataEvent();
        a4.timestamp(2000);
        a4.tag("AA", "1");
        a4.tag("BB", "1");
        a4.tag("BB", "2");
        a4.tag("CC", "0");
        a4.set("Q", 20);
        a4.set("qwerty", ((Long.MAX_VALUE) - (Integer.MAX_VALUE)));
        EventReader<Event> reader = safeWriteReadEvents(a1, a2, a3, a4, a1, a3, a2, a4);
        assertThat(((Iterable<Event>) (reader))).is(EventSeqEqualToCondition.exactlyAs(a1, a2, a3, a4, a1, a3, a2, a4));
    }

    public static class TestDataEvent implements CommonEvent {
        long timestamp = 0;

        SimpleTagCollection tags = new SimpleTagCollection();

        EventReadWriteTest.TestCounterCollection counters = new EventReadWriteTest.TestCounterCollection();

        @Override
        public long timestamp() {
            return timestamp;
        }

        @Override
        public CounterCollection counters() {
            return counters;
        }

        @Override
        public TagCollection tags() {
            return tags;
        }

        public void timestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public void tag(String key, String tag) {
            tags.put(key, tag);
        }

        public void set(String key, long value) {
            counters.set(key, value);
        }
    }

    public static class ThreadEvent extends EventReadWriteTest.TestDataEvent implements ThreadSnapshotEvent {
        String threadName;

        long threadId = -1;

        Thread.State state = null;

        StackFrameList trace;

        @Override
        public long threadId() {
            return threadId;
        }

        public void threadId(long threadId) {
            this.threadId = threadId;
            counters.set("thread.javaId", threadId);
        }

        @Override
        public String threadName() {
            return threadName;
        }

        public void threadName(String threadName) {
            this.threadName = threadName;
            tags.put("thread.javaName", threadName);
        }

        @Override
        public Thread.State threadState() {
            return state;
        }

        public void threadState(Thread.State state) {
            this.state = state;
        }

        @Override
        public StackFrameList stackTrace() {
            return trace;
        }

        public void stackTrace(StackTraceElement[] trace) {
            StackFrame[] ftrace = new StackFrame[trace.length];
            for (int i = 0; i != (trace.length); ++i) {
                ftrace[i] = new StackFrame(trace[i]);
            }
            this.trace = new org.gridkit.jvmtool.stacktrace.StackFrameArray(ftrace);
        }
    }

    private static class TestCounterCollection implements CounterCollection {
        private Map<String, Long> counters = new TreeMap<String, Long>();

        public void set(String key, long value) {
            counters.put(key, value);
        }

        @Override
        public Iterator<String> iterator() {
            return counters.keySet().iterator();
        }

        @Override
        public long getValue(String key) {
            Long n = counters.get(key);
            return n == 0 ? Long.MIN_VALUE : n;
        }

        public EventReadWriteTest.TestCounterCollection clone() {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (String key : this) {
                if ((sb.length()) > 1) {
                    sb.append(", ");
                }
                sb.append(key).append(": ").append(getValue(key));
            }
            sb.append(']');
            return sb.toString();
        }
    }
}

