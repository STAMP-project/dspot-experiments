package org.nd4j.parameterserver.distributed.messages;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.parameterserver.distributed.messages.requests.SkipGramRequestMessage;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class VoidMessageTest {
    @Test
    public void testSerDe1() throws Exception {
        SkipGramRequestMessage message = new SkipGramRequestMessage(10, 12, new int[]{ 10, 20, 30, 40 }, new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (1)), ((byte) (0)) }, ((short) (0)), 0.0, 117L);
        byte[] bytes = message.asBytes();
        SkipGramRequestMessage restored = ((SkipGramRequestMessage) (VoidMessage.fromBytes(bytes)));
        Assert.assertNotEquals(null, restored);
        Assert.assertEquals(message, restored);
        Assert.assertArrayEquals(message.getPoints(), restored.getPoints());
        Assert.assertArrayEquals(message.getCodes(), restored.getCodes());
    }
}

