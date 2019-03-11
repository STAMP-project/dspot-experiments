/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.flow.controller;


import org.geoserver.ows.Request;
import org.junit.Assert;
import org.junit.Test;

import static org.geoserver.flow.controller.FlowControllerTestingThread.ThreadState.COMPLETE;
import static org.geoserver.flow.controller.FlowControllerTestingThread.ThreadState.PROCESSING;
import static org.geoserver.flow.controller.FlowControllerTestingThread.ThreadState.STARTED;


public class IpFlowControllerTest extends AbstractFlowControllerTest {
    @Test
    public void testConcurrentRequestsSingleIPAddress() {
        // an ip based flow controller that will allow just one request at a time
        IpFlowController controller = new IpFlowController(1);
        String ipAddress = "127.0.0.1";
        Request firstRequest = buildIpRequest(ipAddress, "");
        FlowControllerTestingThread tSample = new FlowControllerTestingThread(firstRequest, 0, 0, controller);
        tSample.start();
        waitTerminated(tSample, AbstractFlowControllerTest.MAX_WAIT);
        Assert.assertEquals(COMPLETE, tSample.state);
        String ip = firstRequest.getHttpRequest().getRemoteAddr();
        // make three testing threads that will "process" forever, and will use the ip to identify
        // themselves
        // as the same client, until we interrupt them
        FlowControllerTestingThread t1 = new FlowControllerTestingThread(buildIpRequest(ip, ""), 0, Long.MAX_VALUE, controller);
        FlowControllerTestingThread t2 = new FlowControllerTestingThread(buildIpRequest(ip, ""), 0, Long.MAX_VALUE, controller);
        try {
            // start threads making sure every one of them managed to block somewhere before
            // starting the next one
            t1.start();
            waitBlocked(t1, AbstractFlowControllerTest.MAX_WAIT);
            t2.start();
            waitBlocked(t2, AbstractFlowControllerTest.MAX_WAIT);
            Assert.assertEquals(PROCESSING, t1.state);
            Assert.assertEquals(STARTED, t2.state);
            // let t1 go and wait until its termination. This should allow t2 to go
            t1.interrupt();
            waitTerminated(t1, AbstractFlowControllerTest.MAX_WAIT);
            Assert.assertEquals(COMPLETE, t1.state);
            waitState(PROCESSING, t2, AbstractFlowControllerTest.MAX_WAIT);
            t2.interrupt();
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        } finally {
            waitAndKill(t1, AbstractFlowControllerTest.MAX_WAIT);
            waitAndKill(t2, AbstractFlowControllerTest.MAX_WAIT);
        }
    }

    @Test
    public void testUserAndIPAddressFlowControl() {
        // an ip based flow controller that will allow just one request at a time
        IpFlowController ipController = new IpFlowController(1);
        UserConcurrentFlowController userController = new UserConcurrentFlowController(1);
        String ipAddress = "127.0.0.1";
        Request firstRequest = buildIpRequest(ipAddress, "");
        FlowControllerTestingThread tSample = new FlowControllerTestingThread(firstRequest, 0, 0, userController, ipController);
        tSample.start();
        waitTerminated(tSample, AbstractFlowControllerTest.MAX_WAIT);
        Assert.assertEquals(COMPLETE, tSample.state);
        String ip = firstRequest.getHttpRequest().getRemoteAddr();
        // make three testing threads that will "process" forever, and will use the ip to identify
        // themselves
        // as the same client, until we interrupt them
        FlowControllerTestingThread t1 = new FlowControllerTestingThread(buildIpRequest(ip, ""), 0, Long.MAX_VALUE, ipController);
        FlowControllerTestingThread t2 = new FlowControllerTestingThread(buildIpRequest(ip, ""), 0, Long.MAX_VALUE, ipController);
        try {
            // start threads making sure every one of them managed to block somewhere before
            // starting the next one
            t1.start();
            waitBlocked(t1, AbstractFlowControllerTest.MAX_WAIT);
            t2.start();
            waitBlocked(t2, AbstractFlowControllerTest.MAX_WAIT);
            Assert.assertEquals(PROCESSING, t1.state);
            Assert.assertEquals(STARTED, t2.state);
            // let t1 go and wait until its termination. This should allow t2 to go
            t1.interrupt();
            waitTerminated(t1, AbstractFlowControllerTest.MAX_WAIT);
            Assert.assertEquals(COMPLETE, t1.state);
            waitState(PROCESSING, t2, AbstractFlowControllerTest.MAX_WAIT);
            t2.interrupt();
            waitTerminated(t2, AbstractFlowControllerTest.MAX_WAIT);
            Assert.assertEquals(COMPLETE, t2.state);
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        } finally {
            waitAndKill(t1, AbstractFlowControllerTest.MAX_WAIT);
            waitAndKill(t2, AbstractFlowControllerTest.MAX_WAIT);
        }
    }

    // Test 2 remote addresses that are reported as the same, but have gone through a proxy. These
    // two should not queue up
    @Test
    public void testConcurrentProxiedIPAddresses() {
        IpFlowController controller = new IpFlowController(1);
        String ipAddress = "192.168.1.1";
        Request firstRequest = buildIpRequest(ipAddress, "");
        String ip = firstRequest.getHttpRequest().getRemoteAddr();
        FlowControllerTestingThread t1 = new FlowControllerTestingThread(buildIpRequest(ip, "192.168.1.2"), 0, Long.MAX_VALUE, controller);
        FlowControllerTestingThread t2 = new FlowControllerTestingThread(buildIpRequest(ip, "192.168.1.3"), 0, Long.MAX_VALUE, controller);
        try {
            // start threads making sure every one of them managed to block somewhere before
            // starting the next one
            t1.start();
            waitBlocked(t1, AbstractFlowControllerTest.MAX_WAIT);
            t2.start();
            waitBlocked(t2, AbstractFlowControllerTest.MAX_WAIT);
            // Both threads are processing, there is no queuing in this case
            Assert.assertEquals(PROCESSING, t1.state);
            Assert.assertEquals(PROCESSING, t2.state);
            // let t1 go and wait until its termination. This should allow t2 to go
            t1.interrupt();
            waitTerminated(t1, AbstractFlowControllerTest.MAX_WAIT);
            Assert.assertEquals(COMPLETE, t1.state);
            waitState(PROCESSING, t2, AbstractFlowControllerTest.MAX_WAIT);
            t2.interrupt();
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        } finally {
            waitAndKill(t1, AbstractFlowControllerTest.MAX_WAIT);
            waitAndKill(t2, AbstractFlowControllerTest.MAX_WAIT);
        }
    }
}

