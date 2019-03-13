package org.robolectric;


import android.os.Build.VERSION_CODES;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.JUnit4;
import org.robolectric.annotation.Config;
import org.robolectric.pluginapi.SdkPicker;
import org.robolectric.plugins.SdkCollection;
import org.robolectric.util.inject.Injector;


@RunWith(JUnit4.class)
public class RobolectricTestRunnerMultiApiTest {
    private static final int[] APIS_FOR_TEST = new int[]{ VERSION_CODES.JELLY_BEAN, VERSION_CODES.JELLY_BEAN_MR1, VERSION_CODES.JELLY_BEAN_MR2, VERSION_CODES.KITKAT, VERSION_CODES.LOLLIPOP, VERSION_CODES.LOLLIPOP_MR1, VERSION_CODES.M };

    private static SdkPicker delegateSdkPicker;

    private static final Injector INJECTOR = RobolectricTestRunner.defaultInjector().bind(SdkPicker.class, ( config, usesSdk) -> RobolectricTestRunnerMultiApiTest.delegateSdkPicker.selectSdks(config, usesSdk)).build();

    private RobolectricTestRunner runner;

    private RunNotifier runNotifier;

    private RobolectricTestRunnerMultiApiTest.MyRunListener runListener;

    private int numSupportedApis;

    private String priorResourcesMode;

    private String priorAlwaysInclude;

    private SdkCollection sdkCollection;

    @Test
    public void createChildrenForEachSupportedApi() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestWithNoConfig.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.JELLY_BEAN, VERSION_CODES.JELLY_BEAN_MR1, VERSION_CODES.JELLY_BEAN_MR2, VERSION_CODES.KITKAT, VERSION_CODES.LOLLIPOP, VERSION_CODES.LOLLIPOP_MR1, VERSION_CODES.M);
    }

    @Test
    public void withConfigSdkLatest_shouldUseLatestSupported() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestMethodWithNewestSdk.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.M);
    }

    @Test
    public void withConfigSdkAndMinMax_shouldUseMinMax() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestMethodWithSdkAndMinMax.class);
        try {
            runner.getChildren();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains(("sdk and minSdk/maxSdk may not be specified together" + " (sdk=[16], minSdk=19, maxSdk=21)"));
        }
    }

    @Test
    public void withEnabledSdks_createChildrenForEachSupportedSdk() throws Throwable {
        RobolectricTestRunnerMultiApiTest.delegateSdkPicker = new org.robolectric.plugins.DefaultSdkPicker(new SdkCollection(() -> map(16, 17)), null);
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestWithNoConfig.class);
        assertThat(runner.getChildren()).hasSize(2);
    }

    @Test
    public void shouldAddApiLevelToNameOfAllButHighestNumberedMethodName() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestMethodUpToAndIncludingLollipop.class);
        assertThat(runner.getChildren().get(0).getName()).isEqualTo("testSomeApiLevel[16]");
        assertThat(runner.getChildren().get(1).getName()).isEqualTo("testSomeApiLevel[17]");
        assertThat(runner.getChildren().get(2).getName()).isEqualTo("testSomeApiLevel[18]");
        assertThat(runner.getChildren().get(3).getName()).isEqualTo("testSomeApiLevel[19]");
        assertThat(runner.getChildren().get(4).getName()).isEqualTo("testSomeApiLevel");
    }

    @Test
    public void noConfig() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestWithNoConfig.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.JELLY_BEAN, VERSION_CODES.JELLY_BEAN_MR1, VERSION_CODES.JELLY_BEAN_MR2, VERSION_CODES.KITKAT, VERSION_CODES.LOLLIPOP, VERSION_CODES.LOLLIPOP_MR1, VERSION_CODES.M);
        runner.run(runNotifier);
        assertThat(runListener.ignored).isEmpty();
        assertThat(runListener.finished).hasSize(numSupportedApis);
    }

    @Test
    public void classConfigWithSdkGroup() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestClassConfigWithSdkGroup.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.JELLY_BEAN, VERSION_CODES.LOLLIPOP);
        runner.run(runNotifier);
        assertThat(runListener.ignored).isEmpty();
        // Test method should be run for JellyBean and Lollipop
        assertThat(runListener.finished).hasSize(2);
    }

    @Test
    public void methodConfigWithSdkGroup() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestMethodConfigWithSdkGroup.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.JELLY_BEAN, VERSION_CODES.LOLLIPOP);
        runner.run(runNotifier);
        assertThat(runListener.ignored).isEmpty();
        // Test method should be run for JellyBean and Lollipop
        assertThat(runListener.finished).hasSize(2);
    }

    @Test
    public void classConfigMinSdk() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestClassLollipopAndUp.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.LOLLIPOP, VERSION_CODES.LOLLIPOP_MR1, VERSION_CODES.M);
        runner.run(runNotifier);
        assertThat(runListener.ignored).isEmpty();
        int sdksAfterAndIncludingLollipop = 3;
        assertThat(runListener.finished).hasSize(sdksAfterAndIncludingLollipop);
    }

    @Test
    public void classConfigMaxSdk() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestClassUpToAndIncludingLollipop.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.JELLY_BEAN, VERSION_CODES.JELLY_BEAN_MR1, VERSION_CODES.JELLY_BEAN_MR2, VERSION_CODES.KITKAT, VERSION_CODES.LOLLIPOP);
        runner.run(runNotifier);
        assertThat(runListener.ignored).isEmpty();
        int sdksUpToAndIncludingLollipop = 5;
        assertThat(runListener.finished).hasSize(sdksUpToAndIncludingLollipop);
    }

    @Test
    public void classConfigWithMinSdkAndMaxSdk() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestClassBetweenJellyBeanMr2AndLollipop.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.JELLY_BEAN_MR2, VERSION_CODES.KITKAT, VERSION_CODES.LOLLIPOP);
        runner.run(runNotifier);
        assertThat(runListener.ignored).isEmpty();
        // Since test method should only be run once
        int sdksInclusivelyBetweenJellyBeanMr2AndLollipop = 3;
        assertThat(runListener.finished).hasSize(sdksInclusivelyBetweenJellyBeanMr2AndLollipop);
    }

    @Test
    public void methodConfigMinSdk() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestMethodLollipopAndUp.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.LOLLIPOP, VERSION_CODES.LOLLIPOP_MR1, VERSION_CODES.M);
        runner.run(runNotifier);
        assertThat(runListener.ignored).isEmpty();
        int sdksAfterAndIncludingLollipop = 3;
        assertThat(runListener.finished).hasSize(sdksAfterAndIncludingLollipop);
    }

    @Test
    public void methodConfigMaxSdk() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestMethodUpToAndIncludingLollipop.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.JELLY_BEAN, VERSION_CODES.JELLY_BEAN_MR1, VERSION_CODES.JELLY_BEAN_MR2, VERSION_CODES.KITKAT, VERSION_CODES.LOLLIPOP);
        runner.run(runNotifier);
        assertThat(runListener.ignored).isEmpty();
        int sdksUpToAndIncludingLollipop = 5;
        assertThat(runListener.finished).hasSize(sdksUpToAndIncludingLollipop);
    }

    @Test
    public void methodConfigWithMinSdkAndMaxSdk() throws Throwable {
        runner = runnerOf(RobolectricTestRunnerMultiApiTest.TestMethodBetweenJellyBeanMr2AndLollipop.class);
        assertThat(RobolectricTestRunnerMultiApiTest.apisFor(runner.getChildren())).containsExactly(VERSION_CODES.JELLY_BEAN_MR2, VERSION_CODES.KITKAT, VERSION_CODES.LOLLIPOP);
        runner.run(runNotifier);
        assertThat(runListener.ignored).isEmpty();
        int sdksInclusivelyBetweenJellyBeanMr2AndLollipop = 3;
        assertThat(runListener.finished).hasSize(sdksInclusivelyBetweenJellyBeanMr2AndLollipop);
    }

    @Config(sdk = Config.ALL_SDKS)
    public static class TestWithNoConfig {
        @Test
        public void test() {
        }
    }

    @Config(sdk = { VERSION_CODES.JELLY_BEAN, VERSION_CODES.LOLLIPOP })
    public static class TestClassConfigWithSdkGroup {
        @Test
        public void testShouldRunApi18() {
            assertThat(Build.VERSION.SDK_INT).isIn(Range.closed(VERSION_CODES.JELLY_BEAN, VERSION_CODES.LOLLIPOP));
        }
    }

    @Config(sdk = Config.ALL_SDKS)
    public static class TestMethodConfigWithSdkGroup {
        @Config(sdk = { VERSION_CODES.JELLY_BEAN, VERSION_CODES.LOLLIPOP })
        @Test
        public void testShouldRunApi16() {
            assertThat(Build.VERSION.SDK_INT).isIn(Range.closed(VERSION_CODES.JELLY_BEAN, VERSION_CODES.LOLLIPOP));
        }
    }

    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public static class TestClassLollipopAndUp {
        @Test
        public void testSomeApiLevel() {
            assertThat(Build.VERSION.SDK_INT).isAtLeast(VERSION_CODES.LOLLIPOP);
        }
    }

    @Config(maxSdk = VERSION_CODES.LOLLIPOP)
    public static class TestClassUpToAndIncludingLollipop {
        @Test
        public void testSomeApiLevel() {
            assertThat(Build.VERSION.SDK_INT).isAtMost(VERSION_CODES.LOLLIPOP);
        }
    }

    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2, maxSdk = VERSION_CODES.LOLLIPOP)
    public static class TestClassBetweenJellyBeanMr2AndLollipop {
        @Test
        public void testSomeApiLevel() {
            assertThat(Build.VERSION.SDK_INT).isIn(Range.closed(VERSION_CODES.JELLY_BEAN_MR2, VERSION_CODES.LOLLIPOP));
        }
    }

    @Config(sdk = Config.ALL_SDKS)
    public static class TestMethodLollipopAndUp {
        @Config(minSdk = VERSION_CODES.LOLLIPOP)
        @Test
        public void testSomeApiLevel() {
            assertThat(Build.VERSION.SDK_INT).isAtLeast(VERSION_CODES.LOLLIPOP);
        }
    }

    @Config(sdk = Config.ALL_SDKS)
    public static class TestMethodUpToAndIncludingLollipop {
        @Config(maxSdk = VERSION_CODES.LOLLIPOP)
        @Test
        public void testSomeApiLevel() {
            assertThat(Build.VERSION.SDK_INT).isAtMost(VERSION_CODES.LOLLIPOP);
        }
    }

    @Config(sdk = Config.ALL_SDKS)
    public static class TestMethodBetweenJellyBeanMr2AndLollipop {
        @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2, maxSdk = VERSION_CODES.LOLLIPOP)
        @Test
        public void testSomeApiLevel() {
            assertThat(Build.VERSION.SDK_INT).isIn(Range.closed(VERSION_CODES.JELLY_BEAN_MR2, VERSION_CODES.LOLLIPOP));
        }
    }

    public static class TestMethodWithNewestSdk {
        @Config(sdk = Config.NEWEST_SDK)
        @Test
        public void testWithLatest() {
            assertThat(Build.VERSION.SDK_INT).isEqualTo(VERSION_CODES.M);
        }
    }

    @Config(sdk = Config.ALL_SDKS)
    public static class TestMethodWithSdkAndMinMax {
        @Config(sdk = VERSION_CODES.JELLY_BEAN, minSdk = VERSION_CODES.KITKAT, maxSdk = VERSION_CODES.LOLLIPOP)
        @Test
        public void testWithKitKatAndLollipop() {
            assertThat(Build.VERSION.SDK_INT).isIn(Range.closed(VERSION_CODES.KITKAT, VERSION_CODES.LOLLIPOP));
        }
    }

    private static class MyRunListener extends RunListener {
        private List<String> started = new ArrayList<>();

        private List<String> finished = new ArrayList<>();

        private List<String> ignored = new ArrayList<>();

        @Override
        public void testStarted(Description description) throws Exception {
            started.add(description.getDisplayName());
        }

        @Override
        public void testFinished(Description description) throws Exception {
            finished.add(description.getDisplayName());
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            ignored.add(description.getDisplayName());
        }
    }
}

