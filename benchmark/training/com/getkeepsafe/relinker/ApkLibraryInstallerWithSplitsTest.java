package com.getkeepsafe.relinker;


import Build.VERSION_CODES;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.File;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = VERSION_CODES.LOLLIPOP)
public class ApkLibraryInstallerWithSplitsTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock
    Context context;

    @Mock
    ApplicationInfo applicationInfo;

    @Mock
    ReLinkerInstance instance;

    private final String[] abis = new String[]{ "x86" };

    private ApkLibraryInstaller subject;

    @Test
    public void nullSplitSourceDirInstallCorrectly() throws IOException {
        final File destination = tempFolder.newFile("null-test");
        applicationInfo.sourceDir = getClass().getResource("/fake.apk").getFile();
        applicationInfo.splitSourceDirs = null;
        Mockito.when(context.getApplicationInfo()).thenReturn(applicationInfo);
        subject.installLibrary(context, abis, "libtest.so", destination, instance);
        Mockito.verify(context).getApplicationInfo();
        MatcherAssert.assertThat(fileToString(destination), Is.is("works!"));
    }

    @Test
    public void emptySplitSourceDirInstallCorrectly() throws IOException {
        final File destination = tempFolder.newFile("empty-test");
        applicationInfo.sourceDir = getClass().getResource("/fake.apk").getFile();
        applicationInfo.splitSourceDirs = new String[]{  };
        Mockito.when(context.getApplicationInfo()).thenReturn(applicationInfo);
        subject.installLibrary(context, abis, "libtest.so", destination, instance);
        Mockito.verify(context).getApplicationInfo();
        MatcherAssert.assertThat(fileToString(destination), Is.is("works!"));
    }

    @Test
    public void apkSplitsInstallCorrectly() throws IOException {
        final File destination = tempFolder.newFile("split-test");
        applicationInfo.sourceDir = "/fake/path/nolib.apk";
        String actualApk = getClass().getResource("/fake.apk").getFile();
        applicationInfo.splitSourceDirs = new String[]{ "/another/fake/path/nolib.apk", actualApk };
        Mockito.when(context.getApplicationInfo()).thenReturn(applicationInfo);
        subject.installLibrary(context, abis, "libtest.so", destination, instance);
        Mockito.verify(context).getApplicationInfo();
        MatcherAssert.assertThat(fileToString(destination), Is.is("works!"));
    }
}

