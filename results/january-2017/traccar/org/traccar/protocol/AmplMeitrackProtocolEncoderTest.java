

package org.traccar.protocol;


public class AmplMeitrackProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncode() throws java.lang.Exception {
        org.traccar.protocol.MeitrackProtocolEncoder encoder = new org.traccar.protocol.MeitrackProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_POSITION_SINGLE);
        org.junit.Assert.assertEquals("@@Q25,123456789012345,A10*68\r\n", encoder.encodeCommand(command));
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_SEND_SMS);
        command.set(org.traccar.model.Command.KEY_PHONE, "15360853789");
        command.set(org.traccar.model.Command.KEY_MESSAGE, "Meitrack");
        org.junit.Assert.assertEquals("@@f48,123456789012345,C02,0,15360853789,Meitrack*B0\r\n", encoder.encodeCommand(command));
    }
}

