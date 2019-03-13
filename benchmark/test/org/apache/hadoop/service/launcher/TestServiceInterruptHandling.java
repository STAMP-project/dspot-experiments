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
package org.apache.hadoop.service.launcher;


import ExitUtil.ExitException;
import ExitUtil.HaltException;
import IrqHandler.InterruptData;
import org.apache.hadoop.service.BreakableService;
import org.apache.hadoop.service.launcher.testservices.FailureTestService;
import org.apache.hadoop.util.ExitUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static IrqHandler.CONTROL_C;


/**
 * Test service launcher interrupt handling.
 */
public class TestServiceInterruptHandling extends AbstractServiceLauncherTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestServiceInterruptHandling.class);

    @Test
    public void testRegisterAndRaise() throws Throwable {
        TestServiceInterruptHandling.InterruptCatcher catcher = new TestServiceInterruptHandling.InterruptCatcher();
        String name = CONTROL_C;
        IrqHandler irqHandler = new IrqHandler(name, catcher);
        irqHandler.bind();
        Assert.assertEquals(0, irqHandler.getSignalCount());
        irqHandler.raise();
        // allow for an async event
        Thread.sleep(500);
        IrqHandler.InterruptData data = catcher.interruptData;
        Assert.assertNotNull("interrupt data", data);
        Assert.assertEquals(name, data.getName());
        Assert.assertEquals(1, irqHandler.getSignalCount());
    }

    @Test
    public void testInterruptEscalationShutdown() throws Throwable {
        ExitTrackingServiceLauncher<BreakableService> launcher = new ExitTrackingServiceLauncher(BreakableService.class.getName());
        BreakableService service = new BreakableService();
        launcher.setService(service);
        InterruptEscalator escalator = new InterruptEscalator(launcher, 500);
        // call the interrupt operation directly
        try {
            escalator.interrupted(new IrqHandler.InterruptData("INT", 3));
            Assert.fail(("Expected an exception to be raised in " + escalator));
        } catch (ExitUtil e) {
            assertExceptionDetails(EXIT_INTERRUPTED, "", e);
        }
        // the service is now stopped
        assertStopped(service);
        Assert.assertTrue(("isSignalAlreadyReceived() == false in " + escalator), escalator.isSignalAlreadyReceived());
        Assert.assertFalse(("isForcedShutdownTimedOut() == true in " + escalator), escalator.isForcedShutdownTimedOut());
        // now interrupt it a second time and expect it to escalate to a halt
        try {
            escalator.interrupted(new IrqHandler.InterruptData("INT", 3));
            Assert.fail(("Expected an exception to be raised in " + escalator));
        } catch (ExitUtil e) {
            assertExceptionDetails(EXIT_INTERRUPTED, "", e);
        }
    }

    @Test
    public void testBlockingShutdownTimeouts() throws Throwable {
        ExitTrackingServiceLauncher<FailureTestService> launcher = new ExitTrackingServiceLauncher(FailureTestService.class.getName());
        FailureTestService service = new FailureTestService(false, false, false, 2000);
        launcher.setService(service);
        InterruptEscalator escalator = new InterruptEscalator(launcher, 500);
        // call the interrupt operation directly
        try {
            escalator.interrupted(new IrqHandler.InterruptData("INT", 3));
            Assert.fail(("Expected an exception to be raised from " + escalator));
        } catch (ExitUtil e) {
            assertExceptionDetails(EXIT_INTERRUPTED, "", e);
        }
        Assert.assertTrue(("isForcedShutdownTimedOut() == false in " + escalator), escalator.isForcedShutdownTimedOut());
    }

    private static class InterruptCatcher implements IrqHandler.Interrupted {
        public InterruptData interruptData;

        @Override
        public void interrupted(IrqHandler.InterruptData data) {
            TestServiceInterruptHandling.LOG.info("Interrupt caught");
            this.interruptData = data;
        }
    }
}

