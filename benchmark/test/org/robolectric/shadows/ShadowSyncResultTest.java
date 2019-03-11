package org.robolectric.shadows;


import android.content.SyncResult;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowSyncResultTest {
    @Test
    public void testConstructor() throws Exception {
        SyncResult result = new SyncResult();
        assertThat(result.stats).isNotNull();
    }

    @Test
    public void hasSoftError() throws Exception {
        SyncResult result = new SyncResult();
        Assert.assertFalse(result.hasSoftError());
        (result.stats.numIoExceptions)++;
        Assert.assertTrue(result.hasSoftError());
        Assert.assertTrue(result.hasError());
    }

    @Test
    public void hasHardError() throws Exception {
        SyncResult result = new SyncResult();
        Assert.assertFalse(result.hasHardError());
        (result.stats.numAuthExceptions)++;
        Assert.assertTrue(result.hasHardError());
        Assert.assertTrue(result.hasError());
    }

    @Test
    public void testMadeSomeProgress() throws Exception {
        SyncResult result = new SyncResult();
        Assert.assertFalse(result.madeSomeProgress());
        (result.stats.numInserts)++;
        Assert.assertTrue(result.madeSomeProgress());
    }

    @Test
    public void testClear() throws Exception {
        SyncResult result = new SyncResult();
        result.moreRecordsToGet = true;
        (result.stats.numInserts)++;
        result.clear();
        Assert.assertFalse(result.moreRecordsToGet);
        assertThat(result.stats.numInserts).isEqualTo(0L);
    }
}

