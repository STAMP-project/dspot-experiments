package org.robolectric.shadows;


import android.os.Build.VERSION_CODES;
import android.os.UserManager;
import android.os.storage.StorageManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.robolectric.Shadows.shadowOf;


/**
 * Unit tests for {@link ShadowStorageManager}.
 */
@RunWith(AndroidJUnit4.class)
public class ShadowStorageManagerTest {
    private StorageManager storageManager;

    @Test
    public void getVolumeList() {
        assertThat(shadowOf(storageManager).getVolumeList()).isNotNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void getStorageVolumes() {
        File file1 = new File("/storage/sdcard");
        shadowOf(storageManager).addStorageVolume(buildAndGetStorageVolume(file1, "sd card"));
        assertThat(shadowOf(storageManager).getStorageVolumes()).isNotNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void getStorageVolume() {
        File file1 = new File("/storage/internal");
        File file2 = new File("/storage/sdcard");
        shadowOf(storageManager).addStorageVolume(buildAndGetStorageVolume(file1, "internal"));
        assertThat(shadowOf(storageManager).getStorageVolume(file1)).isNotNull();
        assertThat(shadowOf(storageManager).getStorageVolume(file2)).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void isFileEncryptedNativeOrEmulated() {
        shadowOf(storageManager).setFileEncryptedNativeOrEmulated(true);
        assertThat(StorageManager.isFileEncryptedNativeOrEmulated()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void isUserKeyUnlocked() {
        shadowOf(RuntimeEnvironment.application.getSystemService(UserManager.class)).setUserUnlocked(true);
        assertThat(StorageManager.isUserKeyUnlocked(0)).isTrue();
    }
}

