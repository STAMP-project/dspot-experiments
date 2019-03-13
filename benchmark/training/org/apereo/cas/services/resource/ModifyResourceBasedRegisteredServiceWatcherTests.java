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
import org.springframework.core.io.FileSystemResource;


/**
 * This is {@link ModifyResourceBasedRegisteredServiceWatcherTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class ModifyResourceBasedRegisteredServiceWatcherTests {
    @Test
    @SneakyThrows
    public void verifyOperationFoundModified() {
        val result = new AtomicBoolean(false);
        val registry = new AbstractResourceBasedServiceRegistry(new ClassPathResource("services"), Collections.singletonList(new RegisteredServiceJsonSerializer()), ( o) -> result.set(o.getClass().equals(.class))) {
            @Override
            protected String[] getExtensions() {
                return new String[]{ "json" };
            }
        };
        var results = registry.load();
        Assertions.assertFalse(results.isEmpty());
        val service = registry.findServiceById(1);
        service.setEvaluationOrder(666);
        registry.load();
        val temp = new FileSystemResource(File.createTempFile("Sample-1", ".json"));
        new RegisteredServiceJsonSerializer().to(temp.getFile(), service);
        val watcher = new ModifyResourceBasedRegisteredServiceWatcher(registry);
        watcher.accept(temp.getFile());
        Assertions.assertTrue(result.get());
        Assertions.assertEquals(1, registry.size());
        registry.removeRegisteredService(service);
        Assertions.assertEquals(0, registry.size());
    }
}

