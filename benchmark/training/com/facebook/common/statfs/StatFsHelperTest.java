/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.statfs;


import StatFsHelper.StorageType.EXTERNAL;
import StatFsHelper.StorageType.INTERNAL;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import java.io.File;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link StatFsHelper}.
 */
@RunWith(RobolectricTestRunner.class)
@PrepareForTest({ Environment.class, StatFsHelper.class, SystemClock.class })
@Ignore("t6344387")
public class StatFsHelperTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private File mMockFileInternal;

    private File mMockFileExternal;

    private StatFs mMockStatFsInternal;

    private StatFs mMockStatFsExternal;

    private static final String INTERNAL_PATH = "/data";

    private static final String EXTERNAL_PATH = "/mnt/sdcard/data";

    private static final int INTERNAL_BLOCK_SIZE = 512;

    private static final int EXTERNAL_BLOCK_SIZE = 2048;

    private static final int INTERNAL_BLOCKS_FREE = 16;

    private static final int EXTERNAL_BLOCKS_FREE = 32;

    @Test
    public void testShouldCreateStatFsForInternalAndExternalStorage() {
        expectInternalSetup();
        expectExternalSetup();
        StatFsHelper statFsHelper = new StatFsHelper();
        long freeBytes = statFsHelper.getAvailableStorageSpace(INTERNAL);
        Assert.assertEquals(((StatFsHelperTest.INTERNAL_BLOCK_SIZE) * (StatFsHelperTest.INTERNAL_BLOCKS_FREE)), freeBytes);
        freeBytes = statFsHelper.getAvailableStorageSpace(EXTERNAL);
        Assert.assertEquals(((StatFsHelperTest.EXTERNAL_BLOCK_SIZE) * (StatFsHelperTest.EXTERNAL_BLOCKS_FREE)), freeBytes);
        statFsHelper.resetStats();
        Mockito.verify(mMockStatFsInternal).restat(StatFsHelperTest.INTERNAL_PATH);
        Mockito.verify(mMockStatFsExternal).restat(StatFsHelperTest.EXTERNAL_PATH);
    }

    @Test
    public void testShouldCreateStatFsForInternalStorageOnly() {
        expectInternalSetup();
        // Configure external storage to be absent.
        PowerMockito.when(Environment.getExternalStorageDirectory()).thenReturn(null);
        StatFsHelper statFsHelper = new StatFsHelper();
        long freeBytes = statFsHelper.getAvailableStorageSpace(INTERNAL);
        Assert.assertEquals(((StatFsHelperTest.INTERNAL_BLOCK_SIZE) * (StatFsHelperTest.INTERNAL_BLOCKS_FREE)), freeBytes);
        freeBytes = statFsHelper.getAvailableStorageSpace(EXTERNAL);
        Assert.assertEquals(0, freeBytes);
        statFsHelper.resetStats();
        Mockito.verify(mMockStatFsInternal).restat(StatFsHelperTest.INTERNAL_PATH);
    }

    @Test
    public void testShouldHandleNoInternalStorage() {
        // Configure internal storage to be absent.
        PowerMockito.when(Environment.getDataDirectory()).thenReturn(null);
        // Configure external storage to be absent.
        PowerMockito.when(Environment.getExternalStorageDirectory()).thenReturn(null);
        StatFsHelper statFsHelper = new StatFsHelper();
        long freeBytes = statFsHelper.getAvailableStorageSpace(INTERNAL);
        Assert.assertEquals(0, freeBytes);
        freeBytes = statFsHelper.getAvailableStorageSpace(EXTERNAL);
        Assert.assertEquals(0, freeBytes);
        statFsHelper.resetStats();
    }

    @Test
    public void testShouldHandleExceptionOnExternalCacheCreate() {
        expectInternalSetup();
        // Configure external storage to be present but to throw an exception while instantiating
        // a new StatFs object for external storage.
        Mockito.when(mMockFileExternal.getAbsolutePath()).thenReturn(StatFsHelperTest.EXTERNAL_PATH);
        Mockito.when(mMockFileExternal.exists()).thenReturn(true);
        PowerMockito.when(StatFsHelper.createStatFs(StatFsHelperTest.EXTERNAL_PATH)).thenThrow(new IllegalArgumentException());
        StatFsHelper statFsHelper = new StatFsHelper();
        long freeBytes = statFsHelper.getAvailableStorageSpace(INTERNAL);
        Assert.assertEquals(((StatFsHelperTest.INTERNAL_BLOCK_SIZE) * (StatFsHelperTest.INTERNAL_BLOCKS_FREE)), freeBytes);
        freeBytes = statFsHelper.getAvailableStorageSpace(EXTERNAL);
        Assert.assertEquals(0, freeBytes);
    }

    @Test
    public void testShouldHandleExceptionOnExternalCacheRestat() {
        expectInternalSetup();
        expectExternalSetup();
        Mockito.doThrow(new IllegalArgumentException()).when(mMockStatFsExternal).restat(StatFsHelperTest.EXTERNAL_PATH);
        StatFsHelper statFsHelper = new StatFsHelper();
        statFsHelper.resetStats();
        long freeBytes = statFsHelper.getAvailableStorageSpace(INTERNAL);
        Assert.assertEquals(((StatFsHelperTest.INTERNAL_BLOCK_SIZE) * (StatFsHelperTest.INTERNAL_BLOCKS_FREE)), freeBytes);
        freeBytes = statFsHelper.getAvailableStorageSpace(EXTERNAL);
        Assert.assertEquals(0, freeBytes);
        Mockito.verify(mMockStatFsInternal).restat(StatFsHelperTest.INTERNAL_PATH);
    }

    @Test
    public void testShouldHandleExternalStorageRemoved() {
        expectInternalSetup();
        expectExternalSetup();
        // External dir is present on creation and missing on subsequent resetStatus() calls.
        Mockito.when(mMockFileExternal.exists()).thenReturn(true).thenReturn(false);
        StatFsHelper statFsHelper = new StatFsHelper();
        statFsHelper.resetStats();
        long freeBytes = statFsHelper.getAvailableStorageSpace(INTERNAL);
        Assert.assertEquals(((StatFsHelperTest.INTERNAL_BLOCK_SIZE) * (StatFsHelperTest.INTERNAL_BLOCKS_FREE)), freeBytes);
        freeBytes = statFsHelper.getAvailableStorageSpace(EXTERNAL);
        Assert.assertEquals(0, freeBytes);
        Mockito.verify(mMockStatFsInternal).restat(StatFsHelperTest.INTERNAL_PATH);
    }

    @Test
    public void testShouldHandleExternalStorageReinserted() {
        expectInternalSetup();
        expectExternalSetup();
        // External dir is present on creation, missing on first resetStatus() call, and back on
        // subsequent resetStatus() calls.
        Mockito.when(mMockFileExternal.exists()).thenReturn(true).thenReturn(false).thenReturn(true);
        StatFsHelper statFsHelper = new StatFsHelper();
        statFsHelper.resetStats();
        long freeBytes = statFsHelper.getAvailableStorageSpace(INTERNAL);
        Assert.assertEquals(((StatFsHelperTest.INTERNAL_BLOCK_SIZE) * (StatFsHelperTest.INTERNAL_BLOCKS_FREE)), freeBytes);
        freeBytes = statFsHelper.getAvailableStorageSpace(EXTERNAL);
        Assert.assertEquals(0, freeBytes);
        statFsHelper.resetStats();
        freeBytes = statFsHelper.getAvailableStorageSpace(EXTERNAL);
        Assert.assertEquals(((StatFsHelperTest.EXTERNAL_BLOCK_SIZE) * (StatFsHelperTest.EXTERNAL_BLOCKS_FREE)), freeBytes);
        statFsHelper.resetStats();
        freeBytes = statFsHelper.getAvailableStorageSpace(EXTERNAL);
        Assert.assertEquals(((StatFsHelperTest.EXTERNAL_BLOCK_SIZE) * (StatFsHelperTest.EXTERNAL_BLOCKS_FREE)), freeBytes);
        Mockito.verify(mMockStatFsInternal, Mockito.times(3)).restat(StatFsHelperTest.INTERNAL_PATH);
        Mockito.verify(mMockStatFsExternal).restat(StatFsHelperTest.EXTERNAL_PATH);
    }
}

