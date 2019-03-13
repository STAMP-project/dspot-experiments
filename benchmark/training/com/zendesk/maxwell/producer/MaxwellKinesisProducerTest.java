package com.zendesk.maxwell.producer;


import AbstractAsyncProducer.CallbackCompleter;
import com.zendesk.maxwell.MaxwellConfig;
import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.monitoring.NoOpMetrics;
import com.zendesk.maxwell.replication.BinlogPosition;
import com.zendesk.maxwell.replication.Position;
import com.zendesk.maxwell.row.RowMap;
import java.util.ArrayList;
import org.junit.Test;
import org.mockito.Mockito;


public class MaxwellKinesisProducerTest {
    private static final long TIMESTAMP_MILLISECONDS = 1496712943447L;

    private static final Position POSITION = new Position(new BinlogPosition(1L, "binlog-0001"), 0L);

    @Test
    public void dealsWithTooLargeRecord() throws Exception {
        MaxwellContext context = Mockito.mock(MaxwellContext.class);
        MaxwellConfig config = new MaxwellConfig();
        Mockito.when(context.getConfig()).thenReturn(config);
        Mockito.when(context.getMetrics()).thenReturn(new NoOpMetrics());
        String kinesisStream = "test-stream";
        MaxwellKinesisProducer producer = new MaxwellKinesisProducer(context, kinesisStream);
        RowMap rowMap = new RowMap("insert", "MyDatabase", "MyTable", MaxwellKinesisProducerTest.TIMESTAMP_MILLISECONDS, new ArrayList<String>(), MaxwellKinesisProducerTest.POSITION);
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            r.append("long string");
        }
        rowMap.putData("content", r.toString());
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        producer.sendAsync(rowMap, cc);
        producer.close();
    }
}

