/**
 * Copyright 2013 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ObjectTest {
    @Test
    public void objectCreated() {
        Object a = new Object();
        Assert.assertNotNull(a);
    }

    @Test
    public void differentInstancesNotEqual() {
        Object a = new Object();
        Object b = new Object();
        Assert.assertNotEquals(a, b);
    }

    @Test
    public void sameInstancesAreEqual() {
        Object a = new Object();
        Object b = a;
        Assert.assertEquals(a, b);
    }

    @Test
    public void multipleGetClassCallsReturnSameValue() {
        Object a = new Object();
        Assert.assertSame(a.getClass(), a.getClass());
    }

    @Test
    public void sameClassesAreEqual() {
        Object a = new Object();
        Object b = new Object();
        Assert.assertSame(a.getClass(), b.getClass());
    }

    @Test
    public void properInstanceDetected() {
        Assert.assertTrue(Object.class.isInstance(new Object()));
    }

    @Test
    public void toStringWorks() {
        Assert.assertTrue(new Object().toString().startsWith("java.lang.Object@"));
        Assert.assertTrue(new Object[2].toString().startsWith("[Ljava.lang.Object;@"));
        Assert.assertTrue(new byte[3].toString().startsWith("[B@"));
    }

    @Test
    public void waitWorks() throws InterruptedException {
        long start = System.currentTimeMillis();
        final Object lock = new Object();
        synchronized(lock) {
            lock.wait(110);
        }
        long end = System.currentTimeMillis();
        Assert.assertTrue(((end - start) > 100));
    }
}

