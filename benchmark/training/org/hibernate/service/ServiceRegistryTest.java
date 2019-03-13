package org.hibernate.service;


import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.hamcrest.core.IsInstanceOf;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.Startable;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


public class ServiceRegistryTest {
    private final ServiceRegistry registry = buildRegistry();

    private static final int NUMBER_OF_THREADS = 100;

    private StandardServiceRegistryBuilder standardServiceRegistryBuilder;

    @Test
    @TestForIssue(jiraKey = "HHH-10427")
    public void testOnlyOneInstanceOfTheServiceShouldBeCreated() throws InterruptedException, ExecutionException {
        Future<ServiceRegistryTest.SlowInitializationService>[] serviceIdentities = execute();
        ServiceRegistryTest.SlowInitializationService previousResult = null;
        for (Future<ServiceRegistryTest.SlowInitializationService> future : serviceIdentities) {
            final ServiceRegistryTest.SlowInitializationService result = future.get();
            if (previousResult == null) {
                previousResult = result;
            } else {
                Assert.assertTrue("There are more than one instance of the service", (result == previousResult));
            }
        }
        standardServiceRegistryBuilder.destroy(registry);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11395")
    public void testGetService() {
        Assert.assertThat(registry.getService(ServiceRegistryTest.SlowInitializationService.class), IsInstanceOf.instanceOf(ServiceRegistryTest.SlowInitializationService.class));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11395")
    public void testGetServiceReturnsNullWhenTheServiceInitiatorInitiateServiceReturnsNull() {
        Assert.assertNull(registry.getService(ServiceRegistryTest.FakeService.class));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11395")
    public void testRequireService() {
        Assert.assertThat(registry.requireService(ServiceRegistryTest.SlowInitializationService.class), IsInstanceOf.instanceOf(ServiceRegistryTest.SlowInitializationService.class));
    }

    @Test(expected = NullServiceException.class)
    @TestForIssue(jiraKey = "HHH-11395")
    public void testRequireServiceThrowsAnExceptionWhenTheServiceInitiatorInitiateServiceReturnsNull() {
        Assert.assertNull(registry.requireService(ServiceRegistryTest.FakeService.class));
    }

    public class ServiceCallable implements Callable<ServiceRegistryTest.SlowInitializationService> {
        private final ServiceRegistry registry;

        public ServiceCallable(ServiceRegistry registry) {
            this.registry = registry;
        }

        @Override
        public ServiceRegistryTest.SlowInitializationService call() throws Exception {
            final ServiceRegistryTest.SlowInitializationService service = registry.getService(ServiceRegistryTest.SlowInitializationService.class);
            Assert.assertTrue("The service is not initialized", service.isInitialized());
            Assert.assertTrue("The service is not configured", service.isConfigured());
            Assert.assertTrue("The service is not started", service.isStarted());
            return service;
        }
    }

    public class SlowInitializationService implements Service , Configurable , ServiceRegistryAwareService , Startable {
        private static final int TIME_TO_SLEEP = 100;

        private boolean initialized;

        private boolean configured;

        private boolean started;

        public SlowInitializationService() {
            try {
                Thread.sleep(ServiceRegistryTest.SlowInitializationService.TIME_TO_SLEEP);
            } catch (InterruptedException e) {
            }
        }

        @Override
        public void injectServices(ServiceRegistryImplementor serviceRegistry) {
            try {
                Thread.sleep(ServiceRegistryTest.SlowInitializationService.TIME_TO_SLEEP);
            } catch (InterruptedException e) {
            }
            initialized = true;
        }

        @Override
        public void configure(Map configurationValues) {
            try {
                Thread.sleep(ServiceRegistryTest.SlowInitializationService.TIME_TO_SLEEP);
            } catch (InterruptedException e) {
            }
            configured = true;
        }

        @Override
        public void start() {
            try {
                Thread.sleep(ServiceRegistryTest.SlowInitializationService.TIME_TO_SLEEP);
            } catch (InterruptedException e) {
            }
            started = true;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public boolean isConfigured() {
            return configured;
        }

        public boolean isStarted() {
            return started;
        }
    }

    public class SlowServiceInitiator implements StandardServiceInitiator<ServiceRegistryTest.SlowInitializationService> {
        @Override
        public Class<ServiceRegistryTest.SlowInitializationService> getServiceInitiated() {
            return ServiceRegistryTest.SlowInitializationService.class;
        }

        @Override
        public ServiceRegistryTest.SlowInitializationService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
            return new ServiceRegistryTest.SlowInitializationService();
        }
    }

    public class NullServiceInitiator implements StandardServiceInitiator<ServiceRegistryTest.FakeService> {
        @Override
        public Class<ServiceRegistryTest.FakeService> getServiceInitiated() {
            return ServiceRegistryTest.FakeService.class;
        }

        @Override
        public ServiceRegistryTest.FakeService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
            return null;
        }
    }

    public class FakeService implements Service , Configurable , ServiceRegistryAwareService , Startable {
        @Override
        public void start() {
        }

        @Override
        public void configure(Map configurationValues) {
        }

        @Override
        public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        }
    }
}

