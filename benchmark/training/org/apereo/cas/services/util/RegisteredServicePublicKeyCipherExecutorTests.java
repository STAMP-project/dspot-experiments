package org.apereo.cas.services.util;


import java.util.Optional;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link RegisteredServicePublicKeyCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class RegisteredServicePublicKeyCipherExecutorTests {
    @Test
    public void verifyCipherUnableToEncodeForStringIsTooLong() {
        val svc = RegisteredServicePublicKeyCipherExecutorTests.getService("classpath:keys/RSA1024Public.key");
        val ticketId = RandomStringUtils.randomAlphanumeric(120);
        val e = new RegisteredServicePublicKeyCipherExecutor();
        Assertions.assertNull(e.encode(ticketId, Optional.of(svc)));
    }

    @Test
    public void verifyCipherAbleToEncode() {
        val svc = RegisteredServicePublicKeyCipherExecutorTests.getService("classpath:keys/RSA4096Public.key");
        val ticketId = RandomStringUtils.randomAlphanumeric(120);
        val e = new RegisteredServicePublicKeyCipherExecutor();
        Assertions.assertNotNull(e.encode(ticketId, Optional.of(svc)));
    }
}

