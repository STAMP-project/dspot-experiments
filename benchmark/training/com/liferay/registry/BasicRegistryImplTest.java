/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.registry;


import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Raymond Aug?
 */
public class BasicRegistryImplTest {
    @Test
    public void testEmptyServiceTracker() {
        Registry registry = RegistryUtil.getRegistry();
        ServiceTracker<EventListener, Object> serviceTracker = registry.trackServices(EventListener.class);
        serviceTracker.open();
        Assert.assertNull(serviceTracker.getService());
    }

    @Test
    public void testServiceRanking() {
        Registry registry = RegistryUtil.getRegistry();
        BasicRegistryImplTest.Foo foo1 = new BasicRegistryImplTest.Foo();
        Map<String, Object> properties = new HashMap<>();
        properties.put("service.ranking", 1);
        ServiceRegistration<BasicRegistryImplTest.Foo> serviceRegistration1 = registry.registerService(BasicRegistryImplTest.Foo.class, foo1, properties);
        ServiceReference<BasicRegistryImplTest.Foo> serviceReference = registry.getServiceReference(BasicRegistryImplTest.Foo.class);
        Assert.assertSame(foo1, registry.getService(serviceReference));
        BasicRegistryImplTest.Foo foo2 = new BasicRegistryImplTest.Foo();
        properties = new HashMap<>();
        properties.put("service.ranking", 2);
        ServiceRegistration<BasicRegistryImplTest.Foo> serviceRegistration2 = registry.registerService(BasicRegistryImplTest.Foo.class, foo2, properties);
        serviceReference = registry.getServiceReference(BasicRegistryImplTest.Foo.class);
        Assert.assertSame(foo2, registry.getService(serviceReference));
        serviceRegistration2.unregister();
        serviceReference = registry.getServiceReference(BasicRegistryImplTest.Foo.class);
        Assert.assertSame(foo1, registry.getService(serviceReference));
        serviceRegistration1.unregister();
    }

    @Test
    public void testServiceTrackerCount() {
        Registry registry = RegistryUtil.getRegistry();
        ServiceTracker<BasicRegistryImplTest.Foo, BasicRegistryImplTest.Foo> serviceTracker = registry.trackServices(BasicRegistryImplTest.Foo.class);
        serviceTracker.open();
        Assert.assertEquals(0, serviceTracker.size());
        ServiceRegistration<BasicRegistryImplTest.Foo> serviceRegistrationA = registry.registerService(BasicRegistryImplTest.Foo.class, new BasicRegistryImplTest.Foo() {});
        Assert.assertEquals(1, serviceTracker.size());
        ServiceRegistration<BasicRegistryImplTest.Foo> serviceRegistrationB = registry.registerService(BasicRegistryImplTest.Foo.class, new BasicRegistryImplTest.Foo() {});
        Assert.assertEquals(2, serviceTracker.size());
        serviceRegistrationA.unregister();
        Assert.assertEquals(1, serviceTracker.size());
        serviceRegistrationB.unregister();
        Assert.assertEquals(0, serviceTracker.size());
    }

    @Test
    public void testServiceTrackerCustomizerCalledOncePerEvent() {
        Registry registry = RegistryUtil.getRegistry();
        AtomicInteger addingState = new AtomicInteger(0);
        AtomicInteger midifiedState = new AtomicInteger(0);
        AtomicInteger removedState = new AtomicInteger(0);
        ServiceTracker<BasicRegistryImplTest.Foo, BasicRegistryImplTest.Foo> serviceTracker = registry.trackServices(BasicRegistryImplTest.Foo.class, new BasicRegistryImplTest.FooServiceTracker(addingState, midifiedState, removedState));
        serviceTracker.open();
        ServiceRegistration<BasicRegistryImplTest.Foo> serviceRegistration = registry.registerService(BasicRegistryImplTest.Foo.class, new BasicRegistryImplTest.Foo() {});
        Assert.assertEquals(1, addingState.get());
        serviceRegistration.setProperties(Collections.<String, Object>emptyMap());
        Assert.assertEquals(1, midifiedState.get());
        serviceRegistration.unregister();
        Assert.assertEquals(1, removedState.get());
    }

    public class Foo {}

    public class FooServiceTracker implements ServiceTrackerCustomizer<BasicRegistryImplTest.Foo, BasicRegistryImplTest.Foo> {
        public FooServiceTracker(AtomicInteger addingState, AtomicInteger modifiedState, AtomicInteger removedState) {
            _addingState = addingState;
            _modifiedState = modifiedState;
            _removedState = removedState;
        }

        @Override
        public BasicRegistryImplTest.Foo addingService(ServiceReference<BasicRegistryImplTest.Foo> serviceReference) {
            Registry registry = RegistryUtil.getRegistry();
            _addingState.incrementAndGet();
            return registry.getService(serviceReference);
        }

        @Override
        public void modifiedService(ServiceReference<BasicRegistryImplTest.Foo> serviceReference, BasicRegistryImplTest.Foo service) {
            _modifiedState.incrementAndGet();
        }

        @Override
        public void removedService(ServiceReference<BasicRegistryImplTest.Foo> serviceReference, BasicRegistryImplTest.Foo service) {
            _removedState.incrementAndGet();
        }

        private final AtomicInteger _addingState;

        private final AtomicInteger _modifiedState;

        private final AtomicInteger _removedState;
    }
}

