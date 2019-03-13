package org.apereo.cas.adaptors.u2f.storage;


import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;


/**
 * This is {@link AbstractU2FDeviceRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@DirtiesContext
public abstract class AbstractU2FDeviceRepositoryTests {
    @Test
    public void verifyDeviceSaved() {
        try {
            val deviceRepository = getDeviceRepository();
            registerDevices(deviceRepository);
            val devs = deviceRepository.getRegisteredDevices("casuser");
            AbstractU2FDeviceRepositoryTests.verifyDevicesAvailable(devs);
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}

