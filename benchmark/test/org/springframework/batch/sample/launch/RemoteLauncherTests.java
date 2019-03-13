/**
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.sample.launch;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.launch.JobOperator;


/**
 *
 *
 * @author Dave Syer
 */
public class RemoteLauncherTests {
    private static Log logger = LogFactory.getLog(RemoteLauncherTests.class);

    private static List<Exception> errors = new ArrayList<>();

    private static JobOperator launcher;

    private static JobLoader loader;

    private static Thread thread;

    @Test
    public void testConnect() throws Exception {
        String message = (RemoteLauncherTests.errors.isEmpty()) ? "" : RemoteLauncherTests.errors.get(0).getMessage();
        if (!(RemoteLauncherTests.errors.isEmpty())) {
            Assert.fail(message);
        }
        Assert.assertTrue(RemoteLauncherTests.isConnected());
    }

    @Test
    public void testLaunchBadJob() throws Exception {
        Assert.assertEquals(0, RemoteLauncherTests.errors.size());
        Assert.assertTrue(RemoteLauncherTests.isConnected());
        try {
            RemoteLauncherTests.launcher.start("foo", ("time=" + (new Date().getTime())));
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("NoSuchJobException"));
        }
    }

    @Test
    public void testAvailableJobs() throws Exception {
        Assert.assertEquals(0, RemoteLauncherTests.errors.size());
        Assert.assertTrue(RemoteLauncherTests.isConnected());
        Assert.assertTrue(RemoteLauncherTests.launcher.getJobNames().contains("loopJob"));
    }

    @Test
    public void testPauseJob() throws Exception {
        final int SLEEP_INTERVAL = 600;
        Assert.assertTrue(RemoteLauncherTests.isConnected());
        Assert.assertTrue(RemoteLauncherTests.launcher.getJobNames().contains("loopJob"));
        long executionId = RemoteLauncherTests.launcher.start("loopJob", "");
        // sleep long enough to avoid race conditions (serializable tx isolation
        // doesn't work with HSQL)
        Thread.sleep(SLEEP_INTERVAL);
        RemoteLauncherTests.launcher.stop(executionId);
        Thread.sleep(SLEEP_INTERVAL);
        RemoteLauncherTests.logger.debug(RemoteLauncherTests.launcher.getSummary(executionId));
        long resumedId = RemoteLauncherTests.launcher.restart(executionId);
        Assert.assertNotSame("Picked up the same execution after pause and resume", executionId, resumedId);
        Thread.sleep(SLEEP_INTERVAL);
        RemoteLauncherTests.launcher.stop(resumedId);
        Thread.sleep(SLEEP_INTERVAL);
        RemoteLauncherTests.logger.debug(RemoteLauncherTests.launcher.getSummary(resumedId));
        long resumeId2 = RemoteLauncherTests.launcher.restart(resumedId);
        Assert.assertNotSame("Picked up the same execution after pause and resume", executionId, resumeId2);
        Thread.sleep(SLEEP_INTERVAL);
        RemoteLauncherTests.launcher.stop(resumeId2);
    }
}

