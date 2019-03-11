package org.apereo.cas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class RegisteredServicePublicKeyImplTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "registeredServicePublicKeyImpl.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeAX509CertificateCredentialToJson() throws IOException {
        val publicKeyWritten = new RegisteredServicePublicKeyImpl("location", "algorithm");
        RegisteredServicePublicKeyImplTests.MAPPER.writeValue(RegisteredServicePublicKeyImplTests.JSON_FILE, publicKeyWritten);
        val credentialRead = RegisteredServicePublicKeyImplTests.MAPPER.readValue(RegisteredServicePublicKeyImplTests.JSON_FILE, RegisteredServicePublicKeyImpl.class);
        Assertions.assertEquals(publicKeyWritten, credentialRead);
    }
}

