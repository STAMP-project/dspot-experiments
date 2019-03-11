/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.service;


import STATE.INITED;
import STATE.NOTINITED;
import STATE.STARTED;
import STATE.STOPPED;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.service.Service.STATE;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCompositeService {
    private static final int NUM_OF_SERVICES = 5;

    private static final int FAILED_SERVICE_SEQ_NUMBER = 2;

    private static final Logger LOG = LoggerFactory.getLogger(TestCompositeService.class);

    /**
     * flag to state policy of CompositeService, and hence
     * what to look for after trying to stop a service from another state
     * (e.g inited)
     */
    private static final boolean STOP_ONLY_STARTED_SERVICES = TestCompositeService.CompositeServiceImpl.isPolicyToStopOnlyStartedServices();

    @Test
    public void testCallSequence() {
        TestCompositeService.ServiceManager serviceManager = new TestCompositeService.ServiceManager("ServiceManager");
        // Add services
        for (int i = 0; i < (TestCompositeService.NUM_OF_SERVICES); i++) {
            TestCompositeService.CompositeServiceImpl service = new TestCompositeService.CompositeServiceImpl(i);
            serviceManager.addTestService(service);
        }
        TestCompositeService.CompositeServiceImpl[] services = getServices().toArray(new TestCompositeService.CompositeServiceImpl[0]);
        Assert.assertEquals("Number of registered services ", TestCompositeService.NUM_OF_SERVICES, services.length);
        Configuration conf = new Configuration();
        // Initialise the composite service
        serviceManager.init(conf);
        // verify they were all inited
        assertInState(INITED, services);
        // Verify the init() call sequence numbers for every service
        for (int i = 0; i < (TestCompositeService.NUM_OF_SERVICES); i++) {
            Assert.assertEquals((("For " + (services[i])) + " service, init() call sequence number should have been "), i, services[i].getCallSequenceNumber());
        }
        // Reset the call sequence numbers
        resetServices(services);
        start();
        // verify they were all started
        assertInState(STARTED, services);
        // Verify the start() call sequence numbers for every service
        for (int i = 0; i < (TestCompositeService.NUM_OF_SERVICES); i++) {
            Assert.assertEquals((("For " + (services[i])) + " service, start() call sequence number should have been "), i, services[i].getCallSequenceNumber());
        }
        resetServices(services);
        stop();
        // verify they were all stopped
        assertInState(STOPPED, services);
        // Verify the stop() call sequence numbers for every service
        for (int i = 0; i < (TestCompositeService.NUM_OF_SERVICES); i++) {
            Assert.assertEquals((("For " + (services[i])) + " service, stop() call sequence number should have been "), (((TestCompositeService.NUM_OF_SERVICES) - 1) - i), services[i].getCallSequenceNumber());
        }
        // Try to stop again. This should be a no-op.
        stop();
        // Verify that stop() call sequence numbers for every service don't change.
        for (int i = 0; i < (TestCompositeService.NUM_OF_SERVICES); i++) {
            Assert.assertEquals((("For " + (services[i])) + " service, stop() call sequence number should have been "), (((TestCompositeService.NUM_OF_SERVICES) - 1) - i), services[i].getCallSequenceNumber());
        }
    }

    @Test
    public void testServiceStartup() {
        TestCompositeService.ServiceManager serviceManager = new TestCompositeService.ServiceManager("ServiceManager");
        // Add services
        for (int i = 0; i < (TestCompositeService.NUM_OF_SERVICES); i++) {
            TestCompositeService.CompositeServiceImpl service = new TestCompositeService.CompositeServiceImpl(i);
            if (i == (TestCompositeService.FAILED_SERVICE_SEQ_NUMBER)) {
                service.setThrowExceptionOnStart(true);
            }
            serviceManager.addTestService(service);
        }
        TestCompositeService.CompositeServiceImpl[] services = getServices().toArray(new TestCompositeService.CompositeServiceImpl[0]);
        Configuration conf = new Configuration();
        // Initialise the composite service
        serviceManager.init(conf);
        // Start the composite service
        try {
            start();
            Assert.fail("Exception should have been thrown due to startup failure of last service");
        } catch (TestCompositeService.ServiceTestRuntimeException e) {
            for (int i = 0; i < ((TestCompositeService.NUM_OF_SERVICES) - 1); i++) {
                if ((i >= (TestCompositeService.FAILED_SERVICE_SEQ_NUMBER)) && (TestCompositeService.STOP_ONLY_STARTED_SERVICES)) {
                    // Failed service state should be INITED
                    Assert.assertEquals("Service state should have been ", INITED, getServiceState());
                } else {
                    Assert.assertEquals("Service state should have been ", STOPPED, getServiceState());
                }
            }
        }
    }

    @Test
    public void testServiceStop() {
        TestCompositeService.ServiceManager serviceManager = new TestCompositeService.ServiceManager("ServiceManager");
        // Add services
        for (int i = 0; i < (TestCompositeService.NUM_OF_SERVICES); i++) {
            TestCompositeService.CompositeServiceImpl service = new TestCompositeService.CompositeServiceImpl(i);
            if (i == (TestCompositeService.FAILED_SERVICE_SEQ_NUMBER)) {
                service.setThrowExceptionOnStop(true);
            }
            serviceManager.addTestService(service);
        }
        TestCompositeService.CompositeServiceImpl[] services = getServices().toArray(new TestCompositeService.CompositeServiceImpl[0]);
        Configuration conf = new Configuration();
        // Initialise the composite service
        serviceManager.init(conf);
        start();
        // Stop the composite service
        try {
            stop();
        } catch (TestCompositeService.ServiceTestRuntimeException e) {
        }
        assertInState(STOPPED, services);
    }

    /**
     * Shut down from not-inited: expect nothing to have happened
     */
    @Test
    public void testServiceStopFromNotInited() {
        TestCompositeService.ServiceManager serviceManager = new TestCompositeService.ServiceManager("ServiceManager");
        // Add services
        for (int i = 0; i < (TestCompositeService.NUM_OF_SERVICES); i++) {
            TestCompositeService.CompositeServiceImpl service = new TestCompositeService.CompositeServiceImpl(i);
            serviceManager.addTestService(service);
        }
        TestCompositeService.CompositeServiceImpl[] services = getServices().toArray(new TestCompositeService.CompositeServiceImpl[0]);
        stop();
        assertInState(NOTINITED, services);
    }

    /**
     * Shut down from inited
     */
    @Test
    public void testServiceStopFromInited() {
        TestCompositeService.ServiceManager serviceManager = new TestCompositeService.ServiceManager("ServiceManager");
        // Add services
        for (int i = 0; i < (TestCompositeService.NUM_OF_SERVICES); i++) {
            TestCompositeService.CompositeServiceImpl service = new TestCompositeService.CompositeServiceImpl(i);
            serviceManager.addTestService(service);
        }
        TestCompositeService.CompositeServiceImpl[] services = getServices().toArray(new TestCompositeService.CompositeServiceImpl[0]);
        serviceManager.init(new Configuration());
        stop();
        if (TestCompositeService.STOP_ONLY_STARTED_SERVICES) {
            // this policy => no services were stopped
            assertInState(INITED, services);
        } else {
            assertInState(STOPPED, services);
        }
    }

    /**
     * Use a null configuration & expect a failure
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testInitNullConf() throws Throwable {
        TestCompositeService.ServiceManager serviceManager = new TestCompositeService.ServiceManager("testInitNullConf");
        TestCompositeService.CompositeServiceImpl service = new TestCompositeService.CompositeServiceImpl(0);
        serviceManager.addTestService(service);
        try {
            init(null);
            TestCompositeService.LOG.warn(("Null Configurations are permitted " + serviceManager));
        } catch (ServiceStateException e) {
            // expected
        }
    }

    /**
     * Walk the service through their lifecycle without any children;
     * verify that it all works.
     */
    @Test
    public void testServiceLifecycleNoChildren() {
        TestCompositeService.ServiceManager serviceManager = new TestCompositeService.ServiceManager("ServiceManager");
        serviceManager.init(new Configuration());
        start();
        stop();
    }

    @Test
    public void testAddServiceInInit() throws Throwable {
        BreakableService child = new BreakableService();
        assertInState(NOTINITED, child);
        TestCompositeService.CompositeServiceAddingAChild composite = new TestCompositeService.CompositeServiceAddingAChild(child);
        composite.init(new Configuration());
        assertInState(INITED, child);
    }

    @Test(timeout = 10000)
    public void testAddIfService() {
        CompositeService testService = new CompositeService("TestService") {
            Service service;

            @Override
            public void serviceInit(Configuration conf) {
                Integer notAService = new Integer(0);
                Assert.assertFalse("Added an integer as a service", addIfService(notAService));
                service = new AbstractService("Service") {};
                Assert.assertTrue("Unable to add a service", addIfService(service));
            }
        };
        testService.init(new Configuration());
        Assert.assertEquals("Incorrect number of services", 1, testService.getServices().size());
    }

    @Test
    public void testRemoveService() {
        CompositeService testService = new CompositeService("TestService") {
            @Override
            public void serviceInit(Configuration conf) {
                Integer notAService = new Integer(0);
                Assert.assertFalse("Added an integer as a service", addIfService(notAService));
                Service service1 = new AbstractService("Service1") {};
                addIfService(service1);
                Service service2 = new AbstractService("Service2") {};
                addIfService(service2);
                Service service3 = new AbstractService("Service3") {};
                addIfService(service3);
                removeService(service1);
            }
        };
        testService.init(new Configuration());
        Assert.assertEquals("Incorrect number of services", 2, testService.getServices().size());
    }

    // 
    // Tests for adding child service to parent
    // 
    @Test(timeout = 10000)
    public void testAddUninitedChildBeforeInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        parent.init(new Configuration());
        assertInState(INITED, child);
        parent.start();
        assertInState(STARTED, child);
        parent.stop();
        assertInState(STOPPED, child);
    }

    @Test(timeout = 10000)
    public void testAddUninitedChildInInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        parent.init(new Configuration());
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        assertInState(NOTINITED, child);
        try {
            parent.start();
            Assert.fail(("Expected an exception, got " + parent));
        } catch (ServiceStateException e) {
            // expected
        }
        assertInState(NOTINITED, child);
        parent.stop();
        assertInState(NOTINITED, child);
        Assert.assertEquals("Incorrect number of services", 1, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddUninitedChildInStart() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        parent.init(new Configuration());
        parent.start();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        assertInState(NOTINITED, child);
        parent.stop();
        assertInState(NOTINITED, child);
    }

    @Test(timeout = 10000)
    public void testAddUninitedChildInStop() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        parent.init(new Configuration());
        parent.start();
        parent.stop();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        assertInState(NOTINITED, child);
    }

    @Test(timeout = 10000)
    public void testAddInitedChildBeforeInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        parent.init(new Configuration());
        assertInState(INITED, child);
        parent.start();
        assertInState(STARTED, child);
        parent.stop();
        assertInState(STOPPED, child);
    }

    @Test(timeout = 10000)
    public void testAddInitedChildInInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        parent.init(new Configuration());
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        parent.start();
        assertInState(STARTED, child);
        parent.stop();
        assertInState(STOPPED, child);
    }

    @Test(timeout = 10000)
    public void testAddInitedChildInStart() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        parent.init(new Configuration());
        parent.start();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        assertInState(INITED, child);
        parent.stop();
        assertInState(STOPPED, child);
    }

    @Test(timeout = 10000)
    public void testAddInitedChildInStop() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        parent.init(new Configuration());
        parent.start();
        parent.stop();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        assertInState(INITED, child);
    }

    @Test(timeout = 10000)
    public void testAddStartedChildBeforeInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        start();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        try {
            parent.init(new Configuration());
            Assert.fail(("Expected an exception, got " + parent));
        } catch (ServiceStateException e) {
            // expected
        }
        parent.stop();
        Assert.assertEquals("Incorrect number of services", 1, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStartedChildInInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        start();
        parent.init(new Configuration());
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        parent.start();
        assertInState(STARTED, child);
        parent.stop();
        assertInState(STOPPED, child);
    }

    @Test(timeout = 10000)
    public void testAddStartedChildInStart() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        start();
        parent.init(new Configuration());
        parent.start();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        assertInState(STARTED, child);
        parent.stop();
        assertInState(STOPPED, child);
    }

    @Test(timeout = 10000)
    public void testAddStartedChildInStop() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        start();
        parent.init(new Configuration());
        parent.start();
        parent.stop();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        assertInState(STARTED, child);
    }

    @Test(timeout = 10000)
    public void testAddStoppedChildBeforeInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        start();
        stop();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        try {
            parent.init(new Configuration());
            Assert.fail(("Expected an exception, got " + parent));
        } catch (ServiceStateException e) {
            // expected
        }
        parent.stop();
        Assert.assertEquals("Incorrect number of services", 1, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStoppedChildInInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        start();
        stop();
        parent.init(new Configuration());
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        try {
            parent.start();
            Assert.fail(("Expected an exception, got " + parent));
        } catch (ServiceStateException e) {
            // expected
        }
        assertInState(STOPPED, child);
        parent.stop();
        Assert.assertEquals("Incorrect number of services", 1, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStoppedChildInStart() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        start();
        stop();
        parent.init(new Configuration());
        parent.start();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
        parent.stop();
    }

    @Test(timeout = 10000)
    public void testAddStoppedChildInStop() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService child = new BreakableService();
        child.init(new Configuration());
        start();
        stop();
        parent.init(new Configuration());
        parent.start();
        parent.stop();
        TestCompositeService.AddSiblingService.addChildToService(parent, child);
    }

    // 
    // Tests for adding sibling service to parent
    // 
    @Test(timeout = 10000)
    public void testAddUninitedSiblingBeforeInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.NOTINITED));
        parent.init(new Configuration());
        assertInState(NOTINITED, sibling);
        parent.start();
        assertInState(NOTINITED, sibling);
        parent.stop();
        assertInState(NOTINITED, sibling);
        Assert.assertEquals("Incorrect number of services", 1, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddUninitedSiblingInInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.INITED));
        parent.init(new Configuration());
        try {
            parent.start();
            Assert.fail(("Expected an exception, got " + parent));
        } catch (ServiceStateException e) {
            // expected
        }
        parent.stop();
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddUninitedSiblingInStart() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.STARTED));
        parent.init(new Configuration());
        assertInState(NOTINITED, sibling);
        parent.start();
        assertInState(NOTINITED, sibling);
        parent.stop();
        assertInState(NOTINITED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddUninitedSiblingInStop() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.STOPPED));
        parent.init(new Configuration());
        assertInState(NOTINITED, sibling);
        parent.start();
        assertInState(NOTINITED, sibling);
        parent.stop();
        assertInState(NOTINITED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddInitedSiblingBeforeInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.NOTINITED));
        parent.init(new Configuration());
        assertInState(INITED, sibling);
        parent.start();
        assertInState(INITED, sibling);
        parent.stop();
        assertInState(INITED, sibling);
        Assert.assertEquals("Incorrect number of services", 1, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddInitedSiblingInInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.INITED));
        parent.init(new Configuration());
        assertInState(INITED, sibling);
        parent.start();
        assertInState(STARTED, sibling);
        parent.stop();
        assertInState(STOPPED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddInitedSiblingInStart() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.STARTED));
        parent.init(new Configuration());
        assertInState(INITED, sibling);
        parent.start();
        assertInState(INITED, sibling);
        parent.stop();
        assertInState(STOPPED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddInitedSiblingInStop() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.STOPPED));
        parent.init(new Configuration());
    }

    @Test(timeout = 10000)
    public void testAddStartedSiblingBeforeInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        start();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.NOTINITED));
        parent.init(new Configuration());
        assertInState(STARTED, sibling);
        parent.start();
        assertInState(STARTED, sibling);
        parent.stop();
        assertInState(STARTED, sibling);
        Assert.assertEquals("Incorrect number of services", 1, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStartedSiblingInInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        start();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.INITED));
        parent.init(new Configuration());
        assertInState(STARTED, sibling);
        parent.start();
        assertInState(STARTED, sibling);
        parent.stop();
        assertInState(STOPPED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStartedSiblingInStart() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        start();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.STARTED));
        parent.init(new Configuration());
        assertInState(STARTED, sibling);
        parent.start();
        assertInState(STARTED, sibling);
        parent.stop();
        assertInState(STOPPED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStartedSiblingInStop() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        start();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.STOPPED));
        parent.init(new Configuration());
        assertInState(STARTED, sibling);
        parent.start();
        assertInState(STARTED, sibling);
        parent.stop();
        assertInState(STARTED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStoppedSiblingBeforeInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        start();
        stop();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.NOTINITED));
        parent.init(new Configuration());
        assertInState(STOPPED, sibling);
        parent.start();
        assertInState(STOPPED, sibling);
        parent.stop();
        assertInState(STOPPED, sibling);
        Assert.assertEquals("Incorrect number of services", 1, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStoppedSiblingInInit() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        start();
        stop();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.INITED));
        parent.init(new Configuration());
        assertInState(STOPPED, sibling);
        try {
            parent.start();
            Assert.fail(("Expected an exception, got " + parent));
        } catch (ServiceStateException e) {
            // expected
        }
        parent.stop();
        assertInState(STOPPED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStoppedSiblingInStart() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        start();
        stop();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.STARTED));
        parent.init(new Configuration());
        assertInState(STOPPED, sibling);
        parent.start();
        assertInState(STOPPED, sibling);
        parent.stop();
        assertInState(STOPPED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    @Test(timeout = 10000)
    public void testAddStoppedSiblingInStop() throws Throwable {
        CompositeService parent = new CompositeService("parent");
        BreakableService sibling = new BreakableService();
        sibling.init(new Configuration());
        start();
        stop();
        parent.addService(new TestCompositeService.AddSiblingService(parent, sibling, STATE.STOPPED));
        parent.init(new Configuration());
        assertInState(STOPPED, sibling);
        parent.start();
        assertInState(STOPPED, sibling);
        parent.stop();
        assertInState(STOPPED, sibling);
        Assert.assertEquals("Incorrect number of services", 2, parent.getServices().size());
    }

    public static class CompositeServiceAddingAChild extends CompositeService {
        Service child;

        public CompositeServiceAddingAChild(Service child) {
            super("CompositeServiceAddingAChild");
            this.child = child;
        }

        @Override
        protected void serviceInit(Configuration conf) throws Exception {
            addService(child);
            super.serviceInit(conf);
        }
    }

    public static class ServiceTestRuntimeException extends RuntimeException {
        public ServiceTestRuntimeException(String message) {
            super(message);
        }
    }

    /**
     * This is a composite service that keeps a count of the number of lifecycle
     * events called, and can be set to throw a {@link ServiceTestRuntimeException}
     * during service start or stop
     */
    public static class CompositeServiceImpl extends CompositeService {
        public static boolean isPolicyToStopOnlyStartedServices() {
            return TestCompositeService.STOP_ONLY_STARTED_SERVICES;
        }

        private static int counter = -1;

        private int callSequenceNumber = -1;

        private boolean throwExceptionOnStart;

        private boolean throwExceptionOnStop;

        public CompositeServiceImpl(int sequenceNumber) {
            super(Integer.toString(sequenceNumber));
        }

        @Override
        protected void serviceInit(Configuration conf) throws Exception {
            (TestCompositeService.CompositeServiceImpl.counter)++;
            callSequenceNumber = TestCompositeService.CompositeServiceImpl.counter;
            super.serviceInit(conf);
        }

        @Override
        protected void serviceStart() throws Exception {
            if (throwExceptionOnStart) {
                throw new TestCompositeService.ServiceTestRuntimeException("Fake service start exception");
            }
            (TestCompositeService.CompositeServiceImpl.counter)++;
            callSequenceNumber = TestCompositeService.CompositeServiceImpl.counter;
            super.serviceStart();
        }

        @Override
        protected void serviceStop() throws Exception {
            (TestCompositeService.CompositeServiceImpl.counter)++;
            callSequenceNumber = TestCompositeService.CompositeServiceImpl.counter;
            if (throwExceptionOnStop) {
                throw new TestCompositeService.ServiceTestRuntimeException("Fake service stop exception");
            }
            super.serviceStop();
        }

        public static int getCounter() {
            return TestCompositeService.CompositeServiceImpl.counter;
        }

        public int getCallSequenceNumber() {
            return callSequenceNumber;
        }

        public void reset() {
            callSequenceNumber = -1;
            TestCompositeService.CompositeServiceImpl.counter = -1;
        }

        public static void resetCounter() {
            TestCompositeService.CompositeServiceImpl.counter = -1;
        }

        public void setThrowExceptionOnStart(boolean throwExceptionOnStart) {
            this.throwExceptionOnStart = throwExceptionOnStart;
        }

        public void setThrowExceptionOnStop(boolean throwExceptionOnStop) {
            this.throwExceptionOnStop = throwExceptionOnStop;
        }

        @Override
        public String toString() {
            return "Service " + (getName());
        }
    }

    /**
     * Composite service that makes the addService method public to all
     */
    public static class ServiceManager extends CompositeService {
        public void addTestService(CompositeService service) {
            addService(service);
        }

        public ServiceManager(String name) {
            super(name);
        }
    }

    public static class AddSiblingService extends CompositeService {
        private final CompositeService parent;

        private final Service serviceToAdd;

        private STATE triggerState;

        public AddSiblingService(CompositeService parent, Service serviceToAdd, STATE triggerState) {
            super("ParentStateManipulatorService");
            this.parent = parent;
            this.serviceToAdd = serviceToAdd;
            this.triggerState = triggerState;
        }

        /**
         * Add the serviceToAdd to the parent if this service
         * is in the state requested
         */
        private void maybeAddSibling() {
            if ((getServiceState()) == (triggerState)) {
                parent.addService(serviceToAdd);
            }
        }

        @Override
        protected void serviceInit(Configuration conf) throws Exception {
            maybeAddSibling();
            super.serviceInit(conf);
        }

        @Override
        protected void serviceStart() throws Exception {
            maybeAddSibling();
            super.serviceStart();
        }

        @Override
        protected void serviceStop() throws Exception {
            maybeAddSibling();
            super.serviceStop();
        }

        /**
         * Expose addService method
         *
         * @param parent
         * 		parent service
         * @param child
         * 		child to add
         */
        public static void addChildToService(CompositeService parent, Service child) {
            parent.addService(child);
        }
    }
}

