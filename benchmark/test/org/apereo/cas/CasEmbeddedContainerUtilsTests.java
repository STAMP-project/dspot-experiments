package org.apereo.cas;


import CasEmbeddedContainerUtils.EMBEDDED_CONTAINER_CONFIG_ACTIVE;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;


/**
 * This is {@link CasEmbeddedContainerUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class CasEmbeddedContainerUtilsTests {
    @Test
    public void verifyRuntimeProperties() {
        val map = CasEmbeddedContainerUtils.getRuntimeProperties(true);
        Assertions.assertEquals(1, map.size());
        Assertions.assertTrue(map.containsKey(EMBEDDED_CONTAINER_CONFIG_ACTIVE));
    }

    @Test
    public void verifyCasBanner() {
        val banner = CasEmbeddedContainerUtils.getCasBannerInstance();
        Assertions.assertNotNull(banner);
        val out = new ByteArrayOutputStream();
        banner.printBanner(new MockEnvironment(), getClass(), new PrintStream(out));
        val results = new String(out.toByteArray(), StandardCharsets.UTF_8);
        Assertions.assertNotNull(results);
    }
}

