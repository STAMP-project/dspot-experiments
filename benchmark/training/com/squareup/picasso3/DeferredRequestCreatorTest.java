/**
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.picasso3;


import android.view.ViewTreeObserver;
import android.widget.ImageView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class DeferredRequestCreatorTest {
    @Captor
    ArgumentCaptor<Action> actionCaptor;

    @Test
    public void initWhileAttachedAddsAttachAndPreDrawListener() {
        ImageView target = TestUtils.mockFitImageViewTarget(true);
        ViewTreeObserver observer = target.getViewTreeObserver();
        DeferredRequestCreator request = new DeferredRequestCreator(Mockito.mock(RequestCreator.class), target, null);
        Mockito.verify(observer).addOnPreDrawListener(request);
    }

    @Test
    public void initWhileDetachedAddsAttachListenerWhichDefersPreDrawListener() {
        ImageView target = TestUtils.mockFitImageViewTarget(true);
        Mockito.when(target.getWindowToken()).thenReturn(null);
        ViewTreeObserver observer = target.getViewTreeObserver();
        DeferredRequestCreator request = new DeferredRequestCreator(Mockito.mock(RequestCreator.class), target, null);
        Mockito.verify(target).addOnAttachStateChangeListener(request);
        Mockito.verifyNoMoreInteractions(observer);
        // Attach and ensure we defer to the pre-draw listener.
        request.onViewAttachedToWindow(target);
        Mockito.verify(observer).addOnPreDrawListener(request);
        // Detach and ensure we remove the pre-draw listener from the original VTO.
        request.onViewDetachedFromWindow(target);
        Mockito.verify(observer).removeOnPreDrawListener(request);
    }

    @Test
    public void cancelWhileAttachedRemovesAttachListener() {
        ImageView target = TestUtils.mockFitImageViewTarget(true);
        DeferredRequestCreator request = new DeferredRequestCreator(Mockito.mock(RequestCreator.class), target, null);
        Mockito.verify(target).addOnAttachStateChangeListener(request);
        request.cancel();
        Mockito.verify(target).removeOnAttachStateChangeListener(request);
    }

    @Test
    public void cancelClearsCallback() {
        ImageView target = TestUtils.mockFitImageViewTarget(true);
        Callback callback = TestUtils.mockCallback();
        DeferredRequestCreator request = new DeferredRequestCreator(Mockito.mock(RequestCreator.class), target, callback);
        assertThat(request.callback).isNotNull();
        request.cancel();
        assertThat(request.callback).isNull();
    }

    @Test
    public void cancelClearsTag() {
        ImageView target = TestUtils.mockFitImageViewTarget(true);
        RequestCreator creator = Mockito.mock(RequestCreator.class);
        Mockito.when(creator.getTag()).thenReturn("TAG");
        DeferredRequestCreator request = new DeferredRequestCreator(creator, target, null);
        request.cancel();
        Mockito.verify(creator).clearTag();
    }

    @Test
    public void onLayoutSkipsIfViewIsAttachedAndViewTreeObserverIsDead() {
        ImageView target = TestUtils.mockFitImageViewTarget(false);
        RequestCreator creator = Mockito.mock(RequestCreator.class);
        DeferredRequestCreator request = new DeferredRequestCreator(creator, target, null);
        ViewTreeObserver viewTreeObserver = target.getViewTreeObserver();
        request.onPreDraw();
        Mockito.verify(viewTreeObserver).addOnPreDrawListener(request);
        Mockito.verify(viewTreeObserver).isAlive();
        Mockito.verifyNoMoreInteractions(viewTreeObserver);
        Mockito.verifyZeroInteractions(creator);
    }

    @Test
    public void waitsForAnotherLayoutIfWidthOrHeightIsZero() {
        ImageView target = TestUtils.mockFitImageViewTarget(true);
        Mockito.when(target.getWidth()).thenReturn(0);
        Mockito.when(target.getHeight()).thenReturn(0);
        RequestCreator creator = Mockito.mock(RequestCreator.class);
        DeferredRequestCreator request = new DeferredRequestCreator(creator, target, null);
        request.onPreDraw();
        Mockito.verify(target.getViewTreeObserver(), Mockito.never()).removeOnPreDrawListener(request);
        Mockito.verifyZeroInteractions(creator);
    }

    @Test
    public void cancelSkipsIfViewTreeObserverIsDead() {
        ImageView target = TestUtils.mockFitImageViewTarget(false);
        RequestCreator creator = Mockito.mock(RequestCreator.class);
        DeferredRequestCreator request = new DeferredRequestCreator(creator, target, null);
        request.cancel();
        Mockito.verify(target.getViewTreeObserver(), Mockito.never()).removeOnPreDrawListener(request);
    }

    @Test
    public void preDrawSubmitsRequestAndCleansUp() {
        Picasso picasso = Mockito.mock(Picasso.class);
        Mockito.when(picasso.transformRequest(ArgumentMatchers.any(Request.class))).thenAnswer(TestUtils.TRANSFORM_REQUEST_ANSWER);
        RequestCreator creator = new RequestCreator(picasso, TestUtils.URI_1, 0);
        ImageView target = TestUtils.mockFitImageViewTarget(true);
        Mockito.when(target.getWidth()).thenReturn(100);
        Mockito.when(target.getHeight()).thenReturn(100);
        ViewTreeObserver observer = target.getViewTreeObserver();
        DeferredRequestCreator request = new DeferredRequestCreator(creator, target, null);
        request.onPreDraw();
        Mockito.verify(observer).removeOnPreDrawListener(request);
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        Action value = actionCaptor.getValue();
        assertThat(value).isInstanceOf(ImageViewAction.class);
        assertThat(value.request.targetWidth).isEqualTo(100);
        assertThat(value.request.targetHeight).isEqualTo(100);
    }
}

