package com.bumptech.glide.manager;


import Build.VERSION_CODES;
import Build.VERSION_CODES.JELLY_BEAN;
import RequestManagerRetriever.FRAGMENT_TAG;
import RuntimeEnvironment.application;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.tests.BackgroundUtil;
import com.bumptech.glide.tests.GlideShadowLooper;
import com.bumptech.glide.tests.TearDownGlide;
import com.bumptech.glide.tests.Util;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, shadows = GlideShadowLooper.class)
public class RequestManagerRetrieverTest {
    @Rule
    public TearDownGlide tearDownGlide = new TearDownGlide();

    private static final String PARENT_TAG = "parent";

    private RequestManagerRetrieverTest.RetrieverHarness[] harnesses;

    private RequestManagerRetriever retriever;

    private int initialSdkVersion;

    @Test
    public void testCreatesNewFragmentIfNoneExists() {
        for (RequestManagerRetrieverTest.RetrieverHarness harness : harnesses) {
            harness.doGet();
            Shadows.shadowOf(Looper.getMainLooper()).runToEndOfTasks();
            Assert.assertTrue(harness.hasFragmentWithTag(FRAGMENT_TAG));
        }
    }

    @Test
    public void testReturnsNewManagerIfNoneExists() {
        for (RequestManagerRetrieverTest.RetrieverHarness harness : harnesses) {
            Assert.assertNotNull(harness.doGet());
        }
    }

    @Test
    public void testReturnsExistingRequestManagerIfExists() {
        for (RequestManagerRetrieverTest.RetrieverHarness harness : harnesses) {
            RequestManager requestManager = Mockito.mock(RequestManager.class);
            harness.addFragmentWithTag(FRAGMENT_TAG, requestManager);
            Assert.assertEquals(requestManager, harness.doGet());
        }
    }

    @Test
    public void testReturnsNewRequestManagerIfFragmentExistsButHasNoRequestManager() {
        for (RequestManagerRetrieverTest.RetrieverHarness harness : harnesses) {
            harness.addFragmentWithTag(FRAGMENT_TAG, null);
            Assert.assertNotNull(harness.doGet());
        }
    }

    @Test
    public void testSavesNewRequestManagerToFragmentIfCreatesRequestManagerForExistingFragment() {
        for (RequestManagerRetrieverTest.RetrieverHarness harness : harnesses) {
            harness.addFragmentWithTag(FRAGMENT_TAG, null);
            RequestManager first = harness.doGet();
            RequestManager second = harness.doGet();
            Assert.assertEquals(first, second);
        }
    }

    @Test
    public void testHasValidTag() {
        Assert.assertEquals(RequestManagerRetriever.class.getPackage().getName(), FRAGMENT_TAG);
    }

    @Test
    public void testCanGetRequestManagerFromActivity() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().start().get();
        RequestManager manager = retriever.get(activity);
        Assert.assertEquals(manager, retriever.get(activity));
    }

    @Test
    public void testSupportCanGetRequestManagerFromActivity() {
        FragmentActivity fragmentActivity = Robolectric.buildActivity(FragmentActivity.class).create().start().get();
        RequestManager manager = retriever.get(fragmentActivity);
        Assert.assertEquals(manager, retriever.get(fragmentActivity));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testCanGetRequestManagerFromFragment() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
        Fragment fragment = new Fragment();
        activity.getFragmentManager().beginTransaction().add(fragment, RequestManagerRetrieverTest.PARENT_TAG).commit();
        activity.getFragmentManager().executePendingTransactions();
        RequestManager manager = retriever.get(fragment);
        Assert.assertEquals(manager, retriever.get(fragment));
    }

    @Test
    public void testSupportCanGetRequestManagerFromFragment() {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).create().start().resume().get();
        android.support.v4.app.Fragment fragment = new android.support.v4.app.Fragment();
        activity.getSupportFragmentManager().beginTransaction().add(fragment, RequestManagerRetrieverTest.PARENT_TAG).commit();
        activity.getSupportFragmentManager().executePendingTransactions();
        RequestManager manager = retriever.get(fragment);
        Assert.assertEquals(manager, retriever.get(fragment));
    }

    @Test
    public void testCanGetRequestManagerFromDetachedFragment() {
        helpTestCanGetRequestManagerFromDetachedFragment();
    }

    @Test
    public void testCanGetRequestManagerFromDetachedFragment_PreJellyBeanMr1() {
        Util.setSdkVersionInt(JELLY_BEAN);
        helpTestCanGetRequestManagerFromDetachedFragment();
    }

    @Test
    public void testSupportCanGetRequestManagerFromDetachedFragment() {
        helpTestSupportCanGetRequestManagerFromDetachedFragment();
    }

    @Test
    public void testSupportCanGetRequestManagerFromDetachedFragment_PreJellyBeanMr1() {
        Util.setSdkVersionInt(JELLY_BEAN);
        helpTestSupportCanGetRequestManagerFromDetachedFragment();
    }

    @SuppressWarnings("deprecation")
    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfFragmentNotAttached() {
        Fragment fragment = new Fragment();
        retriever.get(fragment);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsIfSupportFragmentNotAttached() {
        android.support.v4.app.Fragment fragment = new android.support.v4.app.Fragment();
        retriever.get(fragment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfActivityDestroyed() {
        RequestManagerRetrieverTest.RetrieverHarness harness = new RequestManagerRetrieverTest.DefaultRetrieverHarness();
        harness.getController().pause().stop().destroy();
        harness.doGet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfFragmentActivityDestroyed() {
        RequestManagerRetrieverTest.RetrieverHarness harness = new RequestManagerRetrieverTest.SupportRetrieverHarness();
        harness.getController().pause().stop().destroy();
        harness.doGet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfGivenNullContext() {
        retriever.get(((Context) (null)));
    }

    @Test
    public void testChecksIfContextIsFragmentActivity() {
        RequestManagerRetrieverTest.RetrieverHarness harness = new RequestManagerRetrieverTest.SupportRetrieverHarness();
        RequestManager requestManager = harness.doGet();
        Assert.assertEquals(requestManager, retriever.get(((Context) (harness.getController().get()))));
    }

    @Test
    public void testChecksIfContextIsActivity() {
        RequestManagerRetrieverTest.RetrieverHarness harness = new RequestManagerRetrieverTest.DefaultRetrieverHarness();
        RequestManager requestManager = harness.doGet();
        Assert.assertEquals(requestManager, retriever.get(((Context) (harness.getController().get()))));
    }

    @Test
    public void testHandlesContextWrappersForActivities() {
        RequestManagerRetrieverTest.RetrieverHarness harness = new RequestManagerRetrieverTest.DefaultRetrieverHarness();
        RequestManager requestManager = harness.doGet();
        ContextWrapper contextWrapper = new ContextWrapper(harness.getController().get());
        Assert.assertEquals(requestManager, retriever.get(contextWrapper));
    }

    @Test
    public void testHandlesContextWrappersForApplication() {
        ContextWrapper contextWrapper = new ContextWrapper(RuntimeEnvironment.application);
        RequestManager requestManager = retriever.get(application);
        Assert.assertEquals(requestManager, retriever.get(contextWrapper));
    }

    @Test
    public void testReturnsNonNullManagerIfGivenApplicationContext() {
        Assert.assertNotNull(retriever.get(application));
    }

    @Test
    public void testApplicationRequestManagerIsNotPausedWhenRetrieved() {
        RequestManager manager = retriever.get(application);
        Assert.assertFalse(manager.isPaused());
    }

    @Test
    public void testApplicationRequestManagerIsNotReResumedAfterFirstRetrieval() {
        RequestManager manager = retriever.get(application);
        manager.pauseRequests();
        manager = retriever.get(application);
        Assert.assertTrue(manager.isPaused());
    }

    @Test
    public void testDoesNotThrowWhenGetWithContextCalledFromBackgroundThread() throws InterruptedException {
        BackgroundUtil.testInBackground(new BackgroundUtil.BackgroundTester() {
            @Override
            public void runTest() {
                retriever.get(application);
            }
        });
    }

    // See Issue #117: https://github.com/bumptech/glide/issues/117.
    @Test
    public void testCanCallGetInOnAttachToWindowInFragmentInViewPager() {
        // Robolectric by default runs messages posted to the main looper synchronously, the
        // framework does not. We post
        // to the main thread here to work around an issue caused by a recursive method call so we
        // need (and reasonably
        // expect) our message to not run immediately
        Shadows.shadowOf(Looper.getMainLooper()).pause();
        Robolectric.buildActivity(Issue117Activity.class).create().start().resume().visible();
    }

    @Test
    @RequiresApi(VERSION_CODES.JELLY_BEAN_MR1)
    public void testDoesNotThrowIfAskedToGetManagerForActivityPreJellYBeanMr1() {
        Util.setSdkVersionInt(JELLY_BEAN);
        Activity activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
        Activity spyActivity = Mockito.spy(activity);
        Mockito.when(spyActivity.isDestroyed()).thenThrow(new NoSuchMethodError());
        Assert.assertNotNull(retriever.get(spyActivity));
    }

    @SuppressWarnings("deprecation")
    @Test
    @RequiresApi(VERSION_CODES.JELLY_BEAN_MR1)
    public void testDoesNotThrowIfAskedToGetManagerForFragmentPreJellyBeanMr1() {
        Util.setSdkVersionInt(JELLY_BEAN);
        Activity activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
        Fragment fragment = new Fragment();
        activity.getFragmentManager().beginTransaction().add(fragment, "test").commit();
        Fragment spyFragment = Mockito.spy(fragment);
        Mockito.when(spyFragment.getChildFragmentManager()).thenThrow(new NoSuchMethodError());
        Assert.assertNotNull(retriever.get(spyFragment));
    }

    private interface RetrieverHarness {
        ActivityController<?> getController();

        RequestManager doGet();

        boolean hasFragmentWithTag(String tag);

        void addFragmentWithTag(String tag, RequestManager manager);
    }

    final class DefaultRetrieverHarness implements RequestManagerRetrieverTest.RetrieverHarness {
        private final ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class);

        private final android.support.v4.app.Fragment parent;

        DefaultRetrieverHarness() {
            this.parent = new Fragment();
            controller.create();
            controller.get().getFragmentManager().beginTransaction().add(parent, RequestManagerRetrieverTest.PARENT_TAG).commitAllowingStateLoss();
            controller.get().getFragmentManager().executePendingTransactions();
            controller.start().resume();
        }

        @Override
        public ActivityController<?> getController() {
            return controller;
        }

        @Override
        public RequestManager doGet() {
            return retriever.get(controller.get());
        }

        @Override
        public boolean hasFragmentWithTag(String tag) {
            return null != (controller.get().getFragmentManager().findFragmentByTag(FRAGMENT_TAG));
        }

        @SuppressWarnings("deprecation")
        @Override
        public void addFragmentWithTag(String tag, RequestManager requestManager) {
            RequestManagerFragment fragment = new RequestManagerFragment();
            fragment.setRequestManager(requestManager);
            controller.get().getFragmentManager().beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss();
            controller.get().getFragmentManager().executePendingTransactions();
        }
    }

    public class SupportRetrieverHarness implements RequestManagerRetrieverTest.RetrieverHarness {
        private final ActivityController<FragmentActivity> controller = Robolectric.buildActivity(FragmentActivity.class);

        private final android.support.v4.app.Fragment parent;

        public SupportRetrieverHarness() {
            this.parent = new android.support.v4.app.Fragment();
            controller.create();
            controller.get().getSupportFragmentManager().beginTransaction().add(parent, RequestManagerRetrieverTest.PARENT_TAG).commitAllowingStateLoss();
            controller.get().getSupportFragmentManager().executePendingTransactions();
            controller.start().resume();
        }

        @Override
        public ActivityController<?> getController() {
            return controller;
        }

        @Override
        public RequestManager doGet() {
            return retriever.get(controller.get());
        }

        @Override
        public boolean hasFragmentWithTag(String tag) {
            return (controller.get().getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG)) != null;
        }

        @Override
        public void addFragmentWithTag(String tag, RequestManager manager) {
            SupportRequestManagerFragment fragment = new SupportRequestManagerFragment();
            fragment.setRequestManager(manager);
            controller.get().getSupportFragmentManager().beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss();
            controller.get().getSupportFragmentManager().executePendingTransactions();
        }
    }
}

