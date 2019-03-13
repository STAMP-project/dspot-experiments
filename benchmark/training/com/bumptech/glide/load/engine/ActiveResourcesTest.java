package com.bumptech.glide.load.engine;


import android.support.annotation.NonNull;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.ActiveResources.ResourceWeakReference;
import com.bumptech.glide.load.engine.EngineResource.ResourceListener;
import com.bumptech.glide.tests.GlideShadowLooper;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.com.bumptech.glide.util.Executors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(shadows = GlideShadowLooper.class)
public class ActiveResourcesTest {
    @Mock
    private ResourceListener listener;

    @Mock
    private Key key;

    @Mock
    private Resource<Object> resource;

    private ActiveResources resources;

    @Test
    public void get_withMissingKey_returnsNull() {
        assertThat(resources.get(key)).isNull();
    }

    @Test
    public void get_withActiveKey_returnsResource() {
        EngineResource<Object> expected = newCacheableEngineResource();
        resources.activate(key, expected);
        assertThat(resources.get(key)).isEqualTo(expected);
    }

    @Test
    public void get_withDeactivatedKey_returnsNull() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        resources.deactivate(key);
        assertThat(resources.get(key)).isNull();
    }

    @Test
    public void deactivate_withNotActiveKey_doesNotThrow() {
        resources.deactivate(key);
    }

    @Test
    public void get_withActiveAndClearedKey_returnsNull() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        resources.activeEngineResources.get(key).clear();
        assertThat(resources.get(key)).isNull();
    }

    @Test
    public void get_withActiveAndClearedKey_andCacheableResource_callsListenerWithWrappedResource() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        resources.activeEngineResources.get(key).clear();
        resources.get(key);
        ArgumentCaptor<EngineResource<?>> captor = ActiveResourcesTest.getEngineResourceCaptor();
        Mockito.verify(listener).onResourceReleased(ArgumentMatchers.eq(key), captor.capture());
        assertThat(captor.getValue().getResource()).isEqualTo(resource);
    }

    @Test
    public void get_withActiveAndClearedKey_andCacheableResource_callsListenerWithNotRecycleable() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        resources.activeEngineResources.get(key).clear();
        resources.get(key);
        ArgumentCaptor<EngineResource<?>> captor = ActiveResourcesTest.getEngineResourceCaptor();
        Mockito.verify(listener).onResourceReleased(ArgumentMatchers.eq(key), captor.capture());
        captor.getValue().recycle();
        Mockito.verify(resource, Mockito.never()).recycle();
    }

    @Test
    public void get_withActiveAndClearedKey_andCacheableResource_callsListenerWithCacheable() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        resources.activeEngineResources.get(key).clear();
        resources.get(key);
        ArgumentCaptor<EngineResource<?>> captor = ActiveResourcesTest.getEngineResourceCaptor();
        Mockito.verify(listener).onResourceReleased(ArgumentMatchers.eq(key), captor.capture());
        assertThat(captor.getValue().isMemoryCacheable()).isTrue();
    }

    @Test
    public void get_withActiveAndClearedKey_andNotCacheableResource_doesNotCallListener() {
        EngineResource<Object> engineResource = newNonCacheableEngineResource();
        resources.activate(key, engineResource);
        resources.activeEngineResources.get(key).clear();
        resources.get(key);
        Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
    }

    @Test
    public void queueIdle_afterResourceRemovedFromActive_doesNotCallListener() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        resources.deactivate(key);
        enqueueAndWaitForRef(weakRef);
        Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
    }

    @Test
    public void queueIdle_withCacheableResourceInActive_callListener() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        enqueueAndWaitForRef(weakRef);
        ArgumentCaptor<EngineResource<?>> captor = ActiveResourcesTest.getEngineResourceCaptor();
        Mockito.verify(listener).onResourceReleased(ArgumentMatchers.eq(key), captor.capture());
        EngineResource<?> released = captor.getValue();
        assertThat(released.getResource()).isEqualTo(resource);
        assertThat(released.isMemoryCacheable()).isTrue();
        released.recycle();
        Mockito.verify(resource, Mockito.never()).recycle();
    }

    @Test
    public void queueIdle_withNotCacheableResourceInActive_doesNotCallListener() {
        EngineResource<Object> engineResource = newNonCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        weakRef.enqueue();
        enqueueAndWaitForRef(weakRef);
        Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
    }

    @Test
    public void queueIdle_withCacheableResourceInActive_removesResourceFromActive() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        enqueueAndWaitForRef(weakRef);
        assertThat(resources.get(key)).isNull();
    }

    @Test
    public void queueIdle_withNotCacheableResourceInActive_removesResourceFromActive() {
        EngineResource<Object> engineResource = newNonCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        enqueueAndWaitForRef(weakRef);
        assertThat(resources.get(key)).isNull();
    }

    @Test
    public void get_withQueuedReference_returnsResource() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        weakRef.enqueue();
        assertThat(resources.get(key)).isEqualTo(engineResource);
    }

    @Test
    public void get_withQueuedReference_doesNotNotifyListener() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        weakRef.enqueue();
        Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
    }

    @Test
    public void queueIdle_withQueuedReferenceRetrievedFromGet_notifiesListener() {
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        resources.get(key);
        enqueueAndWaitForRef(weakRef);
        ArgumentCaptor<EngineResource<?>> captor = ActiveResourcesTest.getEngineResourceCaptor();
        Mockito.verify(listener).onResourceReleased(ArgumentMatchers.eq(key), captor.capture());
        assertThat(captor.getValue().getResource()).isEqualTo(resource);
    }

    @Test
    public void queueIdle_withQueuedReferenceRetrievedFromGetAndNotCacheable_doesNotNotifyListener() {
        EngineResource<Object> engineResource = newNonCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        CountDownLatch latch = getLatchForClearedRef();
        weakRef.enqueue();
        resources.get(key);
        waitForLatch(latch);
        Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
    }

    @Test
    public void queueIdle_withQueuedReferenceDeactivated_doesNotNotifyListener() {
        final ExecutorService delegate = Executors.newSingleThreadExecutor();
        try {
            final CountDownLatch blockExecutor = new CountDownLatch(1);
            resources = /* isActiveResourceRetentionAllowed= */
            new ActiveResources(true, new Executor() {
                @Override
                public void execute(@NonNull
                final Runnable command) {
                    delegate.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                blockExecutor.await();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            command.run();
                        }
                    });
                }
            });
            resources.setListener(listener);
            EngineResource<Object> engineResource = newCacheableEngineResource();
            resources.activate(key, engineResource);
            ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
            CountDownLatch latch = getLatchForClearedRef();
            weakRef.enqueue();
            resources.deactivate(key);
            blockExecutor.countDown();
            waitForLatch(latch);
            Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
        } finally {
            resources.shutdown();
            com.bumptech.glide.util.Executors.shutdownAndAwaitTermination(delegate);
        }
    }

    @Test
    public void queueIdle_afterReferenceQueuedThenReactivated_doesNotNotifyListener() {
        final ExecutorService delegate = Executors.newSingleThreadExecutor();
        try {
            final CountDownLatch blockExecutor = new CountDownLatch(1);
            resources = /* isActiveResourceRetentionAllowed= */
            new ActiveResources(true, new Executor() {
                @Override
                public void execute(@NonNull
                final Runnable command) {
                    delegate.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                blockExecutor.await();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            command.run();
                        }
                    });
                }
            });
            resources.setListener(listener);
            EngineResource<Object> first = newCacheableEngineResource();
            resources.activate(key, first);
            ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
            CountDownLatch latch = getLatchForClearedRef();
            weakRef.enqueue();
            EngineResource<Object> second = newCacheableEngineResource();
            resources.activate(key, second);
            blockExecutor.countDown();
            waitForLatch(latch);
            Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
        } finally {
            resources.shutdown();
            com.bumptech.glide.util.Executors.shutdownAndAwaitTermination(delegate);
        }
    }

    @Test
    public void activate_withNonCacheableResource_doesNotSaveResource() {
        EngineResource<Object> engineResource = newNonCacheableEngineResource();
        resources.activate(key, engineResource);
        assertThat(resources.activeEngineResources.get(key).resource).isNull();
    }

    @Test
    public void get_withActiveClearedKey_cacheableResource_retentionDisabled_doesNotCallListener() {
        resources = /* isActiveResourceRetentionAllowed= */
        new ActiveResources(false);
        resources.setListener(listener);
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        resources.activeEngineResources.get(key).clear();
        resources.get(key);
        Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
    }

    @Test
    public void get_withQueuedReference_retentionDisabled_returnsResource() {
        resources = /* isActiveResourceRetentionAllowed= */
        new ActiveResources(false);
        resources.setListener(listener);
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        weakRef.enqueue();
        assertThat(resources.get(key)).isEqualTo(engineResource);
    }

    @Test
    public void queueIdle_withQueuedReferenceRetrievedFromGet_retentionDisabled_doesNotNotify() {
        resources = /* isActiveResourceRetentionAllowed= */
        new ActiveResources(false);
        resources.setListener(listener);
        EngineResource<Object> engineResource = newCacheableEngineResource();
        resources.activate(key, engineResource);
        ResourceWeakReference weakRef = resources.activeEngineResources.get(key);
        CountDownLatch latch = getLatchForClearedRef();
        weakRef.enqueue();
        resources.get(key);
        waitForLatch(latch);
        Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
    }
}

