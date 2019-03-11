/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.plugin;


import Lifecycle.PAUSED;
import Lifecycle.RUNNING;
import Lifecycle.STARTING;
import Lifecycle.UNINITIALIZED;
import ServerStatus.Capability.LOCALMODE;
import ServerStatus.Capability.MASTER;
import ServerStatus.Capability.SERVER;
import com.google.common.eventbus.EventBus;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.graylog2.shared.SuppressForbidden;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ServerStatusTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private BaseConfiguration config;

    @Mock
    private EventBus eventBus;

    private ServerStatus status;

    private File tempFile;

    @Test
    public void testGetNodeId() throws Exception {
        Assert.assertEquals(new String(Files.readAllBytes(tempFile.toPath()), StandardCharsets.UTF_8), status.getNodeId().toString());
    }

    @Test
    public void testGetLifecycle() throws Exception {
        Assert.assertEquals(UNINITIALIZED, status.getLifecycle());
    }

    @Test
    public void testSetLifecycleRunning() throws Exception {
        status.start();
        Assert.assertTrue(status.isProcessing());
        Mockito.verify(eventBus).post(RUNNING);
    }

    @Test
    public void testSetLifecycleUninitialized() throws Exception {
        Assert.assertFalse(status.isProcessing());
        Mockito.verify(eventBus, Mockito.never()).post(UNINITIALIZED);
    }

    @Test
    public void testSetLifecycleStarting() throws Exception {
        status.initialize();
        Assert.assertFalse(status.isProcessing());
        Mockito.verify(eventBus).post(STARTING);
    }

    @Test
    public void testSetLifecyclePaused() throws Exception {
        status.pauseMessageProcessing(false);
        Assert.assertFalse(status.isProcessing());
        Mockito.verify(eventBus).post(PAUSED);
    }

    @Test
    public void testAwaitRunning() throws Exception {
        final Runnable runnable = Mockito.mock(Runnable.class);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch stopLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.countDown();
                    status.awaitRunning(runnable);
                } finally {
                    stopLatch.countDown();
                }
            }
        }).start();
        startLatch.await(5, TimeUnit.SECONDS);
        Mockito.verify(runnable, Mockito.never()).run();
        status.start();
        stopLatch.await(5, TimeUnit.SECONDS);
        Mockito.verify(runnable).run();
    }

    @Test
    public void testAwaitRunningWithException() throws Exception {
        final Runnable runnable = Mockito.mock(Runnable.class);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch stopLatch = new CountDownLatch(1);
        final AtomicBoolean exceptionCaught = new AtomicBoolean(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.countDown();
                    status.awaitRunning(runnable);
                    exceptionCaught.set(true);
                } finally {
                    stopLatch.countDown();
                }
            }
        }).start();
        startLatch.await(5, TimeUnit.SECONDS);
        status.start();
        stopLatch.await(5, TimeUnit.SECONDS);
        Assert.assertTrue(exceptionCaught.get());
    }

    @Test
    public void testGetStartedAt() throws Exception {
        Assert.assertNotNull(status.getStartedAt());
    }

    @Test
    @SuppressForbidden("Deliberate invocation")
    public void testGetTimezone() throws Exception {
        Assert.assertEquals(DateTimeZone.getDefault(), status.getTimezone());
    }

    @Test
    public void testAddCapability() throws Exception {
        Assert.assertEquals(status, status.addCapability(SERVER));
        Assert.assertTrue(status.hasCapabilities(MASTER, SERVER));
    }

    @Test
    public void testAddCapabilities() throws Exception {
        Assert.assertEquals(status, status.addCapabilities(LOCALMODE));
        Assert.assertTrue(status.hasCapabilities(MASTER, LOCALMODE));
    }

    @Test
    public void testPauseMessageProcessing() throws Exception {
        status.pauseMessageProcessing();
        Assert.assertEquals(PAUSED, status.getLifecycle());
        Assert.assertTrue(status.processingPauseLocked());
    }

    @Test
    public void testPauseMessageProcessingWithLock() throws Exception {
        status.pauseMessageProcessing(true);
        Assert.assertEquals(PAUSED, status.getLifecycle());
        Assert.assertTrue(status.processingPauseLocked());
    }

    @Test
    public void testPauseMessageProcessingWithoutLock() throws Exception {
        status.pauseMessageProcessing(false);
        Assert.assertEquals(PAUSED, status.getLifecycle());
        Assert.assertFalse(status.processingPauseLocked());
    }

    @Test
    public void testPauseMessageProcessingNotOverridingLock() throws Exception {
        status.pauseMessageProcessing(true);
        status.pauseMessageProcessing(false);
        Assert.assertTrue(status.processingPauseLocked());
    }

    @Test
    public void testResumeMessageProcessingWithoutLock() throws Exception {
        status.pauseMessageProcessing(false);
        status.resumeMessageProcessing();
        Assert.assertEquals(RUNNING, status.getLifecycle());
    }

    @Test(expected = ProcessingPauseLockedException.class)
    public void testResumeMessageProcessingWithLock() throws Exception {
        status.pauseMessageProcessing(true);
        status.resumeMessageProcessing();
    }

    @Test
    public void testUnlockProcessingPause() throws Exception {
        status.pauseMessageProcessing(true);
        Assert.assertTrue(status.processingPauseLocked());
        status.unlockProcessingPause();
        Assert.assertFalse(status.processingPauseLocked());
    }

    @Test
    public void testSetLocalMode() throws Exception {
        status.setLocalMode(false);
        Assert.assertFalse(status.hasCapability(LOCALMODE));
        status.setLocalMode(true);
        Assert.assertTrue(status.hasCapability(LOCALMODE));
    }
}

