/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook.internal;


import CallbackManagerImpl.RequestCodeOffset.Login;
import android.content.Intent;
import bolts.Capture;
import com.facebook.FacebookPowerMockTestCase;
import com.facebook.FacebookSdk;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ CallbackManagerImpl.class })
public final class CallbackManagerImplPowerMockTest extends FacebookPowerMockTestCase {
    @Test
    public void testStaticRegisterValidations() {
        try {
            CallbackManagerImpl.registerStaticCallback(Login.toRequestCode(), null);
            Assert.fail();
        } catch (NullPointerException exception) {
        }
    }

    @Test
    public void testRegisterValidations() {
        CallbackManagerImpl callbackManagerImpl = new CallbackManagerImpl();
        try {
            callbackManagerImpl.registerCallback(Login.toRequestCode(), null);
            Assert.fail();
        } catch (NullPointerException exception) {
        }
    }

    @Test
    public void testCallbackExecuted() {
        final Capture<Boolean> capture = new Capture(false);
        final CallbackManagerImpl callbackManagerImpl = new CallbackManagerImpl();
        callbackManagerImpl.registerCallback(Login.toRequestCode(), new CallbackManagerImpl.Callback() {
            @Override
            public boolean onActivityResult(int resultCode, Intent data) {
                capture.set(true);
                return true;
            }
        });
        callbackManagerImpl.onActivityResult(FacebookSdk.getCallbackRequestCodeOffset(), 1, new Intent());
        Assert.assertTrue(capture.get());
    }

    @Test
    public void testRightCallbackExecuted() {
        final Capture<Boolean> capture = new Capture(false);
        final CallbackManagerImpl callbackManagerImpl = new CallbackManagerImpl();
        callbackManagerImpl.registerCallback(123, new CallbackManagerImpl.Callback() {
            @Override
            public boolean onActivityResult(int resultCode, Intent data) {
                capture.set(true);
                return true;
            }
        });
        callbackManagerImpl.registerCallback(456, new CallbackManagerImpl.Callback() {
            @Override
            public boolean onActivityResult(int resultCode, Intent data) {
                return false;
            }
        });
        callbackManagerImpl.onActivityResult(123, 1, new Intent());
        Assert.assertTrue(capture.get());
    }

    @Test
    public void testStaticCallbackExecuted() {
        final Capture<Boolean> capture = new Capture(false);
        final CallbackManagerImpl callbackManagerImpl = new CallbackManagerImpl();
        callbackManagerImpl.registerStaticCallback(Login.toRequestCode(), new CallbackManagerImpl.Callback() {
            @Override
            public boolean onActivityResult(int resultCode, Intent data) {
                capture.set(true);
                return true;
            }
        });
        callbackManagerImpl.onActivityResult(FacebookSdk.getCallbackRequestCodeOffset(), 1, new Intent());
        Assert.assertTrue(capture.get());
    }

    @Test
    public void testStaticCallbackSkipped() {
        final Capture<Boolean> capture = new Capture(false);
        final Capture<Boolean> captureStatic = new Capture(false);
        final CallbackManagerImpl callbackManagerImpl = new CallbackManagerImpl();
        callbackManagerImpl.registerCallback(Login.toRequestCode(), new CallbackManagerImpl.Callback() {
            @Override
            public boolean onActivityResult(int resultCode, Intent data) {
                capture.set(true);
                return true;
            }
        });
        callbackManagerImpl.registerStaticCallback(Login.toRequestCode(), new CallbackManagerImpl.Callback() {
            @Override
            public boolean onActivityResult(int resultCode, Intent data) {
                captureStatic.set(true);
                return true;
            }
        });
        callbackManagerImpl.onActivityResult(FacebookSdk.getCallbackRequestCodeOffset(), 1, new Intent());
        Assert.assertTrue(capture.get());
        Assert.assertFalse(captureStatic.get());
    }
}

