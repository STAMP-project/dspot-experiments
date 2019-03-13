package org.robolectric.shadows;


import PowerManager.PARTIAL_WAKE_LOCK;
import PowerManager.WakeLock;
import android.os.Build.VERSION_CODES;
import android.os.PowerManager;
import android.os.WorkSource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowPowerManagerTest {
    private PowerManager powerManager;

    @Test
    public void acquire_shouldAcquireAndReleaseReferenceCountedLock() throws Exception {
        PowerManager.WakeLock lock = powerManager.newWakeLock(0, "TAG");
        assertThat(lock.isHeld()).isFalse();
        lock.acquire();
        assertThat(lock.isHeld()).isTrue();
        lock.acquire();
        assertThat(lock.isHeld()).isTrue();
        lock.release();
        assertThat(lock.isHeld()).isTrue();
        lock.release();
        assertThat(lock.isHeld()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void isWakeLockLevelSupported() {
        assertThat(powerManager.isWakeLockLevelSupported(PARTIAL_WAKE_LOCK)).isFalse();
        Shadows.shadowOf(powerManager).setIsWakeLockLevelSupported(PARTIAL_WAKE_LOCK, true);
        assertThat(powerManager.isWakeLockLevelSupported(PARTIAL_WAKE_LOCK)).isTrue();
        Shadows.shadowOf(powerManager).setIsWakeLockLevelSupported(PARTIAL_WAKE_LOCK, false);
        assertThat(powerManager.isWakeLockLevelSupported(PARTIAL_WAKE_LOCK)).isFalse();
    }

    @Test
    public void acquire_shouldLogLatestWakeLock() throws Exception {
        ShadowPowerManager.reset();
        assertThat(ShadowPowerManager.getLatestWakeLock()).isNull();
        PowerManager.WakeLock lock = powerManager.newWakeLock(0, "TAG");
        lock.acquire();
        assertThat(ShadowPowerManager.getLatestWakeLock()).isNotNull();
        assertThat(ShadowPowerManager.getLatestWakeLock()).isSameAs(lock);
        assertThat(lock.isHeld()).isTrue();
        lock.release();
        assertThat(ShadowPowerManager.getLatestWakeLock()).isNotNull();
        assertThat(ShadowPowerManager.getLatestWakeLock()).isSameAs(lock);
        assertThat(lock.isHeld()).isFalse();
        ShadowPowerManager.reset();
        assertThat(ShadowPowerManager.getLatestWakeLock()).isNull();
    }

    @Test
    public void newWakeLock_shouldCreateWakeLock() throws Exception {
        assertThat(powerManager.newWakeLock(0, "TAG")).isNotNull();
    }

    @Test
    public void newWakeLock_shouldSetWakeLockTag() throws Exception {
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(0, "FOO");
        assertThat(Shadows.shadowOf(wakeLock).getTag()).isEqualTo("FOO");
    }

    @Test
    public void newWakeLock_shouldAcquireAndReleaseNonReferenceCountedLock() throws Exception {
        PowerManager.WakeLock lock = powerManager.newWakeLock(0, "TAG");
        lock.setReferenceCounted(false);
        assertThat(lock.isHeld()).isFalse();
        lock.acquire();
        assertThat(lock.isHeld()).isTrue();
        lock.acquire();
        assertThat(lock.isHeld()).isTrue();
        lock.release();
        assertThat(lock.isHeld()).isFalse();
    }

    @Test
    public void newWakeLock_shouldThrowRuntimeExceptionIfLockIsUnderlocked() throws Exception {
        PowerManager.WakeLock lock = powerManager.newWakeLock(0, "TAG");
        try {
            lock.release();
            Assert.fail();
        } catch (RuntimeException e) {
            // Expected.
        }
    }

    @Test
    public void isScreenOn_shouldGetAndSet() {
        assertThat(powerManager.isScreenOn()).isTrue();
        Shadows.shadowOf(powerManager).setIsScreenOn(false);
        assertThat(powerManager.isScreenOn()).isFalse();
    }

    @Test
    public void isReferenceCounted_shouldGetAndSet() throws Exception {
        PowerManager.WakeLock lock = powerManager.newWakeLock(0, "TAG");
        assertThat(Shadows.shadowOf(lock).isReferenceCounted()).isTrue();
        lock.setReferenceCounted(false);
        assertThat(Shadows.shadowOf(lock).isReferenceCounted()).isFalse();
        lock.setReferenceCounted(true);
        assertThat(Shadows.shadowOf(lock).isReferenceCounted()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void isInteractive_shouldGetAndSet() {
        Shadows.shadowOf(powerManager).setIsInteractive(true);
        assertThat(powerManager.isInteractive()).isTrue();
        Shadows.shadowOf(powerManager).setIsInteractive(false);
        assertThat(powerManager.isInteractive()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void isPowerSaveMode_shouldGetAndSet() {
        assertThat(powerManager.isPowerSaveMode()).isFalse();
        Shadows.shadowOf(powerManager).setIsPowerSaveMode(true);
        assertThat(powerManager.isPowerSaveMode()).isTrue();
    }

    @Test
    public void workSource_shouldGetAndSet() throws Exception {
        PowerManager.WakeLock lock = powerManager.newWakeLock(0, "TAG");
        WorkSource workSource = new WorkSource();
        assertThat(Shadows.shadowOf(lock).getWorkSource()).isNull();
        lock.setWorkSource(workSource);
        assertThat(Shadows.shadowOf(lock).getWorkSource()).isEqualTo(workSource);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void isIgnoringBatteryOptimizations_shouldGetAndSet() {
        String packageName = "somepackage";
        assertThat(powerManager.isIgnoringBatteryOptimizations(packageName)).isFalse();
        Shadows.shadowOf(powerManager).setIgnoringBatteryOptimizations(packageName, true);
        assertThat(powerManager.isIgnoringBatteryOptimizations(packageName)).isTrue();
        Shadows.shadowOf(powerManager).setIgnoringBatteryOptimizations(packageName, false);
        assertThat(powerManager.isIgnoringBatteryOptimizations(packageName)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void isDeviceIdleMode_shouldGetAndSet() {
        assertThat(powerManager.isDeviceIdleMode()).isFalse();
        Shadows.shadowOf(powerManager).setIsDeviceIdleMode(true);
        assertThat(powerManager.isDeviceIdleMode()).isTrue();
        Shadows.shadowOf(powerManager).setIsDeviceIdleMode(false);
        assertThat(powerManager.isDeviceIdleMode()).isFalse();
    }

    @Test
    public void reboot_incrementsTimesRebootedAndAppendsRebootReason() {
        assertThat(Shadows.shadowOf(powerManager).getTimesRebooted()).isEqualTo(0);
        assertThat(Shadows.shadowOf(powerManager).getRebootReasons()).isEmpty();
        String rebootReason = "reason";
        powerManager.reboot(rebootReason);
        assertThat(Shadows.shadowOf(powerManager).getTimesRebooted()).isEqualTo(1);
        assertThat(Shadows.shadowOf(powerManager).getRebootReasons()).hasSize(1);
        assertThat(Shadows.shadowOf(powerManager).getRebootReasons()).contains(rebootReason);
    }

    @Test
    public void acquire_shouldIncreaseTimesHeld() throws Exception {
        PowerManager.WakeLock lock = powerManager.newWakeLock(0, "TAG");
        assertThat(Shadows.shadowOf(lock).getTimesHeld()).isEqualTo(0);
        lock.acquire();
        assertThat(Shadows.shadowOf(lock).getTimesHeld()).isEqualTo(1);
        lock.acquire();
        assertThat(Shadows.shadowOf(lock).getTimesHeld()).isEqualTo(2);
    }

    @Test
    public void release_shouldNotDecreaseTimesHeld() throws Exception {
        PowerManager.WakeLock lock = powerManager.newWakeLock(0, "TAG");
        lock.acquire();
        lock.acquire();
        assertThat(Shadows.shadowOf(lock).getTimesHeld()).isEqualTo(2);
        lock.release();
        lock.release();
        assertThat(Shadows.shadowOf(lock).getTimesHeld()).isEqualTo(2);
    }
}

