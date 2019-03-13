package com.amaze.filemanager.filesystem.compressed.extractcontents;


import Extractor.OnUpdate;
import RuntimeEnvironment.application;
import android.content.Context;
import android.os.Environment;
import com.amaze.filemanager.BuildConfig;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.multidex.ShadowMultiDex;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = { ShadowMultiDex.class })
public abstract class AbstractExtractorTest {
    @Test
    public void testFixEntryName() throws Exception {
        Extractor extractor = extractorClass().getConstructor(Context.class, String.class, String.class, OnUpdate.class).newInstance(application, getArchiveFile().getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath(), null);
        Assert.assertEquals("test.txt", extractor.fixEntryName("test.txt"));
        Assert.assertEquals("test.txt", extractor.fixEntryName("/test.txt"));
        Assert.assertEquals("test.txt", extractor.fixEntryName("/////////test.txt"));
        Assert.assertEquals("test/", extractor.fixEntryName("/test/"));
        Assert.assertEquals("test/a/b/c/d/e/", extractor.fixEntryName("/test/a/b/c/d/e/"));
        Assert.assertEquals("a/b/c/d/e/test.txt", extractor.fixEntryName("a/b/c/d/e/test.txt"));
        Assert.assertEquals("a/b/c/d/e/test.txt", extractor.fixEntryName("/a/b/c/d/e/test.txt"));
        Assert.assertEquals("a/b/c/d/e/test.txt", extractor.fixEntryName("///////a/b/c/d/e/test.txt"));
        // It is known redundant slashes inside path components are NOT tampered.
        Assert.assertEquals("a/b/c//d//e//test.txt", extractor.fixEntryName("a/b/c//d//e//test.txt"));
        Assert.assertEquals("a/b/c/d/e/test.txt", extractor.fixEntryName("a/b/c/d/e/test.txt"));
        Assert.assertEquals("test.txt", extractor.fixEntryName("\\test.txt"));
        Assert.assertEquals("test.txt", extractor.fixEntryName("\\\\\\\\\\\\\\\\\\\\test.txt"));
        Assert.assertEquals("a/b/c/d/e/test.txt", extractor.fixEntryName("\\a\\b\\c\\d\\e\\test.txt"));
        Assert.assertEquals("a/b/c/d/e/test.txt", extractor.fixEntryName("\\a\\b/c\\d\\e/test.txt"));
    }

    @Test
    public void testExtractFiles() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Extractor extractor = extractorClass().getConstructor(Context.class, String.class, String.class, OnUpdate.class).newInstance(application, getArchiveFile().getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath(), new Extractor.OnUpdate() {
            @Override
            public void onStart(long totalBytes, String firstEntryName) {
            }

            @Override
            public void onUpdate(String entryPath) {
            }

            @Override
            public void onFinish() {
                latch.countDown();
                try {
                    verifyExtractedArchiveContents();
                } catch (IOException e) {
                    e.printStackTrace();
                    Assert.fail("Error verifying extracted archive contents");
                }
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        });
        extractor.extractEverything();
        latch.await();
    }
}

