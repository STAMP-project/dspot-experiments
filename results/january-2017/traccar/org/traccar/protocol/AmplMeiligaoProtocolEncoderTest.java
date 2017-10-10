

package org.traccar.protocol;


public class AmplMeiligaoProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncode() throws java.lang.Exception {
        org.traccar.protocol.MeiligaoProtocolEncoder encoder = new org.traccar.protocol.MeiligaoProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(1);
        command.setType(org.traccar.model.Command.TYPE_POSITION_SINGLE);
        verifyCommand(encoder, command, binary("404000111234567890123441016cf70d0a"));
        command.setType(org.traccar.model.Command.TYPE_POSITION_PERIODIC);
        command.set(org.traccar.model.Command.KEY_FREQUENCY, 100);
        verifyCommand(encoder, command, binary("40400013123456789012344102000a2f4f0d0a"));
        command.setType(org.traccar.model.Command.TYPE_SET_TIMEZONE);
        command.set(org.traccar.model.Command.KEY_TIMEZONE, (480 * 60));
        verifyCommand(encoder, command, binary("4040001412345678901234413234383030ad0d0a"));
        command.setType(org.traccar.model.Command.TYPE_REBOOT_DEVICE);
        verifyCommand(encoder, command, binary("40400011123456789012344902d53d0d0a"));
        command.setType(org.traccar.model.Command.TYPE_ALARM_GEOFENCE);
        command.set(org.traccar.model.Command.KEY_RADIUS, 1000);
        verifyCommand(encoder, command, binary("4040001312345678901234410603e87bb00d0a"));
    }
}

