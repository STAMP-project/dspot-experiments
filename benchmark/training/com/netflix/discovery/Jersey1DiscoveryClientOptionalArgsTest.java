package com.netflix.discovery;


import com.netflix.discovery.shared.transport.jersey.Jersey1DiscoveryClientOptionalArgs;
import javax.inject.Provider;
import org.junit.Test;


/**
 *
 *
 * @author Matt Nelson
 */
public class Jersey1DiscoveryClientOptionalArgsTest {
    private Jersey1DiscoveryClientOptionalArgs args;

    @Test
    public void testHealthCheckCallbackGuiceProvider() {
        args.setHealthCheckCallbackProvider(new Jersey1DiscoveryClientOptionalArgsTest.GuiceProvider<com.netflix.appinfo.HealthCheckCallback>());
    }

    @Test
    public void testHealthCheckCallbackJavaxProvider() {
        args.setHealthCheckCallbackProvider(new Jersey1DiscoveryClientOptionalArgsTest.JavaxProvider<com.netflix.appinfo.HealthCheckCallback>());
    }

    @Test
    public void testHealthCheckHandlerGuiceProvider() {
        args.setHealthCheckHandlerProvider(new Jersey1DiscoveryClientOptionalArgsTest.GuiceProvider<com.netflix.appinfo.HealthCheckHandler>());
    }

    @Test
    public void testHealthCheckHandlerJavaxProvider() {
        args.setHealthCheckHandlerProvider(new Jersey1DiscoveryClientOptionalArgsTest.JavaxProvider<com.netflix.appinfo.HealthCheckHandler>());
    }

    private class JavaxProvider<T> implements Provider<T> {
        @Override
        public T get() {
            return null;
        }
    }

    private class GuiceProvider<T> implements com.google.inject.Provider<T> {
        @Override
        public T get() {
            return null;
        }
    }
}

