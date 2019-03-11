package org.apereo.cas.services;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class SimpleServiceTests {
    @Test
    public void verifyProperId() {
        Assertions.assertEquals(RegisteredServiceTestUtils.CONST_TEST_URL, RegisteredServiceTestUtils.getService().getId(), "Ids are not equal.");
    }

    @Test
    public void verifyEqualsWithNull() {
        Assertions.assertNotEquals(RegisteredServiceTestUtils.getService(), null, "Service matches null.");
    }

    @Test
    public void verifyEqualsWithBadClass() {
        Assertions.assertNotEquals(RegisteredServiceTestUtils.getService(), new Object(), "Services matches String class.");
    }

    @Test
    public void verifyEquals() {
        Assertions.assertEquals(RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getService(), "Services are not equal.");
    }
}

