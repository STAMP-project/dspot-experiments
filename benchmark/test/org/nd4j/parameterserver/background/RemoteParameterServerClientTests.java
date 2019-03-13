package org.nd4j.parameterserver.background;


import Aeron.Context;
import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.client.ParameterServerClient;


/**
 * Created by agibsonccc on 10/5/16.
 */
@Slf4j
public class RemoteParameterServerClientTests {
    private int parameterLength = 1000;

    private Context ctx;

    private MediaDriver mediaDriver;

    private AtomicInteger masterStatus = new AtomicInteger(0);

    private AtomicInteger slaveStatus = new AtomicInteger(0);

    private Aeron aeron;

    @Test
    public void remoteTests() throws Exception {
        if (((masterStatus.get()) != 0) || ((slaveStatus.get()) != 0))
            throw new IllegalStateException("Master or slave failed to start. Exiting");

        ParameterServerClient client = ParameterServerClient.builder().aeron(aeron).ndarrayRetrieveUrl(BackgroundDaemonStarter.masterResponderUrl()).ndarraySendUrl(BackgroundDaemonStarter.slaveConnectionUrl()).subscriberHost("localhost").masterStatusHost("localhost").masterStatusPort(9200).subscriberPort(40125).subscriberStream(12).build();
        Assert.assertEquals("localhost:40125:12", client.connectionUrl());
        while (!(client.masterStarted())) {
            Thread.sleep(1000);
            log.info("Waiting on master starting.");
        } 
        // flow 1:
        /**
         * Client (40125:12): sends array to listener on slave(40126:10)
         * which publishes to master (40123:11)
         * which adds the array for parameter averaging.
         * In this case totalN should be 1.
         */
        log.info("Pushing ndarray");
        client.pushNDArray(Nd4j.ones(parameterLength));
        while ((client.arraysSentToResponder()) < 1) {
            Thread.sleep(1000);
            log.info("Waiting on ndarray responder to receive array");
        } 
        log.info("Pushed ndarray");
        INDArray arr = client.getArray();
        Assert.assertEquals(Nd4j.ones(1000), arr);
        /* StopWatch stopWatch = new StopWatch();
        long nanoTimeTotal = 0;
        int n = 1000;
        for(int i = 0; i < n; i++) {
        stopWatch.start();
        client.getArray();
        stopWatch.stop();
        nanoTimeTotal += stopWatch.getNanoTime();
        stopWatch.reset();
        }

        System.out.println(nanoTimeTotal / n);
         */
    }
}

