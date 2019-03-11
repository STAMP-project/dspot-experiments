package org.apereo.cas.services.util;


import java.io.File;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link CasAddonsRegisteredServicesJsonSerializerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class CasAddonsRegisteredServicesJsonSerializerTests {
    @Test
    public void verifySupports() {
        val s = new CasAddonsRegisteredServicesJsonSerializer();
        Assertions.assertTrue(s.supports(new File("servicesRegistry.conf")));
    }

    @Test
    public void verifyLoad() {
        val s = new CasAddonsRegisteredServicesJsonSerializer();
        val services = s.load(CasAddonsRegisteredServicesJsonSerializerTests.getServiceRegistryResource());
        Assertions.assertEquals(3, services.size());
    }
}

