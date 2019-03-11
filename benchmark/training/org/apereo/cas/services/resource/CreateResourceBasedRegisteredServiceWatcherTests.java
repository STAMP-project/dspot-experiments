package org.apereo.cas.services.resource;


import java.io.File;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.services.util.RegisteredServiceJsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * This is {@link CreateResourceBasedRegisteredServiceWatcherTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class CreateResourceBasedRegisteredServiceWatcherTests {
    @Test
    @SneakyThrows
    public void verifyOperationFoundCreated() {
        val result = new AtomicBoolean(false);
        val registry = new AbstractResourceBasedServiceRegistry(new ClassPathResource("services"), Collections.singletonList(new RegisteredServiceJsonSerializer()), ( o) -> result.set(o.getClass().equals(.class))) {
            @Override
            protected String[] getExtensions() {
                return new String[]{ "json" };
            }
        };
        var results = registry.load();
        Assertions.assertFalse(results.isEmpty());
        val watcher = new CreateResourceBasedRegisteredServiceWatcher(registry);
        watcher.accept(new File(registry.getServiceRegistryDirectory().toFile(), "Sample-1.json"));
        Assertions.assertTrue(result.get());
        Assertions.assertEquals(1, registry.size());
    }
}

