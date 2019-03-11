package org.apereo.cas;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link DistributedCacheObjectTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DistributedCacheObjectTests {
    @Test
    public void verifyAction() {
        val o = new DistributedCacheObject("objectValue");
        Assertions.assertTrue(o.getProperties().isEmpty());
        o.getProperties().put("key", "value");
        Assertions.assertFalse(o.getProperties().isEmpty());
        Assertions.assertNotNull(o.getProperty("key", String.class));
        Assertions.assertTrue(o.containsProperty("key"));
    }
}

