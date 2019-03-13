/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master;


import PropertyKey.MASTER_WORKER_CONNECT_WAIT_TIME;
import alluxio.ConfigurationRule;
import alluxio.clock.ManualClock;
import alluxio.conf.ServerConfiguration;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link SafeModeManager}.
 */
public class SafeModeManagerTest {
    private static final String SAFEMODE_WAIT_TEST = "100ms";

    private SafeModeManager mSafeModeManager;

    private ManualClock mClock;

    @Rule
    public ConfigurationRule mConfiguration = new ConfigurationRule(ImmutableMap.of(MASTER_WORKER_CONNECT_WAIT_TIME, SafeModeManagerTest.SAFEMODE_WAIT_TEST), ServerConfiguration.global());

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Test
    public void defaultSafeMode() throws Exception {
        Assert.assertTrue(mSafeModeManager.isInSafeMode());
    }

    @Test
    public void enterSafeModeOnPrimaryMasterStart() throws Exception {
        mSafeModeManager.notifyPrimaryMasterStarted();
        Assert.assertTrue(mSafeModeManager.isInSafeMode());
    }

    @Test
    public void enterSafeModeOnRpcServerStart() throws Exception {
        mSafeModeManager.notifyRpcServerStarted();
        Assert.assertTrue(mSafeModeManager.isInSafeMode());
    }

    @Test
    public void leaveSafeModeAfterRpcServerStart() throws Exception {
        mSafeModeManager.notifyRpcServerStarted();
        mClock.addTimeMs(((ServerConfiguration.getMs(MASTER_WORKER_CONNECT_WAIT_TIME)) + 10));
        Assert.assertFalse(mSafeModeManager.isInSafeMode());
    }

    @Test
    public void stayInSafeModeAfterPrimaryMasterStart() throws Exception {
        mSafeModeManager.notifyPrimaryMasterStarted();
        mClock.addTimeMs(((ServerConfiguration.getMs(MASTER_WORKER_CONNECT_WAIT_TIME)) + 10));
        Assert.assertTrue(mSafeModeManager.isInSafeMode());
    }

    @Test
    public void reenterSafeModeOnPrimaryMasterStart() throws Exception {
        mSafeModeManager.notifyRpcServerStarted();
        mClock.addTimeMs(((ServerConfiguration.getMs(MASTER_WORKER_CONNECT_WAIT_TIME)) + 10));
        mSafeModeManager.notifyPrimaryMasterStarted();
        Assert.assertTrue(mSafeModeManager.isInSafeMode());
    }

    @Test
    public void reenterSafeModeOnRpcServerStart() throws Exception {
        mSafeModeManager.notifyRpcServerStarted();
        mClock.addTimeMs(((ServerConfiguration.getMs(MASTER_WORKER_CONNECT_WAIT_TIME)) + 10));
        mSafeModeManager.notifyRpcServerStarted();
        Assert.assertTrue(mSafeModeManager.isInSafeMode());
    }

    @Test
    public void reenterSafeModeOnRpcServerStartWhileInSafeMode() throws Exception {
        mSafeModeManager.notifyRpcServerStarted();
        // Enters safe mode again while in safe mode.
        mClock.addTimeMs(((ServerConfiguration.getMs(MASTER_WORKER_CONNECT_WAIT_TIME)) - 10));
        mSafeModeManager.notifyRpcServerStarted();
        mClock.addTimeMs(((ServerConfiguration.getMs(MASTER_WORKER_CONNECT_WAIT_TIME)) - 10));
        // Verifies safe mode timer is reset.
        Assert.assertTrue(mSafeModeManager.isInSafeMode());
        mClock.addTimeMs(20);
        Assert.assertFalse(mSafeModeManager.isInSafeMode());
    }

    @Test
    public void reenterSafeModeOnPrimaryMasterStartWhileInSafeMode() throws Exception {
        mSafeModeManager.notifyRpcServerStarted();
        // Enters safe mode again while in safe mode.
        mClock.addTimeMs(((ServerConfiguration.getMs(MASTER_WORKER_CONNECT_WAIT_TIME)) - 10));
        mSafeModeManager.notifyPrimaryMasterStarted();
        mClock.addTimeMs(((ServerConfiguration.getMs(MASTER_WORKER_CONNECT_WAIT_TIME)) - 10));
        // Verifies safe mode timer is cleared.
        Assert.assertTrue(mSafeModeManager.isInSafeMode());
        mClock.addTimeMs(20);
        Assert.assertTrue(mSafeModeManager.isInSafeMode());
    }
}

