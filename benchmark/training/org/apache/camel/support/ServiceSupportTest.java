/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.support;


import org.apache.camel.TestSupport;
import org.apache.camel.support.service.ServiceSupport;
import org.junit.Assert;
import org.junit.Test;


public class ServiceSupportTest extends TestSupport {
    private static class MyService extends ServiceSupport {
        protected void doStart() throws Exception {
        }

        protected void doStop() throws Exception {
        }
    }

    @Test
    public void testServiceSupport() throws Exception {
        ServiceSupportTest.MyService service = new ServiceSupportTest.MyService();
        start();
        Assert.assertEquals(true, isStarted());
        Assert.assertEquals(false, isStarting());
        Assert.assertEquals(false, isStopped());
        Assert.assertEquals(false, isStopping());
        stop();
        Assert.assertEquals(true, isStopped());
        Assert.assertEquals(false, isStopping());
        Assert.assertEquals(false, isStarted());
        Assert.assertEquals(false, isStarting());
    }

    @Test
    public void testServiceSupportIsRunAllowed() throws Exception {
        ServiceSupportTest.MyService service = new ServiceSupportTest.MyService();
        Assert.assertEquals(false, isRunAllowed());
        start();
        Assert.assertEquals(true, isRunAllowed());
        // we are allowed to run while suspending/suspended
        suspend();
        Assert.assertEquals(true, isRunAllowed());
        resume();
        Assert.assertEquals(true, isRunAllowed());
        // but if we are stopped then we are not
        stop();
        Assert.assertEquals(false, isRunAllowed());
        shutdown();
        Assert.assertEquals(false, isRunAllowed());
    }

    private static class MyShutdownService extends ServiceSupport {
        private boolean shutdown;

        @Override
        protected void doStart() throws Exception {
        }

        @Override
        protected void doStop() throws Exception {
        }

        @Override
        protected void doShutdown() throws Exception {
            shutdown = true;
        }

        public boolean isShutdown() {
            return shutdown;
        }
    }

    @Test
    public void testServiceSupportShutdown() throws Exception {
        ServiceSupportTest.MyShutdownService service = new ServiceSupportTest.MyShutdownService();
        start();
        Assert.assertEquals(true, isStarted());
        Assert.assertEquals(false, isStarting());
        Assert.assertEquals(false, isStopped());
        Assert.assertEquals(false, isStopping());
        Assert.assertEquals(false, service.isShutdown());
        shutdown();
        Assert.assertEquals(true, isStopped());
        Assert.assertEquals(false, isStopping());
        Assert.assertEquals(false, isStarted());
        Assert.assertEquals(false, isStarting());
        Assert.assertEquals(true, service.isShutdown());
    }

    @Test
    public void testExceptionOnStart() throws Exception {
        ServiceSupportTest.ServiceSupportTestExOnStart service = new ServiceSupportTest.ServiceSupportTestExOnStart();
        // forced not being stopped at start
        Assert.assertEquals(false, isStopped());
        try {
            start();
            Assert.fail("RuntimeException expected");
        } catch (RuntimeException e) {
            Assert.assertEquals(true, isStopped());
            Assert.assertEquals(false, isStopping());
            Assert.assertEquals(false, isStarted());
            Assert.assertEquals(false, isStarting());
        }
    }

    public static class ServiceSupportTestExOnStart extends ServiceSupport {
        public ServiceSupportTestExOnStart() {
            // just for testing force it to not be stopped
            status = -1;
        }

        @Override
        protected void doStart() throws Exception {
            throw new RuntimeException("This service throws an exception when starting");
        }

        @Override
        protected void doStop() throws Exception {
        }
    }
}

