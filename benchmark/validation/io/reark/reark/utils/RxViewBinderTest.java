/**
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.reark.utils;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.Subject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(AndroidSchedulers.class)
public class RxViewBinderTest {
    private RxViewBinder binder;

    private Subject<String> testSubject;

    private TestObserver<String> testObserver;

    @Test
    public void testInitialState() throws Exception {
        testSubject.onNext("testString");
        Assert.assertNull(testObserver);
    }

    @Test
    public void testInitialUnbind() throws Exception {
        binder.unbind();
        Assert.assertNull(testObserver);
    }

    @Test
    public void testBind() throws Exception {
        binder.bind();
        testSubject.onNext("testString");
        Assert.assertNotNull(testObserver);
        Assert.assertFalse(testObserver.isDisposed());
        Assert.assertEquals(1, testObserver.getEvents().get(0).size());
        Assert.assertEquals("testString", testObserver.getEvents().get(0).get(0));
    }

    @Test
    public void testUnbind() throws Exception {
        binder.bind();
        binder.unbind();
        testSubject.onNext("testString");
        Assert.assertNotNull(testObserver);
        Assert.assertTrue(testObserver.isDisposed());
        Assert.assertEquals(0, testObserver.getEvents().get(0).size());
    }

    @Test
    public void testReBind() throws Exception {
        binder.bind();
        binder.unbind();
        // The test subscriber changes with each bind.
        TestObserver<String> firstTestObserver = testObserver;
        testObserver = null;
        binder.bind();
        testSubject.onNext("testString");
        Assert.assertNotNull(firstTestObserver);
        Assert.assertNotNull(testObserver);
        Assert.assertTrue(firstTestObserver.isDisposed());
        Assert.assertFalse(testObserver.isDisposed());
        Assert.assertEquals(0, firstTestObserver.getEvents().get(0).size());
        Assert.assertEquals(1, testObserver.getEvents().get(0).size());
        Assert.assertEquals("testString", testObserver.getEvents().get(0).get(0));
    }

    @Test
    public void testDoubleBind() throws Exception {
        binder.bind();
        // The test subscriber changes with each bind.
        TestObserver<String> firstTestObserver = testObserver;
        testObserver = null;
        binder.bind();
        testSubject.onNext("testString");
        Assert.assertNotNull(firstTestObserver);
        Assert.assertNotNull(testObserver);
        Assert.assertTrue(firstTestObserver.isDisposed());
        Assert.assertFalse(testObserver.isDisposed());
        Assert.assertEquals(0, firstTestObserver.getEvents().get(0).size());
        Assert.assertEquals(1, testObserver.getEvents().get(0).size());
        Assert.assertEquals("testString", testObserver.getEvents().get(0).get(0));
    }
}

