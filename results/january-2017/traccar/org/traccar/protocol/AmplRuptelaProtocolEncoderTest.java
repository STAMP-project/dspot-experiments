

package org.traccar.protocol;


public class AmplRuptelaProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncode() throws java.lang.Exception {
        org.traccar.protocol.RuptelaProtocolEncoder encoder = new org.traccar.protocol.RuptelaProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_CUSTOM);
        command.set(org.traccar.model.Command.KEY_DATA, " Setio 2,1");
        verifyCommand(encoder, command, binary("000b6c20536574696F20322C31eb3e"));
    }
}

