/**
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.animation;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests the {@link Animation} class
 */
public class AnimationTest {
    private static final float EPS = 0.001F;

    private AnimationTest.Container c = new AnimationTest.Container();

    @Test
    public void testStartEndOnce() {
        Animation ani = once();
        AnimationListener listener = Mockito.mock(AnimationListener.class);
        ani.addListener(listener);
        ani.start();
        ani.update(2.5F);
        Mockito.verify(listener, Mockito.times(1)).onStart();
        Mockito.verify(listener, Mockito.times(1)).onEnd();
    }

    @Test
    public void testStartEndValuesOnce() {
        Animation ani = once().start();
        Assert.assertEquals(0.0F, c.value, 0.0F);
        ani.update(2.5F);
        Assert.assertEquals(1.0F, c.value, 0.0F);
    }

    @Test
    public void testStartEndValuesInfinite() {
        Animation ani = infinite().start();
        Assert.assertEquals(0.0F, c.value, 0.0F);
        ani.update(2.5F);
        Assert.assertEquals(0.25F, c.value, AnimationTest.EPS);// (2.5 % 2) / 2

    }

    @Test
    public void testOverflowInfinite() {
        Animation ani = infinite().start();
        Assert.assertEquals(0.0F, c.value, 0.0F);
        ani.update(112.5F);
        Assert.assertEquals(0.25F, c.value, AnimationTest.EPS);// (112.5 % 2) / 2

    }

    @Test
    public void testUpdates() {
        Animation ani = once();
        ani.update(2.5F);// ignored

        Assert.assertEquals(0.0F, c.value, 0.0F);
        ani.start();
        ani.update(0.5F);
        Assert.assertEquals(0.25F, c.value, AnimationTest.EPS);// 0.5 / 2

        ani.pause();
        ani.update(0.5F);// ignored

        Assert.assertEquals(0.25F, c.value, AnimationTest.EPS);// same

        ani.resume();
        ani.update(1.0F);
        Assert.assertEquals(0.75F, c.value, AnimationTest.EPS);// 1.5 / 2

        ani.update(1.0F);
        Assert.assertEquals(1.0F, c.value, 0.0F);// 2.5 / 2 -> capped

        ani.update(1.0F);// ignored

        Assert.assertEquals(1.0F, c.value, 0.0F);// same

    }

    @Test
    public void testStartEndOnceReverse() {
        Animation ani = once().setReverseMode();
        AnimationListener listener = Mockito.mock(AnimationListener.class);
        ani.addListener(listener);
        ani.start();
        ani.update(2.5F);
        Mockito.verify(listener, Mockito.times(1)).onStart();
        Mockito.verify(listener, Mockito.times(1)).onEnd();
    }

    @Test
    public void testUpdatesReverse() {
        Animation ani = once().setReverseMode();
        ani.update(2.5F);// ignored

        Assert.assertEquals(0.0F, c.value, 0.0F);
        ani.start();
        ani.update(0.5F);
        Assert.assertEquals(0.75F, c.value, AnimationTest.EPS);// 1 - 0.5 / 2

        ani.pause();
        ani.update(0.5F);// ignored

        Assert.assertEquals(0.75F, c.value, AnimationTest.EPS);// same

        ani.resume();
        ani.update(1.0F);
        Assert.assertEquals(0.25F, c.value, AnimationTest.EPS);// 1 - 1.5 / 2

        ani.update(1.0F);
        Assert.assertEquals(0.0F, c.value, 0.0F);// 1 - 2.5 / 2 -> capped

        ani.update(1.0F);// ignored

        Assert.assertEquals(0.0F, c.value, 0.0F);// same

    }

    private static class Container {
        float value;
    }
}

