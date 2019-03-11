package com.netflix.discovery;


import Scopes.SINGLETON;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.PropertiesInstanceConfig;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.SimpleEurekaHttpServer;
import com.netflix.discovery.shared.transport.jersey.Jersey1DiscoveryClientOptionalArgs;
import com.netflix.discovery.util.EurekaEntityFunctions;
import com.netflix.discovery.util.InstanceInfoGenerator;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.lifecycle.LifecycleManager;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EurekaClientLifecycleTest {
    private static final String MY_APPLICATION_NAME = "MYAPPLICATION";

    private static final String MY_INSTANCE_ID = "myInstanceId";

    private static final Applications APPLICATIONS = InstanceInfoGenerator.newBuilder(1, 1).build().toApplications();

    private static final Applications APPLICATIONS_DELTA = new Applications(EurekaClientLifecycleTest.APPLICATIONS.getAppsHashCode(), 1L, Collections.<Application>emptyList());

    public static final int TIMEOUT_MS = 2 * 1000;

    public static final EurekaHttpClient requestHandler = Mockito.mock(EurekaHttpClient.class);

    public static SimpleEurekaHttpServer eurekaHttpServer;

    @Test
    public void testEurekaClientLifecycle() throws Exception {
        Injector injector = LifecycleInjector.builder().withModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EurekaInstanceConfig.class).to(EurekaClientLifecycleTest.LocalEurekaInstanceConfig.class);
                bind(EurekaClientConfig.class).to(EurekaClientLifecycleTest.LocalEurekaClientConfig.class);
                bind(AbstractDiscoveryClientOptionalArgs.class).to(Jersey1DiscoveryClientOptionalArgs.class).in(SINGLETON);
            }
        }).build().createInjector();
        LifecycleManager lifecycleManager = injector.getInstance(LifecycleManager.class);
        lifecycleManager.start();
        EurekaClient client = injector.getInstance(EurekaClient.class);
        // Check registration
        Mockito.verify(EurekaClientLifecycleTest.requestHandler, Mockito.timeout(EurekaClientLifecycleTest.TIMEOUT_MS).atLeast(1)).register(ArgumentMatchers.any(InstanceInfo.class));
        // Check registry fetch
        Mockito.verify(EurekaClientLifecycleTest.requestHandler, Mockito.timeout(EurekaClientLifecycleTest.TIMEOUT_MS).times(1)).getApplications();
        Mockito.verify(EurekaClientLifecycleTest.requestHandler, Mockito.timeout(EurekaClientLifecycleTest.TIMEOUT_MS).atLeast(1)).getDelta();
        Assert.assertThat(EurekaEntityFunctions.countInstances(client.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(1)));
        // Shutdown container, and check that unregister happens
        lifecycleManager.close();
        Mockito.verify(EurekaClientLifecycleTest.requestHandler, Mockito.times(1)).cancel(EurekaClientLifecycleTest.MY_APPLICATION_NAME, EurekaClientLifecycleTest.MY_INSTANCE_ID);
    }

    @Test
    public void testBackupRegistryInjection() throws Exception {
        final BackupRegistry backupRegistry = Mockito.mock(BackupRegistry.class);
        Mockito.when(backupRegistry.fetchRegistry()).thenReturn(EurekaClientLifecycleTest.APPLICATIONS);
        Injector injector = LifecycleInjector.builder().withModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EurekaInstanceConfig.class).to(EurekaClientLifecycleTest.LocalEurekaInstanceConfig.class);
                bind(EurekaClientConfig.class).to(EurekaClientLifecycleTest.BadServerEurekaClientConfig1.class);
                bind(BackupRegistry.class).toInstance(backupRegistry);
                bind(AbstractDiscoveryClientOptionalArgs.class).to(Jersey1DiscoveryClientOptionalArgs.class).in(SINGLETON);
            }
        }).build().createInjector();
        LifecycleManager lifecycleManager = injector.getInstance(LifecycleManager.class);
        lifecycleManager.start();
        EurekaClient client = injector.getInstance(EurekaClient.class);
        Mockito.verify(backupRegistry, Mockito.atLeast(1)).fetchRegistry();
        Assert.assertThat(EurekaEntityFunctions.countInstances(client.getApplications()), CoreMatchers.is(CoreMatchers.equalTo(1)));
    }

    @Test(expected = ProvisionException.class)
    public void testEnforcingRegistrationOnInitFastFail() {
        Injector injector = LifecycleInjector.builder().withModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(EurekaInstanceConfig.class).to(EurekaClientLifecycleTest.LocalEurekaInstanceConfig.class);
                bind(EurekaClientConfig.class).to(EurekaClientLifecycleTest.BadServerEurekaClientConfig2.class);
                bind(AbstractDiscoveryClientOptionalArgs.class).to(Jersey1DiscoveryClientOptionalArgs.class).in(SINGLETON);
            }
        }).build().createInjector();
        LifecycleManager lifecycleManager = injector.getInstance(LifecycleManager.class);
        try {
            lifecycleManager.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // this will throw a Guice ProvisionException for the constructor failure
        EurekaClient client = injector.getInstance(EurekaClient.class);
    }

    private static class LocalEurekaInstanceConfig extends PropertiesInstanceConfig {
        @Override
        public String getInstanceId() {
            return EurekaClientLifecycleTest.MY_INSTANCE_ID;
        }

        @Override
        public String getAppname() {
            return EurekaClientLifecycleTest.MY_APPLICATION_NAME;
        }

        @Override
        public int getLeaseRenewalIntervalInSeconds() {
            return 1;
        }
    }

    private static class LocalEurekaClientConfig extends DefaultEurekaClientConfig {
        @Override
        public List<String> getEurekaServerServiceUrls(String myZone) {
            return Collections.singletonList(EurekaClientLifecycleTest.eurekaHttpServer.getServiceURI().toString());
        }

        @Override
        public int getInitialInstanceInfoReplicationIntervalSeconds() {
            return 0;
        }

        @Override
        public int getInstanceInfoReplicationIntervalSeconds() {
            return 1;
        }

        @Override
        public int getRegistryFetchIntervalSeconds() {
            return 1;
        }
    }

    private static class BadServerEurekaClientConfig1 extends EurekaClientLifecycleTest.LocalEurekaClientConfig {
        @Override
        public List<String> getEurekaServerServiceUrls(String myZone) {
            return Collections.singletonList("http://localhost:1/v2/");// Fail fast on bad port number

        }

        @Override
        public boolean shouldRegisterWithEureka() {
            return false;
        }
    }

    private static class BadServerEurekaClientConfig2 extends EurekaClientLifecycleTest.LocalEurekaClientConfig {
        @Override
        public List<String> getEurekaServerServiceUrls(String myZone) {
            return Collections.singletonList("http://localhost:1/v2/");// Fail fast on bad port number

        }

        @Override
        public boolean shouldFetchRegistry() {
            return false;
        }

        @Override
        public boolean shouldEnforceRegistrationAtInit() {
            return true;
        }
    }
}

