package net.openhft.chronicle.queue;


import RollCycles.TEST_SECONDLY;
import java.util.concurrent.atomic.AtomicReference;
import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Assert;
import org.junit.Test;

import static net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder.single;


@RequiredForClient
public class LastAppendedTest extends ChronicleQueueTestBase {
    @Test
    public void testLastWritten() {
        SetTimeProvider timeProvider = new SetTimeProvider();
        try (ChronicleQueue outQueue = single(getTmpDir()).rollCycle(TEST_SECONDLY).sourceId(1).timeProvider(timeProvider).build()) {
            try (ChronicleQueue inQueue = single(getTmpDir()).rollCycle(TEST_SECONDLY).sourceId(2).timeProvider(timeProvider).build()) {
                // write some initial data to the inqueue
                final LastAppendedTest.Msg msg = inQueue.acquireAppender().methodWriterBuilder(LastAppendedTest.Msg.class).recordHistory(true).get();
                msg.msg("somedata-0");
                timeProvider.advanceMillis(1000);
                // write data into the inQueue
                msg.msg("somedata-1");
                // read a message on the in queue and write it to the out queue
                {
                    LastAppendedTest.Msg out = outQueue.acquireAppender().methodWriterBuilder(LastAppendedTest.Msg.class).recordHistory(true).get();
                    MethodReader methodReader = inQueue.createTailer().methodReader(((LastAppendedTest.Msg) (out::msg)));
                    // reads the somedata-0
                    methodReader.readOne();
                    // reads the somedata-1
                    methodReader.readOne();
                }
                // write data into the inQueue
                msg.msg("somedata-2");
                timeProvider.advanceMillis(2000);
                msg.msg("somedata-3");
                msg.msg("somedata-4");
                System.out.println(inQueue.dump());
                AtomicReference<String> actualValue = new AtomicReference<>();
                // check that we are able to pick up from where we left off, in other words the next read should be somedata-2
                {
                    ExcerptTailer excerptTailer = inQueue.createTailer().afterLastWritten(outQueue);
                    MethodReader methodReader = excerptTailer.methodReader(((LastAppendedTest.Msg) (actualValue::set)));
                    methodReader.readOne();
                    Assert.assertEquals("somedata-2", actualValue.get());
                    methodReader.readOne();
                    Assert.assertEquals("somedata-3", actualValue.get());
                    methodReader.readOne();
                    Assert.assertEquals("somedata-4", actualValue.get());
                }
            }
        }
    }

    @Test
    public void testLastWrittenMetadata() {
        SetTimeProvider timeProvider = new SetTimeProvider();
        try (ChronicleQueue outQueue = single(getTmpDir()).rollCycle(TEST_SECONDLY).sourceId(1).timeProvider(timeProvider).build()) {
            try (ChronicleQueue inQueue = single(getTmpDir()).rollCycle(TEST_SECONDLY).sourceId(2).timeProvider(timeProvider).build()) {
                // write some initial data to the inqueue
                final LastAppendedTest.Msg msg = inQueue.acquireAppender().methodWriterBuilder(LastAppendedTest.Msg.class).recordHistory(true).get();
                msg.msg("somedata-0");
                msg.msg("somedata-1");
                // read a message on the in queue and write it to the out queue
                {
                    LastAppendedTest.Msg out = outQueue.acquireAppender().methodWriterBuilder(LastAppendedTest.Msg.class).recordHistory(true).get();
                    MethodReader methodReader = inQueue.createTailer().methodReader(((LastAppendedTest.Msg) (out::msg)));
                    // reads the somedata-0
                    methodReader.readOne();
                    // reads the somedata-1
                    methodReader.readOne();
                }
                // write data into the inQueue
                msg.msg("somedata-2");
                msg.msg("somedata-3");
                msg.msg("somedata-4");
                try (DocumentContext dc = outQueue.acquireAppender().writingDocument(true)) {
                    dc.wire().write("some metadata");
                }
                AtomicReference<String> actualValue = new AtomicReference<>();
                // check that we are able to pick up from where we left off, in other words the next read should be somedata-2
                {
                    ExcerptTailer excerptTailer = inQueue.createTailer().afterLastWritten(outQueue);
                    MethodReader methodReader = excerptTailer.methodReader(((LastAppendedTest.Msg) (actualValue::set)));
                    methodReader.readOne();
                    Assert.assertEquals("somedata-2", actualValue.get());
                    methodReader.readOne();
                    Assert.assertEquals("somedata-3", actualValue.get());
                    methodReader.readOne();
                    Assert.assertEquals("somedata-4", actualValue.get());
                }
            }
        }
    }

    public interface Msg {
        void msg(String s);
    }
}

