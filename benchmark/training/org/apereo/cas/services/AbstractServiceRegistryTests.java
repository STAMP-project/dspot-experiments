package org.apereo.cas.services;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Abstracted service registry tests for all implementations.
 *
 * @author Timur Duehr
 * @since 5.3.0
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractServiceRegistryTests {
    public static final int LOAD_SIZE = 1;

    public static final String GET_PARAMETERS = "getParameters";

    private ServiceRegistry serviceRegistry;

    @Test
    public void verifyEmptyRegistry() {
        Assertions.assertEquals(0, serviceRegistry.load().size(), "Loaded too many");
        Assertions.assertEquals(0, serviceRegistry.size(), "Counted too many");
    }

    @Test
    public void verifyNonExistingService() {
        Assertions.assertNull(this.serviceRegistry.findServiceById(9999991));
        Assertions.assertNull(this.serviceRegistry.findServiceById("9999991"));
    }
}

