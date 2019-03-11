package com.zendesk.maxwell.producer;


import AbstractAsyncProducer.CallbackCompleter;
import com.amazonaws.services.kinesis.producer.IrrecoverableError;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.zendesk.maxwell.MaxwellConfig;
import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.replication.BinlogPosition;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class KinesisCallbackTest {
    @Test
    public void shouldIgnoreProducerErrorByDefault() {
        MaxwellContext context = Mockito.mock(MaxwellContext.class);
        MaxwellConfig config = new MaxwellConfig();
        Mockito.when(context.getConfig()).thenReturn(config);
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        KinesisCallback callback = new KinesisCallback(cc, new com.zendesk.maxwell.replication.Position(new BinlogPosition(1, "binlog-1"), 0L), "key", "value", new Counter(), new Counter(), new Meter(), new Meter(), context);
        IrrecoverableError error = new IrrecoverableError("blah");
        callback.onFailure(error);
        Mockito.verify(cc).markCompleted();
    }

    @Test
    public void shouldTerminateWhenNotIgnoreProducerError() {
        MaxwellContext context = Mockito.mock(MaxwellContext.class);
        MaxwellConfig config = new MaxwellConfig();
        config.ignoreProducerError = false;
        Mockito.when(context.getConfig()).thenReturn(config);
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        KinesisCallback callback = new KinesisCallback(cc, new com.zendesk.maxwell.replication.Position(new BinlogPosition(1, "binlog-1"), 0L), "key", "value", new Counter(), new Counter(), new Meter(), new Meter(), context);
        IrrecoverableError error = new IrrecoverableError("blah");
        callback.onFailure(error);
        Mockito.verify(context).terminate(ArgumentMatchers.any(RuntimeException.class));
        Mockito.verifyZeroInteractions(cc);
    }
}

