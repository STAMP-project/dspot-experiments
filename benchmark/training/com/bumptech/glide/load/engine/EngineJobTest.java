package com.bumptech.glide.load.engine;


import EngineJob.EngineResourceFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.Pools;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.EngineResource.ResourceListener;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.load.engine.executor.MockGlideExecutor;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.tests.Util;
import com.bumptech.glide.util.Executors;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class EngineJobTest {
    private EngineJobTest.EngineJobHarness harness;

    @Test
    public void testOnResourceReadyPassedToCallbacks() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        ShadowLooper.runUiThreadTasks();
        Mockito.verify(harness.cb).onResourceReady(ArgumentMatchers.eq(harness.engineResource), ArgumentMatchers.eq(harness.dataSource));
    }

    @Test
    public void testListenerNotifiedJobCompleteOnOnResourceReady() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        ShadowLooper.runUiThreadTasks();
        Mockito.verify(harness.engineJobListener).onEngineJobComplete(ArgumentMatchers.eq(job), ArgumentMatchers.eq(harness.key), ArgumentMatchers.eq(harness.engineResource));
    }

    @Test
    public void testNotifiesAllCallbacksOnReady() {
        EngineJobTest.MultiCbHarness harness = new EngineJobTest.MultiCbHarness();
        harness.job.start(harness.decodeJob);
        harness.job.onResourceReady(harness.resource, harness.dataSource);
        for (ResourceCallback cb : harness.cbs) {
            Mockito.verify(cb).onResourceReady(ArgumentMatchers.eq(harness.engineResource), ArgumentMatchers.eq(harness.dataSource));
        }
    }

    @Test
    public void testNotifiesAllCallbacksOnException() {
        EngineJobTest.MultiCbHarness harness = new EngineJobTest.MultiCbHarness();
        harness.job.start(harness.decodeJob);
        GlideException exception = new GlideException("test");
        harness.job.onLoadFailed(exception);
        for (ResourceCallback cb : harness.cbs) {
            Mockito.verify(cb).onLoadFailed(ArgumentMatchers.eq(exception));
        }
    }

    @Test
    public void testAcquiresResourceOncePerCallback() {
        EngineJobTest.MultiCbHarness harness = new EngineJobTest.MultiCbHarness();
        harness.job.start(harness.decodeJob);
        harness.job.onResourceReady(harness.resource, harness.dataSource);
        // Acquired once and then released while notifying.
        InOrder order = Mockito.inOrder(harness.engineResource);
        order.verify(harness.engineResource, Mockito.times(((harness.numCbs) + 1))).acquire();
        order.verify(harness.engineResource, Mockito.times(1)).release();
    }

    @Test
    public void testListenerNotifiedJobCompleteOnException() {
        harness = new EngineJobTest.EngineJobHarness();
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.onLoadFailed(new GlideException("test"));
        ShadowLooper.runUiThreadTasks();
        Mockito.verify(harness.engineJobListener).onEngineJobComplete(ArgumentMatchers.eq(job), ArgumentMatchers.eq(harness.key), ArgumentMatchers.isNull(EngineResource.class));
    }

    @Test
    public void testResourceIsCacheableWhenIsCacheableOnReady() {
        harness.isCacheable = true;
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        ShadowLooper.runUiThreadTasks();
        Mockito.verify(harness.factory).build(Util.anyResource(), ArgumentMatchers.eq(harness.isCacheable), ArgumentMatchers.eq(harness.key), ArgumentMatchers.eq(harness.resourceListener));
    }

    @Test
    public void testResourceIsCacheableWhenNotIsCacheableOnReady() {
        harness.isCacheable = false;
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        ShadowLooper.runUiThreadTasks();
        Mockito.verify(harness.factory).build(Util.anyResource(), ArgumentMatchers.eq(harness.isCacheable), ArgumentMatchers.eq(harness.key), ArgumentMatchers.eq(harness.resourceListener));
    }

    @Test
    public void testListenerNotifiedOfCancelOnCancel() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.cancel();
        Mockito.verify(harness.engineJobListener).onEngineJobCancelled(ArgumentMatchers.eq(job), ArgumentMatchers.eq(harness.key));
    }

    @Test
    public void testOnResourceReadyNotDeliveredAfterCancel() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.cancel();
        job.onResourceReady(harness.resource, harness.dataSource);
        ShadowLooper.runUiThreadTasks();
        Mockito.verify(harness.cb, Mockito.never()).onResourceReady(Util.anyResource(), Util.isADataSource());
    }

    @Test
    public void testOnExceptionNotDeliveredAfterCancel() {
        harness = new EngineJobTest.EngineJobHarness();
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.cancel();
        job.onLoadFailed(new GlideException("test"));
        ShadowLooper.runUiThreadTasks();
        Mockito.verify(harness.cb, Mockito.never()).onLoadFailed(ArgumentMatchers.any(GlideException.class));
    }

    @Test
    public void testRemovingAllCallbacksCancelsRunner() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.removeCallback(harness.cb);
        Assert.assertTrue(job.isCancelled());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removingSomeCallbacksDoesNotCancelRunner() {
        EngineJob<Object> job = harness.getJob();
        job.addCallback(Mockito.mock(ResourceCallback.class), Executors.directExecutor());
        job.removeCallback(harness.cb);
        Assert.assertFalse(job.isCancelled());
    }

    @Test
    public void testResourceIsAcquiredOncePerConsumerAndOnceForCache() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        // Once while notifying and once for single callback.
        Mockito.verify(harness.engineResource, Mockito.times(2)).acquire();
    }

    @Test
    public void testDoesNotNotifyCancelledIfCompletes() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        Mockito.verify(harness.engineJobListener, Mockito.never()).onEngineJobCancelled(ArgumentMatchers.eq(job), ArgumentMatchers.eq(harness.key));
    }

    @Test
    public void testDoesNotNotifyCancelledIfAlreadyCancelled() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.cancel();
        job.cancel();
        Mockito.verify(harness.engineJobListener, Mockito.times(1)).onEngineJobCancelled(ArgumentMatchers.eq(job), ArgumentMatchers.eq(harness.key));
    }

    @Test
    public void testDoesNotNotifyCancelledIfReceivedException() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.onLoadFailed(new GlideException("test"));
        Mockito.verify(harness.engineJobListener).onEngineJobComplete(ArgumentMatchers.eq(job), ArgumentMatchers.eq(harness.key), ArgumentMatchers.isNull(EngineResource.class));
        Mockito.verify(harness.engineJobListener, Mockito.never()).onEngineJobCancelled(ArgumentMatchers.any(EngineJob.class), ArgumentMatchers.any(Key.class));
    }

    @Test
    public void testReleasesResourceIfCancelledOnReady() {
        Looper looper = harness.mainHandler.getLooper();
        Shadows.shadowOf(looper).pause();
        final EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.cancel();
        job.onResourceReady(harness.resource, harness.dataSource);
        Mockito.verify(harness.resource).recycle();
    }

    @Test
    public void testDoesNotAcquireOnceForMemoryCacheIfNotCacheable() {
        harness.isCacheable = false;
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        Mockito.verify(harness.engineResource, Mockito.times(2)).acquire();
    }

    @Test
    public void testNotifiesNewCallbackOfResourceIfCallbackIsAddedDuringOnResourceReady() {
        final EngineJob<Object> job = harness.getJob();
        final ResourceCallback existingCallback = Mockito.mock(ResourceCallback.class);
        final ResourceCallback newCallback = Mockito.mock(ResourceCallback.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                job.addCallback(newCallback, Executors.directExecutor());
                return null;
            }
        }).when(existingCallback).onResourceReady(Util.anyResource(), Util.isADataSource());
        job.addCallback(existingCallback, Executors.directExecutor());
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        Mockito.verify(newCallback).onResourceReady(ArgumentMatchers.eq(harness.engineResource), ArgumentMatchers.eq(harness.dataSource));
    }

    @Test
    public void testNotifiesNewCallbackOfExceptionIfCallbackIsAddedDuringOnException() {
        harness = new EngineJobTest.EngineJobHarness();
        final EngineJob<Object> job = harness.getJob();
        final ResourceCallback existingCallback = Mockito.mock(ResourceCallback.class);
        final ResourceCallback newCallback = Mockito.mock(ResourceCallback.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                job.addCallback(newCallback, Executors.directExecutor());
                return null;
            }
        }).when(existingCallback).onLoadFailed(ArgumentMatchers.any(GlideException.class));
        GlideException exception = new GlideException("test");
        job.addCallback(existingCallback, Executors.directExecutor());
        job.start(harness.decodeJob);
        job.onLoadFailed(exception);
        Mockito.verify(newCallback).onLoadFailed(ArgumentMatchers.eq(exception));
    }

    @Test
    public void testRemovingCallbackDuringOnResourceReadyIsIgnoredIfCallbackHasAlreadyBeenCalled() {
        final EngineJob<Object> job = harness.getJob();
        final ResourceCallback cb = Mockito.mock(ResourceCallback.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                job.removeCallback(cb);
                return null;
            }
        }).when(cb).onResourceReady(Util.anyResource(), Util.isADataSource());
        job.addCallback(cb, Executors.directExecutor());
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        Mockito.verify(cb, Mockito.times(1)).onResourceReady(Util.anyResource(), Util.isADataSource());
    }

    @Test
    public void testRemovingCallbackDuringOnExceptionIsIgnoredIfCallbackHasAlreadyBeenCalled() {
        harness = new EngineJobTest.EngineJobHarness();
        final EngineJob<Object> job = harness.getJob();
        final ResourceCallback cb = Mockito.mock(ResourceCallback.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                job.removeCallback(cb);
                return null;
            }
        }).when(cb).onLoadFailed(ArgumentMatchers.any(GlideException.class));
        GlideException exception = new GlideException("test");
        job.addCallback(cb, Executors.directExecutor());
        job.start(harness.decodeJob);
        job.onLoadFailed(exception);
        Mockito.verify(cb, Mockito.times(1)).onLoadFailed(ArgumentMatchers.eq(exception));
    }

    @Test
    public void testRemovingCallbackDuringOnResourceReadyPreventsCallbackFromBeingCalledIfNotYetCalled() {
        final EngineJob<Object> job = harness.getJob();
        final ResourceCallback notYetCalled = Mockito.mock(ResourceCallback.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                job.removeCallback(notYetCalled);
                return null;
            }
        }).when(harness.cb).onResourceReady(Util.anyResource(), Util.isADataSource());
        job.addCallback(notYetCalled, Executors.directExecutor());
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        Mockito.verify(notYetCalled, Mockito.never()).onResourceReady(Util.anyResource(), Util.isADataSource());
    }

    @Test
    public void testRemovingCallbackDuringOnResourceReadyPreventsResourceFromBeingAcquiredForCallback() {
        final EngineJob<Object> job = harness.getJob();
        final ResourceCallback notYetCalled = Mockito.mock(ResourceCallback.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                job.removeCallback(notYetCalled);
                return null;
            }
        }).when(harness.cb).onResourceReady(Util.anyResource(), Util.isADataSource());
        job.addCallback(notYetCalled, Executors.directExecutor());
        job.start(harness.decodeJob);
        job.onResourceReady(harness.resource, harness.dataSource);
        // Once for notifying, once for called.
        Mockito.verify(harness.engineResource, Mockito.times(2)).acquire();
    }

    @Test
    public void testRemovingCallbackDuringOnExceptionPreventsCallbackFromBeingCalledIfNotYetCalled() {
        harness = new EngineJobTest.EngineJobHarness();
        final EngineJob<Object> job = harness.getJob();
        final ResourceCallback called = Mockito.mock(ResourceCallback.class);
        final ResourceCallback notYetCalled = Mockito.mock(ResourceCallback.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                job.removeCallback(notYetCalled);
                return null;
            }
        }).when(called).onLoadFailed(ArgumentMatchers.any(GlideException.class));
        job.addCallback(called, Executors.directExecutor());
        job.addCallback(notYetCalled, Executors.directExecutor());
        job.start(harness.decodeJob);
        job.onLoadFailed(new GlideException("test"));
        Mockito.verify(notYetCalled, Mockito.never()).onResourceReady(Util.anyResource(), Util.isADataSource());
    }

    @Test
    public void testCancelsDecodeJobOnCancel() {
        EngineJob<Object> job = harness.getJob();
        job.start(harness.decodeJob);
        job.cancel();
        Mockito.verify(harness.decodeJob).cancel();
    }

    @Test
    public void testSubmitsDecodeJobToSourceServiceOnSubmitForSource() {
        EngineJob<Object> job = harness.getJob();
        harness.diskCacheService.shutdownNow();
        job.reschedule(harness.decodeJob);
        Mockito.verify(harness.decodeJob).run();
    }

    @Test
    public void testSubmitsDecodeJobToDiskCacheServiceWhenDecodingFromCacheOnStart() {
        EngineJob<Object> job = harness.getJob();
        Mockito.when(harness.decodeJob.willDecodeFromCache()).thenReturn(true);
        harness.sourceService.shutdownNow();
        job.start(harness.decodeJob);
        Mockito.verify(harness.decodeJob).run();
    }

    @Test
    public void testSubmitsDecodeJobToSourceServiceWhenDecodingFromSourceOnlyOnStart() {
        EngineJob<Object> job = harness.getJob();
        Mockito.when(harness.decodeJob.willDecodeFromCache()).thenReturn(false);
        harness.diskCacheService.shutdownNow();
        job.start(harness.decodeJob);
        Mockito.verify(harness.decodeJob).run();
    }

    @Test
    public void testSubmitsDecodeJobToUnlimitedSourceServiceWhenDecodingFromSourceOnlyOnStart() {
        harness.useUnlimitedSourceGeneratorPool = true;
        EngineJob<Object> job = harness.getJob();
        Mockito.when(harness.decodeJob.willDecodeFromCache()).thenReturn(false);
        harness.diskCacheService.shutdownNow();
        job.start(harness.decodeJob);
        Mockito.verify(harness.decodeJob).run();
    }

    @SuppressWarnings("unchecked")
    private static class MultiCbHarness {
        final Key key = Mockito.mock(Key.class);

        final Resource<Object> resource = Util.mockResource();

        final EngineResource<Object> engineResource = Mockito.mock(EngineResource.class);

        final EngineJobListener engineJobListener = Mockito.mock(EngineJobListener.class);

        final ResourceListener resourceListener = Mockito.mock(ResourceListener.class);

        final boolean isCacheable = true;

        final boolean useUnlimitedSourceGeneratorPool = false;

        final boolean useAnimationPool = false;

        final boolean onlyRetrieveFromCache = false;

        final int numCbs = 10;

        final List<ResourceCallback> cbs = new ArrayList<>();

        final EngineResourceFactory factory = Mockito.mock(EngineResourceFactory.class);

        final EngineJob<Object> job;

        final GlideExecutor diskCacheService = MockGlideExecutor.newMainThreadExecutor();

        final GlideExecutor sourceService = MockGlideExecutor.newMainThreadExecutor();

        final GlideExecutor sourceUnlimitedService = MockGlideExecutor.newMainThreadExecutor();

        final GlideExecutor animationService = MockGlideExecutor.newMainThreadExecutor();

        final Pools.Pool<EngineJob<?>> pool = new Pools.SimplePool<>(1);

        final DecodeJob<Object> decodeJob = Mockito.mock(DecodeJob.class);

        final DataSource dataSource = DataSource.LOCAL;

        public MultiCbHarness() {
            Mockito.when(factory.build(resource, isCacheable, key, resourceListener)).thenReturn(engineResource);
            job = new EngineJob(diskCacheService, sourceService, sourceUnlimitedService, animationService, engineJobListener, resourceListener, pool, factory);
            job.init(key, isCacheable, useUnlimitedSourceGeneratorPool, useAnimationPool, onlyRetrieveFromCache);
            for (int i = 0; i < (numCbs); i++) {
                cbs.add(Mockito.mock(ResourceCallback.class));
            }
            for (ResourceCallback cb : cbs) {
                job.addCallback(cb, Executors.directExecutor());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static class EngineJobHarness {
        final EngineResourceFactory factory = Mockito.mock(EngineResourceFactory.class);

        final Key key = Mockito.mock(Key.class);

        final Handler mainHandler = new Handler();

        final ResourceCallback cb = Mockito.mock(ResourceCallback.class);

        final Resource<Object> resource = Util.mockResource();

        final EngineResource<Object> engineResource = Mockito.mock(EngineResource.class);

        final EngineJobListener engineJobListener = Mockito.mock(EngineJobListener.class);

        final ResourceListener resourceListener = Mockito.mock(ResourceListener.class);

        final GlideExecutor diskCacheService = MockGlideExecutor.newMainThreadExecutor();

        final GlideExecutor sourceService = MockGlideExecutor.newMainThreadExecutor();

        final GlideExecutor sourceUnlimitedService = MockGlideExecutor.newMainThreadExecutor();

        final GlideExecutor animationService = MockGlideExecutor.newMainThreadExecutor();

        boolean isCacheable = true;

        boolean useUnlimitedSourceGeneratorPool = false;

        final boolean useAnimationPool = false;

        final boolean onlyRetrieveFromCache = false;

        final DecodeJob<Object> decodeJob = Mockito.mock(DecodeJob.class);

        final Pools.Pool<EngineJob<?>> pool = new Pools.SynchronizedPool<>(1);

        final DataSource dataSource = DataSource.DATA_DISK_CACHE;

        EngineJob<Object> getJob() {
            Mockito.when(factory.build(resource, isCacheable, key, resourceListener)).thenReturn(engineResource);
            EngineJob<Object> result = new EngineJob(diskCacheService, sourceService, sourceUnlimitedService, animationService, engineJobListener, resourceListener, pool, factory);
            result.init(key, isCacheable, useUnlimitedSourceGeneratorPool, useAnimationPool, onlyRetrieveFromCache);
            result.addCallback(cb, Executors.directExecutor());
            return result;
        }
    }
}

