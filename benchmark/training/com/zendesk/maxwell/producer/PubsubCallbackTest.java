package com.zendesk.maxwell.producer;


import AbstractAsyncProducer.CallbackCompleter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.zendesk.maxwell.MaxwellConfig;
import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.replication.BinlogPosition;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PubsubCallbackTest {
    @Test
    public void shouldIgnoreProducerErrorByDefault() {
        MaxwellContext context = Mockito.mock(MaxwellContext.class);
        MaxwellConfig config = new MaxwellConfig();
        Mockito.when(context.getConfig()).thenReturn(config);
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        PubsubCallback callback = new PubsubCallback(cc, new com.zendesk.maxwell.replication.Position(new BinlogPosition(1, "binlog-1"), 0L), "value", new Counter(), new Counter(), new Meter(), new Meter(), context);
        Throwable t = new Throwable("blah");
        callback.onFailure(t);
        Mockito.verify(cc).markCompleted();
    }

    @Test
    public void shouldTerminateWhenNotIgnoreProducerError() {
        MaxwellContext context = Mockito.mock(MaxwellContext.class);
        MaxwellConfig config = new MaxwellConfig();
        config.ignoreProducerError = false;
        Mockito.when(context.getConfig()).thenReturn(config);
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        PubsubCallback callback = new PubsubCallback(cc, new com.zendesk.maxwell.replication.Position(new BinlogPosition(1, "binlog-1"), 0L), "value", new Counter(), new Counter(), new Meter(), new Meter(), context);
        Throwable t = new Throwable("blah");
        callback.onFailure(t);
        Mockito.verify(context).terminate(ArgumentMatchers.any(RuntimeException.class));
        Mockito.verifyZeroInteractions(cc);
    }
}

