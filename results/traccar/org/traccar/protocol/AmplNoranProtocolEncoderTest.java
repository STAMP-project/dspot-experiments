

package org.traccar.protocol;


public class AmplNoranProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncode() throws java.lang.Exception {
        org.traccar.protocol.NoranProtocolEncoder encoder = new org.traccar.protocol.NoranProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_ENGINE_STOP);
        verifyCommand(encoder, command, binary("0d0a2a4b5700440002000000000000002a4b572c3030302c3030372c3030303030302c302300000000000000000000000000000000000000000000000000000000000d0a"));
    }
}

