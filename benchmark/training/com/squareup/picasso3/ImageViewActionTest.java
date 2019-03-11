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


import Bitmap.Config;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static LoadedFrom.MEMORY;


@RunWith(RobolectricTestRunner.class)
public class ImageViewActionTest {
    @Test(expected = AssertionError.class)
    public void throwsErrorWithNullResult() {
        ImageView target = TestUtils.mockImageViewTarget();
        ImageViewAction action = new ImageViewAction(Mockito.mock(Picasso.class), target, null, null, 0, false, null);
        action.complete(null);
    }

    @Test
    public void invokesTargetAndCallbackSuccessIfTargetIsNotNull() {
        Bitmap bitmap = TestUtils.makeBitmap();
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        PlatformLruCache cache = new PlatformLruCache(0);
        Picasso picasso = new Picasso(RuntimeEnvironment.application, dispatcher, TestUtils.UNUSED_CALL_FACTORY, null, cache, null, TestUtils.NO_TRANSFORMERS, TestUtils.NO_HANDLERS, Mockito.mock(Stats.class), Config.ARGB_8888, false, false);
        ImageView target = TestUtils.mockImageViewTarget();
        Callback callback = TestUtils.mockCallback();
        ImageViewAction request = new ImageViewAction(picasso, target, null, null, 0, false, callback);
        request.complete(new RequestHandler.Result(bitmap, MEMORY));
        Mockito.verify(target).setImageDrawable(ArgumentMatchers.any(PicassoDrawable.class));
        Mockito.verify(callback).onSuccess();
    }

    @Test
    public void invokesTargetAndCallbackErrorIfTargetIsNotNullWithErrorResourceId() {
        ImageView target = TestUtils.mockImageViewTarget();
        Callback callback = TestUtils.mockCallback();
        Picasso mock = Mockito.mock(Picasso.class);
        ImageViewAction request = new ImageViewAction(mock, target, null, null, TestUtils.RESOURCE_ID_1, false, callback);
        Exception e = new RuntimeException();
        request.error(e);
        Mockito.verify(target).setImageResource(TestUtils.RESOURCE_ID_1);
        Mockito.verify(callback).onError(e);
    }

    @Test
    public void invokesErrorIfTargetIsNotNullWithErrorResourceId() {
        ImageView target = TestUtils.mockImageViewTarget();
        Callback callback = TestUtils.mockCallback();
        Picasso mock = Mockito.mock(Picasso.class);
        ImageViewAction request = new ImageViewAction(mock, target, null, null, TestUtils.RESOURCE_ID_1, false, callback);
        Exception e = new RuntimeException();
        request.error(e);
        Mockito.verify(target).setImageResource(TestUtils.RESOURCE_ID_1);
        Mockito.verify(callback).onError(e);
    }

    @Test
    public void invokesErrorIfTargetIsNotNullWithErrorDrawable() {
        Drawable errorDrawable = Mockito.mock(Drawable.class);
        ImageView target = TestUtils.mockImageViewTarget();
        Callback callback = TestUtils.mockCallback();
        Picasso mock = Mockito.mock(Picasso.class);
        ImageViewAction request = new ImageViewAction(mock, target, null, errorDrawable, 0, false, callback);
        Exception e = new RuntimeException();
        request.error(e);
        Mockito.verify(target).setImageDrawable(errorDrawable);
        Mockito.verify(callback).onError(e);
    }

    @Test
    public void clearsCallbackOnCancel() {
        Picasso picasso = Mockito.mock(Picasso.class);
        ImageView target = TestUtils.mockImageViewTarget();
        Callback callback = TestUtils.mockCallback();
        ImageViewAction request = new ImageViewAction(picasso, target, null, null, 0, false, callback);
        request.cancel();
        assertThat(request.callback).isNull();
    }

    @Test
    public void stopPlaceholderAnimationOnError() {
        Picasso picasso = Mockito.mock(Picasso.class);
        AnimationDrawable placeholder = Mockito.mock(AnimationDrawable.class);
        ImageView target = TestUtils.mockImageViewTarget();
        Mockito.when(target.getDrawable()).thenReturn(placeholder);
        ImageViewAction request = new ImageViewAction(picasso, target, null, null, 0, false, null);
        request.error(new RuntimeException());
        Mockito.verify(placeholder).stop();
    }
}

