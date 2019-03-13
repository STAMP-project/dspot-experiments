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
package com.facebook;


import FacebookSdk.CALLBACK_OFFSET_CHANGED_AFTER_INIT;
import FacebookSdk.CALLBACK_OFFSET_NEGATIVE;
import RuntimeEnvironment.application;
import android.os.ConditionVariable;
import com.facebook.internal.FetchedAppSettingsManager;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ FacebookSdk.class, Utility.class, FetchedAppSettingsManager.class })
public final class FacebookSdkPowerMockTest extends FacebookPowerMockTestCase {
    @Test
    public void testGetExecutor() {
        final ConditionVariable condition = new ConditionVariable();
        FacebookSdk.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                condition.open();
            }
        });
        boolean success = condition.block(5000);
        Assert.assertTrue(success);
    }

    @Test
    public void testSetExecutor() {
        final ConditionVariable condition = new ConditionVariable();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        final Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                Assert.assertEquals(runnable, command);
                command.run();
                condition.open();
            }
        };
        Executor original = FacebookSdk.getExecutor();
        try {
            FacebookSdk.setExecutor(executor);
            FacebookSdk.getExecutor().execute(runnable);
            boolean success = condition.block(5000);
            Assert.assertTrue(success);
        } finally {
            FacebookSdk.setExecutor(original);
        }
    }

    @Test
    public void testFacebookDomain() {
        FacebookSdk.setFacebookDomain("beta.facebook.com");
        String graphUrlBase = ServerProtocol.getGraphUrlBase();
        Assert.assertEquals("https://graph.beta.facebook.com", graphUrlBase);
        FacebookSdk.setFacebookDomain("facebook.com");
    }

    @Test
    public void testLoadDefaults() throws Exception {
        // Set to null since the value might have been set by another test
        FacebookSdk.setApplicationId(null);
        stub(method(FacebookSdk.class, "isInitialized")).toReturn(true);
        FacebookSdk.loadDefaultsFromMetadata(mockContextWithAppIdAndClientToken());
        Assert.assertEquals("1234", FacebookSdk.getApplicationId());
        Assert.assertEquals("abcd", FacebookSdk.getClientToken());
    }

    @Test
    public void testLoadDefaultsDoesNotOverwrite() throws Exception {
        stub(method(FacebookSdk.class, "isInitialized")).toReturn(true);
        FacebookSdk.setApplicationId("hello");
        FacebookSdk.setClientToken("world");
        FacebookSdk.loadDefaultsFromMetadata(mockContextWithAppIdAndClientToken());
        Assert.assertEquals("hello", FacebookSdk.getApplicationId());
        Assert.assertEquals("world", FacebookSdk.getClientToken());
        Assert.assertEquals(false, FacebookSdk.getAutoLogAppEventsEnabled());
    }

    @Test
    public void testRequestCodeOffsetAfterInit() throws Exception {
        FacebookSdk.setApplicationId("123456789");
        FacebookSdk.sdkInitialize(application);
        try {
            FacebookSdk.sdkInitialize(application, 1000);
            Assert.fail();
        } catch (FacebookException exception) {
            Assert.assertEquals(CALLBACK_OFFSET_CHANGED_AFTER_INIT, exception.getMessage());
        }
    }

    @Test
    public void testRequestCodeOffsetNegative() throws Exception {
        FacebookSdk.setApplicationId("123456789");
        try {
            // last bit set, so negative
            FacebookSdk.sdkInitialize(application, -87117812);
            Assert.fail();
        } catch (FacebookException exception) {
            Assert.assertEquals(CALLBACK_OFFSET_NEGATIVE, exception.getMessage());
        }
    }

    @Test
    public void testRequestCodeOffset() throws Exception {
        FacebookSdk.setApplicationId("123456789");
        FacebookSdk.sdkInitialize(application, 1000);
        Assert.assertEquals(1000, FacebookSdk.getCallbackRequestCodeOffset());
    }

    @Test
    public void testRequestCodeRange() {
        FacebookSdk.setApplicationId("123456789");
        FacebookSdk.sdkInitialize(application, 1000);
        Assert.assertTrue(FacebookSdk.isFacebookRequestCode(1000));
        Assert.assertTrue(FacebookSdk.isFacebookRequestCode(1099));
        Assert.assertFalse(FacebookSdk.isFacebookRequestCode(999));
        Assert.assertFalse(FacebookSdk.isFacebookRequestCode(1100));
        Assert.assertFalse(FacebookSdk.isFacebookRequestCode(0));
    }
}

