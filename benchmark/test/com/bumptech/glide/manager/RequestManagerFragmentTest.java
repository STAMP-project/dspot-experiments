package com.bumptech.glide.manager;


import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import com.bumptech.glide.RequestManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class RequestManagerFragmentTest {
    private static final String TAG = "tag";

    private RequestManagerFragmentTest.Harness[] harnesses;

    @Test
    public void testSupportCanSetAndGetRequestManager() {
        runTest(new RequestManagerFragmentTest.TestCase() {
            @Override
            public void runTest(RequestManagerFragmentTest.Harness harness) {
                RequestManager manager = Mockito.mock(RequestManager.class);
                harness.setRequestManager(manager);
                Assert.assertEquals(manager, harness.getManager());
            }
        });
    }

    @Test
    public void testReturnsLifecycle() {
        runTest(new RequestManagerFragmentTest.TestCase() {
            @Override
            public void runTest(RequestManagerFragmentTest.Harness harness) {
                Assert.assertEquals(harness.getHarnessLifecycle(), harness.getFragmentLifecycle());
            }
        });
    }

    @Test
    public void testDoesNotAddNullRequestManagerToLifecycleWhenSet() {
        runTest(new RequestManagerFragmentTest.TestCase() {
            @Override
            public void runTest(RequestManagerFragmentTest.Harness harness) {
                harness.setRequestManager(null);
                Mockito.verify(harness.getHarnessLifecycle(), Mockito.never()).addListener(ArgumentMatchers.any(LifecycleListener.class));
            }
        });
    }

    @Test
    public void testCallsLifecycleStart() {
        runTest(new RequestManagerFragmentTest.TestCase() {
            @Override
            public void runTest(RequestManagerFragmentTest.Harness harness) {
                harness.getController().start();
                Mockito.verify(harness.getHarnessLifecycle()).onStart();
            }
        });
    }

    @Test
    public void testCallsRequestManagerStop() {
        runTest(new RequestManagerFragmentTest.TestCase() {
            @Override
            public void runTest(RequestManagerFragmentTest.Harness harness) {
                harness.getController().start().resume().pause().stop();
                Mockito.verify(harness.getHarnessLifecycle()).onStop();
            }
        });
    }

    @Test
    public void testCallsRequestManagerDestroy() {
        runTest(new RequestManagerFragmentTest.TestCase() {
            @Override
            public void runTest(RequestManagerFragmentTest.Harness harness) {
                harness.getController().start().resume().pause().stop().destroy();
                Mockito.verify(harness.getHarnessLifecycle()).onDestroy();
            }
        });
    }

    @Test
    public void testOnLowMemoryCallOnNullRequestManagerDoesNotCrash() {
        runTest(new RequestManagerFragmentTest.TestCase() {
            @Override
            public void runTest(RequestManagerFragmentTest.Harness harness) {
                harness.onLowMemory();
            }
        });
    }

    @Test
    public void testOnTrimMemoryCallOnNullRequestManagerDoesNotCrash() {
        runTest(new RequestManagerFragmentTest.TestCase() {
            @Override
            public void runTest(RequestManagerFragmentTest.Harness harness) {
                /* level */
                harness.onTrimMemory(100);
            }
        });
    }

    private interface TestCase {
        void runTest(RequestManagerFragmentTest.Harness harness);
    }

    private interface Harness {
        RequestManager getManager();

        void setRequestManager(RequestManager manager);

        ActivityFragmentLifecycle getHarnessLifecycle();

        ActivityFragmentLifecycle getFragmentLifecycle();

        ActivityController<?> getController();

        void onLowMemory();

        void onTrimMemory(@SuppressWarnings("SameParameterValue")
        int level);
    }

    @SuppressWarnings("deprecation")
    private static class RequestManagerHarness implements RequestManagerFragmentTest.Harness {
        private final ActivityController<Activity> controller;

        private final RequestManagerFragment fragment;

        private final ActivityFragmentLifecycle lifecycle = Mockito.mock(ActivityFragmentLifecycle.class);

        public RequestManagerHarness() {
            fragment = new RequestManagerFragment(lifecycle);
            controller = Robolectric.buildActivity(Activity.class).create();
            controller.get().getFragmentManager().beginTransaction().add(fragment, RequestManagerFragmentTest.TAG).commit();
            controller.get().getFragmentManager().executePendingTransactions();
        }

        @Override
        public String toString() {
            return "DefaultHarness";
        }

        @Override
        public RequestManager getManager() {
            return fragment.getRequestManager();
        }

        @Override
        public void setRequestManager(RequestManager requestManager) {
            fragment.setRequestManager(requestManager);
        }

        @Override
        public ActivityFragmentLifecycle getHarnessLifecycle() {
            return lifecycle;
        }

        @Override
        public ActivityFragmentLifecycle getFragmentLifecycle() {
            return fragment.getGlideLifecycle();
        }

        @Override
        public ActivityController<?> getController() {
            return controller;
        }

        @Override
        public void onLowMemory() {
            fragment.onLowMemory();
        }

        @Override
        public void onTrimMemory(int level) {
            fragment.onTrimMemory(level);
        }
    }

    private static class SupportRequestManagerHarness implements RequestManagerFragmentTest.Harness {
        private final SupportRequestManagerFragment supportFragment;

        private final ActivityController<FragmentActivity> supportController;

        private final ActivityFragmentLifecycle lifecycle = Mockito.mock(ActivityFragmentLifecycle.class);

        public SupportRequestManagerHarness() {
            supportFragment = new SupportRequestManagerFragment(lifecycle);
            supportController = Robolectric.buildActivity(FragmentActivity.class).create();
            supportController.get().getSupportFragmentManager().beginTransaction().add(supportFragment, RequestManagerFragmentTest.TAG).commit();
            supportController.get().getSupportFragmentManager().executePendingTransactions();
        }

        @Override
        public String toString() {
            return "SupportHarness";
        }

        @Override
        public RequestManager getManager() {
            return supportFragment.getRequestManager();
        }

        @Override
        public void setRequestManager(RequestManager manager) {
            supportFragment.setRequestManager(manager);
        }

        @Override
        public ActivityFragmentLifecycle getHarnessLifecycle() {
            return lifecycle;
        }

        @Override
        public ActivityFragmentLifecycle getFragmentLifecycle() {
            return supportFragment.getGlideLifecycle();
        }

        @Override
        public ActivityController<?> getController() {
            return supportController;
        }

        @Override
        public void onLowMemory() {
            supportFragment.onLowMemory();
        }

        @Override
        public void onTrimMemory(int level) {
            // Do nothing.
        }
    }
}

