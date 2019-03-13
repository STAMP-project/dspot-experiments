package org.nd4j.parameterserver.node;


import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.aeron.ipc.NDArrayMessage;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.client.ParameterServerClient;


/**
 * Created by agibsonccc on 12/3/16.
 */
@Slf4j
public class ParameterServerNodeTest {
    private static MediaDriver mediaDriver;

    private static Aeron aeron;

    private static ParameterServerNode parameterServerNode;

    private static int parameterLength = 4;

    private static int masterStatusPort = 40323 + (new Random().nextInt(15999));

    private static int statusPort = (ParameterServerNodeTest.masterStatusPort) - 1299;

    @Test
    public void testSimulateRun() throws Exception {
        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numCores);
        ParameterServerClient[] clients = new ParameterServerClient[numCores];
        String host = "localhost";
        for (int i = 0; i < numCores; i++) {
            clients[i] = ParameterServerClient.builder().aeron(ParameterServerNodeTest.aeron).masterStatusHost(host).masterStatusPort(ParameterServerNodeTest.statusPort).subscriberHost(host).subscriberPort((40325 + i)).subscriberStream((10 + i)).ndarrayRetrieveUrl(ParameterServerNodeTest.parameterServerNode.getSubscriber()[i].getResponder().connectionUrl()).ndarraySendUrl(ParameterServerNodeTest.parameterServerNode.getSubscriber()[i].getSubscriber().connectionUrl()).build();
        }
        Thread.sleep(60000);
        // no arrays have been sent yet
        for (int i = 0; i < numCores; i++) {
            Assert.assertFalse(clients[i].isReadyForNext());
        }
        // send "numCores" arrays, the default parameter server updater
        // is synchronous so it should be "ready" when number of updates == number of workers
        for (int i = 0; i < numCores; i++) {
            clients[i].pushNDArrayMessage(NDArrayMessage.wholeArrayUpdate(Nd4j.ones(ParameterServerNodeTest.parameterLength)));
        }
        Thread.sleep(10000);
        // all arrays should have been sent
        for (int i = 0; i < numCores; i++) {
            Assert.assertTrue(clients[i].isReadyForNext());
        }
        Thread.sleep(10000);
        for (int i = 0; i < 1; i++) {
            Assert.assertEquals(Nd4j.valueArrayOf(1, ParameterServerNodeTest.parameterLength, numCores), clients[i].getArray());
            Thread.sleep(1000);
        }
        executorService.shutdown();
        Thread.sleep(60000);
        ParameterServerNodeTest.parameterServerNode.close();
    }
}

