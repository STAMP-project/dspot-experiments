package org.kairosdb.datastore.cassandra;


import CassandraModule.CQLBatchFactory;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.google.common.collect.ImmutableSortedMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.datapoints.LongDataPointFactory;
import org.kairosdb.core.datapoints.LongDataPointFactoryImpl;
import org.kairosdb.core.queue.EventCompletionCallBack;
import org.kairosdb.eventbus.Publisher;
import org.kairosdb.events.BatchReductionEvent;
import org.kairosdb.events.DataPointEvent;
import org.kairosdb.events.RowKeyEvent;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BatchHandlerTest {
    private BatchHandler m_batchHandler;

    private EventCompletionCallBack m_callBack;

    private DataCache<DataPointsRowKey> m_rowKeyDataCache;

    private DataCache<String> m_metricDataCache;

    private Publisher<RowKeyEvent> m_rowKeyEventPublisher;

    private Publisher<BatchReductionEvent> m_batchReductionEventPublisher;

    private CQLBatchFactory m_cqlBatchFactory;

    private class FakeCQLBatch extends CQLBatch {
        private List<DataPointsRowKey> m_newRowKeys = new ArrayList<>();

        private List<String> m_newMetrics = new ArrayList<>();

        private RuntimeException m_exceptionToThrow;

        public FakeCQLBatch(RuntimeException exceptionToThrow) {
            super(null, null, null, null);
            m_exceptionToThrow = exceptionToThrow;
        }

        @Override
        public void addRowKey(String metricName, DataPointsRowKey rowKey, int rowKeyTtl) {
            m_newRowKeys.add(rowKey);
        }

        @Override
        public void addMetricName(String metricName) {
            m_newMetrics.add(metricName);
        }

        @Override
        public void addDataPoint(DataPointsRowKey rowKey, int columnTime, DataPoint dataPoint, int ttl) throws IOException {
        }

        @Override
        public void submitBatch() {
            if ((m_exceptionToThrow) != null)
                throw m_exceptionToThrow;

        }

        @Override
        public List<DataPointsRowKey> getNewRowKeys() {
            return m_newRowKeys;
        }

        @Override
        public List<String> getNewMetrics() {
            return m_newMetrics;
        }
    }

    @Test
    public void test_rowKeyPublisher_getsCalled() throws Exception {
        LongDataPointFactory dataPointFactory = new LongDataPointFactoryImpl();
        long now = System.currentTimeMillis();
        ImmutableSortedMap<String, String> tags = ImmutableSortedMap.of("host", "bob");
        List<DataPointEvent> events = Arrays.asList(new DataPointEvent("metric_name", tags, dataPointFactory.createDataPoint(now, 42L)));
        setup(events);
        Mockito.when(m_cqlBatchFactory.create()).thenReturn(new BatchHandlerTest.FakeCQLBatch(null));
        m_batchHandler.retryCall();
        Mockito.verify(m_rowKeyEventPublisher).post(ArgumentMatchers.any());
    }

    @Test
    public void test_rowKeyCache_gets_cleared() throws Exception {
        LongDataPointFactory dataPointFactory = new LongDataPointFactoryImpl();
        long now = System.currentTimeMillis();
        ImmutableSortedMap<String, String> tags = ImmutableSortedMap.of("host", "bob");
        List<DataPointEvent> events = Arrays.asList(new DataPointEvent("metric_name", tags, dataPointFactory.createDataPoint(now, 42L)));
        setup(events);
        RuntimeException e = Mockito.mock(NoHostAvailableException.class);
        Mockito.when(e.getMessage()).thenReturn("hey");
        Mockito.when(m_cqlBatchFactory.create()).thenReturn(new BatchHandlerTest.FakeCQLBatch(e));
        try {
            m_batchHandler.retryCall();
        } catch (Exception e1) {
        } finally {
            Mockito.verify(m_rowKeyEventPublisher).post(ArgumentMatchers.any());
            assertThat(m_rowKeyDataCache.getCachedKeys()).isEmpty();
        }
    }
}

