package com.amaze.filemanager.filesystem;


import android.os.Environment;
import com.amaze.filemanager.BuildConfig;
import com.amaze.filemanager.activities.MainActivity;
import com.amaze.filemanager.test.ShadowShellInteractive;
import eu.chainfire.libsuperuser.Shell;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.multidex.ShadowMultiDex;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = { ShadowMultiDex.class, ShadowShellInteractive.class })
public class RootHelperTest {
    private static final File sysroot = new File(Environment.getExternalStorageDirectory(), "sysroot");

    private static final List<String> expected = Arrays.asList("srv", "var", "tmp", "bin", "lib", "usr", "1.txt", "2.txt", "3.txt", "4.txt", "symlink1.txt", "symlink2.txt", "symlink3.txt", "symlink4.txt");

    @Test
    public void testNonRoot() throws InterruptedException {
        runVerify(false);
    }

    @Test
    public void testRoot() throws IllegalArgumentException, InterruptedException, SecurityException {
        MainActivity.shellInteractive = new Shell.Builder().setShell("/bin/false").open();
        runVerify(true);
    }
}

