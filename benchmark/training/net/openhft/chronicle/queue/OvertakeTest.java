package net.openhft.chronicle.queue;


import BufferMode.None;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import org.junit.Assert;
import org.junit.Test;


/**
 * Index runs away on double close - AM
 */
@RequiredForClient
public class OvertakeTest {
    private String path;

    private long a_index;

    private int messages = 500;

    @Test
    public void appendAndTail() {
        ChronicleQueue tailer_queue = ChronicleQueue.singleBuilder(path).testBlockSize().writeBufferMode(None).build();
        ExcerptTailer tailer = tailer_queue.createTailer();
        tailer = tailer.toStart();
        long t_index;
        t_index = OvertakeTest.doReadBad(tailer, messages, false);
        Assert.assertEquals(a_index, t_index);
        tailer = tailer_queue.createTailer();
        tailer = tailer.toStart();
        t_index = OvertakeTest.doReadBad(tailer, messages, true);
        Assert.assertEquals(a_index, t_index);
    }

    @Test
    public void threadingTest() throws Exception {
        System.out.println("Continue appending");
        ExecutorService execService = Executors.newFixedThreadPool(2);
        SynchronousQueue<Long> sync = new SynchronousQueue<>();
        long t_index;
        OvertakeTest.MyAppender myapp = new OvertakeTest.MyAppender(sync);
        Future<Long> f = execService.submit(myapp);
        ChronicleQueue tailer_queue = ChronicleQueue.singleBuilder(path).testBlockSize().writeBufferMode(None).build();
        t_index = 0;
        OvertakeTest.MyTailer mytailer = new OvertakeTest.MyTailer(tailer_queue, t_index, sync);
        Future<Long> f2 = execService.submit(mytailer);
        t_index = f2.get(10, TimeUnit.SECONDS);
        a_index = f.get(10, TimeUnit.SECONDS);
        Assert.assertTrue(((a_index) == t_index));
    }

    class MyAppender implements Callable<Long> {
        ChronicleQueue queue;

        ExcerptAppender appender;

        SynchronousQueue<Long> sync;

        MyAppender(// SingleChronicleQueue q,
        SynchronousQueue<Long> sync) {
            // queue = q;
            this.sync = sync;
        }

        @Override
        public Long call() throws Exception {
            queue = // .testBlockSize()
            // .rollCycle(TEST_DAILY)
            ChronicleQueue.singleBuilder(path).writeBufferMode(None).build();
            appender = queue.acquireAppender();
            for (int i = 0; i < 50; i++) {
                appender.writeDocument(( wireOut) -> wireOut.write("log").marshallable(( m) -> m.write("msg").text("hello world2 ")));
            }
            long index = appender.lastIndexAppended();
            sync.put(index);
            Long fromReader = sync.take();
            if (index != fromReader) {
                System.out.println(((("Writer:Not the same:" + index) + " vs. ") + fromReader));
            }
            for (int i = 0; i < 50; i++) {
                appender.writeDocument(( wireOut) -> wireOut.write("log").marshallable(( m) -> m.write("msg").text("hello world2 ")));
            }
            index = appender.lastIndexAppended();
            sync.put(index);
            return index;
        }
    }

    class MyTailer implements Callable<Long> {
        ChronicleQueue queue;

        long startIndex;

        SynchronousQueue<Long> sync;

        MyTailer(ChronicleQueue q, long s, SynchronousQueue<Long> sync) {
            queue = q;
            startIndex = s;
            this.sync = sync;
        }

        @Override
        public Long call() throws Exception {
            ExcerptTailer tailer = queue.createTailer();
            tailer.moveToIndex(startIndex);
            Long fromWriter = sync.take();
            long index = OvertakeTest.doReadBad(tailer, ((messages) + 50), false);
            if (index != fromWriter) {
                System.out.println(((("Reader:1 Not the same:" + index) + " vs. ") + fromWriter));
            }
            sync.put(index);
            fromWriter = sync.take();
            index = OvertakeTest.doReadBad(tailer, 50, false);
            if (index != fromWriter) {
                System.out.println(((("Reader:2 Not the same:" + index) + " vs. ") + fromWriter));
            }
            return index;
        }
    }
}

