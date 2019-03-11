package org.kairosdb.core.queue;


import QueueProcessor.DeliveryThread;
import com.google.common.eventbus.EventBus;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.kairosdb.core.TestDataPointFactory;
import org.kairosdb.core.datapoints.LongDataPointFactory;
import org.kairosdb.core.datapoints.LongDataPointFactoryImpl;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.events.DataPointEvent;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import se.ugli.bigqueue.BigArray;


/**
 * Created by bhawkins on 10/15/16.
 */
public class QueueProcessorTest {
    private LongDataPointFactory m_longDataPointFactory = new LongDataPointFactoryImpl();

    private DeliveryThread m_deliveryThread;

    private class TestExecutor implements ExecutorService {
        @Override
        public void execute(Runnable command) {
            m_deliveryThread = ((QueueProcessor.DeliveryThread) (command));
        }

        @Override
        public void shutdown() {
        }

        @Override
        public List<Runnable> shutdownNow() {
            return null;
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return null;
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return null;
        }

        @Override
        public Future<?> submit(Runnable task) {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return null;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_bigArray_readingEmptyArray() {
        BigArray bigArray = new BigArray("big_array", "kairos_queue", ((512 * 1024) * 1024));
        long index = bigArray.getTailIndex();
        byte[] data = bigArray.get(index);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_bigArray_readingNonExistingIndex() {
        BigArray bigArray = new BigArray("big_array", "kairos_queue", ((512 * 1024) * 1024));
        long index = bigArray.getTailIndex();
        index++;
        byte[] data = bigArray.get(index);
    }

    @Test
    public void test_eventIsPulledFromMemoryQueue() throws DatastoreException {
        BigArray bigArray = Mockito.mock(BigArray.class);
        Mockito.when(bigArray.append(ArgumentMatchers.any())).thenReturn(0L);
        Mockito.when(bigArray.getTailIndex()).thenReturn(0L);
        Mockito.when(bigArray.getHeadIndex()).thenReturn(1L);
        DataPointEventSerializer serializer = new DataPointEventSerializer(new TestDataPointFactory());
        ProcessorHandler processorHandler = Mockito.mock(ProcessorHandler.class);
        QueueProcessor queueProcessor = new FileQueueProcessor(serializer, bigArray, new QueueProcessorTest.TestExecutor(), 2, 10, 500, 1, 500);
        queueProcessor.setProcessorHandler(processorHandler);
        DataPointEvent event = createDataPointEvent();
        queueProcessor.put(event);
        m_deliveryThread.setRunOnce(true);
        m_deliveryThread.run();
        Mockito.verify(bigArray, Mockito.times(1)).append(ArgumentMatchers.eq(serializer.serializeEvent(event)));
        Mockito.verify(processorHandler, Mockito.times(1)).handleEvents(ArgumentMatchers.eq(Arrays.asList(event)), ArgumentMatchers.any(), ArgumentMatchers.eq(false));
        Mockito.verify(bigArray, Mockito.times(0)).get(ArgumentMatchers.anyLong());
    }

    @Test
    public void test_eventIsPulledFromMemoryQueueThenBigArray() throws DatastoreException {
        BigArray bigArray = Mockito.mock(BigArray.class);
        Mockito.when(bigArray.append(ArgumentMatchers.any())).thenReturn(0L);
        Mockito.when(bigArray.getHeadIndex()).thenReturn(2L);
        DataPointEventSerializer serializer = new DataPointEventSerializer(new TestDataPointFactory());
        ProcessorHandler processorHandler = Mockito.mock(ProcessorHandler.class);
        QueueProcessor queueProcessor = new FileQueueProcessor(serializer, bigArray, new QueueProcessorTest.TestExecutor(), 3, 1, 500, 1, 500);
        queueProcessor.setProcessorHandler(processorHandler);
        DataPointEvent event = createDataPointEvent();
        queueProcessor.put(event);
        Mockito.when(bigArray.append(ArgumentMatchers.any())).thenReturn(1L);
        queueProcessor.put(event);
        Mockito.when(bigArray.get(0L)).thenReturn(serializer.serializeEvent(event));
        Mockito.when(bigArray.get(1L)).thenReturn(serializer.serializeEvent(event));
        m_deliveryThread.setRunOnce(true);
        m_deliveryThread.run();
        Mockito.verify(bigArray, Mockito.times(2)).append(ArgumentMatchers.eq(serializer.serializeEvent(event)));
        Mockito.verify(processorHandler, Mockito.times(1)).handleEvents(ArgumentMatchers.eq(Arrays.asList(event, event)), ArgumentMatchers.any(), ArgumentMatchers.eq(false));
        Mockito.verify(bigArray, Mockito.times(1)).get(ArgumentMatchers.anyLong());
    }

    @Test
    public void test_checkPointIsCalled() throws DatastoreException {
        final EventBus eventBus = Mockito.mock(EventBus.class);
        BigArray bigArray = Mockito.mock(BigArray.class);
        Mockito.when(bigArray.append(ArgumentMatchers.any())).thenReturn(0L);
        Mockito.when(bigArray.getHeadIndex()).thenReturn(2L);
        DataPointEventSerializer serializer = new DataPointEventSerializer(new TestDataPointFactory());
        ProcessorHandler processorHandler = new ProcessorHandler() {
            @Override
            public void handleEvents(List<DataPointEvent> events, EventCompletionCallBack eventCompletionCallBack, boolean fullBatch) {
                System.out.println(("Handling events " + (events.size())));
                eventCompletionCallBack.complete();
            }
        };
        QueueProcessor queueProcessor = new FileQueueProcessor(serializer, bigArray, new QueueProcessorTest.TestExecutor(), 3, 2, (-1), 1, 500);
        queueProcessor.setProcessorHandler(processorHandler);
        DataPointEvent event = createDataPointEvent();
        queueProcessor.put(event);
        Mockito.when(bigArray.append(ArgumentMatchers.any())).thenReturn(1L);
        queueProcessor.put(event);
        Mockito.when(bigArray.get(1L)).thenReturn(serializer.serializeEvent(event));
        m_deliveryThread.setRunOnce(true);
        m_deliveryThread.run();
        Mockito.verify(bigArray, Mockito.times(2)).append(ArgumentMatchers.eq(serializer.serializeEvent(event)));
        // verify(bigArray, times(1)).get(anyLong()); //Item taken from memory
        Mockito.verify(bigArray, Mockito.times(1)).removeBeforeIndex(ArgumentMatchers.eq(1L));
    }
}

