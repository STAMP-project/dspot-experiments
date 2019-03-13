package com.amaze.filemanager.asynchronous.services;


import R.string.multiple_invalid_archive_entries;
import RuntimeEnvironment.application;
import android.os.Environment;
import com.amaze.filemanager.BuildConfig;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.multidex.ShadowMultiDex;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = { ShadowMultiDex.class })
public class ExtractServiceTest {
    private File zipfile1 = new File(Environment.getExternalStorageDirectory(), "zip-slip.zip");

    private File zipfile2 = new File(Environment.getExternalStorageDirectory(), "zip-slip-win.zip");

    private File zipfile3 = new File(Environment.getExternalStorageDirectory(), "test-archive.zip");

    private File rarfile = new File(Environment.getExternalStorageDirectory(), "test-archive.rar");

    private File tarfile = new File(Environment.getExternalStorageDirectory(), "test-archive.tar");

    private File tarballfile = new File(Environment.getExternalStorageDirectory(), "test-archive.tar.gz");

    private ExtractService service;

    @Test
    public void testExtractZipSlip() {
        performTest(zipfile1);
        Assert.assertEquals(application.getString(multiple_invalid_archive_entries), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testExtractZipSlipWin() {
        performTest(zipfile2);
        Assert.assertEquals(application.getString(multiple_invalid_archive_entries), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testExtractZipNormal() {
        performTest(zipfile3);
        Assert.assertNull(ShadowToast.getLatestToast());
        Assert.assertNull(ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testExtractRar() {
        performTest(rarfile);
        Assert.assertNull(ShadowToast.getLatestToast());
        Assert.assertNull(ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testExtractTar() {
        performTest(tarfile);
        Assert.assertNull(ShadowToast.getLatestToast());
        Assert.assertNull(ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testExtractTarGz() {
        performTest(tarballfile);
        Assert.assertNull(ShadowToast.getLatestToast());
        Assert.assertNull(ShadowToast.getTextOfLatestToast());
    }
}

