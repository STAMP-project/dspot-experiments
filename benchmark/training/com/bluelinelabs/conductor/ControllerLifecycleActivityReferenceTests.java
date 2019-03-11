package com.bluelinelabs.conductor;


import Controller.LifecycleListener;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.bluelinelabs.conductor.util.ActivityProxy;
import com.bluelinelabs.conductor.util.MockChangeHandler;
import com.bluelinelabs.conductor.util.TestController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ControllerLifecycleActivityReferenceTests {
    private Router router;

    private ActivityProxy activityProxy;

    @Test
    public void testSingleControllerActivityOnPush() {
        Controller controller = new TestController();
        Assert.assertNull(controller.getActivity());
        ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener listener = new ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener();
        controller.addLifecycleListener(listener);
        router.pushController(RouterTransaction.with(controller).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertEquals(Collections.singletonList(true), listener.changeEndReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postCreateViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postAttachReferences);
        Assert.assertEquals(Collections.emptyList(), listener.postDetachReferences);
        Assert.assertEquals(Collections.emptyList(), listener.postDestroyViewReferences);
        Assert.assertEquals(Collections.emptyList(), listener.postDestroyReferences);
    }

    @Test
    public void testChildControllerActivityOnPush() {
        Controller parent = new TestController();
        router.pushController(RouterTransaction.with(parent).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        TestController child = new TestController();
        Assert.assertNull(getActivity());
        ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener listener = new ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener();
        addLifecycleListener(listener);
        Router childRouter = parent.getChildRouter(((ViewGroup) (parent.getView().findViewById(TestController.VIEW_ID))));
        childRouter.pushController(RouterTransaction.with(child).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertEquals(Collections.singletonList(true), listener.changeEndReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postCreateViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postAttachReferences);
        Assert.assertEquals(Collections.emptyList(), listener.postDetachReferences);
        Assert.assertEquals(Collections.emptyList(), listener.postDestroyViewReferences);
        Assert.assertEquals(Collections.emptyList(), listener.postDestroyReferences);
    }

    @Test
    public void testSingleControllerActivityOnPop() {
        Controller controller = new TestController();
        ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener listener = new ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener();
        controller.addLifecycleListener(listener);
        router.pushController(RouterTransaction.with(controller).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        router.popCurrentController();
        Assert.assertEquals(Arrays.asList(true, true), listener.changeEndReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postCreateViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postAttachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDetachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyReferences);
    }

    @Test
    public void testChildControllerActivityOnPop() {
        Controller parent = new TestController();
        router.pushController(RouterTransaction.with(parent).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        TestController child = new TestController();
        ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener listener = new ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener();
        addLifecycleListener(listener);
        Router childRouter = parent.getChildRouter(((ViewGroup) (parent.getView().findViewById(TestController.VIEW_ID))));
        childRouter.setPopsLastView(true);
        childRouter.pushController(RouterTransaction.with(child).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        childRouter.popCurrentController();
        Assert.assertEquals(Arrays.asList(true, true), listener.changeEndReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postCreateViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postAttachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDetachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyReferences);
    }

    @Test
    public void testChildControllerActivityOnParentPop() {
        Controller parent = new TestController();
        router.pushController(RouterTransaction.with(parent).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        TestController child = new TestController();
        ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener listener = new ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener();
        addLifecycleListener(listener);
        Router childRouter = parent.getChildRouter(((ViewGroup) (parent.getView().findViewById(TestController.VIEW_ID))));
        childRouter.setPopsLastView(true);
        childRouter.pushController(RouterTransaction.with(child).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        router.popCurrentController();
        Assert.assertEquals(Collections.singletonList(true), listener.changeEndReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postCreateViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postAttachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDetachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyReferences);
    }

    @Test
    public void testSingleControllerActivityOnDestroy() {
        Controller controller = new TestController();
        ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener listener = new ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener();
        controller.addLifecycleListener(listener);
        router.pushController(RouterTransaction.with(controller).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        activityProxy.pause().stop(false).destroy();
        Assert.assertEquals(Collections.singletonList(true), listener.changeEndReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postCreateViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postAttachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDetachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyReferences);
    }

    @Test
    public void testChildControllerActivityOnDestroy() {
        Controller parent = new TestController();
        router.pushController(RouterTransaction.with(parent).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        TestController child = new TestController();
        ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener listener = new ControllerLifecycleActivityReferenceTests.ActivityReferencingLifecycleListener();
        addLifecycleListener(listener);
        Router childRouter = parent.getChildRouter(((ViewGroup) (parent.getView().findViewById(TestController.VIEW_ID))));
        childRouter.setPopsLastView(true);
        childRouter.pushController(RouterTransaction.with(child).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        activityProxy.pause().stop(false).destroy();
        Assert.assertEquals(Collections.singletonList(true), listener.changeEndReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postCreateViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postAttachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDetachReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyViewReferences);
        Assert.assertEquals(Collections.singletonList(true), listener.postDestroyReferences);
    }

    static class ActivityReferencingLifecycleListener extends Controller.LifecycleListener {
        final List<Boolean> changeEndReferences = new ArrayList<>();

        final List<Boolean> postCreateViewReferences = new ArrayList<>();

        final List<Boolean> postAttachReferences = new ArrayList<>();

        final List<Boolean> postDetachReferences = new ArrayList<>();

        final List<Boolean> postDestroyViewReferences = new ArrayList<>();

        final List<Boolean> postDestroyReferences = new ArrayList<>();

        @Override
        public void onChangeEnd(@NonNull
        Controller controller, @NonNull
        ControllerChangeHandler changeHandler, @NonNull
        ControllerChangeType changeType) {
            changeEndReferences.add(((controller.getActivity()) != null));
        }

        @Override
        public void postCreateView(@NonNull
        Controller controller, @NonNull
        View view) {
            postCreateViewReferences.add(((controller.getActivity()) != null));
        }

        @Override
        public void postAttach(@NonNull
        Controller controller, @NonNull
        View view) {
            postAttachReferences.add(((controller.getActivity()) != null));
        }

        @Override
        public void postDetach(@NonNull
        Controller controller, @NonNull
        View view) {
            postDetachReferences.add(((controller.getActivity()) != null));
        }

        @Override
        public void postDestroyView(@NonNull
        Controller controller) {
            postDestroyViewReferences.add(((controller.getActivity()) != null));
        }

        @Override
        public void postDestroy(@NonNull
        Controller controller) {
            postDestroyReferences.add(((controller.getActivity()) != null));
        }
    }
}

