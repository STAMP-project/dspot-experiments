/**
 * Copyright 2019-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.dataflow;


import com.facebook.litho.choreographercompat.ChoreographerCompat.FrameCallback;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(ComponentsTestRunner.class)
public class MockTimingSourceTest {
    private MockTimingSource mTimingSource;

    @Test
    public void testPostFrameCallback() {
        ArrayList<FrameCallback> callbacks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            FrameCallback callback = Mockito.mock(FrameCallback.class);
            callbacks.add(callback);
            mTimingSource.postFrameCallback(callback);
        }
        mTimingSource.step(1);
        for (int i = 0; i < (callbacks.size()); i++) {
            Mockito.verify(callbacks.get(i)).doFrame(ArgumentMatchers.anyInt());
        }
    }

    @Test
    public void testPostFrameCallbackDelayed() {
        FrameCallback callback1 = Mockito.mock(FrameCallback.class);
        FrameCallback callback2 = Mockito.mock(FrameCallback.class);
        FrameCallback delayedCallback = Mockito.mock(FrameCallback.class);
        mTimingSource.postFrameCallback(callback1);
        mTimingSource.postFrameCallbackDelayed(delayedCallback, 20);
        mTimingSource.postFrameCallback(callback2);
        mTimingSource.step(1);
        Mockito.verify(callback1).doFrame(ArgumentMatchers.anyInt());
        Mockito.verify(callback2).doFrame(ArgumentMatchers.anyInt());
        Mockito.verify(delayedCallback, Mockito.never()).doFrame(ArgumentMatchers.anyInt());
        mTimingSource.step(1);
        Mockito.verify(delayedCallback).doFrame(ArgumentMatchers.anyInt());
    }

    @Test
    public void testNestedFrameCallbacks() {
        FrameCallback callback = new FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                mTimingSource.postFrameCallback(new FrameCallback() {
                    @Override
                    public void doFrame(long frameTimeNanos) {
                        Assert.fail("Nested FrameCallback should not be called in the same step");
                    }
                });
            }
        };
        mTimingSource.postFrameCallback(callback);
        mTimingSource.step(1);
    }
}

