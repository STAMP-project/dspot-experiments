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
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseCorePluginsTest extends ResetPluginsParseTest {
    @Test
    public void testQueryControllerDefaultImpl() {
        ParseQueryController controller = ParseCorePlugins.getInstance().getQueryController();
        Assert.assertThat(controller, CoreMatchers.instanceOf(CacheQueryController.class));
    }

    @Test
    public void testRegisterQueryController() {
        ParseQueryController controller = new ParseCorePluginsTest.TestQueryController();
        ParseCorePlugins.getInstance().registerQueryController(controller);
        Assert.assertSame(controller, ParseCorePlugins.getInstance().getQueryController());
    }

    @Test(expected = IllegalStateException.class)
    public void testRegisterQueryControllerWhenAlreadySet() {
        ParseCorePlugins.getInstance().getQueryController();// sets to default

        ParseQueryController controller = new ParseCorePluginsTest.TestQueryController();
        ParseCorePlugins.getInstance().registerQueryController(controller);
    }

    // TODO(grantland): testFileControllerDefaultImpl with ParseFileController interface
    @Test
    public void testRegisterFileController() {
        ParseFileController controller = new ParseCorePluginsTest.TestFileController();
        ParseCorePlugins.getInstance().registerFileController(controller);
        Assert.assertSame(controller, ParseCorePlugins.getInstance().getFileController());
    }

    // TODO(grantland): testRegisterFileControllerWhenAlreadySet when getCacheDir is no longer global
    // TODO(mengyan): testAnalyticsControllerDefaultImpl when getEventuallyQueue is no longer global
    @Test
    public void testRegisterAnalyticsController() {
        ParseAnalyticsController controller = new ParseCorePluginsTest.TestAnalyticsController();
        ParseCorePlugins.getInstance().registerAnalyticsController(controller);
        Assert.assertSame(controller, ParseCorePlugins.getInstance().getAnalyticsController());
    }

    // TODO(mengyan): testRegisterAnalyticsControllerWhenAlreadySet when getEventuallyQueue is no longer global
    @Test
    public void testCloudCodeControllerDefaultImpl() {
        ParseCloudCodeController controller = ParseCorePlugins.getInstance().getCloudCodeController();
        Assert.assertThat(controller, CoreMatchers.instanceOf(ParseCloudCodeController.class));
    }

    @Test
    public void testRegisterCloudCodeController() {
        ParseCloudCodeController controller = new ParseCorePluginsTest.TestCloudCodeController();
        ParseCorePlugins.getInstance().registerCloudCodeController(controller);
        Assert.assertSame(controller, ParseCorePlugins.getInstance().getCloudCodeController());
    }

    @Test(expected = IllegalStateException.class)
    public void testRegisterCloudCodeControllerWhenAlreadySet() {
        ParseCorePlugins.getInstance().getCloudCodeController();// sets to default

        ParseCloudCodeController controller = new ParseCorePluginsTest.TestCloudCodeController();
        ParseCorePlugins.getInstance().registerCloudCodeController(controller);
    }

    // TODO(mengyan): testConfigControllerDefaultImpl when getCacheDir is no longer global
    @Test
    public void testRegisterConfigController() {
        ParseConfigController controller = new ParseCorePluginsTest.TestConfigController();
        ParseCorePlugins.getInstance().registerConfigController(controller);
        Assert.assertSame(controller, ParseCorePlugins.getInstance().getConfigController());
    }

    // TODO(mengyan): testRegisterConfigControllerWhenAlreadySet when getCacheDir is no longer global
    @Test
    public void testPushControllerDefaultImpl() {
        ParsePushController controller = ParseCorePlugins.getInstance().getPushController();
        Assert.assertThat(controller, CoreMatchers.instanceOf(ParsePushController.class));
    }

    @Test
    public void testRegisterPushController() {
        ParsePushController controller = new ParseCorePluginsTest.TestPushController();
        ParseCorePlugins.getInstance().registerPushController(controller);
        Assert.assertSame(controller, ParseCorePlugins.getInstance().getPushController());
    }

    @Test(expected = IllegalStateException.class)
    public void testRegisterPushControllerWhenAlreadySet() {
        ParseCorePlugins.getInstance().getPushController();// sets to default

        ParsePushController controller = new ParseCorePluginsTest.TestPushController();
        ParseCorePlugins.getInstance().registerPushController(controller);
    }

    @Test
    public void testRegisterPushChannelsController() {
        ParsePushChannelsController controller = new ParsePushChannelsController();
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
        Assert.assertSame(controller, ParseCorePlugins.getInstance().getPushChannelsController());
    }

    @Test(expected = IllegalStateException.class)
    public void testRegisterPushChannelsControllerWhenAlreadySet() {
        ParseCorePlugins.getInstance().getPushChannelsController();// sets to default

        ParsePushChannelsController controller = new ParsePushChannelsController();
        ParseCorePlugins.getInstance().registerPushChannelsController(controller);
    }

    private static class TestQueryController implements ParseQueryController {
        @Override
        public <T extends ParseObject> Task<List<T>> findAsync(ParseQuery.State<T> state, ParseUser user, Task<Void> cancellationToken) {
            return null;
        }

        @Override
        public <T extends ParseObject> Task<Integer> countAsync(ParseQuery.State<T> state, ParseUser user, Task<Void> cancellationToken) {
            return null;
        }

        @Override
        public <T extends ParseObject> Task<T> getFirstAsync(ParseQuery.State<T> state, ParseUser user, Task<Void> cancellationToken) {
            return null;
        }
    }

    private static class TestFileController extends ParseFileController {
        public TestFileController() {
            super(null, null);
        }
    }

    private static class TestAnalyticsController extends ParseAnalyticsController {
        public TestAnalyticsController() {
            super(null);
        }
    }

    private static class TestCloudCodeController extends ParseCloudCodeController {
        public TestCloudCodeController() {
            super(null);
        }
    }

    private static class TestConfigController extends ParseConfigController {
        public TestConfigController() {
            super(null, null);
        }
    }

    private static class TestPushController extends ParsePushController {
        public TestPushController() {
            super(null);
        }
    }
}

