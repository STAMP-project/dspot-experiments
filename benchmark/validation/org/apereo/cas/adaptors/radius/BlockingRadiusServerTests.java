package org.apereo.cas.adaptors.radius;


import net.jradius.exception.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * This is {@link BlockingRadiusServerTests}.
 * Runs test cases against a radius server running on "https://console.ironwifi.com/".
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class BlockingRadiusServerTests extends AbstractRadiusServerTests {
    public static final String XYZ = "xyz";

    @Test
    public void verifyBadSecret() throws Exception {
        Assertions.assertThrows(TimeoutException.class, () -> authenticate(BlockingRadiusServerTests.XYZ, BlockingRadiusServerTests.XYZ));
    }

    @Test
    public void verifyBadPorts() {
        Assertions.assertThrows(TimeoutException.class, () -> authenticate(BlockingRadiusServerTests.XYZ, BlockingRadiusServerTests.XYZ));
    }

    @Test
    public void verifyBadAddress() {
        Assertions.assertThrows(TimeoutException.class, () -> authenticate(BlockingRadiusServerTests.XYZ, BlockingRadiusServerTests.XYZ));
    }
}

