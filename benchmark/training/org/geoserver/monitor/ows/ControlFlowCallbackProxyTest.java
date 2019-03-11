/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor.ows;


import Status.RUNNING;
import Status.WAITING;
import java.lang.reflect.Proxy;
import org.geoserver.monitor.Monitor;
import org.geoserver.monitor.RequestData;
import org.geoserver.ows.DispatcherCallback;
import org.geoserver.ows.Request;
import org.geoserver.ows.Response;
import org.geoserver.platform.Operation;
import org.geoserver.platform.Service;
import org.geoserver.platform.ServiceException;
import org.junit.Assert;
import org.junit.Test;


public class ControlFlowCallbackProxyTest {
    static Monitor monitor;

    @Test
    public void test() throws Exception {
        final RequestData data = ControlFlowCallbackProxyTest.monitor.start();
        DispatcherCallback callback = new DispatcherCallback() {
            public Service serviceDispatched(Request request, Service service) throws ServiceException {
                return null;
            }

            public Response responseDispatched(Request request, Operation operation, Object result, Response response) {
                return null;
            }

            public Object operationExecuted(Request request, Operation operation, Object result) {
                return null;
            }

            public Operation operationDispatched(Request request, Operation operation) {
                Assert.assertEquals(WAITING, data.getStatus());
                return operation;
            }

            public Request init(Request request) {
                return null;
            }

            public void finished(Request request) {
            }
        };
        callback = createProxy(callback);
        callback.operationDispatched(new Request(), new Operation("foo", new Service("bar", null, null, null), null, null));
        Assert.assertEquals(RUNNING, data.getStatus());
    }

    @Test
    public void testGetRunningAndBlockedRequests() throws Exception {
        DispatcherCallback callback = new ControlFlowCallbackProxyTest.MyDispatcherCallback();
        callback = createProxy(callback);
        ControlFlowCallbackProxy proxy = ((ControlFlowCallbackProxy) (Proxy.getInvocationHandler(callback)));
        Assert.assertNotNull(proxy);
        Assert.assertEquals(10L, proxy.getRunningRequests());
        Assert.assertEquals(2L, proxy.getBlockedRequests());
    }

    public static class MyDispatcherCallback implements DispatcherCallback {
        public long getRunningRequests() {
            return 10;
        }

        public long getBlockedRequests() {
            return 2;
        }

        public Service serviceDispatched(Request request, Service service) throws ServiceException {
            return null;
        }

        public Response responseDispatched(Request request, Operation operation, Object result, Response response) {
            return null;
        }

        public Object operationExecuted(Request request, Operation operation, Object result) {
            return null;
        }

        public Operation operationDispatched(Request request, Operation operation) {
            return null;
        }

        public Request init(Request request) {
            return null;
        }

        public void finished(Request request) {
        }
    }
}

