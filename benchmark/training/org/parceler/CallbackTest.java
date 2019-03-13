/**
 * Copyright 2011-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler;


import android.os.Parcelable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CallbackTest {
    @Parcel
    public static class ReferencedClass {
        boolean wrapCallbackCalled = false;

        boolean unwrapCallbackCalled = false;

        @OnWrap
        public void wrap() {
            wrapCallbackCalled = true;
        }

        @OnUnwrap
        public void unwrap() {
            unwrapCallbackCalled = true;
        }
    }

    public static class BaseClass {
        boolean baseWrapCallbackCalled = false;

        boolean baseUnwrapCallbackCalled = false;

        @OnWrap
        public void baseWrap() {
            baseWrapCallbackCalled = true;
        }

        @OnUnwrap
        public void baseUnwrap() {
            baseUnwrapCallbackCalled = true;
        }
    }

    @Parcel
    public static class CallbackExample extends CallbackTest.BaseClass {
        boolean wrapCallbackCalled = false;

        boolean unwrapCallbackCalled = false;

        CallbackTest.ReferencedClass referenced = new CallbackTest.ReferencedClass();

        @OnWrap
        public void wrap() {
            wrapCallbackCalled = true;
        }

        @OnUnwrap
        public void unwrap() {
            unwrapCallbackCalled = true;
        }
    }

    @Test
    public void testCallbacks() {
        CallbackTest.CallbackExample example = new CallbackTest.CallbackExample();
        Assert.assertFalse(example.wrapCallbackCalled);
        Assert.assertFalse(example.unwrapCallbackCalled);
        Assert.assertFalse(example.baseWrapCallbackCalled);
        Assert.assertFalse(example.baseUnwrapCallbackCalled);
        Assert.assertFalse(example.referenced.wrapCallbackCalled);
        Assert.assertFalse(example.referenced.unwrapCallbackCalled);
        Parcelable wrappedExample = ParcelsTestUtil.wrap(example);
        Assert.assertTrue(example.wrapCallbackCalled);
        Assert.assertFalse(example.unwrapCallbackCalled);
        Assert.assertTrue(example.baseWrapCallbackCalled);
        Assert.assertFalse(example.baseUnwrapCallbackCalled);
        Assert.assertTrue(example.referenced.wrapCallbackCalled);
        Assert.assertFalse(example.referenced.unwrapCallbackCalled);
        CallbackTest.CallbackExample output = Parcels.unwrap(wrappedExample);
        Assert.assertTrue(output.wrapCallbackCalled);
        Assert.assertTrue(output.unwrapCallbackCalled);
        Assert.assertTrue(output.baseWrapCallbackCalled);
        Assert.assertTrue(output.baseUnwrapCallbackCalled);
        Assert.assertTrue(output.referenced.wrapCallbackCalled);
        Assert.assertTrue(output.referenced.unwrapCallbackCalled);
    }
}

