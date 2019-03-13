package org.nd4j.parameterserver.distributed.transport;


import NodeRole.CLIENT;
import NodeRole.SHARD;
import Transport.ThreadingModel.DEDICATED_THREADS;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration;
import org.nd4j.parameterserver.distributed.logic.ClientRouter;
import org.nd4j.parameterserver.distributed.logic.completion.Clipboard;
import org.nd4j.parameterserver.distributed.logic.routing.InterleavedRouter;
import org.nd4j.parameterserver.distributed.messages.VoidMessage;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class RoutedTransportTest {
    /**
     * This test
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessaging1() throws Exception {
        List<String> list = new ArrayList<>();
        for (int t = 0; t < 5; t++) {
            list.add(("127.0.0.1:3838" + t));
        }
        VoidConfiguration voidConfiguration = // this port will be used only by client
        VoidConfiguration.builder().shardAddresses(list).unicastPort(43120).build();
        // first of all we start shards
        RoutedTransport[] transports = new RoutedTransport[list.size()];
        for (int t = 0; t < (transports.length); t++) {
            Clipboard clipboard = new Clipboard();
            transports[t] = new RoutedTransport();
            transports[t].setIpAndPort("127.0.0.1", Integer.valueOf(("3838" + t)));
            transports[t].init(voidConfiguration, clipboard, SHARD, "127.0.0.1", voidConfiguration.getUnicastPort(), ((short) (t)));
        }
        for (int t = 0; t < (transports.length); t++) {
            transports[t].launch(DEDICATED_THREADS);
        }
        // now we start client, for this test we'll have only one client
        Clipboard clipboard = new Clipboard();
        RoutedTransport clientTransport = new RoutedTransport();
        clientTransport.setIpAndPort("127.0.0.1", voidConfiguration.getUnicastPort());
        // setRouter call should be called before init, and we need
        ClientRouter router = new InterleavedRouter(0);
        clientTransport.setRouter(router);
        router.init(voidConfiguration, clientTransport);
        clientTransport.init(voidConfiguration, clipboard, CLIENT, "127.0.0.1", voidConfiguration.getUnicastPort(), ((short) (-1)));
        clientTransport.launch(DEDICATED_THREADS);
        // we send message somewhere
        VoidMessage message = new org.nd4j.parameterserver.distributed.messages.requests.IntroductionRequestMessage("127.0.0.1", voidConfiguration.getUnicastPort());
        clientTransport.sendMessage(message);
        Thread.sleep(500);
        message = transports[0].messages.poll(1, TimeUnit.SECONDS);
        Assert.assertNotEquals(null, message);
        for (int t = 1; t < (transports.length); t++) {
            message = transports[t].messages.poll(1, TimeUnit.SECONDS);
            Assert.assertEquals(null, message);
        }
        /**
         * This is very important part, shutting down all transports
         */
        for (RoutedTransport transport : transports) {
            transport.shutdown();
        }
        clientTransport.shutdown();
    }
}

