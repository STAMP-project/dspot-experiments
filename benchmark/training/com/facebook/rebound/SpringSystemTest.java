/**
 * Copyright (c) 2013, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.rebound;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class SpringSystemTest {
    private BaseSpringSystem mSpringSystemSpy;

    private SynchronousLooper mSynchronousLooper;

    private Spring mMockSpring;

    @Test
    public void testRegisterSpringOnCreation() {
        Spring spring = mSpringSystemSpy.createSpring();
        Mockito.verify(mSpringSystemSpy).registerSpring(spring);
    }

    @Test
    public void testCreateSpringWithoutName() {
        Spring spring = mSpringSystemSpy.createSpring();
        Assert.assertNotNull(spring.getId());
        Assert.assertEquals(spring, mSpringSystemSpy.getSpringById(spring.getId()));
        Assert.assertEquals(1, mSpringSystemSpy.getAllSprings().size());
    }

    @Test
    public void testLoop() {
        mSpringSystemSpy.registerSpring(mMockSpring);
        Mockito.when(mMockSpring.systemShouldAdvance()).thenReturn(true, false);
        mSpringSystemSpy.activateSpring(mMockSpring.getId());
        Mockito.verify(mSpringSystemSpy, Mockito.times(2)).advance(mSynchronousLooper.getTimeStep());
        Mockito.verify(mMockSpring, Mockito.times(1)).advance(((mSynchronousLooper.getTimeStep()) / 1000));
        Assert.assertTrue(mSpringSystemSpy.getIsIdle());
    }

    @Test
    public void testLoopWithMultiplePassesRunsAndTerminates() {
        InOrder inOrder = Mockito.inOrder(mMockSpring, mSpringSystemSpy);
        Mockito.when(mMockSpring.systemShouldAdvance()).thenReturn(true, true, true, false);
        mSpringSystemSpy.registerSpring(mMockSpring);
        mSpringSystemSpy.activateSpring(mMockSpring.getId());
        double stepMillis = mSynchronousLooper.getTimeStep();
        double stepSeconds = (mSynchronousLooper.getTimeStep()) / 1000;
        inOrder.verify(mSpringSystemSpy, Mockito.times(1)).advance(stepMillis);
        inOrder.verify(mMockSpring, Mockito.times(1)).advance(stepSeconds);
        inOrder.verify(mSpringSystemSpy, Mockito.times(1)).advance(stepMillis);
        inOrder.verify(mMockSpring, Mockito.times(1)).advance(stepSeconds);
        inOrder.verify(mSpringSystemSpy, Mockito.times(1)).advance(stepMillis);
        inOrder.verify(mMockSpring, Mockito.times(1)).advance(stepSeconds);
        inOrder.verify(mSpringSystemSpy, Mockito.times(1)).advance(stepMillis);// one extra pass through the system

        Assert.assertTrue(mSpringSystemSpy.getIsIdle());
    }

    @Test
    public void testSpringSystemListener() {
        SpringSystemListener listener = Mockito.spy(new SpringSystemListener() {
            @Override
            public void onBeforeIntegrate(BaseSpringSystem springSystem) {
            }

            @Override
            public void onAfterIntegrate(BaseSpringSystem springSystem) {
            }
        });
        InOrder inOrder = Mockito.inOrder(listener);
        mSpringSystemSpy.registerSpring(mMockSpring);
        Mockito.when(mMockSpring.systemShouldAdvance()).thenReturn(true, false);
        mSpringSystemSpy.addListener(listener);
        mSpringSystemSpy.activateSpring(mMockSpring.getId());
        inOrder.verify(listener).onBeforeIntegrate(mSpringSystemSpy);
        inOrder.verify(listener).onAfterIntegrate(mSpringSystemSpy);
        inOrder.verify(listener).onBeforeIntegrate(mSpringSystemSpy);
        inOrder.verify(listener).onAfterIntegrate(mSpringSystemSpy);
        mSpringSystemSpy.removeListener(listener);
        mSpringSystemSpy.activateSpring(mMockSpring.getId());
        inOrder.verify(listener, Mockito.never()).onBeforeIntegrate(mSpringSystemSpy);
        inOrder.verify(listener, Mockito.never()).onAfterIntegrate(mSpringSystemSpy);
    }

    @Test
    public void testActivatingAndDeactivatingSpring() {
        Mockito.when(mMockSpring.systemShouldAdvance()).thenReturn(true, false, false);
        InOrder inOrder = Mockito.inOrder(mMockSpring, mSpringSystemSpy);
        mSpringSystemSpy.registerSpring(mMockSpring);
        Assert.assertTrue(mSpringSystemSpy.getIsIdle());
        double stepMillis = mSynchronousLooper.getTimeStep();
        double stepSeconds = (mSynchronousLooper.getTimeStep()) / 1000;
        mSpringSystemSpy.activateSpring(mMockSpring.getId());
        inOrder.verify(mSpringSystemSpy).advance(stepMillis);
        inOrder.verify(mMockSpring).systemShouldAdvance();
        inOrder.verify(mMockSpring).advance(stepSeconds);
        inOrder.verify(mSpringSystemSpy).advance(stepMillis);
        inOrder.verify(mMockSpring).systemShouldAdvance();
        inOrder.verify(mMockSpring, Mockito.never()).advance(stepSeconds);
        Assert.assertTrue(mSpringSystemSpy.getIsIdle());
        mSpringSystemSpy.loop(stepMillis);
        inOrder.verify(mSpringSystemSpy).advance(stepMillis);
        inOrder.verify(mMockSpring, Mockito.never()).systemShouldAdvance();
        inOrder.verify(mMockSpring, Mockito.never()).advance(stepSeconds);
        Assert.assertTrue(mSpringSystemSpy.getIsIdle());
        mSpringSystemSpy.activateSpring(mMockSpring.getId());
        inOrder.verify(mSpringSystemSpy).advance(stepMillis);
        inOrder.verify(mMockSpring).systemShouldAdvance();
        inOrder.verify(mMockSpring, Mockito.never()).advance(stepSeconds);
        Assert.assertTrue(mSpringSystemSpy.getIsIdle());
        mSpringSystemSpy.loop(stepMillis);
        inOrder.verify(mSpringSystemSpy).advance(stepMillis);
        inOrder.verify(mMockSpring, Mockito.never()).systemShouldAdvance();
        inOrder.verify(mMockSpring, Mockito.never()).advance(stepSeconds);
        Assert.assertTrue(mSpringSystemSpy.getIsIdle());
    }

    @Test
    public void testCanAddListenersWhileIterating() {
        Mockito.when(mMockSpring.systemShouldAdvance()).thenReturn(true, false);
        mSpringSystemSpy.addListener(new SpringSystemTest.SimpleSpringSystemListener() {
            @Override
            public void onAfterIntegrate(BaseSpringSystem springSystem) {
                springSystem.addListener(new SpringSystemTest.SimpleSpringSystemListener());
            }
        });
        mSpringSystemSpy.addListener(new SpringSystemTest.SimpleSpringSystemListener());
        mSpringSystemSpy.loop(1);
    }

    @Test
    public void testCanRemoveListenersWhileIterating() {
        Mockito.when(mMockSpring.systemShouldAdvance()).thenReturn(true, true, false);
        final SpringSystemTest.SimpleSpringSystemListener nextListener = new SpringSystemTest.SimpleSpringSystemListener();
        mSpringSystemSpy.addListener(new SpringSystemTest.SimpleSpringSystemListener() {
            @Override
            public void onAfterIntegrate(BaseSpringSystem springSystem) {
                springSystem.removeListener(nextListener);
            }
        });
        mSpringSystemSpy.addListener(nextListener);
        mSpringSystemSpy.loop(1);
        mSpringSystemSpy.loop(1);
        mSpringSystemSpy.loop(1);
    }

    private class SimpleSpringSystemListener implements SpringSystemListener {
        @Override
        public void onBeforeIntegrate(BaseSpringSystem springSystem) {
        }

        @Override
        public void onAfterIntegrate(BaseSpringSystem springSystem) {
        }
    }
}

