

package org.traccar.protocol;


public class AmplCityeasyProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncode() throws java.lang.Exception {
        org.traccar.protocol.CityeasyProtocolEncoder encoder = new org.traccar.protocol.CityeasyProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_SET_TIMEZONE);
        command.set(org.traccar.model.Command.KEY_TIMEZONE, (6 * 3600));
        verifyCommand(encoder, command, binary("5353001100080001680000000B60820D0A"));
    }
}

