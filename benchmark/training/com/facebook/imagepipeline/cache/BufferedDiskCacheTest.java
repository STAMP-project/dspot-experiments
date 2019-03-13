/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.cache;


import bolts.Task;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.cache.common.MultiCacheKey;
import com.facebook.cache.common.WriterCallback;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteStreams;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.testing.TestExecutorService;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@PrepareOnlyThisForTest(StagingArea.class)
@Config(manifest = Config.NONE)
public class BufferedDiskCacheTest {
    @Mock
    public FileCache mFileCache;

    @Mock
    public PooledByteBufferFactory mByteBufferFactory;

    @Mock
    public PooledByteStreams mPooledByteStreams;

    @Mock
    public StagingArea mStagingArea;

    @Mock
    public ImageCacheStatsTracker mImageCacheStatsTracker;

    @Mock
    public PooledByteBuffer mPooledByteBuffer;

    @Mock
    public InputStream mInputStream;

    @Mock
    public BinaryResource mBinaryResource;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private MultiCacheKey mCacheKey;

    private AtomicBoolean mIsCancelled;

    private BufferedDiskCache mBufferedDiskCache;

    private CloseableReference<PooledByteBuffer> mCloseableReference;

    private EncodedImage mEncodedImage;

    private TestExecutorService mReadPriorityExecutor;

    private TestExecutorService mWritePriorityExecutor;

    @Test
    public void testHasKeySyncFromFileCache() {
        Mockito.when(mFileCache.hasKeySync(mCacheKey)).thenReturn(true);
        Assert.assertTrue(mBufferedDiskCache.containsSync(mCacheKey));
    }

    @Test
    public void testHasKeySyncFromStagingArea() {
        Mockito.when(mStagingArea.containsKey(mCacheKey)).thenReturn(true);
        Assert.assertTrue(mBufferedDiskCache.containsSync(mCacheKey));
    }

    @Test
    public void testDoesntAlwaysHaveKeySync() {
        Mockito.when(mFileCache.hasKey(mCacheKey)).thenReturn(true);
        Assert.assertFalse(mBufferedDiskCache.containsSync(mCacheKey));
    }

    @Test
    public void testSyncDiskCacheCheck() {
        Mockito.when(((mStagingArea.containsKey(mCacheKey)) || (mFileCache.hasKey(mCacheKey)))).thenReturn(true);
        Assert.assertTrue(mBufferedDiskCache.diskCheckSync(mCacheKey));
    }

    @Test
    public void testQueriesDiskCache() throws Exception {
        Mockito.when(mFileCache.getResource(ArgumentMatchers.eq(mCacheKey))).thenReturn(mBinaryResource);
        Task<EncodedImage> readTask = mBufferedDiskCache.get(mCacheKey, mIsCancelled);
        mReadPriorityExecutor.runUntilIdle();
        Mockito.verify(mFileCache).getResource(ArgumentMatchers.eq(mCacheKey));
        EncodedImage result = readTask.getResult();
        Assert.assertEquals(2, result.getByteBufferRef().getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(mPooledByteBuffer, result.getByteBufferRef().get());
    }

    @Test
    public void testCacheGetCancellation() throws Exception {
        Mockito.when(mFileCache.getResource(mCacheKey)).thenReturn(mBinaryResource);
        Task<EncodedImage> readTask = mBufferedDiskCache.get(mCacheKey, mIsCancelled);
        mIsCancelled.set(true);
        mReadPriorityExecutor.runUntilIdle();
        Mockito.verify(mFileCache, Mockito.never()).getResource(mCacheKey);
        Assert.assertTrue(BufferedDiskCacheTest.isTaskCancelled(readTask));
    }

    @Test
    public void testGetDoesNotThrow() throws Exception {
        Task<EncodedImage> readTask = mBufferedDiskCache.get(mCacheKey, mIsCancelled);
        Mockito.when(mFileCache.getResource(mCacheKey)).thenThrow(new RuntimeException("Should not be propagated"));
        Assert.assertFalse(readTask.isFaulted());
        Assert.assertNull(readTask.getResult());
    }

    @Test
    public void testWritesToDiskCache() throws Exception {
        mBufferedDiskCache.put(mCacheKey, mEncodedImage);
        Mockito.reset(mPooledByteBuffer);
        Mockito.when(mPooledByteBuffer.size()).thenReturn(0);
        final ArgumentCaptor<WriterCallback> wcCapture = ArgumentCaptor.forClass(WriterCallback.class);
        Mockito.when(mFileCache.insert(ArgumentMatchers.eq(mCacheKey), wcCapture.capture())).thenReturn(null);
        mWritePriorityExecutor.runUntilIdle();
        OutputStream os = Mockito.mock(OutputStream.class);
        wcCapture.getValue().write(os);
        // Ref count should be equal to 2 ('owned' by the mCloseableReference and other 'owned' by
        // mEncodedImage)
        Assert.assertEquals(2, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
    }

    @Test
    public void testCacheMiss() throws Exception {
        Task<EncodedImage> readTask = mBufferedDiskCache.get(mCacheKey, mIsCancelled);
        mReadPriorityExecutor.runUntilIdle();
        Mockito.verify(mFileCache).getResource(ArgumentMatchers.eq(mCacheKey));
        Assert.assertNull(readTask.getResult());
    }

    @Test
    public void testPutBumpsRefCountBeforeSubmit() {
        mBufferedDiskCache.put(mCacheKey, mEncodedImage);
        Assert.assertEquals(3, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
    }

    @Test
    public void testManagesReference() throws Exception {
        mBufferedDiskCache.put(mCacheKey, mEncodedImage);
        mWritePriorityExecutor.runUntilIdle();
        Assert.assertEquals(2, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
    }

    @Test
    public void testPins() {
        mBufferedDiskCache.put(mCacheKey, mEncodedImage);
        Mockito.verify(mStagingArea).put(mCacheKey, mEncodedImage);
    }

    @Test
    public void testFromStagingArea() throws Exception {
        Mockito.when(mStagingArea.get(mCacheKey)).thenReturn(mEncodedImage);
        Assert.assertEquals(2, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(mCloseableReference.getUnderlyingReferenceTestOnly(), mBufferedDiskCache.get(mCacheKey, mIsCancelled).getResult().getByteBufferRef().getUnderlyingReferenceTestOnly());
    }

    @Test
    public void testFromStagingAreaLater() throws Exception {
        Task<EncodedImage> readTask = mBufferedDiskCache.get(mCacheKey, mIsCancelled);
        Assert.assertFalse(readTask.isCompleted());
        Mockito.when(mStagingArea.get(mCacheKey)).thenReturn(mEncodedImage);
        mReadPriorityExecutor.runUntilIdle();
        EncodedImage result = readTask.getResult();
        Assert.assertSame(result, mEncodedImage);
        Mockito.verify(mFileCache, Mockito.never()).getResource(ArgumentMatchers.eq(mCacheKey));
        // Ref count should be equal to 3 (One for mCloseableReference, one that is cloned when
        // mEncodedImage is created and a third one that is cloned when the method getByteBufferRef is
        // called in EncodedImage).
        Assert.assertEquals(3, result.getByteBufferRef().getUnderlyingReferenceTestOnly().getRefCountTestOnly());
    }

    @Test
    public void testUnpins() {
        mBufferedDiskCache.put(mCacheKey, mEncodedImage);
        mWritePriorityExecutor.runUntilIdle();
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mStagingArea).remove(ArgumentMatchers.eq(mCacheKey), argumentCaptor.capture());
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertSame(mEncodedImage.getUnderlyingReferenceTestOnly(), encodedImage.getUnderlyingReferenceTestOnly());
    }

    @Test
    public void testContainsFromStagingAreaLater() {
        Task<Boolean> readTask = mBufferedDiskCache.contains(mCacheKey);
        Assert.assertFalse(readTask.isCompleted());
        Mockito.when(mStagingArea.get(mCacheKey)).thenReturn(mEncodedImage);
        mReadPriorityExecutor.runUntilIdle();
        Mockito.verify(mFileCache, Mockito.never()).getResource(ArgumentMatchers.eq(mCacheKey));
    }

    @Test
    public void testRemoveFromStagingArea() {
        mBufferedDiskCache.remove(mCacheKey);
        Mockito.verify(mStagingArea).remove(mCacheKey);
    }

    @Test
    public void testClearFromStagingArea() {
        mBufferedDiskCache.clearAll();
        Mockito.verify(mStagingArea).clearAll();
    }
}

