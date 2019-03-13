package com.bumptech.glide;


import android.app.Application;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import com.bumptech.glide.manager.ConnectivityMonitor;
import com.bumptech.glide.manager.ConnectivityMonitor.ConnectivityListener;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.bumptech.glide.manager.RequestTracker;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.tests.BackgroundUtil;
import com.bumptech.glide.tests.GlideShadowLooper;
import com.bumptech.glide.tests.TearDownGlide;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, shadows = GlideShadowLooper.class)
public class RequestManagerTest {
    @Rule
    public TearDownGlide tearDownGlide = new TearDownGlide();

    @Mock
    private Lifecycle lifecycle = Mockito.mock(Lifecycle.class);

    @Mock
    private RequestManagerTreeNode treeNode = Mockito.mock(RequestManagerTreeNode.class);

    private RequestManager manager;

    private ConnectivityMonitor connectivityMonitor;

    private RequestTracker requestTracker;

    private ConnectivityListener connectivityListener;

    private Application context;

    private BaseTarget<Drawable> target;

    @Test
    public void testPauseRequestsPausesRequests() {
        manager.pauseRequests();
        Mockito.verify(requestTracker).pauseRequests();
    }

    @Test
    public void testResumeRequestsResumesRequests() {
        manager.resumeRequests();
        Mockito.verify(requestTracker).resumeRequests();
    }

    @Test
    public void testPausesRequestsOnStop() {
        manager.onStart();
        manager.onStop();
        Mockito.verify(requestTracker).pauseRequests();
    }

    @Test
    public void testResumesRequestsOnStart() {
        manager.onStart();
        Mockito.verify(requestTracker).resumeRequests();
    }

    @Test
    public void testClearsRequestsOnDestroy() {
        manager.onDestroy();
        Mockito.verify(requestTracker).clearRequests();
    }

    @Test
    public void testAddsConnectivityMonitorToLifecycleWhenConstructed() {
        Mockito.verify(lifecycle).addListener(ArgumentMatchers.eq(connectivityMonitor));
    }

    @Test
    public void testAddsSelfToLifecycleWhenConstructed() {
        Mockito.verify(lifecycle).addListener(ArgumentMatchers.eq(manager));
    }

    @Test
    public void testRestartsRequestOnConnected() {
        connectivityListener.onConnectivityChanged(true);
        Mockito.verify(requestTracker).restartRequests();
    }

    @Test
    public void testDoesNotRestartRequestsOnDisconnected() {
        connectivityListener.onConnectivityChanged(false);
        Mockito.verify(requestTracker, Mockito.never()).restartRequests();
    }

    @Test
    public void resumeRequests_whenCalledOnBackgroundThread_doesNotThrow() throws InterruptedException {
        BackgroundUtil.testInBackground(new BackgroundUtil.BackgroundTester() {
            @Override
            public void runTest() {
                manager.resumeRequests();
            }
        });
    }

    @Test
    public void pauseRequests_whenCalledOnBackgroundThread_doesNotThrow() throws InterruptedException {
        BackgroundUtil.testInBackground(new BackgroundUtil.BackgroundTester() {
            @Override
            public void runTest() {
                manager.pauseRequests();
            }
        });
    }

    @Test
    public void testDelegatesIsPausedToRequestTracker() {
        Mockito.when(requestTracker.isPaused()).thenReturn(true);
        Assert.assertTrue(manager.isPaused());
        Mockito.when(requestTracker.isPaused()).thenReturn(false);
        Assert.assertFalse(manager.isPaused());
    }

    @Test
    public void clear_withRequestStartedInSiblingManager_doesNotThrow() {
        final RequestManager child1 = new RequestManager(Glide.get(context), lifecycle, new RequestManagerTreeNode() {
            @NonNull
            @Override
            public Set<RequestManager> getDescendants() {
                return Collections.emptySet();
            }
        }, context);
        final RequestManager child2 = new RequestManager(Glide.get(context), lifecycle, new RequestManagerTreeNode() {
            @NonNull
            @Override
            public Set<RequestManager> getDescendants() {
                return Collections.emptySet();
            }
        }, context);
        new RequestManager(Glide.get(context), lifecycle, new RequestManagerTreeNode() {
            @NonNull
            @Override
            public Set<RequestManager> getDescendants() {
                return new java.util.HashSet(Arrays.asList(child1, child2));
            }
        }, context);
        File file = new File("fake");
        child1.load(file).into(target);
        child2.clear(target);
    }

    @Test
    public void clear_withRequestStartedInChildManager_doesNotThrow() {
        final RequestManager child = new RequestManager(Glide.get(context), lifecycle, new RequestManagerTreeNode() {
            @NonNull
            @Override
            public Set<RequestManager> getDescendants() {
                return Collections.emptySet();
            }
        }, context);
        RequestManager parent = new RequestManager(Glide.get(context), lifecycle, new RequestManagerTreeNode() {
            @NonNull
            @Override
            public Set<RequestManager> getDescendants() {
                return Collections.singleton(child);
            }
        }, context);
        File file = new File("fake");
        child.load(file).into(target);
        parent.clear(target);
    }

    @Test
    public void clear_withRequestStartedInParentManager_doesNotThrow() {
        final RequestManager child = new RequestManager(Glide.get(context), lifecycle, new RequestManagerTreeNode() {
            @NonNull
            @Override
            public Set<RequestManager> getDescendants() {
                return Collections.emptySet();
            }
        }, context);
        RequestManager parent = new RequestManager(Glide.get(context), lifecycle, new RequestManagerTreeNode() {
            @NonNull
            @Override
            public Set<RequestManager> getDescendants() {
                return Collections.singleton(child);
            }
        }, context);
        File file = new File("fake");
        parent.load(file).into(target);
        child.clear(target);
    }
}

