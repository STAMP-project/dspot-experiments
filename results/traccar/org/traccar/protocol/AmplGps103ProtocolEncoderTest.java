

package org.traccar.protocol;


public class AmplGps103ProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncodePositionPeriodic() throws java.lang.Exception {
        org.traccar.protocol.Gps103ProtocolEncoder encoder = new org.traccar.protocol.Gps103ProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_POSITION_PERIODIC);
        command.set(org.traccar.model.Command.KEY_FREQUENCY, 300);
        org.junit.Assert.assertEquals("**,imei:123456789012345,C,05m", encoder.encodeCommand(command));
    }

    @org.junit.Test
    public void testEncodeCustom() throws java.lang.Exception {
        org.traccar.protocol.Gps103ProtocolEncoder encoder = new org.traccar.protocol.Gps103ProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_CUSTOM);
        command.set(org.traccar.model.Command.KEY_DATA, "H,080");
        org.junit.Assert.assertEquals("**,imei:123456789012345,H,080", encoder.encodeCommand(command));
    }
}

