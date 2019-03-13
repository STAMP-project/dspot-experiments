package com.baeldung.concurrent.waitandnotify;


import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class NetworkIntegrationTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private String expected;

    @Test
    public void givenSenderAndReceiver_whenSendingPackets_thenNetworkSynchronized() {
        Data data = new Data();
        Thread sender = new Thread(new Sender(data));
        Thread receiver = new Thread(new Receiver(data));
        sender.start();
        receiver.start();
        // wait for sender and receiver to finish before we test against expected
        try {
            sender.join();
            receiver.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread Interrupted");
        }
        Assert.assertEquals(expected, outContent.toString());
    }
}

