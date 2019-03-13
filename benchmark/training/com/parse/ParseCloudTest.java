/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import bolts.Task;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// For android.os.Looper
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseCloudTest extends ResetPluginsParseTest {
    // region testGetCloudCodeController
    @Test
    public void testGetCloudCodeController() {
        ParseCloudCodeController controller = Mockito.mock(ParseCloudCodeController.class);
        ParseCorePlugins.getInstance().registerCloudCodeController(controller);
        Assert.assertSame(controller, ParseCloud.getCloudCodeController());
    }

    // endregion
    // region testCallFunctions
    @Test
    public void testCallFunctionAsync() throws Exception {
        ParseCloudCodeController controller = mockParseCloudCodeControllerWithResponse("result");
        ParseCorePlugins.getInstance().registerCloudCodeController(controller);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("key1", Arrays.asList(1, 2, 3));
        parameters.put("key2", "value1");
        Task cloudCodeTask = ParseCloud.callFunctionInBackground("name", parameters);
        ParseTaskUtils.wait(cloudCodeTask);
        Mockito.verify(controller, Mockito.times(1)).callFunctionInBackground(ArgumentMatchers.eq("name"), ArgumentMatchers.eq(parameters), ArgumentMatchers.isNull(String.class));
        Assert.assertTrue(cloudCodeTask.isCompleted());
        Assert.assertNull(cloudCodeTask.getError());
        Assert.assertThat(cloudCodeTask.getResult(), IsInstanceOf.instanceOf(String.class));
        Assert.assertEquals("result", cloudCodeTask.getResult());
    }

    @Test
    public void testCallFunctionSync() throws Exception {
        ParseCloudCodeController controller = mockParseCloudCodeControllerWithResponse("result");
        ParseCorePlugins.getInstance().registerCloudCodeController(controller);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("key1", Arrays.asList(1, 2, 3));
        parameters.put("key2", "value1");
        Object result = ParseCloud.callFunction("name", parameters);
        Mockito.verify(controller, Mockito.times(1)).callFunctionInBackground(ArgumentMatchers.eq("name"), ArgumentMatchers.eq(parameters), ArgumentMatchers.isNull(String.class));
        Assert.assertThat(result, IsInstanceOf.instanceOf(String.class));
        Assert.assertEquals("result", result);
    }

    @Test
    public void testCallFunctionNullCallback() {
        ParseCloudCodeController controller = mockParseCloudCodeControllerWithResponse("result");
        ParseCorePlugins.getInstance().registerCloudCodeController(controller);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("key1", Arrays.asList(1, 2, 3));
        parameters.put("key2", "value1");
        ParseCloud.callFunctionInBackground("name", parameters, null);
        Mockito.verify(controller, Mockito.times(1)).callFunctionInBackground(ArgumentMatchers.eq("name"), ArgumentMatchers.eq(parameters), ArgumentMatchers.isNull(String.class));
    }

    @Test
    public void testCallFunctionNormalCallback() throws Exception {
        ParseCloudCodeController controller = mockParseCloudCodeControllerWithResponse("result");
        ParseCorePlugins.getInstance().registerCloudCodeController(controller);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("key1", Arrays.asList(1, 2, 3));
        parameters.put("key2", "value1");
        final Semaphore done = new Semaphore(0);
        ParseCloud.callFunctionInBackground("name", parameters, new FunctionCallback<Object>() {
            @Override
            public void done(Object result, ParseException e) {
                Assert.assertNull(e);
                Assert.assertThat(result, IsInstanceOf.instanceOf(String.class));
                Assert.assertEquals("result", result);
                done.release();
            }
        });
        // Make sure the callback is called
        Assert.assertTrue(done.tryAcquire(1, 10, TimeUnit.SECONDS));
        Mockito.verify(controller, Mockito.times(1)).callFunctionInBackground(ArgumentMatchers.eq("name"), ArgumentMatchers.eq(parameters), ArgumentMatchers.isNull(String.class));
    }
}

