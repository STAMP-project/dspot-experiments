/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.servlet;


import com.codahale.metrics.MetricRegistry;
import com.linkedin.kafka.cruisecontrol.async.OperationFuture;
import com.linkedin.kafka.cruisecontrol.servlet.response.PauseSamplingResult;
import com.linkedin.kafka.cruisecontrol.servlet.response.ResumeSamplingResult;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import org.apache.kafka.common.utils.Time;
import org.junit.Assert;
import org.junit.Test;


public class SessionManagerTest {
    @Test
    public void testCreateAndCloseSession() {
        SessionManagerTest.TestContext context = prepareRequests(true, 1);
        SessionManager sessionManager = new SessionManager(1, 1000, context.time(), new MetricRegistry(), null);
        sessionManager.getAndCreateSessionIfNotExist(context.request(0), () -> new OperationFuture("testCreateSession"), 0);
        Assert.assertEquals(1, sessionManager.numSessions());
        sessionManager.closeSession(context.request(0), false);
        Assert.assertEquals(0, sessionManager.numSessions());
    }

    @Test
    public void testSessionExpiration() {
        SessionManagerTest.TestContext context = prepareRequests(true, 2);
        SessionManager sessionManager = new SessionManager(2, 1000, context.time(), new MetricRegistry(), null);
        List<OperationFuture> futures = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            futures.add(sessionManager.getAndCreateSessionIfNotExist(context.request(i), () -> new OperationFuture("testSessionExpiration"), 0));
        }
        Assert.assertEquals(2, sessionManager.numSessions());
        // Sleep to 1 ms before expiration.
        context.time().sleep(999);
        sessionManager.expireOldSessions();
        Assert.assertEquals(2, sessionManager.numSessions());
        for (Future future : futures) {
            Assert.assertFalse(future.isDone());
            Assert.assertFalse(future.isCancelled());
        }
        // Sleep to the exact time to expire
        context.time().sleep(1);
        sessionManager.expireOldSessions();
        Assert.assertEquals("All the sessions should have been expired", 0, sessionManager.numSessions());
        for (Future future : futures) {
            Assert.assertTrue(future.isDone());
            Assert.assertTrue(future.isCancelled());
        }
    }

    @Test
    public void testCreateSessionReachingCapacity() {
        SessionManagerTest.TestContext context = prepareRequests(false, 2);
        SessionManager sessionManager = new SessionManager(1, 1000, context.time(), new MetricRegistry(), null);
        sessionManager.getAndCreateSessionIfNotExist(context.request(0), () -> new OperationFuture("testCreateSession"), 0);
        Assert.assertEquals(1, sessionManager.numSessions());
        Assert.assertNull(sessionManager.getFuture(context.request(1)));
        // Adding same request again should have no impact
        sessionManager.getAndCreateSessionIfNotExist(context.request(0), () -> new OperationFuture("testCreateSession"), 1);
        Assert.assertEquals(1, sessionManager.numSessions());
        try {
            sessionManager.getAndCreateSessionIfNotExist(context.request(1), () -> new OperationFuture("testCreateSession"), 0);
            Assert.fail("Should have thrown exception due to session capacity reached.");
        } catch (RuntimeException e) {
            // let it go.
        }
    }

    @Test
    public void testMultipleOperationRequest() {
        SessionManagerTest.TestContext context = prepareRequests(false, 1);
        SessionManager sessionManager = new SessionManager(1, 1000, context.time(), new MetricRegistry(), null);
        HttpServletRequest request = context.request(0);
        OperationFuture future1 = new OperationFuture("future1");
        OperationFuture future2 = new OperationFuture("future2");
        for (int i = 0; i < 2; i++) {
            // Create the first future.
            OperationFuture firstFuture = sessionManager.getAndCreateSessionIfNotExist(request, () -> future1, 0);
            Assert.assertSame(firstFuture, future1);
            future1.complete(new PauseSamplingResult());
            // create the second future.
            OperationFuture secondFuture = sessionManager.getAndCreateSessionIfNotExist(request, () -> future2, 1);
            Assert.assertSame(secondFuture, future2);
            future2.complete(new ResumeSamplingResult());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSkipStep() {
        SessionManagerTest.TestContext context = prepareRequests(false, 1);
        SessionManager sessionManager = new SessionManager(1, 1000, context.time(), new MetricRegistry(), null);
        HttpServletRequest request = context.request(0);
        sessionManager.getAndCreateSessionIfNotExist(request, () -> null, 1);
    }

    private static class TestContext {
        private List<HttpServletRequest> _requests;

        private Time _time;

        private TestContext(List<HttpServletRequest> requests, Time time) {
            _requests = requests;
            _time = time;
        }

        private HttpServletRequest request(int index) {
            return _requests.get(index);
        }

        private Time time() {
            return _time;
        }
    }
}

