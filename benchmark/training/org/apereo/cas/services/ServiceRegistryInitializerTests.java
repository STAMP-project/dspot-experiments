package org.apereo.cas.services;


import java.util.Collections;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class ServiceRegistryInitializerTests {
    @Test
    public void ensureInitFromJsonDoesNotCreateDuplicates() {
        val initialService = ServiceRegistryInitializerTests.newService();
        val servicesManager = Mockito.mock(ServicesManager.class);
        val jsonServiceRegistry = Mockito.mock(ServiceRegistry.class);
        Mockito.when(jsonServiceRegistry.load()).thenReturn(Collections.singletonList(initialService));
        val serviceRegistry = new InMemoryServiceRegistry(Mockito.mock(ApplicationEventPublisher.class));
        val serviceRegistryInitializer = new ServiceRegistryInitializer(jsonServiceRegistry, serviceRegistry, servicesManager);
        serviceRegistryInitializer.initServiceRegistryIfNecessary();
        Assertions.assertEquals(1, serviceRegistry.size());
        val initialService2 = ServiceRegistryInitializerTests.newService();
        Mockito.when(jsonServiceRegistry.load()).thenReturn(Collections.singletonList(initialService2));
        serviceRegistryInitializer.initServiceRegistryIfNecessary();
        Assertions.assertEquals(1, serviceRegistry.size());
    }
}

