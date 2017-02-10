

package org.traccar.protocol;


public class AmplT800xProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncode() throws java.lang.Exception {
        org.traccar.protocol.T800xProtocolEncoder encoder = new org.traccar.protocol.T800xProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_CUSTOM);
        command.set(org.traccar.model.Command.KEY_DATA, "RELAY,0000,On#");
        verifyCommand(encoder, command, binary("232381001e000101234567890123450152454c41592c303030302c4f6e23"));
    }
}

