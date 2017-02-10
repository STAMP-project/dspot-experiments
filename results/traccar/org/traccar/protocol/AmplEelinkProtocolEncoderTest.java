

package org.traccar.protocol;


public class AmplEelinkProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncode() throws java.lang.Exception {
        org.traccar.protocol.EelinkProtocolEncoder encoder = new org.traccar.protocol.EelinkProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_ENGINE_STOP);
        verifyCommand(encoder, command, binary("676780000f0000010000000052454c41592c3123"));
    }
}

