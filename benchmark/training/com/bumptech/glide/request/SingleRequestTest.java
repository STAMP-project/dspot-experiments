package com.bumptech.glide.request;


import DataSource.DATA_DISK_CACHE;
import DataSource.LOCAL;
import DataSource.MEMORY_CACHE;
import DataSource.REMOTE;
import Engine.LoadStatus;
import Priority.LOW;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.tests.Util;
import com.bumptech.glide.util.Executors;
import com.google.common.base.Equivalence;
import com.google.common.testing.EquivalenceTester;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
@SuppressWarnings("rawtypes")
public class SingleRequestTest {
    private SingleRequestTest.SingleRequestBuilder builder;

    @Mock
    private RequestListener<List> listener1;

    @Mock
    private RequestListener<List> listener2;

    @Test
    public void testIsNotCompleteBeforeReceivingResource() {
        SingleRequest<List> request = builder.build();
        Assert.assertFalse(request.isComplete());
    }

    @Test
    public void testCanHandleNullResources() {
        SingleRequest<List> request = builder.addRequestListener(listener1).build();
        request.onResourceReady(null, LOCAL);
        Assert.assertTrue(request.isFailed());
        Mockito.verify(listener1).onLoadFailed(SingleRequestTest.isAGlideException(), ArgumentMatchers.isA(Number.class), ArgumentMatchers.eq(builder.target), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testCanHandleEmptyResources() {
        SingleRequest<List> request = builder.addRequestListener(listener1).build();
        Mockito.when(builder.resource.get()).thenReturn(null);
        request.onResourceReady(builder.resource, REMOTE);
        Assert.assertTrue(request.isFailed());
        Mockito.verify(builder.engine).release(ArgumentMatchers.eq(builder.resource));
        Mockito.verify(listener1).onLoadFailed(SingleRequestTest.isAGlideException(), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testCanHandleNonConformingResources() {
        SingleRequest<List> request = builder.addRequestListener(listener1).build();
        Mockito.when(get()).thenReturn("Invalid mocked String, this should be a List");
        request.onResourceReady(builder.resource, DATA_DISK_CACHE);
        Assert.assertTrue(request.isFailed());
        Mockito.verify(builder.engine).release(ArgumentMatchers.eq(builder.resource));
        Mockito.verify(listener1).onLoadFailed(SingleRequestTest.isAGlideException(), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testIsNotFailedAfterClear() {
        SingleRequest<List> request = builder.build();
        request.onResourceReady(null, DATA_DISK_CACHE);
        request.clear();
        Assert.assertFalse(request.isFailed());
    }

    @Test
    public void testIsNotFailedAfterBegin() {
        SingleRequest<List> request = builder.build();
        request.onResourceReady(null, DATA_DISK_CACHE);
        request.begin();
        Assert.assertFalse(request.isFailed());
    }

    @Test
    public void testIsCompleteAfterReceivingResource() {
        SingleRequest<List> request = builder.build();
        request.onResourceReady(builder.resource, LOCAL);
        Assert.assertTrue(request.isComplete());
    }

    @Test
    public void testIsNotCompleteAfterClear() {
        SingleRequest<List> request = builder.build();
        request.onResourceReady(builder.resource, REMOTE);
        request.clear();
        Assert.assertFalse(request.isComplete());
    }

    @Test
    public void testIsCancelledAfterClear() {
        SingleRequest<List> request = builder.build();
        request.clear();
        Assert.assertTrue(request.isCleared());
    }

    @Test
    public void clear_notifiesTarget() {
        SingleRequest<List> request = builder.build();
        request.clear();
        Mockito.verify(builder.target).onLoadCleared(ArgumentMatchers.any(Drawable.class));
    }

    @Test
    public void testDoesNotNotifyTargetTwiceIfClearedTwiceInARow() {
        SingleRequest<List> request = builder.build();
        request.clear();
        request.clear();
        Mockito.verify(builder.target, Mockito.times(1)).onLoadCleared(ArgumentMatchers.any(Drawable.class));
    }

    @Test
    public void clear_doesNotNotifyTarget_ifRequestCoordinatorReturnsFalseForCanClear() {
        Mockito.when(builder.requestCoordinator.canNotifyCleared(ArgumentMatchers.any(Request.class))).thenReturn(false);
        SingleRequest<List> request = builder.build();
        request.clear();
        Mockito.verify(builder.target, Mockito.never()).onLoadCleared(ArgumentMatchers.any(Drawable.class));
    }

    @Test
    public void testResourceIsNotCompleteWhenAskingCoordinatorIfCanSetImage() {
        RequestCoordinator requestCoordinator = Mockito.mock(RequestCoordinator.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Request request = ((Request) (invocation.getArguments()[0]));
                Assert.assertFalse(request.isComplete());
                return true;
            }
        }).when(requestCoordinator).canSetImage(ArgumentMatchers.any(Request.class));
        SingleRequest<List> request = builder.setRequestCoordinator(requestCoordinator).build();
        request.onResourceReady(builder.resource, DATA_DISK_CACHE);
        Mockito.verify(requestCoordinator).canSetImage(ArgumentMatchers.eq(request));
    }

    @Test
    public void testIsNotFailedWithoutException() {
        SingleRequest<List> request = builder.build();
        Assert.assertFalse(request.isFailed());
    }

    @Test
    public void testIsFailedAfterException() {
        SingleRequest<List> request = builder.build();
        request.onLoadFailed(new GlideException("test"));
        Assert.assertTrue(request.isFailed());
    }

    @Test
    public void testIgnoresOnSizeReadyIfNotWaitingForSize() {
        SingleRequest<List> request = builder.build();
        request.begin();
        request.onSizeReady(100, 100);
        request.onSizeReady(100, 100);
        /* useAnimationPool= */
        Mockito.verify(builder.engine, Mockito.times(1)).load(ArgumentMatchers.eq(builder.glideContext), ArgumentMatchers.eq(builder.model), ArgumentMatchers.eq(builder.signature), ArgumentMatchers.eq(100), ArgumentMatchers.eq(100), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(List.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.any(DiskCacheStrategy.class), ArgumentMatchers.eq(builder.transformations), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Options.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ResourceCallback.class), SingleRequestTest.anyExecutor());
    }

    @Test
    public void testIsFailedAfterNoResultAndNullException() {
        SingleRequest<List> request = builder.build();
        request.onLoadFailed(new GlideException("test"));
        Assert.assertTrue(request.isFailed());
    }

    @Test
    public void testEngineLoadCancelledOnCancel() {
        Engine.LoadStatus loadStatus = Mockito.mock(LoadStatus.class);
        Mockito.when(builder.engine.load(ArgumentMatchers.eq(builder.glideContext), ArgumentMatchers.eq(builder.model), ArgumentMatchers.eq(builder.signature), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(List.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.any(DiskCacheStrategy.class), ArgumentMatchers.eq(builder.transformations), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Options.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ResourceCallback.class), SingleRequestTest.anyExecutor())).thenReturn(loadStatus);
        SingleRequest<List> request = builder.build();
        request.begin();
        request.onSizeReady(100, 100);
        request.clear();
        Mockito.verify(loadStatus).cancel();
    }

    @Test
    public void testResourceIsRecycledOnClear() {
        SingleRequest<List> request = builder.build();
        request.onResourceReady(builder.resource, REMOTE);
        request.clear();
        Mockito.verify(builder.engine).release(ArgumentMatchers.eq(builder.resource));
    }

    @Test
    public void testPlaceholderDrawableIsSet() {
        Drawable expected = new android.graphics.drawable.ColorDrawable(Color.RED);
        SingleRequestTest.MockTarget target = new SingleRequestTest.MockTarget();
        SingleRequest<List> request = builder.setPlaceholderDrawable(expected).setTarget(target).build();
        request.begin();
        assertThat(target.currentPlaceholder).isEqualTo(expected);
    }

    @Test
    public void testErrorDrawableIsSetOnLoadFailed() {
        Drawable expected = new android.graphics.drawable.ColorDrawable(Color.RED);
        SingleRequestTest.MockTarget target = new SingleRequestTest.MockTarget();
        SingleRequest<List> request = builder.setErrorDrawable(expected).setTarget(target).build();
        request.onLoadFailed(new GlideException("test"));
        assertThat(target.currentPlaceholder).isEqualTo(expected);
    }

    @Test
    public void testPlaceholderDrawableSetOnNullModelWithNoErrorDrawable() {
        Drawable placeholder = new android.graphics.drawable.ColorDrawable(Color.RED);
        SingleRequestTest.MockTarget target = new SingleRequestTest.MockTarget();
        SingleRequest<List> request = builder.setErrorDrawable(placeholder).setTarget(target).setModel(null).build();
        request.begin();
        assertThat(target.currentPlaceholder).isEqualTo(placeholder);
    }

    @Test
    public void testErrorDrawableSetOnNullModelWithErrorDrawable() {
        Drawable placeholder = new android.graphics.drawable.ColorDrawable(Color.RED);
        Drawable errorPlaceholder = new android.graphics.drawable.ColorDrawable(Color.GREEN);
        SingleRequestTest.MockTarget target = new SingleRequestTest.MockTarget();
        SingleRequest<List> request = builder.setPlaceholderDrawable(placeholder).setErrorDrawable(errorPlaceholder).setTarget(target).setModel(null).build();
        request.begin();
        assertThat(target.currentPlaceholder).isEqualTo(errorPlaceholder);
    }

    @Test
    public void testFallbackDrawableSetOnNullModelWithErrorAndFallbackDrawables() {
        Drawable placeholder = new android.graphics.drawable.ColorDrawable(Color.RED);
        Drawable errorPlaceholder = new android.graphics.drawable.ColorDrawable(Color.GREEN);
        Drawable fallback = new android.graphics.drawable.ColorDrawable(Color.BLUE);
        SingleRequestTest.MockTarget target = new SingleRequestTest.MockTarget();
        SingleRequest<List> request = builder.setPlaceholderDrawable(placeholder).setErrorDrawable(errorPlaceholder).setFallbackDrawable(fallback).setTarget(target).setModel(null).build();
        request.begin();
        assertThat(target.currentPlaceholder).isEqualTo(fallback);
    }

    @Test
    public void testIsNotRunningBeforeRunCalled() {
        Assert.assertFalse(builder.build().isRunning());
    }

    @Test
    public void testIsRunningAfterRunCalled() {
        Request request = builder.build();
        request.begin();
        Assert.assertTrue(request.isRunning());
    }

    @Test
    public void testIsNotRunningAfterComplete() {
        SingleRequest<List> request = builder.build();
        request.begin();
        request.onResourceReady(builder.resource, REMOTE);
        Assert.assertFalse(request.isRunning());
    }

    @Test
    public void testIsNotRunningAfterFailing() {
        SingleRequest<List> request = builder.build();
        request.begin();
        request.onLoadFailed(new GlideException("test"));
        Assert.assertFalse(request.isRunning());
    }

    @Test
    public void testIsNotRunningAfterClear() {
        SingleRequest<List> request = builder.build();
        request.begin();
        request.clear();
        Assert.assertFalse(request.isRunning());
    }

    @Test
    public void testCallsTargetOnResourceReadyIfNoRequestListener() {
        SingleRequest<List> request = builder.build();
        request.onResourceReady(builder.resource, LOCAL);
        Mockito.verify(builder.target).onResourceReady(ArgumentMatchers.eq(builder.result), SingleRequestTest.anyTransition());
    }

    @Test
    public void testCallsTargetOnResourceReadyIfAllRequestListenersReturnFalse() {
        SingleRequest<List> request = builder.addRequestListener(listener1).addRequestListener(listener2).build();
        Mockito.when(listener1.onResourceReady(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), Util.isADataSource(), ArgumentMatchers.anyBoolean())).thenReturn(false);
        Mockito.when(listener2.onResourceReady(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), Util.isADataSource(), ArgumentMatchers.anyBoolean())).thenReturn(false);
        request.onResourceReady(builder.resource, LOCAL);
        Mockito.verify(builder.target).onResourceReady(ArgumentMatchers.eq(builder.result), SingleRequestTest.anyTransition());
    }

    @Test
    public void testDoesNotCallTargetOnResourceReadyIfAnyRequestListenerReturnsTrue() {
        SingleRequest<List> request = builder.addRequestListener(listener1).addRequestListener(listener2).build();
        Mockito.when(listener1.onResourceReady(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), Util.isADataSource(), ArgumentMatchers.anyBoolean())).thenReturn(false);
        Mockito.when(listener1.onResourceReady(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), Util.isADataSource(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        request.onResourceReady(builder.resource, REMOTE);
        Mockito.verify(builder.target, Mockito.never()).onResourceReady(ArgumentMatchers.any(List.class), SingleRequestTest.anyTransition());
    }

    @Test
    public void testCallsTargetOnExceptionIfNoRequestListener() {
        SingleRequest<List> request = builder.build();
        request.onLoadFailed(new GlideException("test"));
        Mockito.verify(builder.target).onLoadFailed(ArgumentMatchers.eq(builder.errorDrawable));
    }

    @Test
    public void testCallsTargetOnExceptionIfAllRequestListenersReturnFalse() {
        SingleRequest<List> request = builder.addRequestListener(listener1).addRequestListener(listener2).build();
        Mockito.when(listener1.onLoadFailed(SingleRequestTest.isAGlideException(), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), ArgumentMatchers.anyBoolean())).thenReturn(false);
        Mockito.when(listener2.onLoadFailed(SingleRequestTest.isAGlideException(), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), ArgumentMatchers.anyBoolean())).thenReturn(false);
        request.onLoadFailed(new GlideException("test"));
        Mockito.verify(builder.target).onLoadFailed(ArgumentMatchers.eq(builder.errorDrawable));
    }

    @Test
    public void testDoesNotCallTargetOnExceptionIfAnyRequestListenerReturnsTrue() {
        SingleRequest<List> request = builder.addRequestListener(listener1).addRequestListener(listener2).build();
        Mockito.when(listener1.onLoadFailed(SingleRequestTest.isAGlideException(), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), ArgumentMatchers.anyBoolean())).thenReturn(false);
        Mockito.when(listener2.onLoadFailed(SingleRequestTest.isAGlideException(), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), ArgumentMatchers.anyBoolean())).thenReturn(true);
        request.onLoadFailed(new GlideException("test"));
        Mockito.verify(builder.target, Mockito.never()).onLoadFailed(ArgumentMatchers.any(Drawable.class));
    }

    @Test
    public void testRequestListenerIsCalledWithResourceResult() {
        SingleRequest<List> request = builder.addRequestListener(listener1).build();
        request.onResourceReady(builder.resource, DATA_DISK_CACHE);
        Mockito.verify(listener1).onResourceReady(ArgumentMatchers.eq(builder.result), ArgumentMatchers.any(Number.class), SingleRequestTest.isAListTarget(), Util.isADataSource(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testRequestListenerIsCalledWithModel() {
        SingleRequest<List> request = builder.addRequestListener(listener1).build();
        request.onResourceReady(builder.resource, DATA_DISK_CACHE);
        Mockito.verify(listener1).onResourceReady(ArgumentMatchers.any(List.class), ArgumentMatchers.eq(builder.model), SingleRequestTest.isAListTarget(), Util.isADataSource(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testRequestListenerIsCalledWithTarget() {
        SingleRequest<List> request = builder.addRequestListener(listener1).build();
        request.onResourceReady(builder.resource, DATA_DISK_CACHE);
        Mockito.verify(listener1).onResourceReady(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Number.class), ArgumentMatchers.eq(builder.target), Util.isADataSource(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testRequestListenerIsCalledWithLoadedFromMemoryIfLoadCompletesSynchronously() {
        final SingleRequest<List> request = builder.addRequestListener(listener1).build();
        Mockito.when(/* useAnimationPool= */
        builder.engine.load(ArgumentMatchers.eq(builder.glideContext), ArgumentMatchers.eq(builder.model), ArgumentMatchers.eq(builder.signature), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(List.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.any(DiskCacheStrategy.class), ArgumentMatchers.eq(builder.transformations), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Options.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ResourceCallback.class), SingleRequestTest.anyExecutor())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                request.onResourceReady(builder.resource, MEMORY_CACHE);
                return null;
            }
        });
        request.begin();
        request.onSizeReady(100, 100);
        Mockito.verify(listener1).onResourceReady(ArgumentMatchers.eq(builder.result), ArgumentMatchers.any(Number.class), SingleRequestTest.isAListTarget(), ArgumentMatchers.eq(MEMORY_CACHE), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testRequestListenerIsCalledWithNotLoadedFromMemoryCacheIfLoadCompletesAsynchronously() {
        SingleRequest<List> request = builder.addRequestListener(listener1).build();
        request.onSizeReady(100, 100);
        request.onResourceReady(builder.resource, LOCAL);
        Mockito.verify(listener1).onResourceReady(ArgumentMatchers.eq(builder.result), ArgumentMatchers.any(Number.class), SingleRequestTest.isAListTarget(), ArgumentMatchers.eq(LOCAL), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testRequestListenerIsCalledWithIsFirstResourceIfNoRequestCoordinator() {
        SingleRequest<List> request = builder.setRequestCoordinator(null).addRequestListener(listener1).build();
        request.onResourceReady(builder.resource, DATA_DISK_CACHE);
        Mockito.verify(listener1).onResourceReady(ArgumentMatchers.eq(builder.result), ArgumentMatchers.any(Number.class), SingleRequestTest.isAListTarget(), Util.isADataSource(), ArgumentMatchers.eq(true));
    }

    @Test
    public void testRequestListenerIsCalledWithFirstImageIfRequestCoordinatorReturnsNoResourceSet() {
        SingleRequest<List> request = builder.addRequestListener(listener1).build();
        Mockito.when(builder.requestCoordinator.isAnyResourceSet()).thenReturn(false);
        request.onResourceReady(builder.resource, DATA_DISK_CACHE);
        Mockito.verify(listener1).onResourceReady(ArgumentMatchers.eq(builder.result), ArgumentMatchers.any(Number.class), SingleRequestTest.isAListTarget(), Util.isADataSource(), ArgumentMatchers.eq(true));
    }

    @Test
    public void testRequestListenerIsCalledWithNotIsFirstRequestIfRequestCoordinatorReturnsResourceSet() {
        SingleRequest<List> request = builder.addRequestListener(listener1).build();
        Mockito.when(builder.requestCoordinator.isAnyResourceSet()).thenReturn(true);
        request.onResourceReady(builder.resource, DATA_DISK_CACHE);
        Mockito.verify(listener1).onResourceReady(ArgumentMatchers.eq(builder.result), ArgumentMatchers.any(Number.class), SingleRequestTest.isAListTarget(), Util.isADataSource(), ArgumentMatchers.eq(false));
    }

    @Test
    public void testTargetIsCalledWithAnimationFromFactory() {
        SingleRequest<List> request = builder.build();
        Transition<List> transition = SingleRequestTest.mockTransition();
        Mockito.when(builder.transitionFactory.build(ArgumentMatchers.any(DataSource.class), ArgumentMatchers.anyBoolean())).thenReturn(transition);
        request.onResourceReady(builder.resource, DATA_DISK_CACHE);
        Mockito.verify(builder.target).onResourceReady(ArgumentMatchers.eq(builder.result), ArgumentMatchers.eq(transition));
    }

    @Test
    public void testCallsGetSizeIfOverrideWidthIsLessThanZero() {
        SingleRequest<List> request = builder.setOverrideWidth((-1)).setOverrideHeight(100).build();
        request.begin();
        Mockito.verify(builder.target).getSize(ArgumentMatchers.any(SizeReadyCallback.class));
    }

    @Test
    public void testCallsGetSizeIfOverrideHeightIsLessThanZero() {
        SingleRequest<List> request = builder.setOverrideWidth(100).setOverrideHeight((-1)).build();
        request.begin();
        Mockito.verify(builder.target).getSize(ArgumentMatchers.any(SizeReadyCallback.class));
    }

    @Test
    public void testDoesNotCallGetSizeIfOverrideWidthAndHeightAreSet() {
        SingleRequest<List> request = builder.setOverrideWidth(100).setOverrideHeight(100).build();
        request.begin();
        Mockito.verify(builder.target, Mockito.never()).getSize(ArgumentMatchers.any(SizeReadyCallback.class));
    }

    @Test
    public void testCallsEngineWithOverrideWidthAndHeightIfSet() {
        SingleRequest<List> request = builder.setOverrideWidth(1).setOverrideHeight(2).build();
        request.begin();
        /* useAnimationPool= */
        Mockito.verify(builder.engine).load(ArgumentMatchers.eq(builder.glideContext), ArgumentMatchers.eq(builder.model), ArgumentMatchers.eq(builder.signature), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(List.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.any(DiskCacheStrategy.class), ArgumentMatchers.eq(builder.transformations), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Options.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ResourceCallback.class), SingleRequestTest.anyExecutor());
    }

    @Test
    public void testDoesNotSetErrorDrawableIfRequestCoordinatorDoesntAllowIt() {
        SingleRequest<List> request = builder.setErrorDrawable(new android.graphics.drawable.ColorDrawable(Color.RED)).build();
        Mockito.when(builder.requestCoordinator.canNotifyStatusChanged(ArgumentMatchers.any(Request.class))).thenReturn(false);
        request.onLoadFailed(new GlideException("test"));
        Mockito.verify(builder.target, Mockito.never()).onLoadFailed(ArgumentMatchers.any(Drawable.class));
    }

    @Test
    public void testCanReRunClearedRequests() {
        Mockito.doAnswer(new SingleRequestTest.CallSizeReady(100, 100)).when(builder.target).getSize(ArgumentMatchers.any(SizeReadyCallback.class));
        Mockito.when(/* useAnimationPool= */
        builder.engine.load(ArgumentMatchers.eq(builder.glideContext), ArgumentMatchers.eq(builder.model), ArgumentMatchers.eq(builder.signature), ArgumentMatchers.eq(100), ArgumentMatchers.eq(100), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(List.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.any(DiskCacheStrategy.class), ArgumentMatchers.eq(builder.transformations), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Options.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ResourceCallback.class), SingleRequestTest.anyExecutor())).thenAnswer(new SingleRequestTest.CallResourceCallback(builder.resource));
        SingleRequest<List> request = builder.build();
        request.begin();
        request.clear();
        request.begin();
        Mockito.verify(builder.target, Mockito.times(2)).onResourceReady(ArgumentMatchers.eq(builder.result), SingleRequestTest.anyTransition());
    }

    @Test
    public void testResourceOnlyReceivesOneGetOnResourceReady() {
        SingleRequest<List> request = builder.build();
        request.onResourceReady(builder.resource, LOCAL);
        get();
    }

    @Test
    public void testDoesNotStartALoadIfOnSizeReadyIsCalledAfterClear() {
        SingleRequest<List> request = builder.build();
        request.clear();
        request.onSizeReady(100, 100);
        /* useAnimationPool= */
        Mockito.verify(builder.engine, Mockito.never()).load(ArgumentMatchers.eq(builder.glideContext), ArgumentMatchers.eq(builder.model), ArgumentMatchers.eq(builder.signature), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(List.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.any(DiskCacheStrategy.class), ArgumentMatchers.eq(builder.transformations), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Options.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ResourceCallback.class), SingleRequestTest.anyExecutor());
    }

    @Test
    public void testCallsSourceUnlimitedExecutorEngineIfOptionsIsSet() {
        Mockito.doAnswer(new SingleRequestTest.CallSizeReady(100, 100)).when(builder.target).getSize(ArgumentMatchers.any(SizeReadyCallback.class));
        SingleRequest<List> request = builder.setUseUnlimitedSourceGeneratorsPool(true).build();
        request.begin();
        /* useAnimationPool= */
        Mockito.verify(builder.engine).load(ArgumentMatchers.eq(builder.glideContext), ArgumentMatchers.eq(builder.model), ArgumentMatchers.eq(builder.signature), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(List.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.any(DiskCacheStrategy.class), ArgumentMatchers.eq(builder.transformations), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Options.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(true), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ResourceCallback.class), SingleRequestTest.anyExecutor());
    }

    @Test
    public void testCallsSourceExecutorEngineIfOptionsIsSet() {
        Mockito.doAnswer(new SingleRequestTest.CallSizeReady(100, 100)).when(builder.target).getSize(ArgumentMatchers.any(SizeReadyCallback.class));
        SingleRequest<List> request = builder.setUseUnlimitedSourceGeneratorsPool(false).build();
        request.begin();
        /* useAnimationPool= */
        Mockito.verify(builder.engine).load(ArgumentMatchers.eq(builder.glideContext), ArgumentMatchers.eq(builder.model), ArgumentMatchers.eq(builder.signature), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(Object.class), ArgumentMatchers.eq(List.class), ArgumentMatchers.any(Priority.class), ArgumentMatchers.any(DiskCacheStrategy.class), ArgumentMatchers.eq(builder.transformations), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(Options.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(false), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ResourceCallback.class), SingleRequestTest.anyExecutor());
    }

    // Varargs
    @Test
    @SuppressWarnings("unchecked")
    public void testIsEquivalentTo() {
        EquivalenceTester<SingleRequestTest.SingleRequestBuilder> tester = EquivalenceTester.of(new Equivalence<SingleRequestTest.SingleRequestBuilder>() {
            @Override
            protected boolean doEquivalent(@NonNull
            SingleRequestTest.SingleRequestBuilder a, @NonNull
            SingleRequestTest.SingleRequestBuilder b) {
                return (a.build().isEquivalentTo(b.build())) && (b.build().isEquivalentTo(a.build()));
            }

            @Override
            protected int doHash(@NonNull
            SingleRequestTest.SingleRequestBuilder listSingleRequest) {
                return 0;
            }
        });
        // Non-null request listeners are treated as equivalent, even if they're not equal.
        tester.addEquivalenceGroup(new SingleRequestTest.SingleRequestBuilder().addRequestListener(listener1), new SingleRequestTest.SingleRequestBuilder().addRequestListener(listener2)).addEquivalenceGroup(new SingleRequestTest.SingleRequestBuilder().setOverrideHeight(500), new SingleRequestTest.SingleRequestBuilder().setOverrideHeight(500)).addEquivalenceGroup(new SingleRequestTest.SingleRequestBuilder().setOverrideWidth(500), new SingleRequestTest.SingleRequestBuilder().setOverrideWidth(500)).addEquivalenceGroup(new SingleRequestTest.SingleRequestBuilder().setModel(12345), new SingleRequestTest.SingleRequestBuilder().setModel(12345)).addEquivalenceGroup(new SingleRequestTest.SingleRequestBuilder().setModel(null), new SingleRequestTest.SingleRequestBuilder().setModel(null)).addEquivalenceGroup(new SingleRequestTest.SingleRequestBuilder().setPriority(LOW), new SingleRequestTest.SingleRequestBuilder().setPriority(LOW)).test();
    }

    static final class SingleRequestBuilder {
        private Engine engine = Mockito.mock(Engine.class);

        private Number model = 123456;

        @SuppressWarnings("unchecked")
        private Target<List> target = Mockito.mock(Target.class);

        private Resource<List> resource = Util.mockResource();

        private RequestCoordinator requestCoordinator = Mockito.mock(RequestCoordinator.class);

        private Drawable placeholderDrawable = null;

        private Drawable errorDrawable = null;

        private Drawable fallbackDrawable = null;

        @SuppressWarnings("unchecked")
        private List<RequestListener<List>> requestListeners = new ArrayList<>();

        @SuppressWarnings("unchecked")
        private final TransitionFactory<List> transitionFactory = Mockito.mock(TransitionFactory.class);

        private int overrideWidth = -1;

        private int overrideHeight = -1;

        private List<?> result = new ArrayList<>();

        private final GlideContext glideContext = Mockito.mock(GlideContext.class);

        private final Key signature = new ObjectKey(12345);

        private Priority priority = Priority.HIGH;

        private boolean useUnlimitedSourceGeneratorsPool = false;

        private final Class<List> transcodeClass = List.class;

        private final Map<Class<?>, Transformation<?>> transformations = new HashMap<>();

        SingleRequestBuilder() {
            Mockito.when(requestCoordinator.canSetImage(ArgumentMatchers.any(Request.class))).thenReturn(true);
            Mockito.when(requestCoordinator.canNotifyCleared(ArgumentMatchers.any(Request.class))).thenReturn(true);
            Mockito.when(requestCoordinator.canNotifyStatusChanged(ArgumentMatchers.any(Request.class))).thenReturn(true);
            Mockito.when(resource.get()).thenReturn(result);
        }

        SingleRequestTest.SingleRequestBuilder setEngine(Engine engine) {
            this.engine = engine;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setModel(Number model) {
            this.model = model;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setTarget(Target<List> target) {
            this.target = target;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setResource(Resource<List> resource) {
            this.resource = resource;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setRequestCoordinator(RequestCoordinator requestCoordinator) {
            this.requestCoordinator = requestCoordinator;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setPlaceholderDrawable(Drawable placeholderDrawable) {
            this.placeholderDrawable = placeholderDrawable;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setErrorDrawable(Drawable errorDrawable) {
            this.errorDrawable = errorDrawable;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setFallbackDrawable(Drawable fallbackDrawable) {
            this.fallbackDrawable = fallbackDrawable;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder addRequestListener(RequestListener<List> requestListener) {
            this.requestListeners.add(requestListener);
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setOverrideWidth(int overrideWidth) {
            this.overrideWidth = overrideWidth;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setOverrideHeight(int overrideHeight) {
            this.overrideHeight = overrideHeight;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setResult(List<?> result) {
            this.result = result;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setPriority(Priority priority) {
            this.priority = priority;
            return this;
        }

        SingleRequestTest.SingleRequestBuilder setUseUnlimitedSourceGeneratorsPool(boolean useUnlimitedSourceGeneratorsPool) {
            this.useUnlimitedSourceGeneratorsPool = useUnlimitedSourceGeneratorsPool;
            return this;
        }

        SingleRequest<List> build() {
            RequestOptions requestOptions = new RequestOptions().error(errorDrawable).placeholder(placeholderDrawable).fallback(fallbackDrawable).override(overrideWidth, overrideHeight).priority(priority).signature(signature).useUnlimitedSourceGeneratorsPool(useUnlimitedSourceGeneratorsPool);
            return /* context= */
            /* glideContext= */
            /* targetListener= */
            SingleRequest.obtain(glideContext, glideContext, model, transcodeClass, requestOptions, overrideWidth, overrideHeight, priority, target, null, requestListeners, requestCoordinator, engine, transitionFactory, Executors.directExecutor());
        }
    }

    private static class CallResourceCallback implements Answer {
        private final Resource resource;

        CallResourceCallback(Resource resource) {
            this.resource = resource;
        }

        @Override
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            ResourceCallback cb = ((ResourceCallback) (invocationOnMock.getArguments()[((invocationOnMock.getArguments().length) - 2)]));
            cb.onResourceReady(resource, REMOTE);
            return null;
        }
    }

    private static class CallSizeReady implements Answer {
        private final int width;

        private final int height;

        CallSizeReady(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            SizeReadyCallback cb = ((SizeReadyCallback) (invocationOnMock.getArguments()[0]));
            cb.onSizeReady(width, height);
            return null;
        }
    }

    private static class MockTarget implements Target<List> {
        private Drawable currentPlaceholder;

        @Override
        public void onLoadCleared(@Nullable
        Drawable placeholder) {
            currentPlaceholder = placeholder;
        }

        @Override
        public void onLoadStarted(@Nullable
        Drawable placeholder) {
            currentPlaceholder = placeholder;
        }

        @Override
        public void onLoadFailed(@Nullable
        Drawable errorDrawable) {
            currentPlaceholder = errorDrawable;
        }

        @Override
        public void onResourceReady(@NonNull
        List resource, @Nullable
        Transition<? super List> transition) {
            currentPlaceholder = null;
        }

        @Override
        public void getSize(@NonNull
        SizeReadyCallback cb) {
        }

        @Override
        public void removeCallback(@NonNull
        SizeReadyCallback cb) {
            // Do nothing.
        }

        @Override
        public void setRequest(@Nullable
        Request request) {
        }

        @Nullable
        @Override
        public Request getRequest() {
            return null;
        }

        @Override
        public void onStart() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onDestroy() {
        }
    }
}

