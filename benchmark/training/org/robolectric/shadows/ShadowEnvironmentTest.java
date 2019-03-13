package org.robolectric.shadows;


import Environment.DIRECTORY_MOVIES;
import Environment.MEDIA_MOUNTED;
import Environment.MEDIA_REMOVED;
import ShadowEnvironment.EXTERNAL_CACHE_DIR;
import ShadowEnvironment.EXTERNAL_FILES_DIR;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowEnvironmentTest {
    @Test
    public void getExternalStorageState_shouldReturnStorageState() {
        assertThat(Environment.getExternalStorageState()).isEqualTo(MEDIA_REMOVED);
        ShadowEnvironment.setExternalStorageState(MEDIA_MOUNTED);
        assertThat(Environment.getExternalStorageState()).isEqualTo(MEDIA_MOUNTED);
    }

    @Test
    public void getExternalStorageDirectory_shouldReturnDirectory() {
        assertThat(Environment.getExternalStorageDirectory().exists()).isTrue();
    }

    @Test
    public void getExternalStoragePublicDirectory_shouldReturnDirectory() {
        final File path = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES);
        assertThat(path.exists()).isTrue();
        assertThat(path).isEqualTo(new File(EXTERNAL_FILES_DIR.toFile(), Environment.DIRECTORY_MOVIES));
    }

    @Test
    public void getExternalStoragePublicDirectory_shouldReturnSameDirectory() {
        File path1 = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES);
        File path2 = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES);
        assertThat(path1).isEqualTo(path2);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void isExternalStorageRemovable_primaryShouldReturnSavedValue() {
        assertThat(Environment.isExternalStorageRemovable()).isFalse();
        ShadowEnvironment.setExternalStorageRemovable(Environment.getExternalStorageDirectory(), true);
        assertThat(Environment.isExternalStorageRemovable()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void isExternalStorageRemovable_shouldReturnSavedValue() {
        final File file = new File("/mnt/media/file");
        assertThat(Environment.isExternalStorageRemovable(file)).isFalse();
        ShadowEnvironment.setExternalStorageRemovable(file, true);
        assertThat(Environment.isExternalStorageRemovable(file)).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void isExternalStorageEmulated_shouldReturnSavedValue() {
        final File file = new File("/mnt/media/file");
        assertThat(Environment.isExternalStorageEmulated(file)).isFalse();
        ShadowEnvironment.setExternalStorageEmulated(file, true);
        assertThat(Environment.isExternalStorageEmulated(file)).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void storageIsLazy() {
        Assert.assertNull(EXTERNAL_CACHE_DIR);
        Assert.assertNull(EXTERNAL_FILES_DIR);
        Environment.getExternalStorageDirectory();
        Environment.getExternalStoragePublicDirectory(null);
        Assert.assertNotNull(EXTERNAL_CACHE_DIR);
        Assert.assertNotNull(EXTERNAL_FILES_DIR);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void reset_shouldClearRemovableFiles() {
        final File file = new File("foo");
        ShadowEnvironment.setExternalStorageRemovable(file, true);
        assertThat(Environment.isExternalStorageRemovable(file)).isTrue();
        ShadowEnvironment.reset();
        assertThat(Environment.isExternalStorageRemovable(file)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void reset_shouldClearEmulatedFiles() {
        final File file = new File("foo");
        ShadowEnvironment.setExternalStorageEmulated(file, true);
        assertThat(Environment.isExternalStorageEmulated(file)).isTrue();
        ShadowEnvironment.reset();
        assertThat(Environment.isExternalStorageEmulated(file)).isFalse();
    }

    @Test
    public void isExternalStorageEmulatedNoArg_shouldReturnSavedValue() {
        ShadowEnvironment.setIsExternalStorageEmulated(true);
        assertThat(Environment.isExternalStorageEmulated()).isTrue();
        ShadowEnvironment.reset();
        assertThat(Environment.isExternalStorageEmulated()).isFalse();
    }

    @Test
    @Config(sdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void getExternalStorageStateJB() throws Exception {
        ShadowEnvironment.setExternalStorageState("blah");
        assertThat(ShadowEnvironment.getExternalStorageState()).isEqualTo("blah");
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT, maxSdk = VERSION_CODES.LOLLIPOP)
    public void getExternalStorageStatePreLollipopMR1() throws Exception {
        File storageDir1 = ShadowEnvironment.addExternalDir("dir1");
        File storageDir2 = ShadowEnvironment.addExternalDir("dir2");
        ShadowEnvironment.setExternalStorageState(storageDir1, MEDIA_MOUNTED);
        ShadowEnvironment.setExternalStorageState(storageDir2, MEDIA_REMOVED);
        ShadowEnvironment.setExternalStorageState("blah");
        assertThat(ShadowEnvironment.getStorageState(storageDir1)).isEqualTo(MEDIA_MOUNTED);
        assertThat(ShadowEnvironment.getStorageState(storageDir2)).isEqualTo(MEDIA_REMOVED);
        assertThat(ShadowEnvironment.getStorageState(new File(storageDir1, "subpath"))).isEqualTo(MEDIA_MOUNTED);
        assertThat(ShadowEnvironment.getExternalStorageState()).isEqualTo("blah");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP_MR1)
    public void getExternalStorageState() throws Exception {
        File storageDir1 = ShadowEnvironment.addExternalDir("dir1");
        File storageDir2 = ShadowEnvironment.addExternalDir("dir2");
        ShadowEnvironment.setExternalStorageState(storageDir1, MEDIA_MOUNTED);
        ShadowEnvironment.setExternalStorageState(storageDir2, MEDIA_REMOVED);
        ShadowEnvironment.setExternalStorageState("blah");
        assertThat(ShadowEnvironment.getExternalStorageState(storageDir1)).isEqualTo(MEDIA_MOUNTED);
        assertThat(ShadowEnvironment.getStorageState(storageDir1)).isEqualTo(MEDIA_MOUNTED);
        assertThat(ShadowEnvironment.getExternalStorageState(storageDir2)).isEqualTo(MEDIA_REMOVED);
        assertThat(ShadowEnvironment.getStorageState(storageDir2)).isEqualTo(MEDIA_REMOVED);
        assertThat(ShadowEnvironment.getExternalStorageState(new File(storageDir1, "subpath"))).isEqualTo(MEDIA_MOUNTED);
        assertThat(ShadowEnvironment.getStorageState(new File(storageDir1, "subpath"))).isEqualTo(MEDIA_MOUNTED);
        assertThat(ShadowEnvironment.getExternalStorageState()).isEqualTo("blah");
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void isExternalStorageEmulated() {
        ShadowEnvironment.setIsExternalStorageEmulated(true);
        assertThat(Environment.isExternalStorageEmulated()).isTrue();
        ShadowEnvironment.setIsExternalStorageEmulated(false);
        assertThat(Environment.isExternalStorageEmulated()).isFalse();
    }
}

