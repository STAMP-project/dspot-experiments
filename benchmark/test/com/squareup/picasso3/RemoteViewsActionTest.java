/**
 * Copyright (C) 2014 Square, Inc.
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


import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RemoteViews;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import com.squareup.picasso3.RemoteViewsAction.RemoteViewsTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static LoadedFrom.NETWORK;


// 
@RunWith(RobolectricTestRunner.class)
public class RemoteViewsActionTest {
    private Picasso picasso;

    private RemoteViews remoteViews;

    @Test
    public void completeSetsBitmapOnRemoteViews() {
        Callback callback = TestUtils.mockCallback();
        Bitmap bitmap = TestUtils.makeBitmap();
        RemoteViewsAction action = createAction(callback);
        action.complete(new RequestHandler.Result(bitmap, NETWORK));
        Mockito.verify(remoteViews).setImageViewBitmap(1, bitmap);
        Mockito.verify(callback).onSuccess();
    }

    @Test
    public void errorWithNoResourceIsNoop() {
        Callback callback = TestUtils.mockCallback();
        RemoteViewsAction action = createAction(callback);
        Exception e = new RuntimeException();
        action.error(e);
        Mockito.verifyZeroInteractions(remoteViews);
        Mockito.verify(callback).onError(e);
    }

    @Test
    public void errorWithResourceSetsResource() {
        Callback callback = TestUtils.mockCallback();
        RemoteViewsAction action = createAction(1, callback);
        Exception e = new RuntimeException();
        action.error(e);
        Mockito.verify(remoteViews).setImageViewResource(1, 1);
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

    static class TestableRemoteViewsAction extends RemoteViewsAction {
        TestableRemoteViewsAction(Picasso picasso, Request data, @DrawableRes
        int errorResId, RemoteViewsTarget target, Callback callback) {
            super(picasso, data, errorResId, target, callback);
        }

        @Override
        void update() {
        }

        @NonNull
        @Override
        Object getTarget() {
            throw new AssertionError();
        }
    }
}

