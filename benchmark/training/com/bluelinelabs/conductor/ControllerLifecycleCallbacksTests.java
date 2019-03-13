package com.bluelinelabs.conductor;


import RetainViewMode.RETAIN_DETACH;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.bluelinelabs.conductor.Controller.LifecycleListener;
import com.bluelinelabs.conductor.changehandler.SimpleSwapChangeHandler;
import com.bluelinelabs.conductor.util.ActivityProxy;
import com.bluelinelabs.conductor.util.CallState;
import com.bluelinelabs.conductor.util.MockChangeHandler;
import com.bluelinelabs.conductor.util.TestActivity;
import com.bluelinelabs.conductor.util.TestController;
import com.bluelinelabs.conductor.util.ViewUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ControllerLifecycleCallbacksTests {
    private Router router;

    private ActivityProxy activityProxy;

    private CallState currentCallState;

    @Test
    public void testNormalLifecycle() {
        TestController controller = new TestController();
        attachLifecycleListener(controller);
        CallState expectedCallState = new CallState(false);
        assertCalls(expectedCallState, controller);
        router.pushController(RouterTransaction.with(controller).pushChangeHandler(getPushHandler(expectedCallState, controller)).popChangeHandler(getPopHandler(expectedCallState, controller)));
        assertCalls(expectedCallState, controller);
        router.popCurrentController();
        Assert.assertNull(getView());
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testLifecycleWithActivityStop() {
        TestController controller = new TestController();
        attachLifecycleListener(controller);
        CallState expectedCallState = new CallState(false);
        assertCalls(expectedCallState, controller);
        router.pushController(RouterTransaction.with(controller).pushChangeHandler(getPushHandler(expectedCallState, controller)));
        assertCalls(expectedCallState, controller);
        activityProxy.getActivity().isDestroying = true;
        activityProxy.pause();
        assertCalls(expectedCallState, controller);
        activityProxy.stop(false);
        (expectedCallState.detachCalls)++;
        assertCalls(expectedCallState, controller);
        Assert.assertNotNull(getView());
        ViewUtils.reportAttached(getView(), false);
        (expectedCallState.saveViewStateCalls)++;
        (expectedCallState.destroyViewCalls)++;
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testLifecycleWithActivityDestroy() {
        TestController controller = new TestController();
        attachLifecycleListener(controller);
        CallState expectedCallState = new CallState(false);
        assertCalls(expectedCallState, controller);
        router.pushController(RouterTransaction.with(controller).pushChangeHandler(getPushHandler(expectedCallState, controller)));
        assertCalls(expectedCallState, controller);
        activityProxy.getActivity().isDestroying = true;
        activityProxy.pause();
        assertCalls(expectedCallState, controller);
        activityProxy.stop(true);
        (expectedCallState.saveViewStateCalls)++;
        (expectedCallState.detachCalls)++;
        (expectedCallState.destroyViewCalls)++;
        assertCalls(expectedCallState, controller);
        activityProxy.destroy();
        (expectedCallState.contextUnavailableCalls)++;
        (expectedCallState.destroyCalls)++;
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testLifecycleWithActivityConfigurationChange() {
        TestController controller = new TestController();
        attachLifecycleListener(controller);
        CallState expectedCallState = new CallState(false);
        assertCalls(expectedCallState, controller);
        router.pushController(RouterTransaction.with(controller).pushChangeHandler(getPushHandler(expectedCallState, controller)).tag("root"));
        assertCalls(expectedCallState, controller);
        activityProxy.getActivity().isChangingConfigurations = true;
        Bundle bundle = new Bundle();
        activityProxy.saveInstanceState(bundle);
        (expectedCallState.saveViewStateCalls)++;
        (expectedCallState.saveInstanceStateCalls)++;
        assertCalls(expectedCallState, controller);
        activityProxy.pause();
        assertCalls(expectedCallState, controller);
        activityProxy.stop(true);
        (expectedCallState.detachCalls)++;
        (expectedCallState.destroyViewCalls)++;
        assertCalls(expectedCallState, controller);
        activityProxy.destroy();
        (expectedCallState.contextUnavailableCalls)++;
        assertCalls(expectedCallState, controller);
        createActivityController(bundle, false);
        controller = ((TestController) (router.getControllerWithTag("root")));
        (expectedCallState.contextAvailableCalls)++;
        (expectedCallState.restoreInstanceStateCalls)++;
        (expectedCallState.restoreViewStateCalls)++;
        (expectedCallState.changeStartCalls)++;
        (expectedCallState.createViewCalls)++;
        // Lifecycle listener isn't attached during restore, grab the current views from the controller for this stuff...
        currentCallState.restoreInstanceStateCalls = controller.currentCallState.restoreInstanceStateCalls;
        currentCallState.restoreViewStateCalls = controller.currentCallState.restoreViewStateCalls;
        currentCallState.changeStartCalls = controller.currentCallState.changeStartCalls;
        currentCallState.changeEndCalls = controller.currentCallState.changeEndCalls;
        currentCallState.createViewCalls = controller.currentCallState.createViewCalls;
        currentCallState.attachCalls = controller.currentCallState.attachCalls;
        currentCallState.contextAvailableCalls = controller.currentCallState.contextAvailableCalls;
        assertCalls(expectedCallState, controller);
        activityProxy.start().resume();
        currentCallState.changeEndCalls = controller.currentCallState.changeEndCalls;
        currentCallState.attachCalls = controller.currentCallState.attachCalls;
        (expectedCallState.changeEndCalls)++;
        (expectedCallState.attachCalls)++;
        assertCalls(expectedCallState, controller);
        activityProxy.resume();
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testLifecycleWithActivityBackground() {
        TestController controller = new TestController();
        attachLifecycleListener(controller);
        CallState expectedCallState = new CallState(false);
        assertCalls(expectedCallState, controller);
        router.pushController(RouterTransaction.with(controller).pushChangeHandler(getPushHandler(expectedCallState, controller)));
        assertCalls(expectedCallState, controller);
        activityProxy.pause();
        Bundle bundle = new Bundle();
        activityProxy.saveInstanceState(bundle);
        (expectedCallState.saveInstanceStateCalls)++;
        (expectedCallState.saveViewStateCalls)++;
        assertCalls(expectedCallState, controller);
        activityProxy.resume();
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testLifecycleCallOrder() {
        final TestController testController = new TestController();
        final CallState callState = new CallState(false);
        addLifecycleListener(new LifecycleListener() {
            @Override
            public void preCreateView(@NonNull
            Controller controller) {
                (callState.createViewCalls)++;
                Assert.assertEquals(1, callState.createViewCalls);
                Assert.assertEquals(0, testController.currentCallState.createViewCalls);
                Assert.assertEquals(0, callState.attachCalls);
                Assert.assertEquals(0, testController.currentCallState.attachCalls);
                Assert.assertEquals(0, callState.detachCalls);
                Assert.assertEquals(0, testController.currentCallState.detachCalls);
                Assert.assertEquals(0, callState.destroyViewCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(0, callState.destroyCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyCalls);
            }

            @Override
            public void postCreateView(@NonNull
            Controller controller, @NonNull
            View view) {
                (callState.createViewCalls)++;
                Assert.assertEquals(2, callState.createViewCalls);
                Assert.assertEquals(1, testController.currentCallState.createViewCalls);
                Assert.assertEquals(0, callState.attachCalls);
                Assert.assertEquals(0, testController.currentCallState.attachCalls);
                Assert.assertEquals(0, callState.detachCalls);
                Assert.assertEquals(0, testController.currentCallState.detachCalls);
                Assert.assertEquals(0, callState.destroyViewCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(0, callState.destroyCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyCalls);
            }

            @Override
            public void preAttach(@NonNull
            Controller controller, @NonNull
            View view) {
                (callState.attachCalls)++;
                Assert.assertEquals(2, callState.createViewCalls);
                Assert.assertEquals(1, testController.currentCallState.createViewCalls);
                Assert.assertEquals(1, callState.attachCalls);
                Assert.assertEquals(0, testController.currentCallState.attachCalls);
                Assert.assertEquals(0, callState.detachCalls);
                Assert.assertEquals(0, testController.currentCallState.detachCalls);
                Assert.assertEquals(0, callState.destroyViewCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(0, callState.destroyCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyCalls);
            }

            @Override
            public void postAttach(@NonNull
            Controller controller, @NonNull
            View view) {
                (callState.attachCalls)++;
                Assert.assertEquals(2, callState.createViewCalls);
                Assert.assertEquals(1, testController.currentCallState.createViewCalls);
                Assert.assertEquals(2, callState.attachCalls);
                Assert.assertEquals(1, testController.currentCallState.attachCalls);
                Assert.assertEquals(0, callState.detachCalls);
                Assert.assertEquals(0, testController.currentCallState.detachCalls);
                Assert.assertEquals(0, callState.destroyViewCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(0, callState.destroyCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyCalls);
            }

            @Override
            public void preDetach(@NonNull
            Controller controller, @NonNull
            View view) {
                (callState.detachCalls)++;
                Assert.assertEquals(2, callState.createViewCalls);
                Assert.assertEquals(1, testController.currentCallState.createViewCalls);
                Assert.assertEquals(2, callState.attachCalls);
                Assert.assertEquals(1, testController.currentCallState.attachCalls);
                Assert.assertEquals(1, callState.detachCalls);
                Assert.assertEquals(0, testController.currentCallState.detachCalls);
                Assert.assertEquals(0, callState.destroyViewCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(0, callState.destroyCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyCalls);
            }

            @Override
            public void postDetach(@NonNull
            Controller controller, @NonNull
            View view) {
                (callState.detachCalls)++;
                Assert.assertEquals(2, callState.createViewCalls);
                Assert.assertEquals(1, testController.currentCallState.createViewCalls);
                Assert.assertEquals(2, callState.attachCalls);
                Assert.assertEquals(1, testController.currentCallState.attachCalls);
                Assert.assertEquals(2, callState.detachCalls);
                Assert.assertEquals(1, testController.currentCallState.detachCalls);
                Assert.assertEquals(0, callState.destroyViewCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(0, callState.destroyCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyCalls);
            }

            @Override
            public void preDestroyView(@NonNull
            Controller controller, @NonNull
            View view) {
                (callState.destroyViewCalls)++;
                Assert.assertEquals(2, callState.createViewCalls);
                Assert.assertEquals(1, testController.currentCallState.createViewCalls);
                Assert.assertEquals(2, callState.attachCalls);
                Assert.assertEquals(1, testController.currentCallState.attachCalls);
                Assert.assertEquals(2, callState.detachCalls);
                Assert.assertEquals(1, testController.currentCallState.detachCalls);
                Assert.assertEquals(1, callState.destroyViewCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(0, callState.destroyCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyCalls);
            }

            @Override
            public void postDestroyView(@NonNull
            Controller controller) {
                (callState.destroyViewCalls)++;
                Assert.assertEquals(2, callState.createViewCalls);
                Assert.assertEquals(1, testController.currentCallState.createViewCalls);
                Assert.assertEquals(2, callState.attachCalls);
                Assert.assertEquals(1, testController.currentCallState.attachCalls);
                Assert.assertEquals(2, callState.detachCalls);
                Assert.assertEquals(1, testController.currentCallState.detachCalls);
                Assert.assertEquals(2, callState.destroyViewCalls);
                Assert.assertEquals(1, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(0, callState.destroyCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyCalls);
            }

            @Override
            public void preDestroy(@NonNull
            Controller controller) {
                (callState.destroyCalls)++;
                Assert.assertEquals(2, callState.createViewCalls);
                Assert.assertEquals(1, testController.currentCallState.createViewCalls);
                Assert.assertEquals(2, callState.attachCalls);
                Assert.assertEquals(1, testController.currentCallState.attachCalls);
                Assert.assertEquals(2, callState.detachCalls);
                Assert.assertEquals(1, testController.currentCallState.detachCalls);
                Assert.assertEquals(2, callState.destroyViewCalls);
                Assert.assertEquals(1, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(1, callState.destroyCalls);
                Assert.assertEquals(0, testController.currentCallState.destroyCalls);
            }

            @Override
            public void postDestroy(@NonNull
            Controller controller) {
                (callState.destroyCalls)++;
                Assert.assertEquals(2, callState.createViewCalls);
                Assert.assertEquals(1, testController.currentCallState.createViewCalls);
                Assert.assertEquals(2, callState.attachCalls);
                Assert.assertEquals(1, testController.currentCallState.attachCalls);
                Assert.assertEquals(2, callState.detachCalls);
                Assert.assertEquals(1, testController.currentCallState.detachCalls);
                Assert.assertEquals(2, callState.destroyViewCalls);
                Assert.assertEquals(1, testController.currentCallState.destroyViewCalls);
                Assert.assertEquals(2, callState.destroyCalls);
                Assert.assertEquals(1, testController.currentCallState.destroyCalls);
            }
        });
        router.pushController(RouterTransaction.with(testController).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        router.popController(testController);
        Assert.assertEquals(2, callState.createViewCalls);
        Assert.assertEquals(2, callState.attachCalls);
        Assert.assertEquals(2, callState.detachCalls);
        Assert.assertEquals(2, callState.destroyViewCalls);
        Assert.assertEquals(2, callState.destroyCalls);
    }

    @Test
    public void testChildLifecycle() {
        Controller parent = new TestController();
        router.pushController(RouterTransaction.with(parent).pushChangeHandler(MockChangeHandler.defaultHandler()));
        TestController child = new TestController();
        attachLifecycleListener(child);
        CallState expectedCallState = new CallState(false);
        assertCalls(expectedCallState, child);
        Router childRouter = parent.getChildRouter(((ViewGroup) (parent.getView().findViewById(TestController.VIEW_ID))));
        childRouter.setRoot(RouterTransaction.with(child).pushChangeHandler(getPushHandler(expectedCallState, child)).popChangeHandler(getPopHandler(expectedCallState, child)));
        assertCalls(expectedCallState, child);
        parent.removeChildRouter(childRouter);
        assertCalls(expectedCallState, child);
    }

    @Test
    public void testChildLifecycle2() {
        Controller parent = new TestController();
        router.pushController(RouterTransaction.with(parent).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        TestController child = new TestController();
        attachLifecycleListener(child);
        CallState expectedCallState = new CallState(false);
        assertCalls(expectedCallState, child);
        Router childRouter = parent.getChildRouter(((ViewGroup) (parent.getView().findViewById(TestController.VIEW_ID))));
        childRouter.setRoot(RouterTransaction.with(child).pushChangeHandler(getPushHandler(expectedCallState, child)).popChangeHandler(getPopHandler(expectedCallState, child)));
        assertCalls(expectedCallState, child);
        router.popCurrentController();
        (expectedCallState.detachCalls)++;
        (expectedCallState.destroyViewCalls)++;
        (expectedCallState.contextUnavailableCalls)++;
        (expectedCallState.destroyCalls)++;
        assertCalls(expectedCallState, child);
    }

    @Test
    public void testChildLifecycleOrderingAfterUnexpectedAttach() {
        Controller parent = new TestController();
        parent.setRetainViewMode(RETAIN_DETACH);
        router.pushController(RouterTransaction.with(parent).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        TestController child = new TestController();
        child.setRetainViewMode(RETAIN_DETACH);
        Router childRouter = parent.getChildRouter(((ViewGroup) (parent.getView().findViewById(TestController.VIEW_ID))));
        childRouter.setRoot(RouterTransaction.with(child).pushChangeHandler(new SimpleSwapChangeHandler()).popChangeHandler(new SimpleSwapChangeHandler()));
        Assert.assertTrue(parent.isAttached());
        Assert.assertTrue(isAttached());
        ViewUtils.reportAttached(parent.getView(), false, true);
        Assert.assertFalse(parent.isAttached());
        Assert.assertFalse(isAttached());
        ViewUtils.reportAttached(getView(), true);
        Assert.assertFalse(parent.isAttached());
        Assert.assertFalse(isAttached());
        ViewUtils.reportAttached(parent.getView(), true);
        Assert.assertTrue(parent.isAttached());
        Assert.assertTrue(isAttached());
    }
}

