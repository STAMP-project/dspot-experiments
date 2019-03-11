package com.baeldung.reactor;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class NetworkTrafficProducerPushUnitTest {
    @Test
    public void givenFluxWithAsynchronousPushWithListener_whenListenerIsInvoked_thenItemCollectedByTheSubscriber() throws InterruptedException {
        List<String> elements = new ArrayList<>();
        NetworkTrafficProducerPush trafficProducer = new NetworkTrafficProducerPush();
        trafficProducer.subscribe(elements::add);
        trafficProducer.onPacket("Packet[A18]");
        assertThat(elements).containsExactly("Packet[A18]");
    }
}

