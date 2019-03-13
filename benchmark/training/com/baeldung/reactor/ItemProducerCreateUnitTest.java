package com.baeldung.reactor;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class ItemProducerCreateUnitTest {
    @Test
    public void givenFluxWithAsynchronousCreate_whenProduceItemsFromDifferentThread_thenAllItemsAreCollectedByTheSubscriber() throws InterruptedException {
        List<String> elements = new ArrayList<>();
        ItemProducerCreate producer = new ItemProducerCreate();
        producer.create().subscribe(elements::add);
        Thread producerThread = new Thread(() -> {
            List<String> items = new ArrayList<>();
            items.add("Item 1");
            items.add("Item 2");
            items.add("Item 3");
            producer.listener.accept(items);
        });
        producerThread.start();
        producerThread.join();
        assertThat(elements).containsExactly("Item 1", "Item 2", "Item 3");
    }
}

