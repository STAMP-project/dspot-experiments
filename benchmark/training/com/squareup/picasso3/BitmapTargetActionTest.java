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


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static LoadedFrom.MEMORY;


@RunWith(RobolectricTestRunner.class)
public class BitmapTargetActionTest {
    @Test(expected = AssertionError.class)
    public void throwsErrorWithNullResult() {
        BitmapTarget target = TestUtils.mockTarget();
        BitmapTargetAction request = new BitmapTargetAction(Mockito.mock(Picasso.class), target, null, null, 0);
        request.complete(null);
    }

    @Test
    public void invokesSuccessIfTargetIsNotNull() {
        Bitmap bitmap = TestUtils.makeBitmap();
        BitmapTarget target = TestUtils.mockTarget();
        BitmapTargetAction request = new BitmapTargetAction(Mockito.mock(Picasso.class), target, null, null, 0);
        request.complete(new RequestHandler.Result(bitmap, MEMORY));
        Mockito.verify(target).onBitmapLoaded(bitmap, LoadedFrom.MEMORY);
    }

    @Test
    public void invokesOnBitmapFailedIfTargetIsNotNullWithErrorDrawable() {
        Drawable errorDrawable = Mockito.mock(Drawable.class);
        BitmapTarget target = TestUtils.mockTarget();
        BitmapTargetAction request = new BitmapTargetAction(Mockito.mock(Picasso.class), target, null, errorDrawable, 0);
        Exception e = new RuntimeException();
        request.error(e);
        Mockito.verify(target).onBitmapFailed(e, errorDrawable);
    }

    @Test
    public void invokesOnBitmapFailedIfTargetIsNotNullWithErrorResourceId() {
        Drawable errorDrawable = Mockito.mock(Drawable.class);
        BitmapTarget target = TestUtils.mockTarget();
        Context context = Mockito.mock(Context.class);
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        PlatformLruCache cache = new PlatformLruCache(0);
        Picasso picasso = new Picasso(context, dispatcher, TestUtils.UNUSED_CALL_FACTORY, null, cache, null, TestUtils.NO_TRANSFORMERS, TestUtils.NO_HANDLERS, Mockito.mock(Stats.class), ARGB_8888, false, false);
        Resources res = Mockito.mock(Resources.class);
        BitmapTargetAction request = new BitmapTargetAction(picasso, target, null, null, TestUtils.RESOURCE_ID_1);
        Mockito.when(context.getResources()).thenReturn(res);
        Mockito.when(res.getDrawable(TestUtils.RESOURCE_ID_1)).thenReturn(errorDrawable);
        Exception e = new RuntimeException();
        request.error(e);
        Mockito.verify(target).onBitmapFailed(e, errorDrawable);
    }

    @Test
    public void recyclingInSuccessThrowsException() {
        BitmapTarget bad = new BitmapTarget() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bitmap.recycle();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                throw new AssertionError();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                throw new AssertionError();
            }
        };
        Picasso picasso = Mockito.mock(Picasso.class);
        Bitmap bitmap = TestUtils.makeBitmap();
        BitmapTargetAction tr = new BitmapTargetAction(picasso, bad, null, null, 0);
        try {
            tr.complete(new RequestHandler.Result(bitmap, MEMORY));
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
    }
}

