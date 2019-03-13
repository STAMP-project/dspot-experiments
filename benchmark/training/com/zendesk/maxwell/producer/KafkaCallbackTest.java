package com.zendesk.maxwell.producer;


import AbstractAsyncProducer.CallbackCompleter;
import com.zendesk.maxwell.MaxwellConfig;
import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.row.RowIdentity;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.NotEnoughReplicasException;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class KafkaCallbackTest {
    @Test
    public void shouldIgnoreProducerErrorByDefault() {
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        MaxwellContext context = makeContext(new MaxwellConfig());
        KafkaCallback callback = makeCallback(context, cc, null, null);
        Exception error = new NotEnoughReplicasException("blah");
        callback.onCompletion(new RecordMetadata(new TopicPartition("topic", 1), 1, 1, 1, new Long(1), 1, 1), error);
        Mockito.verify(cc).markCompleted();
    }

    @Test
    public void shouldTerminateWhenNotIgnoringProducerError() {
        MaxwellConfig config = new MaxwellConfig();
        config.ignoreProducerError = false;
        MaxwellContext context = makeContext(config);
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        MaxwellKafkaProducerWorker producer = Mockito.mock(MaxwellKafkaProducerWorker.class);
        KafkaCallback callback = makeCallback(context, cc, producer, null);
        Exception error = new NotEnoughReplicasException("blah");
        callback.onCompletion(new RecordMetadata(new TopicPartition("topic", 1), 1, 1, 1, new Long(1), 1, 1), error);
        Mockito.verify(context).terminate(error);
        Mockito.verifyZeroInteractions(producer);
        Mockito.verifyZeroInteractions(cc);
    }

    @Test
    public void shouldPublishFallbackRecordOnRecordTooLargeWhenConfigured() throws Exception {
        MaxwellConfig config = new MaxwellConfig();
        config.deadLetterTopic = "dead_letters";
        MaxwellContext context = makeContext(config);
        MaxwellKafkaProducerWorker producer = Mockito.mock(MaxwellKafkaProducerWorker.class);
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        RowIdentity id = new RowIdentity("a", "b", null);
        KafkaCallback callback = makeCallback(context, cc, producer, id);
        Exception error = new RecordTooLargeException();
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("topic", 1), 1, 1, 1, new Long(1), 1, 1);
        callback.onCompletion(recordMetadata, error);
        // don't complete yet!
        Mockito.verifyZeroInteractions(cc);
        ArgumentCaptor<KafkaCallback> cbCaptor = ArgumentCaptor.forClass(KafkaCallback.class);
        Mockito.verify(producer).sendFallbackAsync(ArgumentMatchers.eq("dead_letters"), ArgumentMatchers.eq(id), cbCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq(error));
        Assert.assertEquals(null, cbCaptor.getValue().getFallbackTopic());
        cbCaptor.getValue().onCompletion(recordMetadata, null);
        Mockito.verify(cc).markCompleted();
    }

    @Test
    public void shouldPublishFallbackRecordOnRetriableExceptionWhenConfiguredWithFallbackAndIgnoreErrors() throws Exception {
        MaxwellConfig config = new MaxwellConfig();
        config.deadLetterTopic = "dead_letters";
        config.ignoreProducerError = true;
        MaxwellContext context = makeContext(config);
        MaxwellKafkaProducerWorker producer = Mockito.mock(MaxwellKafkaProducerWorker.class);
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        RowIdentity id = new RowIdentity("a", "b", null);
        KafkaCallback callback = makeCallback(context, cc, producer, id);
        Exception error = new NotEnoughReplicasException("blah");
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("topic", 1), 1, 1, 1, new Long(1), 1, 1);
        callback.onCompletion(recordMetadata, error);
        // don't complete yet!
        Mockito.verifyZeroInteractions(cc);
        ArgumentCaptor<KafkaCallback> cbCaptor = ArgumentCaptor.forClass(KafkaCallback.class);
        Mockito.verify(producer).sendFallbackAsync(ArgumentMatchers.eq("dead_letters"), ArgumentMatchers.eq(id), cbCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq(error));
        Assert.assertEquals(null, cbCaptor.getValue().getFallbackTopic());
        cbCaptor.getValue().onCompletion(recordMetadata, null);
        Mockito.verify(cc).markCompleted();
    }

    @Test
    public void shouldNotPublishFallbackRecordIfNotConfigured() {
        MaxwellConfig config = new MaxwellConfig();
        config.deadLetterTopic = null;
        MaxwellContext context = makeContext(config);
        MaxwellKafkaProducerWorker producer = Mockito.mock(MaxwellKafkaProducerWorker.class);
        AbstractAsyncProducer.CallbackCompleter cc = Mockito.mock(CallbackCompleter.class);
        RowIdentity id = new RowIdentity("a", "b", null);
        KafkaCallback callback = makeCallback(context, cc, producer, id);
        Exception error = new RecordTooLargeException();
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("topic", 1), 1, 1, 1, new Long(1), 1, 1);
        callback.onCompletion(recordMetadata, error);
        Mockito.verify(cc).markCompleted();
        Mockito.verifyZeroInteractions(producer);
    }
}

