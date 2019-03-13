package org.nd4j.aeron.ipc;


import Aeron.Context;
import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.agrona.CloseHelper;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by agibsonccc on 9/22/16.
 */
public class NdArrayIpcTest {
    private MediaDriver mediaDriver;

    private static Logger log = LoggerFactory.getLogger(NdArrayIpcTest.class);

    private Context ctx;

    private String channel = "aeron:udp?endpoint=localhost:" + (40132 + (new Random().nextInt(3000)));

    private int streamId = 10;

    private int length = ((int) (1.0E7));

    @Test
    public void testMultiThreadedIpc() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        INDArray arr = Nd4j.scalar(1.0);
        final AtomicBoolean running = new AtomicBoolean(true);
        Aeron aeron = Aeron.connect(getContext());
        int numSubscribers = 10;
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
                    System.out.println("Callback invoked for subscriber on ndarray ipc test");
                    running.set(false);
                }

                @Override
                public void onNDArrayPartial(INDArray arr, long idx, int... dimensions) {
                }

                @Override
                public void onNDArray(INDArray arr) {
                    System.out.println("Callback invoked for subscriber on ndarray ipc test");
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
            t.setDaemon(true);
            t.start();
            subscribers[i] = subscriber;
        }
        AeronNDArrayPublisher publisher = AeronNDArrayPublisher.builder().streamId(streamId).channel(channel).aeron(aeron).build();
        Thread.sleep(10000);
        for (int i = 0; (i < 10) && (running.get()); i++) {
            executorService.execute(() -> {
                try {
                    NdArrayIpcTest.log.info("About to send array.");
                    publisher.publish(arr);
                    NdArrayIpcTest.log.info("Sent array");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(30000);
        for (int i = 0; i < numSubscribers; i++)
            CloseHelper.close(subscribers[i]);

        CloseHelper.close(publisher);
        CloseHelper.close(aeron);
        Assert.assertFalse(running.get());
    }

    @Test
    public void testIpc() throws Exception {
        INDArray arr = Nd4j.scalar(1.0);
        final AtomicBoolean running = new AtomicBoolean(true);
        Aeron aeron = Aeron.connect(getContext());
        AeronNDArraySubscriber subscriber = AeronNDArraySubscriber.builder().streamId(streamId).aeron(aeron).channel(channel).running(running).ndArrayCallback(new NDArrayCallback() {
            /**
             * A listener for ndarray message
             *
             * @param message
             * 		the message for the callback
             */
            @Override
            public void onNDArrayMessage(NDArrayMessage message) {
                System.out.println(arr);
                running.set(false);
            }

            @Override
            public void onNDArrayPartial(INDArray arr, long idx, int... dimensions) {
            }

            @Override
            public void onNDArray(INDArray arr) {
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
        while (!(subscriber.launched()))
            Thread.sleep(1000);

        Thread.sleep(10000);
        AeronNDArrayPublisher publisher = AeronNDArrayPublisher.builder().streamId(streamId).aeron(aeron).channel(channel).build();
        for (int i = 0; (i < 1) && (running.get()); i++) {
            publisher.publish(arr);
        }
        Thread.sleep(30000);
        Assert.assertFalse(running.get());
        publisher.close();
        subscriber.close();
    }
}

