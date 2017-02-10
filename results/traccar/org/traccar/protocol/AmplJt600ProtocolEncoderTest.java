

package org.traccar.protocol;


public class AmplJt600ProtocolEncoderTest extends org.traccar.ProtocolTest {
    org.traccar.protocol.Jt600ProtocolEncoder encoder = new org.traccar.protocol.Jt600ProtocolEncoder();

    org.traccar.model.Command command = new org.traccar.model.Command();

    @org.junit.Test
    public void testEngineStop() throws java.lang.Exception {
        command.setType(org.traccar.model.Command.TYPE_ENGINE_STOP);
        org.junit.Assert.assertEquals("(S07,0)", encoder.encodeCommand(command));
    }

    @org.junit.Test
    public void testEngineResume() throws java.lang.Exception {
        command.setType(org.traccar.model.Command.TYPE_ENGINE_RESUME);
        org.junit.Assert.assertEquals("(S07,1)", encoder.encodeCommand(command));
    }

    @org.junit.Test
    public void testSetTimezone() throws java.lang.Exception {
        command.setType(org.traccar.model.Command.TYPE_SET_TIMEZONE);
        command.set(org.traccar.model.Command.KEY_TIMEZONE, (240 * 60));
        org.junit.Assert.assertEquals("(S09,1,240)", encoder.encodeCommand(command));
    }

    @org.junit.Test
    public void testReboot() throws java.lang.Exception {
        command.setType(org.traccar.model.Command.TYPE_REBOOT_DEVICE);
        org.junit.Assert.assertEquals("(S17)", encoder.encodeCommand(command));
    }
}

