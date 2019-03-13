/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.osgi;


import HazelcastInternalOSGiService.DEFAULT_GROUP_NAME;
import HazelcastInternalOSGiService.DEFAULT_ID;
import HazelcastOSGiService.HAZELCAST_OSGI_GROUPING_DISABLED;
import HazelcastOSGiService.HAZELCAST_OSGI_REGISTER_DISABLED;
import HazelcastOSGiService.HAZELCAST_OSGI_START;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.osgi.impl.HazelcastInternalOSGiService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleException;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class HazelcastOSGiServiceTest extends HazelcastTestSupport {
    private TestBundle bundle;

    private TestBundleContext bundleContext;

    private HazelcastOSGiServiceTest.TestBundleRegisterDeregisterListener registerDeregisterListener;

    private class TestBundleRegisterDeregisterListener implements TestBundle.RegisterDeregisterListener {
        final String className;

        boolean throwExceptionOnRegister;

        boolean throwExceptionOnDeregister;

        private TestBundleRegisterDeregisterListener() {
            this.className = null;
        }

        private TestBundleRegisterDeregisterListener(String className) {
            this.className = className;
        }

        @Override
        public void onRegister(String clazz, TestServiceReference serviceReference) {
            if (throwExceptionOnRegister) {
                if ((className) != null) {
                    if (clazz.equals(className)) {
                        throw new IllegalStateException("You cannot register!");
                    }
                } else {
                    throw new IllegalStateException("You cannot register!");
                }
            }
        }

        @Override
        public void onDeregister(String clazz, TestServiceReference serviceReference) {
            if (throwExceptionOnDeregister) {
                if ((className) != null) {
                    if (clazz.equals(className)) {
                        throw new IllegalStateException("You cannot deregister!");
                    }
                } else {
                    throw new IllegalStateException("You cannot deregister!");
                }
            }
        }
    }

    @Test
    public void serviceRetrievedSuccessfully() {
        HazelcastInternalOSGiService service = getService();
        Assert.assertNotNull(service);
    }

    @Test
    public void bundleOfServiceRetrievedSuccessfully() {
        HazelcastInternalOSGiService service = getService();
        Assert.assertEquals(bundle, service.getOwnerBundle());
    }

    @Test
    public void idOfServiceRetrievedSuccessfully() {
        HazelcastInternalOSGiService service = getService();
        Assert.assertEquals(DEFAULT_ID, service.getId());
    }

    @Test
    public void serviceDeactivatedAndThenActivatedSuccessfully() throws BundleException {
        HazelcastInternalOSGiService service = getService();
        Assert.assertTrue(service.isActive());
        bundle.stop();
        Assert.assertFalse(service.isActive());
        Assert.assertNull(getService());
        bundle.start();
        Assert.assertTrue(service.isActive());
        Assert.assertNotNull(getService());
    }

    @Test
    public void defaultInstanceNotExistWhenItIsNotSpecified() {
        HazelcastInternalOSGiService service = getService();
        Assert.assertNull(service.getDefaultHazelcastInstance());
    }

    @Test
    public void defaultInstanceExistWhenItIsSpecified() throws BundleException {
        String propValue = System.getProperty(HAZELCAST_OSGI_START);
        TestBundle testBundle = null;
        try {
            System.setProperty(HAZELCAST_OSGI_START, "true");
            testBundle = new TestBundle();
            testBundle.start();
            HazelcastInternalOSGiService service = getService(testBundle.getBundleContext());
            Assert.assertNotNull(service);
            Assert.assertNotNull(service.getDefaultHazelcastInstance());
        } finally {
            if (propValue != null) {
                System.setProperty(HAZELCAST_OSGI_START, propValue);
            }
            if (testBundle != null) {
                testBundle.stop();
            }
        }
    }

    @Test
    public void serviceCouldNotBeActivatedWhenThereIsExceptionWhileRegisteringDefaultInstance() throws BundleException {
        String propValue = System.getProperty(HAZELCAST_OSGI_START);
        TestBundle testBundle = null;
        try {
            System.setProperty(HAZELCAST_OSGI_START, "true");
            HazelcastOSGiServiceTest.TestBundleRegisterDeregisterListener registerDeregisterListener = new HazelcastOSGiServiceTest.TestBundleRegisterDeregisterListener(HazelcastInstance.class.getName());
            registerDeregisterListener.throwExceptionOnRegister = true;
            testBundle = new TestBundle(registerDeregisterListener);
            try {
                testBundle.start();
                Assert.fail(("OSGI service could not be activated because of exception while registering default instance." + " It is expected to get `IllegalStateException` here!"));
            } catch (IllegalStateException e) {
                // Since bundle is not active, it is expected to get `IllegalStateException`
            }
        } finally {
            if (propValue != null) {
                System.setProperty(HAZELCAST_OSGI_START, propValue);
            }
            if (testBundle != null) {
                testBundle.stop();
            }
        }
    }

    @Test
    public void serviceCouldNotBeActivatedWhenThereIsExceptionWhileRegisteringService() throws BundleException {
        String propValue = System.getProperty(HAZELCAST_OSGI_START);
        TestBundle testBundle = null;
        try {
            System.setProperty(HAZELCAST_OSGI_START, "true");
            HazelcastOSGiServiceTest.TestBundleRegisterDeregisterListener registerDeregisterListener = new HazelcastOSGiServiceTest.TestBundleRegisterDeregisterListener(HazelcastOSGiService.class.getName());
            registerDeregisterListener.throwExceptionOnRegister = true;
            testBundle = new TestBundle(registerDeregisterListener);
            try {
                testBundle.start();
                Assert.fail(("OSGI service could not be activated because of exception while registering default instance." + "It is expected to get `IllegalStateException` here!"));
            } catch (IllegalStateException e) {
                // Since bundle is not active, it is expected to get `IllegalStateException`
            }
        } finally {
            if (propValue != null) {
                System.setProperty(HAZELCAST_OSGI_START, propValue);
            }
            if (testBundle != null) {
                testBundle.stop();
            }
        }
    }

    @Test
    public void newInstanceRetrievedSuccessfullyWithoutConfiguration() {
        HazelcastInternalOSGiService service = getService();
        HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance();
        Assert.assertNotNull(osgiInstance);
        HazelcastInstance instance = osgiInstance.getDelegatedInstance();
        Assert.assertNotNull(instance);
    }

    @Test
    public void newInstanceRetrievedSuccessfullyWithConfiguration() {
        final String INSTANCE_NAME = "test-osgi-instance";
        HazelcastInternalOSGiService service = getService();
        Config config = new Config(INSTANCE_NAME);
        HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance(config);
        Assert.assertNotNull(osgiInstance);
        Assert.assertEquals(config.getInstanceName(), osgiInstance.getConfig().getInstanceName());
        HazelcastInstance instance = osgiInstance.getDelegatedInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals(config.getInstanceName(), instance.getConfig().getInstanceName());
    }

    @Test
    public void newInstanceRegisteredAsServiceWhenRegistrationIsNotDisabled() throws BundleException {
        HazelcastInternalOSGiService service = getService();
        service.newHazelcastInstance();
        Assert.assertNull(bundleContext.getServiceReference(HazelcastOSGiInstance.class.getName()));
    }

    @Test
    public void newInstanceNotRegisteredAsServiceWhenRegistrationIsDisabled() throws BundleException {
        String propValue = System.getProperty(HAZELCAST_OSGI_REGISTER_DISABLED);
        TestBundle testBundle = null;
        try {
            System.setProperty(HAZELCAST_OSGI_REGISTER_DISABLED, "true");
            testBundle = new TestBundle();
            TestBundleContext testBundleContext = testBundle.getBundleContext();
            testBundle.start();
            HazelcastInternalOSGiService service = getService(testBundleContext);
            Assert.assertNotNull(service);
            service.newHazelcastInstance();
            Assert.assertNull(testBundleContext.getServiceReference(HazelcastOSGiInstance.class.getName()));
        } finally {
            if (propValue != null) {
                System.setProperty(HAZELCAST_OSGI_REGISTER_DISABLED, propValue);
            }
            if (testBundle != null) {
                testBundle.stop();
            }
        }
    }

    @Test
    public void groupNameIsSetToDefaultGroupNameOfBundleWhenGroupingIsNotDisabled() {
        HazelcastInternalOSGiService service = getService();
        HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance();
        Assert.assertEquals(DEFAULT_GROUP_NAME, osgiInstance.getConfig().getGroupConfig().getName());
        HazelcastInstance instance = osgiInstance.getDelegatedInstance();
        Assert.assertEquals(DEFAULT_GROUP_NAME, instance.getConfig().getGroupConfig().getName());
    }

    @Test
    public void groupNameIsSetToDefaultGroupNameOfBundleWhenConfigIsGivenWithoutSpecifiedGroupConfigAndGroupingIsNotDisabled() {
        Config config = new Config();
        HazelcastInternalOSGiService service = getService();
        HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance(config);
        Assert.assertEquals(DEFAULT_GROUP_NAME, osgiInstance.getConfig().getGroupConfig().getName());
        HazelcastInstance instance = osgiInstance.getDelegatedInstance();
        Assert.assertEquals(DEFAULT_GROUP_NAME, instance.getConfig().getGroupConfig().getName());
    }

    @Test
    public void groupNameIsSetToDefaultGroupNameOfBundleWhenConfigIsGivenWithNullGroupConfigAndGroupingIsNotDisabled() {
        Config config = new Config();
        config.setGroupConfig(null);
        HazelcastInternalOSGiService service = getService();
        HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance(config);
        Assert.assertEquals(DEFAULT_GROUP_NAME, osgiInstance.getConfig().getGroupConfig().getName());
        HazelcastInstance instance = osgiInstance.getDelegatedInstance();
        Assert.assertEquals(DEFAULT_GROUP_NAME, instance.getConfig().getGroupConfig().getName());
    }

    @Test
    public void groupNameIsSetToSpecifiedGroupNameWhenGroupingIsNotDisabled() {
        final String GROUP_NAME = "my-osgi-group";
        HazelcastInternalOSGiService service = getService();
        Config config = new Config();
        config.getGroupConfig().setName(GROUP_NAME);
        HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance(config);
        Assert.assertEquals(GROUP_NAME, osgiInstance.getConfig().getGroupConfig().getName());
        HazelcastInstance instance = osgiInstance.getDelegatedInstance();
        Assert.assertEquals(GROUP_NAME, instance.getConfig().getGroupConfig().getName());
    }

    @Test
    public void groupNameIsSetToDefaultGroupNameWhenGroupingIsDisabled() throws BundleException {
        String propValue = System.getProperty(HAZELCAST_OSGI_GROUPING_DISABLED);
        TestBundle testBundle = null;
        try {
            System.setProperty(HAZELCAST_OSGI_GROUPING_DISABLED, "true");
            testBundle = new TestBundle();
            TestBundleContext testBundleContext = testBundle.getBundleContext();
            testBundle.start();
            HazelcastInternalOSGiService service = getService(testBundleContext);
            Assert.assertNotNull(service);
            HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance();
            Assert.assertEquals(GroupConfig.DEFAULT_GROUP_NAME, osgiInstance.getConfig().getGroupConfig().getName());
            HazelcastInstance instance = osgiInstance.getDelegatedInstance();
            Assert.assertEquals(GroupConfig.DEFAULT_GROUP_NAME, instance.getConfig().getGroupConfig().getName());
        } finally {
            if (propValue != null) {
                System.setProperty(HAZELCAST_OSGI_GROUPING_DISABLED, propValue);
            }
            if (testBundle != null) {
                testBundle.stop();
            }
        }
    }

    @Test
    public void groupNameIsSetToSpecifiedGroupNameWhenGroupingIsDisabled() throws BundleException {
        final String GROUP_NAME = "my-osgi-group";
        String propValue = System.getProperty(HAZELCAST_OSGI_GROUPING_DISABLED);
        TestBundle testBundle = null;
        try {
            System.setProperty(HAZELCAST_OSGI_GROUPING_DISABLED, "true");
            testBundle = new TestBundle();
            TestBundleContext testBundleContext = testBundle.getBundleContext();
            testBundle.start();
            HazelcastInternalOSGiService service = getService(testBundleContext);
            Assert.assertNotNull(service);
            Config config = new Config();
            config.getGroupConfig().setName(GROUP_NAME);
            HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance(config);
            Assert.assertEquals(GROUP_NAME, osgiInstance.getConfig().getGroupConfig().getName());
            HazelcastInstance instance = osgiInstance.getDelegatedInstance();
            Assert.assertEquals(GROUP_NAME, instance.getConfig().getGroupConfig().getName());
        } finally {
            if (propValue != null) {
                System.setProperty(HAZELCAST_OSGI_GROUPING_DISABLED, propValue);
            }
            if (testBundle != null) {
                testBundle.stop();
            }
        }
    }

    @Test
    public void instanceRetrievedSuccessfullyWithItsName() {
        final String INSTANCE_NAME = "test-osgi-instance";
        HazelcastInternalOSGiService service = getService();
        Config config = new Config(INSTANCE_NAME);
        service.newHazelcastInstance(config);
        HazelcastOSGiInstance osgiInstance = service.getHazelcastInstanceByName(INSTANCE_NAME);
        Assert.assertNotNull(osgiInstance);
        Assert.assertEquals(config.getInstanceName(), osgiInstance.getConfig().getInstanceName());
        HazelcastInstance instance = osgiInstance.getDelegatedInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals(config.getInstanceName(), instance.getConfig().getInstanceName());
    }

    @Test
    public void allInstancesRetrievedSuccessfully() {
        Set<HazelcastOSGiInstance> osgiInstances = new HashSet<HazelcastOSGiInstance>();
        HazelcastInternalOSGiService service = getService();
        osgiInstances.add(service.newHazelcastInstance());
        osgiInstances.add(service.newHazelcastInstance(new Config("test-osgi-instance")));
        Set<HazelcastOSGiInstance> allOSGiInstances = service.getAllHazelcastInstances();
        Assert.assertEquals(osgiInstances.size(), allOSGiInstances.size());
        HazelcastTestSupport.assertContainsAll(allOSGiInstances, osgiInstances);
    }

    @Test
    public void instanceShutdownSuccessfully() {
        final String INSTANCE_NAME = "test-osgi-instance";
        HazelcastInternalOSGiService service = getService();
        Config config = new Config(INSTANCE_NAME);
        HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance(config);
        Assert.assertTrue(osgiInstance.getLifecycleService().isRunning());
        HazelcastInstance instance = osgiInstance.getDelegatedInstance();
        Assert.assertTrue(instance.getLifecycleService().isRunning());
        service.shutdownHazelcastInstance(osgiInstance);
        Assert.assertFalse(osgiInstance.getLifecycleService().isRunning());
        Assert.assertFalse(instance.getLifecycleService().isRunning());
        Assert.assertNull(service.getHazelcastInstanceByName(INSTANCE_NAME));
        Assert.assertFalse(service.getAllHazelcastInstances().contains(osgiInstance));
    }

    @Test
    public void instanceShutdownSuccessfullyAlthoughThereIsExceptionWhileDeregister() {
        final String INSTANCE_NAME = "test-osgi-instance";
        registerDeregisterListener.throwExceptionOnDeregister = true;
        HazelcastInternalOSGiService service = getService();
        Config config = new Config(INSTANCE_NAME);
        HazelcastOSGiInstance osgiInstance = service.newHazelcastInstance(config);
        Assert.assertTrue(osgiInstance.getLifecycleService().isRunning());
        HazelcastInstance instance = osgiInstance.getDelegatedInstance();
        Assert.assertTrue(instance.getLifecycleService().isRunning());
        service.shutdownHazelcastInstance(osgiInstance);
        Assert.assertFalse(osgiInstance.getLifecycleService().isRunning());
        Assert.assertFalse(instance.getLifecycleService().isRunning());
        Assert.assertNull(service.getHazelcastInstanceByName(INSTANCE_NAME));
        Assert.assertFalse(service.getAllHazelcastInstances().contains(osgiInstance));
    }

    @Test
    public void allInstancesShutdownSuccessfully() {
        Set<HazelcastOSGiInstance> osgiInstances = new HashSet<HazelcastOSGiInstance>();
        HazelcastInternalOSGiService service = getService();
        osgiInstances.add(service.newHazelcastInstance());
        osgiInstances.add(service.newHazelcastInstance(new Config("test-osgi-instance")));
        Set<HazelcastOSGiInstance> allOSGiInstances = service.getAllHazelcastInstances();
        for (HazelcastOSGiInstance osgiInstance : allOSGiInstances) {
            Assert.assertTrue(osgiInstance.getLifecycleService().isRunning());
            HazelcastInstance instance = osgiInstance.getDelegatedInstance();
            Assert.assertTrue(instance.getLifecycleService().isRunning());
        }
        service.shutdownAll();
        for (HazelcastOSGiInstance osgiInstance : osgiInstances) {
            Assert.assertFalse(osgiInstance.getLifecycleService().isRunning());
            HazelcastInstance instance = osgiInstance.getDelegatedInstance();
            Assert.assertFalse(instance.getLifecycleService().isRunning());
        }
        allOSGiInstances = service.getAllHazelcastInstances();
        Assert.assertEquals(0, allOSGiInstances.size());
    }

    @Test
    public void allInstancesShutdownSuccessfullyAlthoughThereIsExceptionWhileDeregister() {
        Set<HazelcastOSGiInstance> osgiInstances = new HashSet<HazelcastOSGiInstance>();
        registerDeregisterListener.throwExceptionOnDeregister = true;
        HazelcastInternalOSGiService service = getService();
        osgiInstances.add(service.newHazelcastInstance());
        osgiInstances.add(service.newHazelcastInstance(new Config("test-osgi-instance")));
        Set<HazelcastOSGiInstance> allOSGiInstances = service.getAllHazelcastInstances();
        for (HazelcastOSGiInstance osgiInstance : allOSGiInstances) {
            Assert.assertTrue(osgiInstance.getLifecycleService().isRunning());
            HazelcastInstance instance = osgiInstance.getDelegatedInstance();
            Assert.assertTrue(instance.getLifecycleService().isRunning());
        }
        service.shutdownAll();
        for (HazelcastOSGiInstance osgiInstance : osgiInstances) {
            Assert.assertFalse(osgiInstance.getLifecycleService().isRunning());
            HazelcastInstance instance = osgiInstance.getDelegatedInstance();
            Assert.assertFalse(instance.getLifecycleService().isRunning());
        }
        allOSGiInstances = service.getAllHazelcastInstances();
        Assert.assertEquals(0, allOSGiInstances.size());
    }

    @Test
    public void serviceIsNotOperationalWhenItIsNotActive() throws BundleException {
        TestBundle testBundle = null;
        try {
            testBundle = new TestBundle();
            TestBundleContext testBundleContext = testBundle.getBundleContext();
            testBundle.start();
            HazelcastInternalOSGiService service = getService(testBundleContext);
            Assert.assertNotNull(service);
            testBundle.stop();
            testBundle = null;
            try {
                service.newHazelcastInstance();
                Assert.fail(("OSGI service is not active so it is not in operation mode." + " It is expected to get `IllegalStateException` here!"));
            } catch (IllegalStateException e) {
                // since the bundle is not active, it is expected to get `IllegalStateException`
            }
        } finally {
            if (testBundle != null) {
                testBundle.stop();
            }
        }
    }
}

