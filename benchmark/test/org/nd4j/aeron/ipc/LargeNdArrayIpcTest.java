package org.nd4j.aeron.ipc;


import Aeron.Context;
import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.agrona.CloseHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by agibsonccc on 9/22/16.
 */
@Slf4j
public class LargeNdArrayIpcTest {
    private MediaDriver mediaDriver;

    private Context ctx;

    private String channel = "aeron:udp?endpoint=localhost:" + (40123 + (new Random().nextInt(130)));

    private int streamId = 10;

    private int length = ((int) (1.0E7));

    @Test
    public void testMultiThreadedIpcBig() throws Exception {
        int length = ((int) (1.0E7));
        INDArray arr = Nd4j.ones(length);
        AeronNDArrayPublisher publisher;
        ctx = new Aeron.Context().publicationConnectionTimeout((-1)).availableImageHandler(AeronUtil::printAvailableImage).unavailableImageHandler(AeronUtil::printUnavailableImage).aeronDirectoryName(mediaDriver.aeronDirectoryName()).keepAliveInterval(10000).errorHandler(( err) -> err.printStackTrace());
        final AtomicBoolean running = new AtomicBoolean(true);
        Aeron aeron = Aeron.connect(ctx);
        int numSubscribers = 1;
        AeronNDArraySubscriber[] subscribers = new AeronNDArraySubscriber[numSubscribers];
        for (int i = 0; i < numSubscribers; i++) {
            AeronNDArraySubscriber subscriber = AeronNDArraySubscriber.builder().streamId(streamId).ctx(getContext()).channel(channel).aeron(aeron).running(running).ndArrayCallback(new NDArrayCallback() {
                /**
                 * A listener for ndarray message
                 *
                 * @param message
                 * 		the message for the callback
                 */
                @Override
                public void onNDArrayMessage(NDArrayMessage message) {
                    running.set(false);
                }

                @Override
                public void onNDArrayPartial(INDArray arr, long idx, int... dimensions) {
                }

                @Override
                public void onNDArray(INDArray arr) {
                    running.set(false);
                }
            }).build();
            Thread t = new Thread(() -> {
                try {
                    subscriber.launch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.start();
            subscribers[i] = subscriber;
        }
        Thread.sleep(10000);
        publisher = AeronNDArrayPublisher.builder().publishRetryTimeOut(3000).streamId(streamId).channel(channel).aeron(aeron).build();
        for (int i = 0; (i < 1) && (running.get()); i++) {
            log.info("About to send array.");
            publisher.publish(arr);
            log.info("Sent array");
        }
        Thread.sleep(30000);
        for (int i = 0; i < numSubscribers; i++)
            CloseHelper.close(subscribers[i]);

        CloseHelper.close(aeron);
        CloseHelper.close(publisher);
        Assert.assertFalse(running.get());
    }
}

