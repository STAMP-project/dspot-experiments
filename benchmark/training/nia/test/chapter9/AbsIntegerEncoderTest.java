package nia.test.chapter9;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import nia.chapter9.AbsIntegerEncoder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Listing 9.4 Testing the AbsIntegerEncoder
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class AbsIntegerEncoderTest {
    @Test
    public void testEncoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt((i * (-1)));
        }
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        Assert.assertTrue(channel.writeOutbound(buf));
        Assert.assertTrue(channel.finish());
        // read bytes
        for (int i = 1; i < 10; i++) {
            Assert.assertEquals(i, channel.readOutbound());
        }
        Assert.assertNull(channel.readOutbound());
    }
}

