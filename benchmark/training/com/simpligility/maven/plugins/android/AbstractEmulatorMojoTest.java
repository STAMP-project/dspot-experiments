package com.simpligility.maven.plugins.android;


import CommandExecutor.Factory;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ AndroidDebugBridge.class, Factory.class })
@Ignore("Does not work anymore with new sdk")
public class AbstractEmulatorMojoTest {
    private static final String AVD_NAME = "emulator";

    private static final long DEFAULT_TIMEOUT = 500;

    private AbstractEmulatorMojoTest.AbstractEmulatorMojoToTest abstractEmulatorMojo;

    private CommandExecutor mockExecutor;

    private AndroidDebugBridge mockAndroidDebugBridge;

    @Test
    public void testStartAndroidEmulatorWithTimeoutToConnect() throws ExecutionException, MojoExecutionException {
        boolean onlineAtSecondTry = false;
        int extraBootStatusPollCycles = -1;// ignored

        abstractEmulatorMojo.setWait(AbstractEmulatorMojoTest.DEFAULT_TIMEOUT);
        IDevice emulatorDevice = withEmulatorDevice(onlineAtSecondTry, extraBootStatusPollCycles);
        withConnectedDebugBridge(emulatorDevice);
        try {
            startAndroidEmulator();
            Assert.fail();
        } catch (MojoExecutionException e) {
            verify(mockExecutor);
        }
    }

    @Test
    public void testStartAndroidEmulatorAlreadyBooted() throws ExecutionException, MojoExecutionException {
        boolean onlineAtSecondTry = true;
        int extraBootStatusPollCycles = 0;
        abstractEmulatorMojo.setWait(AbstractEmulatorMojoTest.DEFAULT_TIMEOUT);
        IDevice emulatorDevice = withEmulatorDevice(onlineAtSecondTry, extraBootStatusPollCycles);
        withConnectedDebugBridge(emulatorDevice);
        startAndroidEmulator();
        verify(mockExecutor);
    }

    @Test
    public void testStartAndroidEmulatorWithOngoingBoot() throws ExecutionException, MojoExecutionException {
        boolean onlineAtSecondTry = true;
        int extraBootStatusPollCycles = 1;
        abstractEmulatorMojo.setWait(((extraBootStatusPollCycles * 5000) + 500));
        IDevice emulatorDevice = withEmulatorDevice(onlineAtSecondTry, extraBootStatusPollCycles);
        withConnectedDebugBridge(emulatorDevice);
        startAndroidEmulator();
        verify(mockExecutor);
    }

    @Test
    public void testStartAndroidEmulatorWithBootTimeout() throws ExecutionException, MojoExecutionException {
        boolean onlineAtSecondTry = true;
        int extraBootStatusPollCycles = -1;
        abstractEmulatorMojo.setWait(AbstractEmulatorMojoTest.DEFAULT_TIMEOUT);
        IDevice emulatorDevice = withEmulatorDevice(onlineAtSecondTry, extraBootStatusPollCycles);
        withConnectedDebugBridge(emulatorDevice);
        try {
            startAndroidEmulator();
            Assert.fail();
        } catch (MojoExecutionException e) {
            verify(mockExecutor);
        }
    }

    private class AbstractEmulatorMojoToTest extends AbstractEmulatorMojo {
        private long wait = AbstractEmulatorMojoTest.DEFAULT_TIMEOUT;

        public long getWait() {
            return wait;
        }

        public void setWait(long wait) {
            this.wait = wait;
        }

        @Override
        protected AndroidSdk getAndroidSdk() {
            return new SdkTestSupport().getSdk_with_platform_default();
        }

        @Override
        public void execute() throws MojoExecutionException, MojoFailureException {
        }

        @Override
        protected AndroidDebugBridge initAndroidDebugBridge() throws MojoExecutionException {
            return mockAndroidDebugBridge;
        }

        @Override
        String determineAvd() {
            return AbstractEmulatorMojoTest.AVD_NAME;
        }

        @Override
        String determineWait() {
            return String.valueOf(wait);
        }
    }
}

