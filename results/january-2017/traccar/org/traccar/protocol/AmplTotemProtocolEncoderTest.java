

package org.traccar.protocol;


public class AmplTotemProtocolEncoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testEncode() throws java.lang.Exception {
        org.traccar.protocol.TotemProtocolEncoder encoder = new org.traccar.protocol.TotemProtocolEncoder();
        org.traccar.model.Command command = new org.traccar.model.Command();
        command.setDeviceId(2);
        command.setType(org.traccar.model.Command.TYPE_ENGINE_STOP);
        command.set(org.traccar.model.Command.KEY_DEVICE_PASSWORD, "000000");
        org.junit.Assert.assertEquals("*000000,025,C,1#", encoder.encodeCommand(command));
    }

    /* amplification of org.traccar.protocol.TotemProtocolEncoderTest#testEncode */
    @org.junit.Test(timeout = 1000)
    public void testEncode_cf11_failAssert6_add48_add148() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.traccar.protocol.TotemProtocolEncoder encoder = new org.traccar.protocol.TotemProtocolEncoder();
            org.traccar.model.Command command = new org.traccar.model.Command();
            // MethodCallAdder
            command.setDeviceId(2);
            // MethodCallAdder
            command.setDeviceId(2);
            command.setDeviceId(2);
            command.setType(org.traccar.model.Command.TYPE_ENGINE_STOP);
            command.set(org.traccar.model.Command.KEY_DEVICE_PASSWORD, "000000");
            // StatementAdderOnAssert create null value
            org.traccar.Protocol vc_2 = (org.traccar.Protocol)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2);
            // StatementAdderMethod cloned existing statement
            vc_2.getSupportedCommands();
            // MethodAssertGenerator build local variable
            Object o_12_0 = encoder.encodeCommand(command);
            org.junit.Assert.fail("testEncode_cf11 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

