package com.bumptech.glide.load.engine;


import DataSource.MEMORY_CACHE;
import DiskCache.Factory;
import DiskCacheStrategy.ALL;
import Engine.DecodeJobFactory;
import Engine.EngineJobFactory;
import Engine.LoadStatus;
import Priority.HIGH;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.load.engine.executor.MockGlideExecutor;
import com.bumptech.glide.request.ResourceCallback;
import com.bumptech.glide.tests.BackgroundUtil;
import com.bumptech.glide.tests.GlideShadowLooper;
import com.bumptech.glide.tests.Util;
import com.bumptech.glide.util.Executors;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, shadows = { GlideShadowLooper.class })
@SuppressWarnings("unchecked")
public class EngineTest {
    private EngineTest.EngineTestHarness harness;

    @Test
    public void testNewRunnerIsCreatedAndPostedWithNoExistingLoad() {
        harness.doLoad();
        Mockito.verify(harness.job).start(((DecodeJob) (ArgumentMatchers.any())));
    }

    @Test
    public void testCallbackIsAddedToNewEngineJobWithNoExistingLoad() {
        harness.doLoad();
        Mockito.verify(harness.job).addCallback(ArgumentMatchers.eq(harness.cb), ArgumentMatchers.any(Executor.class));
    }

    @Test
    public void testLoadStatusIsReturnedForNewLoad() {
        Assert.assertNotNull(harness.doLoad());
    }

    @Test
    public void testEngineJobReceivesRemoveCallbackFromLoadStatus() {
        Engine.LoadStatus loadStatus = harness.doLoad();
        loadStatus.cancel();
        Mockito.verify(harness.job).removeCallback(ArgumentMatchers.eq(harness.cb));
    }

    @Test
    public void testNewRunnerIsAddedToRunnersMap() {
        harness.doLoad();
        assertThat(harness.jobs.getAll()).containsKey(harness.cacheKey);
    }

    @Test
    public void testNewRunnerIsNotCreatedAndPostedWithExistingLoad() {
        harness.doLoad();
        harness.doLoad();
        Mockito.verify(harness.job, Mockito.times(1)).start(((DecodeJob) (ArgumentMatchers.any())));
    }

    @Test
    public void testCallbackIsAddedToExistingRunnerWithExistingLoad() {
        harness.doLoad();
        ResourceCallback newCallback = Mockito.mock(ResourceCallback.class);
        harness.cb = newCallback;
        harness.doLoad();
        Mockito.verify(harness.job).addCallback(ArgumentMatchers.eq(newCallback), ArgumentMatchers.any(Executor.class));
    }

    @Test
    public void testLoadStatusIsReturnedForExistingJob() {
        harness.doLoad();
        Engine.LoadStatus loadStatus = harness.doLoad();
        Assert.assertNotNull(loadStatus);
    }

    @Test
    public void testResourceIsReturnedFromActiveResourcesIfPresent() {
        harness.activeResources.activate(harness.cacheKey, harness.resource);
        harness.doLoad();
        Mockito.verify(harness.cb).onResourceReady(ArgumentMatchers.eq(harness.resource), ArgumentMatchers.eq(MEMORY_CACHE));
    }

    @Test
    public void testResourceIsAcquiredIfReturnedFromActiveResources() {
        harness.activeResources.activate(harness.cacheKey, harness.resource);
        harness.doLoad();
        Mockito.verify(harness.resource).acquire();
    }

    @Test
    public void testNewLoadIsNotStartedIfResourceIsActive() {
        harness.activeResources.activate(harness.cacheKey, harness.resource);
        harness.doLoad();
        Mockito.verify(harness.job, Mockito.never()).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void testNullLoadStatusIsReturnedIfResourceIsActive() {
        harness.activeResources.activate(harness.cacheKey, harness.resource);
        Assert.assertNull(harness.doLoad());
    }

    @Test
    public void load_withResourceInActiveResources_doesNotCheckMemoryCache() {
        harness.activeResources.activate(harness.cacheKey, harness.resource);
        harness.doLoad();
        Mockito.verify(harness.cb).onResourceReady(ArgumentMatchers.eq(harness.resource), ArgumentMatchers.eq(MEMORY_CACHE));
        Mockito.verify(harness.cache, Mockito.never()).remove(ArgumentMatchers.any(Key.class));
    }

    @Test
    public void testActiveResourcesIsNotCheckedIfNotMemoryCacheable() {
        harness.activeResources.activate(harness.cacheKey, harness.resource);
        harness.isMemoryCacheable = false;
        harness.doLoad();
        Mockito.verify(harness.resource, Mockito.never()).acquire();
        Mockito.verify(harness.job).start(((DecodeJob) (ArgumentMatchers.any())));
    }

    @Test
    public void testCacheIsCheckedIfMemoryCacheable() {
        Mockito.when(harness.cache.remove(ArgumentMatchers.eq(harness.cacheKey))).thenReturn(harness.resource);
        harness.doLoad();
        Mockito.verify(harness.cb).onResourceReady(ArgumentMatchers.eq(harness.resource), ArgumentMatchers.eq(MEMORY_CACHE));
    }

    @Test
    public void testCacheIsNotCheckedIfNotMemoryCacheable() {
        Mockito.when(harness.cache.remove(ArgumentMatchers.eq(harness.cacheKey))).thenReturn(harness.resource);
        harness.isMemoryCacheable = false;
        harness.doLoad();
        Mockito.verify(harness.job).start(((DecodeJob) (ArgumentMatchers.any())));
    }

    @Test
    public void testResourceIsReturnedFromCacheIfPresent() {
        Mockito.when(harness.cache.remove(ArgumentMatchers.eq(harness.cacheKey))).thenReturn(harness.resource);
        harness.doLoad();
        Mockito.verify(harness.cb).onResourceReady(ArgumentMatchers.eq(harness.resource), ArgumentMatchers.eq(MEMORY_CACHE));
    }

    @Test
    public void testHandlesNonEngineResourcesFromCacheIfPresent() {
        final Object expected = new Object();
        @SuppressWarnings("rawtypes")
        Resource fromCache = Util.mockResource();
        Mockito.when(fromCache.get()).thenReturn(expected);
        Mockito.when(harness.cache.remove(ArgumentMatchers.eq(harness.cacheKey))).thenReturn(fromCache);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                Resource<?> resource = ((Resource<?>) (invocationOnMock.getArguments()[0]));
                Assert.assertEquals(expected, resource.get());
                return null;
            }
        }).when(harness.cb).onResourceReady(Util.anyResource(), Util.isADataSource());
        harness.doLoad();
        Mockito.verify(harness.cb).onResourceReady(Util.anyResource(), Util.isADataSource());
    }

    @Test
    public void testResourceIsAddedToActiveResourceIfReturnedFromCache() {
        Mockito.when(harness.cache.remove(ArgumentMatchers.eq(harness.cacheKey))).thenReturn(harness.resource);
        harness.doLoad();
        EngineResource<?> activeResource = harness.activeResources.get(harness.cacheKey);
        assertThat(activeResource).isEqualTo(harness.resource);
    }

    @Test
    public void testResourceIsAcquiredIfReturnedFromCache() {
        Mockito.when(harness.cache.remove(ArgumentMatchers.eq(harness.cacheKey))).thenReturn(harness.resource);
        harness.doLoad();
        Mockito.verify(harness.resource).acquire();
    }

    @Test
    public void testNewLoadIsNotStartedIfResourceIsCached() {
        Mockito.when(harness.cache.remove(ArgumentMatchers.eq(harness.cacheKey))).thenReturn(harness.resource);
        harness.doLoad();
        Mockito.verify(harness.job, Mockito.never()).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void testNullLoadStatusIsReturnedForCachedResource() {
        Mockito.when(harness.cache.remove(ArgumentMatchers.eq(harness.cacheKey))).thenReturn(harness.resource);
        Engine.LoadStatus loadStatus = harness.doLoad();
        Assert.assertNull(loadStatus);
    }

    @Test
    public void testRunnerIsRemovedFromRunnersOnEngineNotifiedJobComplete() {
        harness.doLoad();
        harness.callOnEngineJobComplete();
        assertThat(harness.jobs.getAll()).doesNotContainKey(harness.cacheKey);
    }

    @Test
    public void testEngineIsNotSetAsResourceListenerIfResourceIsNullOnJobComplete() {
        harness.doLoad();
        /* resource= */
        harness.getEngine().onEngineJobComplete(harness.job, harness.cacheKey, null);
    }

    @Test
    public void testResourceIsAddedToActiveResourcesOnEngineComplete() {
        Mockito.when(harness.resource.isMemoryCacheable()).thenReturn(true);
        harness.callOnEngineJobComplete();
        EngineResource<?> resource = harness.activeResources.get(harness.cacheKey);
        assertThat(harness.resource).isEqualTo(resource);
    }

    @Test
    public void testDoesNotPutNullResourceInActiveResourcesOnEngineComplete() {
        /* resource= */
        harness.getEngine().onEngineJobComplete(harness.job, harness.cacheKey, null);
        assertThat(harness.activeResources.get(harness.cacheKey)).isNull();
    }

    @Test
    public void testDoesNotPutResourceThatIsNotCacheableInActiveResourcesOnEngineComplete() {
        Mockito.when(harness.resource.isMemoryCacheable()).thenReturn(false);
        harness.callOnEngineJobComplete();
        assertThat(harness.activeResources.get(harness.cacheKey)).isNull();
    }

    @Test
    public void testRunnerIsRemovedFromRunnersOnEngineNotifiedJobCancel() {
        harness.doLoad();
        harness.getEngine().onEngineJobCancelled(harness.job, harness.cacheKey);
        assertThat(harness.jobs.getAll()).doesNotContainKey(harness.cacheKey);
    }

    @Test
    public void testJobIsNotRemovedFromJobsIfOldJobIsCancelled() {
        harness.doLoad();
        harness.getEngine().onEngineJobCancelled(Mockito.mock(EngineJob.class), harness.cacheKey);
        Assert.assertEquals(harness.job, harness.jobs.get(harness.cacheKey, harness.onlyRetrieveFromCache));
    }

    @Test
    public void testResourceIsAddedToCacheOnReleased() {
        final Object expected = new Object();
        Mockito.when(harness.resource.isMemoryCacheable()).thenReturn(true);
        Mockito.when(harness.resource.get()).thenReturn(expected);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                Resource<?> resource = ((Resource<?>) (invocationOnMock.getArguments()[1]));
                Assert.assertEquals(expected, resource.get());
                return null;
            }
        }).when(harness.cache).put(ArgumentMatchers.eq(harness.cacheKey), Util.anyResource());
        harness.getEngine().onResourceReleased(harness.cacheKey, harness.resource);
        Mockito.verify(harness.cache).put(ArgumentMatchers.eq(harness.cacheKey), Util.anyResource());
    }

    @Test
    public void testResourceIsNotAddedToCacheOnReleasedIfNotCacheable() {
        Mockito.when(harness.resource.isMemoryCacheable()).thenReturn(false);
        harness.getEngine().onResourceReleased(harness.cacheKey, harness.resource);
        Mockito.verify(harness.cache, Mockito.never()).put(ArgumentMatchers.eq(harness.cacheKey), ArgumentMatchers.eq(harness.resource));
    }

    @Test
    public void testResourceIsRecycledIfNotCacheableWhenReleased() {
        Mockito.when(harness.resource.isMemoryCacheable()).thenReturn(false);
        harness.getEngine().onResourceReleased(harness.cacheKey, harness.resource);
        Mockito.verify(harness.resourceRecycler).recycle(ArgumentMatchers.eq(harness.resource));
    }

    @Test
    public void testResourceIsRemovedFromActiveResourcesWhenReleased() {
        harness.activeResources.activate(harness.cacheKey, harness.resource);
        harness.getEngine().onResourceReleased(harness.cacheKey, harness.resource);
        assertThat(harness.activeResources.get(harness.cacheKey)).isNull();
    }

    @Test
    public void testEngineAddedAsListenerToMemoryCache() {
        harness.getEngine();
        Mockito.verify(harness.cache).setResourceRemovedListener(ArgumentMatchers.eq(harness.getEngine()));
    }

    @Test
    public void testResourceIsRecycledWhenRemovedFromCache() {
        harness.getEngine().onResourceRemoved(harness.resource);
        Mockito.verify(harness.resourceRecycler).recycle(ArgumentMatchers.eq(harness.resource));
    }

    @Test
    public void testJobIsPutInJobWithCacheKeyWithRelevantIds() {
        harness.doLoad();
        assertThat(harness.jobs.getAll()).containsEntry(harness.cacheKey, harness.job);
    }

    @Test
    public void testKeyFactoryIsGivenNecessaryArguments() {
        harness.doLoad();
        Mockito.verify(harness.keyFactory).buildKey(ArgumentMatchers.eq(harness.model), ArgumentMatchers.eq(harness.signature), ArgumentMatchers.eq(harness.width), ArgumentMatchers.eq(harness.height), ArgumentMatchers.eq(harness.transformations), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(harness.options));
    }

    @Test
    public void testFactoryIsGivenNecessaryArguments() {
        harness.doLoad();
        /* isMemoryCacheable */
        /* useUnlimitedSourceGeneratorPool */
        /* useAnimationPool= */
        /* onlyRetrieveFromCache= */
        Mockito.verify(harness.engineJobFactory).build(ArgumentMatchers.eq(harness.cacheKey), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void testFactoryIsGivenNecessaryArgumentsWithUnlimitedPool() {
        harness.useUnlimitedSourceGeneratorPool = true;
        harness.doLoad();
        /* isMemoryCacheable */
        /* useUnlimitedSourceGeneratorPool */
        /* useAnimationPool= */
        /* onlyRetrieveFromCache= */
        Mockito.verify(harness.engineJobFactory).build(ArgumentMatchers.eq(harness.cacheKey), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }

    @Test
    public void testReleaseReleasesEngineResource() {
        EngineResource<Object> engineResource = Mockito.mock(EngineResource.class);
        harness.getEngine().release(engineResource);
        Mockito.verify(engineResource).release();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfAskedToReleaseNonEngineResource() {
        harness.getEngine().release(Util.mockResource());
    }

    @Test
    public void load_whenCalledOnBackgroundThread_doesNotThrow() throws InterruptedException {
        BackgroundUtil.testInBackground(new BackgroundUtil.BackgroundTester() {
            @Override
            public void runTest() {
                harness.doLoad();
            }
        });
    }

    @Test
    public void load_afterResourceIsLoadedInActiveResources_returnsFromMemoryCache() {
        Mockito.when(harness.resource.isMemoryCacheable()).thenReturn(true);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                harness.callOnEngineJobComplete();
                return null;
            }
        }).when(harness.job).start(ArgumentMatchers.any(DecodeJob.class));
        harness.doLoad();
        harness.doLoad();
        Mockito.verify(harness.cb).onResourceReady(ArgumentMatchers.any(Resource.class), ArgumentMatchers.eq(MEMORY_CACHE));
    }

    @Test
    public void load_afterResourceIsLoadedAndReleased_returnsFromMemoryCache() {
        harness.cache = new LruResourceCache(100);
        Mockito.when(harness.resource.isMemoryCacheable()).thenReturn(true);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) {
                harness.callOnEngineJobComplete();
                return null;
            }
        }).when(harness.job).start(ArgumentMatchers.any(DecodeJob.class));
        harness.doLoad();
        harness.getEngine().onResourceReleased(harness.cacheKey, harness.resource);
        harness.doLoad();
        Mockito.verify(harness.cb).onResourceReady(ArgumentMatchers.any(Resource.class), ArgumentMatchers.eq(MEMORY_CACHE));
    }

    @Test
    public void load_withOnlyRetrieveFromCache_andPreviousNormalLoad_startsNewLoad() {
        EngineJob<?> first = harness.job;
        harness.doLoad();
        EngineJob<?> second = Mockito.mock(EngineJob.class);
        harness.job = second;
        harness.onlyRetrieveFromCache = true;
        harness.doLoad();
        Mockito.verify(first).start(ArgumentMatchers.any(DecodeJob.class));
        Mockito.verify(second).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void load_withNormalLoad_afterPreviousRetrieveFromCache_startsNewLoad() {
        EngineJob<?> first = harness.job;
        harness.onlyRetrieveFromCache = true;
        harness.doLoad();
        EngineJob<?> second = Mockito.mock(EngineJob.class);
        harness.job = second;
        harness.onlyRetrieveFromCache = false;
        harness.doLoad();
        Mockito.verify(first).start(ArgumentMatchers.any(DecodeJob.class));
        Mockito.verify(second).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void load_afterFinishedOnlyRetrieveFromCache_withPendingNormal_doesNotStartNewLoad() {
        EngineJob<?> firstNormal = harness.job;
        harness.doLoad();
        harness.job = Mockito.mock(EngineJob.class);
        harness.onlyRetrieveFromCache = true;
        harness.doLoad();
        harness.callOnEngineJobComplete();
        EngineJob<?> secondNormal = Mockito.mock(EngineJob.class);
        harness.job = secondNormal;
        harness.onlyRetrieveFromCache = false;
        harness.doLoad();
        Mockito.verify(firstNormal).start(ArgumentMatchers.any(DecodeJob.class));
        Mockito.verify(secondNormal, Mockito.never()).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void load_afterCancelledOnlyRetrieveFromCache_withPendingNormal_doesNotStartNewLoad() {
        EngineJob<?> firstNormal = harness.job;
        harness.doLoad();
        harness.job = Mockito.mock(EngineJob.class);
        harness.onlyRetrieveFromCache = true;
        harness.doLoad();
        harness.getEngine().onEngineJobCancelled(harness.job, harness.cacheKey);
        EngineJob<?> secondNormal = Mockito.mock(EngineJob.class);
        harness.job = secondNormal;
        harness.onlyRetrieveFromCache = false;
        harness.doLoad();
        Mockito.verify(firstNormal).start(ArgumentMatchers.any(DecodeJob.class));
        Mockito.verify(secondNormal, Mockito.never()).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void load_withOnlyRetrieveFromCache_withOtherRetrieveFromCachePending_doesNotStartNew() {
        harness.onlyRetrieveFromCache = true;
        harness.doLoad();
        EngineJob<?> second = Mockito.mock(EngineJob.class);
        harness.job = second;
        harness.doLoad();
        Mockito.verify(second, Mockito.never()).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void load_withOnlyRetrieveFromCache_afterPreviousFinishedOnlyFromCacheLoad_startsNew() {
        harness.onlyRetrieveFromCache = true;
        harness.doLoad();
        harness.callOnEngineJobComplete();
        EngineJob<?> second = Mockito.mock(EngineJob.class);
        harness.job = second;
        harness.doLoad();
        Mockito.verify(second).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void load_withOnlyRetrieveFromCache_afterPreviousCancelledOnlyFromCacheLoad_startsNew() {
        harness.onlyRetrieveFromCache = true;
        harness.doLoad();
        harness.getEngine().onEngineJobCancelled(harness.job, harness.cacheKey);
        EngineJob<?> second = Mockito.mock(EngineJob.class);
        harness.job = second;
        harness.doLoad();
        Mockito.verify(second).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void onEngineJobComplete_withOldJobForKey_doesNotRemoveJob() {
        harness.doLoad();
        harness.getEngine().onEngineJobComplete(Mockito.mock(EngineJob.class), harness.cacheKey, harness.resource);
        harness.job = Mockito.mock(EngineJob.class);
        harness.doLoad();
        Mockito.verify(harness.job, Mockito.never()).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void onEngineJobCancelled_withOldJobForKey_doesNotRemoveJob() {
        harness.doLoad();
        harness.getEngine().onEngineJobCancelled(Mockito.mock(EngineJob.class), harness.cacheKey);
        harness.job = Mockito.mock(EngineJob.class);
        harness.doLoad();
        Mockito.verify(harness.job, Mockito.never()).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void onEngineJobComplete_withOnlyRetrieveFromCacheAndOldJobForKey_doesNotRemoveJob() {
        harness.onlyRetrieveFromCache = true;
        harness.doLoad();
        harness.getEngine().onEngineJobComplete(Mockito.mock(EngineJob.class), harness.cacheKey, harness.resource);
        harness.job = Mockito.mock(EngineJob.class);
        harness.doLoad();
        Mockito.verify(harness.job, Mockito.never()).start(ArgumentMatchers.any(DecodeJob.class));
    }

    @Test
    public void onEngineJobCancelled_withOnlyRetrieveFromCacheAndOldJobForKey_doesNotRemoveJob() {
        harness.onlyRetrieveFromCache = true;
        harness.doLoad();
        harness.getEngine().onEngineJobCancelled(Mockito.mock(EngineJob.class), harness.cacheKey);
        harness.job = Mockito.mock(EngineJob.class);
        harness.doLoad();
        Mockito.verify(harness.job, Mockito.never()).start(ArgumentMatchers.any(DecodeJob.class));
    }

    private static class EngineTestHarness {
        final EngineKey cacheKey = Mockito.mock(EngineKey.class);

        final EngineKeyFactory keyFactory = Mockito.mock(EngineKeyFactory.class);

        ResourceCallback cb = Mockito.mock(ResourceCallback.class);

        @SuppressWarnings("rawtypes")
        final EngineResource resource = Mockito.mock(EngineResource.class);

        final Jobs jobs = new Jobs();

        final ActiveResources activeResources = /* isActiveResourceRetentionAllowed= */
        new ActiveResources(true);

        final int width = 100;

        final int height = 100;

        final Object model = new Object();

        MemoryCache cache = Mockito.mock(MemoryCache.class);

        EngineJob<?> job;

        private Engine engine;

        final EngineJobFactory engineJobFactory = Mockito.mock(EngineJobFactory.class);

        final DecodeJobFactory decodeJobFactory = Mockito.mock(DecodeJobFactory.class);

        final ResourceRecycler resourceRecycler = Mockito.mock(ResourceRecycler.class);

        final Key signature = Mockito.mock(Key.class);

        final Map<Class<?>, Transformation<?>> transformations = new HashMap<>();

        final Options options = new Options();

        final GlideContext glideContext = Mockito.mock(GlideContext.class);

        boolean isMemoryCacheable = true;

        boolean useUnlimitedSourceGeneratorPool = false;

        boolean onlyRetrieveFromCache = false;

        final boolean isScaleOnlyOrNoTransform = true;

        EngineTestHarness() {
            Mockito.when(keyFactory.buildKey(ArgumentMatchers.eq(model), ArgumentMatchers.eq(signature), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(transformations), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(options))).thenReturn(cacheKey);
            Mockito.when(resource.getResource()).thenReturn(Mockito.mock(Resource.class));
            job = Mockito.mock(EngineJob.class);
        }

        void callOnEngineJobComplete() {
            getEngine().onEngineJobComplete(job, cacheKey, resource);
        }

        LoadStatus doLoad() {
            Mockito.when(engineJobFactory.build(ArgumentMatchers.eq(cacheKey), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(((EngineJob<Object>) (job)));
            Mockito.when(job.onlyRetrieveFromCache()).thenReturn(onlyRetrieveFromCache);
            return /* resourceClass */
            /* transcodeClass */
            /* isTransformationRequired */
            /* useAnimationPool= */
            getEngine().load(glideContext, model, signature, width, height, Object.class, Object.class, HIGH, ALL, transformations, false, isScaleOnlyOrNoTransform, options, isMemoryCacheable, useUnlimitedSourceGeneratorPool, false, onlyRetrieveFromCache, cb, Executors.directExecutor());
        }

        Engine getEngine() {
            if ((engine) == null) {
                engine = /* isActiveResourceRetentionAllowed= */
                new Engine(cache, Mockito.mock(Factory.class), GlideExecutor.newDiskCacheExecutor(), MockGlideExecutor.newMainThreadExecutor(), MockGlideExecutor.newMainThreadExecutor(), MockGlideExecutor.newMainThreadExecutor(), jobs, keyFactory, activeResources, engineJobFactory, decodeJobFactory, resourceRecycler, true);
            }
            return engine;
        }
    }
}

