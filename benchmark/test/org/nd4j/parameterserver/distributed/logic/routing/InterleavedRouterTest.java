package org.nd4j.parameterserver.distributed.logic.routing;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration;
import org.nd4j.parameterserver.distributed.messages.VoidMessage;
import org.nd4j.parameterserver.distributed.messages.requests.InitializationRequestMessage;
import org.nd4j.parameterserver.distributed.messages.requests.SkipGramRequestMessage;
import org.nd4j.parameterserver.distributed.transport.Transport;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class InterleavedRouterTest {
    VoidConfiguration configuration;

    Transport transport;

    long originator;

    /**
     * Testing default assignment for everything, but training requests
     *
     * @throws Exception
     * 		
     */
    @Test
    public void assignTarget1() throws Exception {
        InterleavedRouter router = new InterleavedRouter();
        router.init(configuration, transport);
        for (int i = 0; i < 100; i++) {
            VoidMessage message = new InitializationRequestMessage(100, 10, 123, false, false, 10);
            int target = router.assignTarget(message);
            Assert.assertTrue(((target >= 0) && (target <= 3)));
            Assert.assertEquals(originator, message.getOriginatorId());
        }
    }

    /**
     * Testing assignment for training message
     *
     * @throws Exception
     * 		
     */
    @Test
    public void assignTarget2() throws Exception {
        InterleavedRouter router = new InterleavedRouter();
        router.init(configuration, transport);
        int[] w1 = new int[]{ 512, 345, 486, 212 };
        for (int i = 0; i < (w1.length); i++) {
            SkipGramRequestMessage message = new SkipGramRequestMessage(w1[i], 1, new int[]{ 1, 2, 3 }, new byte[]{ 0, 0, 1 }, ((short) (0)), 0.02, 119);
            int target = router.assignTarget(message);
            Assert.assertEquals(((w1[i]) % (configuration.getNumberOfShards())), target);
            Assert.assertEquals(originator, message.getOriginatorId());
        }
    }

    /**
     * Testing default assignment for everything, but training requests.
     * Difference here is pre-defined default index, for everything but TrainingMessages
     *
     * @throws Exception
     * 		
     */
    @Test
    public void assignTarget3() throws Exception {
        InterleavedRouter router = new InterleavedRouter(2);
        router.init(configuration, transport);
        for (int i = 0; i < 3; i++) {
            VoidMessage message = new InitializationRequestMessage(100, 10, 123, false, false, 10);
            int target = router.assignTarget(message);
            Assert.assertEquals(2, target);
            Assert.assertEquals(originator, message.getOriginatorId());
        }
    }
}

