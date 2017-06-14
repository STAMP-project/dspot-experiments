

package org.traccar.helper;


public class AmplStringFinderTest {
    @org.junit.Test
    public void testFind() {
        org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("hello world", java.nio.charset.StandardCharsets.US_ASCII);
        org.junit.Assert.assertEquals((-1), buf.indexOf(0, buf.writerIndex(), new org.traccar.helper.StringFinder("bar")));
        org.junit.Assert.assertEquals(6, buf.indexOf(0, buf.writerIndex(), new org.traccar.helper.StringFinder("world")));
        org.junit.Assert.assertEquals((-1), buf.indexOf(0, buf.writerIndex(), new org.traccar.helper.StringFinder("worlds")));
        org.junit.Assert.assertEquals(0, buf.indexOf(0, buf.writerIndex(), new org.traccar.helper.StringFinder("hell")));
    }
}

