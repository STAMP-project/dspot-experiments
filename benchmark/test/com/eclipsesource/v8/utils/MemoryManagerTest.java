/**
 * *****************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 * ****************************************************************************
 */
package com.eclipsesource.v8.utils;


import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import org.junit.Assert;
import org.junit.Test;


public class MemoryManagerTest {
    private V8 v8;

    @SuppressWarnings("resource")
    @Test
    public void testMemoryManagerReleasesObjects() {
        MemoryManager memoryManager = new MemoryManager(v8);
        new V8Object(v8);
        memoryManager.release();
        Assert.assertEquals(0, v8.getObjectReferenceCount());
    }

    @SuppressWarnings("resource")
    @Test
    public void testObjectIsReleased() {
        MemoryManager memoryManager = new MemoryManager(v8);
        V8Object object = new V8Object(v8);
        memoryManager.release();
        Assert.assertTrue(object.isReleased());
    }

    @Test
    public void testMemoryManagerReleasesFunctions() {
        MemoryManager memoryManager = new MemoryManager(v8);
        v8.executeScript("(function() {})");
        memoryManager.release();
        Assert.assertEquals(0, v8.getObjectReferenceCount());
    }

    @Test
    public void testMemoryReferenceCount0() {
        MemoryManager memoryManager = new MemoryManager(v8);
        Assert.assertEquals(0, memoryManager.getObjectReferenceCount());
    }

    @Test
    public void testMemoryReferenceCount0_AfterRemove() {
        MemoryManager memoryManager = new MemoryManager(v8);
        close();
        Assert.assertEquals(0, memoryManager.getObjectReferenceCount());
    }

    @Test
    public void testMemoryReferenceCount() {
        MemoryManager memoryManager = new MemoryManager(v8);
        v8.executeScript("(function() {})");
        Assert.assertEquals(1, memoryManager.getObjectReferenceCount());
        memoryManager.release();
        Assert.assertEquals(0, v8.getObjectReferenceCount());
    }

    @Test
    public void testMemoryManagerReleasesReturnedObjects() {
        MemoryManager memoryManager = new MemoryManager(v8);
        v8.executeScript("foo = {}; foo");
        Assert.assertEquals(1, v8.getObjectReferenceCount());
        memoryManager.release();
        Assert.assertEquals(0, v8.getObjectReferenceCount());
    }

    @Test
    public void testReleasedMemoryManagerDoesTrackObjects() {
        MemoryManager memoryManager = new MemoryManager(v8);
        memoryManager.release();
        V8Object object = new V8Object(v8);
        Assert.assertEquals(1, v8.getObjectReferenceCount());
        object.close();
    }

    @SuppressWarnings("resource")
    @Test
    public void testNestedMemoryManagers() {
        MemoryManager memoryManager1 = new MemoryManager(v8);
        MemoryManager memoryManager2 = new MemoryManager(v8);
        new V8Object(v8);
        memoryManager2.release();
        new V8Object(v8);
        Assert.assertEquals(1, v8.getObjectReferenceCount());
        memoryManager1.release();
        Assert.assertEquals(0, v8.getObjectReferenceCount());
    }

    @SuppressWarnings("resource")
    @Test
    public void testNestedMemoryManagerHasProperObjectCount() {
        MemoryManager memoryManager1 = new MemoryManager(v8);
        new V8Object(v8);
        MemoryManager memoryManager2 = new MemoryManager(v8);
        new V8Object(v8);
        Assert.assertEquals(2, memoryManager1.getObjectReferenceCount());
        Assert.assertEquals(1, memoryManager2.getObjectReferenceCount());
        memoryManager2.release();
        Assert.assertEquals(1, memoryManager1.getObjectReferenceCount());
        memoryManager1.release();
    }

    @SuppressWarnings("resource")
    @Test
    public void testNestedMemoryManager_ReverseReleaseOrder() {
        MemoryManager memoryManager1 = new MemoryManager(v8);
        new V8Object(v8);
        MemoryManager memoryManager2 = new MemoryManager(v8);
        new V8Object(v8);
        Assert.assertEquals(2, memoryManager1.getObjectReferenceCount());
        Assert.assertEquals(1, memoryManager2.getObjectReferenceCount());
        memoryManager1.release();
        Assert.assertEquals(0, memoryManager2.getObjectReferenceCount());
        memoryManager2.release();
    }

    @Test(expected = IllegalStateException.class)
    public void testMemoryManagerReleased_CannotCallGetObjectReferenceCount() {
        MemoryManager memoryManager = new MemoryManager(v8);
        memoryManager.release();
        memoryManager.getObjectReferenceCount();
    }

    @Test
    public void testCanReleaseTwice() {
        MemoryManager memoryManager = new MemoryManager(v8);
        memoryManager.release();
        memoryManager.release();
    }

    @Test
    public void testIsReleasedTrue() {
        MemoryManager memoryManager = new MemoryManager(v8);
        memoryManager.release();
        Assert.assertTrue(memoryManager.isReleased());
    }

    @Test
    public void testIsReleasedFalse() {
        MemoryManager memoryManager = new MemoryManager(v8);
        Assert.assertFalse(memoryManager.isReleased());
    }

    @Test
    public void testPersistObject() {
        MemoryManager memoryManager = new MemoryManager(v8);
        V8Object object = new V8Object(v8);
        memoryManager.persist(object);
        memoryManager.release();
        Assert.assertFalse(object.isReleased());
        object.close();
    }

    @Test
    public void testPersistNonManagedObject() {
        V8Object object = new V8Object(v8);
        MemoryManager memoryManager = new MemoryManager(v8);
        memoryManager.persist(object);
        memoryManager.release();
        Assert.assertFalse(object.isReleased());
        object.close();
    }

    @SuppressWarnings("resource")
    @Test
    public void testTwins() {
        MemoryManager memoryManager = new MemoryManager(v8);
        V8Object object = new V8Object(v8);
        object.twin();
        Assert.assertEquals(2, memoryManager.getObjectReferenceCount());
        memoryManager.release();
    }

    @Test
    public void testTwinsReleaseOne() {
        MemoryManager memoryManager = new MemoryManager(v8);
        V8Object object = new V8Object(v8);
        object.twin();
        object.close();
        Assert.assertEquals(1, memoryManager.getObjectReferenceCount());
        memoryManager.release();
    }

    @Test
    public void testGetObjectTwice() {
        v8.executeVoidScript("foo = {}");
        MemoryManager memoryManager = new MemoryManager(v8);
        V8Object foo1 = v8.getObject("foo");
        close();
        Assert.assertEquals(1, memoryManager.getObjectReferenceCount());
        memoryManager.release();
        Assert.assertTrue(foo1.isReleased());
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotCallPersistOnReleasedManager() {
        MemoryManager memoryManager = new MemoryManager(v8);
        V8Object object = new V8Object(v8);
        memoryManager.release();
        memoryManager.persist(object);
    }

    MemoryManager memoryManager;
}

