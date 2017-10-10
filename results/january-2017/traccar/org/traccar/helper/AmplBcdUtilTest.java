

package org.traccar.helper;


public class AmplBcdUtilTest {
    @org.junit.Test
    public void testReadInteger() {
        byte[] buf = new byte[]{ 1 , ((byte) (144)) , 52 };
        int result = org.traccar.helper.BcdUtil.readInteger(org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer(buf), 5);
        org.junit.Assert.assertEquals(1903, result);
    }

    @org.junit.Test
    public void testReadCoordinate() {
        byte[] buf = new byte[]{ 3 , ((byte) (133)) , 34 , 89 , 52 };
        double result = org.traccar.helper.BcdUtil.readCoordinate(org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer(buf));
        org.junit.Assert.assertEquals(38.870989, result, 1.0E-5);
    }
}

