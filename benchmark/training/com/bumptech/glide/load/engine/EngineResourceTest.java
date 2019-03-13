package com.bumptech.glide.load.engine;


import EngineResource.ResourceListener;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.tests.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class EngineResourceTest {
    private EngineResource<Object> engineResource;

    @Mock
    private ResourceListener listener;

    @Mock
    private Key cacheKey;

    @Mock
    private Resource<Object> resource;

    @Test
    public void testCanAcquireAndRelease() {
        engineResource.acquire();
        engineResource.release();
        Mockito.verify(listener).onResourceReleased(cacheKey, engineResource);
    }

    @Test
    public void testCanAcquireMultipleTimesAndRelease() {
        engineResource.acquire();
        engineResource.acquire();
        engineResource.release();
        engineResource.release();
        Mockito.verify(listener).onResourceReleased(ArgumentMatchers.eq(cacheKey), ArgumentMatchers.eq(engineResource));
    }

    @Test
    public void testDelegatesGetToWrappedResource() {
        Object expected = new Object();
        Mockito.when(resource.get()).thenReturn(expected);
        Assert.assertEquals(expected, engineResource.get());
    }

    @Test
    public void testDelegatesGetSizeToWrappedResource() {
        int expectedSize = 1234;
        Mockito.when(resource.getSize()).thenReturn(expectedSize);
        Assert.assertEquals(expectedSize, engineResource.getSize());
    }

    @Test
    public void testRecyclesWrappedResourceWhenRecycled() {
        engineResource.acquire();
        engineResource.release();
        engineResource.recycle();
        Mockito.verify(resource).recycle();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfRecycledTwice() {
        engineResource.recycle();
        engineResource.recycle();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfReleasedBeforeAcquired() {
        engineResource.release();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfRecycledWhileAcquired() {
        engineResource.acquire();
        engineResource.recycle();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfAcquiredAfterRecycled() {
        engineResource.recycle();
        engineResource.acquire();
    }

    @Test
    public void testThrowsIfAcquiredOnBackgroundThread() throws InterruptedException {
        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    engineResource.acquire();
                } catch (IllegalThreadStateException e) {
                    return;
                }
                Assert.fail("Failed to receive expected IllegalThreadStateException");
            }
        });
        otherThread.start();
        otherThread.join();
    }

    @Test
    public void testThrowsIfReleasedOnBackgroundThread() throws InterruptedException {
        engineResource.acquire();
        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    engineResource.release();
                } catch (IllegalThreadStateException e) {
                    return;
                }
                Assert.fail("Failed to receive expected IllegalThreadStateException");
            }
        });
        otherThread.start();
        otherThread.join();
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsIfReleasedMoreThanAcquired() {
        engineResource.acquire();
        engineResource.release();
        engineResource.release();
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfWrappedResourceIsNull() {
        /* toWrap= */
        /* isMemoryCacheable= */
        /* isRecyclable= */
        new EngineResource(null, false, true, cacheKey, listener);
    }

    @Test
    public void testCanSetAndGetIsCacheable() {
        engineResource = /* isMemoryCacheable= */
        /* isRecyclable= */
        new EngineResource(Util.mockResource(), true, true, cacheKey, listener);
        Assert.assertTrue(engineResource.isMemoryCacheable());
        engineResource = /* isMemoryCacheable= */
        /* isRecyclable= */
        new EngineResource(Util.mockResource(), false, true, cacheKey, listener);
        Assert.assertFalse(engineResource.isMemoryCacheable());
    }

    @Test
    public void release_whenNotRecycleable_doesNotRecycleResource() {
        resource = Util.mockResource();
        engineResource = /* isMemoryCacheable= */
        /* isRecyclable= */
        new EngineResource(resource, true, false, cacheKey, listener);
        engineResource.recycle();
        Mockito.verify(listener, Mockito.never()).onResourceReleased(ArgumentMatchers.any(Key.class), ArgumentMatchers.any(EngineResource.class));
        Mockito.verify(resource, Mockito.never()).recycle();
    }
}

