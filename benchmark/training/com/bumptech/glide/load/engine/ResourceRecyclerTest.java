package com.bumptech.glide.load.engine;


import android.os.Looper;
import com.bumptech.glide.tests.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class ResourceRecyclerTest {
    private ResourceRecycler recycler;

    @Test
    public void testRecyclesResourceSynchronouslyIfNotAlreadyRecyclingResource() {
        Resource<?> resource = Util.mockResource();
        Shadows.shadowOf(Looper.getMainLooper()).pause();
        recycler.recycle(resource);
        Mockito.verify(resource).recycle();
    }

    @Test
    public void testDoesNotRecycleChildResourceSynchronously() {
        Resource<?> parent = Util.mockResource();
        final Resource<?> child = Util.mockResource();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                recycler.recycle(child);
                return null;
            }
        }).when(parent).recycle();
        Shadows.shadowOf(Looper.getMainLooper()).pause();
        recycler.recycle(parent);
        Mockito.verify(parent).recycle();
        Mockito.verify(child, Mockito.never()).recycle();
        Shadows.shadowOf(Looper.getMainLooper()).runOneTask();
        Mockito.verify(child).recycle();
    }
}

