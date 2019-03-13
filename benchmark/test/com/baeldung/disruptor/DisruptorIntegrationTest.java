package com.baeldung.disruptor;


import ProducerType.MULTI;
import ProducerType.SINGLE;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.Test;


public class DisruptorIntegrationTest {
    private Disruptor<ValueEvent> disruptor;

    private WaitStrategy waitStrategy;

    @Test
    public void whenMultipleProducerSingleConsumer_thenOutputInFifoOrder() {
        final EventConsumer eventConsumer = new SingleEventPrintConsumer();
        final EventProducer eventProducer = new DelayedMultiEventProducer();
        createDisruptor(MULTI, eventConsumer);
        final RingBuffer<ValueEvent> ringBuffer = disruptor.start();
        startProducing(ringBuffer, 32, eventProducer);
        disruptor.halt();
        disruptor.shutdown();
    }

    @Test
    public void whenSingleProducerSingleConsumer_thenOutputInFifoOrder() {
        final EventConsumer eventConsumer = new SingleEventConsumer();
        final EventProducer eventProducer = new SingleEventProducer();
        createDisruptor(SINGLE, eventConsumer);
        final RingBuffer<ValueEvent> ringBuffer = disruptor.start();
        startProducing(ringBuffer, 32, eventProducer);
        disruptor.halt();
        disruptor.shutdown();
    }

    @Test
    public void whenSingleProducerMultipleConsumer_thenOutputInFifoOrder() {
        final EventConsumer eventConsumer = new MultiEventConsumer();
        final EventProducer eventProducer = new SingleEventProducer();
        createDisruptor(SINGLE, eventConsumer);
        final RingBuffer<ValueEvent> ringBuffer = disruptor.start();
        startProducing(ringBuffer, 32, eventProducer);
        disruptor.halt();
        disruptor.shutdown();
    }

    @Test
    public void whenMultipleProducerMultipleConsumer_thenOutputInFifoOrder() {
        final EventConsumer eventConsumer = new MultiEventPrintConsumer();
        final EventProducer eventProducer = new DelayedMultiEventProducer();
        createDisruptor(MULTI, eventConsumer);
        final RingBuffer<ValueEvent> ringBuffer = disruptor.start();
        startProducing(ringBuffer, 32, eventProducer);
        disruptor.halt();
        disruptor.shutdown();
    }
}

