

package org.traccar.protocol;


public class AmplTeltonikaProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncode() throws java.lang.Exception {
        org.traccar.protocol.TeltonikaProtocolEncoder encoder = new org.traccar.protocol.TeltonikaProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_CUSTOM);
        command.set(org.traccar.model.Command.KEY_DATA, "setdigout 11");
        verifyCommand(encoder, command, binary("00000000000000160C01050000000E7365746469676F75742031310D0A010000E258"));
    }
}

