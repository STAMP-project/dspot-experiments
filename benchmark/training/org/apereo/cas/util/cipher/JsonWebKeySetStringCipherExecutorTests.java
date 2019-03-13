package org.apereo.cas.util.cipher;


import java.io.File;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;


/**
 * This is {@link JsonWebKeySetStringCipherExecutorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class JsonWebKeySetStringCipherExecutorTests {
    @Test
    @SneakyThrows
    public void verifyAction() {
        val jwksKeystore = new ClassPathResource("sample.jwks");
        val data = IOUtils.toString(jwksKeystore.getInputStream(), StandardCharsets.UTF_8);
        val keystoreFile = new File(FileUtils.getTempDirectoryPath(), "sample.jwks");
        FileUtils.write(keystoreFile, data, StandardCharsets.UTF_8);
        try (val webServer = new org.apereo.cas.util.MockWebServer(8435, new org.springframework.core.io.ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE);val cipher = new JsonWebKeySetStringCipherExecutor(keystoreFile, "http://localhost:8435")) {
            webServer.start();
            val token = cipher.encode("Misagh");
            Assertions.assertEquals("Misagh", cipher.decode(token));
        }
    }
}

