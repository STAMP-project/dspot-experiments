package com.baeldung.java9.reactive;


import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;


public class BaeldungSubscriberImplIntegrationTest {
    private static final int ITEM_SIZE = 10;

    private SubmissionPublisher<String> publisher;

    private BaeldungSubscriberImpl<String> subscriber;

    @Rule
    public Stopwatch stopwatch = new Stopwatch() {};

    @Test
    public void testReactiveStreamCount() {
        IntStream.range(0, BaeldungSubscriberImplIntegrationTest.ITEM_SIZE).forEach(( item) -> publisher.submit((item + "")));
        publisher.close();
        do {
            // wait for subscribers to complete all processing.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (!(subscriber.isCompleted()) );
        int count = subscriber.getCounter();
        Assert.assertEquals(BaeldungSubscriberImplIntegrationTest.ITEM_SIZE, count);
    }

    @Test
    public void testReactiveStreamTime() {
        IntStream.range(0, BaeldungSubscriberImplIntegrationTest.ITEM_SIZE).forEach(( item) -> publisher.submit((item + "")));
        publisher.close();
        do {
            // wait for subscribers to complete all processing.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (!(subscriber.isCompleted()) );
        // The runtime in seconds should be equal to the number of items.
        Assert.assertTrue(((stopwatch.runtime(TimeUnit.SECONDS)) >= (BaeldungSubscriberImplIntegrationTest.ITEM_SIZE)));
    }

    @Test
    public void testReactiveStreamOffer() {
        IntStream.range(0, BaeldungSubscriberImplIntegrationTest.ITEM_SIZE).forEach(( item) -> publisher.offer((item + ""), ( subscriber, string) -> {
            // Returning false means this item will be dropped (no retry), if blocked.
            return false;
        }));
        publisher.close();
        do {
            // wait for subscribers to complete all processing.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (!(subscriber.isCompleted()) );
        int count = subscriber.getCounter();
        // Because 10 items were offered and the buffer capacity was 3, few items will not be processed.
        Assert.assertTrue(((BaeldungSubscriberImplIntegrationTest.ITEM_SIZE) > count));
    }
}

