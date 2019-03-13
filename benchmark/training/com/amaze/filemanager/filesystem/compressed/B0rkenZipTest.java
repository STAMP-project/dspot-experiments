package com.amaze.filemanager.filesystem.compressed;


import Extractor.OnUpdate;
import R.string.multiple_invalid_archive_entries;
import RuntimeEnvironment.application;
import android.os.Environment;
import com.amaze.filemanager.BuildConfig;
import com.amaze.filemanager.adapters.data.CompressedObjectParcelable;
import com.amaze.filemanager.asynchronous.asynctasks.compress.ZipHelperTask;
import com.amaze.filemanager.filesystem.compressed.extractcontents.Extractor;
import java.io.File;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.multidex.ShadowMultiDex;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = { ShadowMultiDex.class })
public class B0rkenZipTest {
    private File zipfile1 = new File(Environment.getExternalStorageDirectory(), "zip-slip.zip");

    private File zipfile2 = new File(Environment.getExternalStorageDirectory(), "zip-slip-win.zip");

    private File zipfile3 = new File(Environment.getExternalStorageDirectory(), "test-slashprefix.zip");

    private OnUpdate emptyListener = new Extractor.OnUpdate() {
        @Override
        public void onStart(long totalBytes, String firstEntryName) {
        }

        @Override
        public void onUpdate(String entryPath) {
        }

        @Override
        public void onFinish() {
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    };

    @Test
    public void testExtractZipWithWrongPathUnix() throws Exception {
        Extractor extractor = new com.amaze.filemanager.filesystem.compressed.extractcontents.helpers.ZipExtractor(RuntimeEnvironment.application, zipfile1.getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath(), emptyListener);
        extractor.extractEverything();
        Assert.assertEquals(1, extractor.getInvalidArchiveEntries().size());
        Assert.assertTrue(new File(Environment.getExternalStorageDirectory(), "good.txt").exists());
    }

    @Test
    public void testExtractZipWithWrongPathWindows() throws Exception {
        Extractor extractor = new com.amaze.filemanager.filesystem.compressed.extractcontents.helpers.ZipExtractor(RuntimeEnvironment.application, zipfile2.getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath(), emptyListener);
        extractor.extractEverything();
        Assert.assertEquals(1, extractor.getInvalidArchiveEntries().size());
        Assert.assertTrue(new File(Environment.getExternalStorageDirectory(), "good.txt").exists());
    }

    @Test
    public void testExtractZipWithSlashPrefixEntry() throws Exception {
        Extractor extractor = new com.amaze.filemanager.filesystem.compressed.extractcontents.helpers.ZipExtractor(RuntimeEnvironment.application, zipfile3.getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath(), emptyListener);
        extractor.extractFiles(new String[]{ "/test.txt" });
        Assert.assertEquals(0, extractor.getInvalidArchiveEntries().size());
        Assert.assertTrue(new File(Environment.getExternalStorageDirectory(), "test.txt").exists());
    }

    @Test
    public void testZipHelperTaskShouldOmitInvalidEntries() throws Exception {
        ZipHelperTask task = new ZipHelperTask(RuntimeEnvironment.application, zipfile1.getAbsolutePath(), null, false, ( data) -> {
        });
        List<CompressedObjectParcelable> result = task.execute().get();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("good.txt", result.get(0).path);
        Assert.assertEquals(application.getString(multiple_invalid_archive_entries), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testZipHelperTaskShouldOmitInvalidEntriesWithBackslash() throws Exception {
        ZipHelperTask task = new ZipHelperTask(RuntimeEnvironment.application, zipfile2.getAbsolutePath(), null, false, ( data) -> {
        });
        List<CompressedObjectParcelable> result = task.execute().get();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("good.txt", result.get(0).path);
        Assert.assertEquals(application.getString(multiple_invalid_archive_entries), ShadowToast.getTextOfLatestToast());
    }
}

