package com.bluelinelabs.conductor;


import android.os.Bundle;
import android.view.ViewGroup;
import com.bluelinelabs.conductor.util.ActivityProxy;
import com.bluelinelabs.conductor.util.MockChangeHandler;
import com.bluelinelabs.conductor.util.TestController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ReattachCaseTests {
    private ActivityProxy activityProxy;

    private Router router;

    @Test
    public void testNeedsAttachingOnPauseAndOrientation() {
        final TestController controllerA = new TestController();
        final TestController controllerB = new TestController();
        router.pushController(RouterTransaction.with(controllerA).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertTrue(isAttached());
        Assert.assertFalse(isAttached());
        sleepWakeDevice();
        Assert.assertTrue(isAttached());
        Assert.assertFalse(isAttached());
        router.pushController(RouterTransaction.with(controllerB).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        activityProxy.rotate();
        router.rebindIfNeeded();
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
    }

    @Test
    public void testChildNeedsAttachOnPauseAndOrientation() {
        final Controller controllerA = new TestController();
        final Controller childController = new TestController();
        final Controller controllerB = new TestController();
        router.pushController(RouterTransaction.with(controllerA).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Router childRouter = controllerA.getChildRouter(((ViewGroup) (controllerA.getView().findViewById(TestController.VIEW_ID))));
        childRouter.pushController(RouterTransaction.with(childController).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertTrue(controllerA.isAttached());
        Assert.assertTrue(childController.isAttached());
        Assert.assertFalse(controllerB.isAttached());
        sleepWakeDevice();
        Assert.assertTrue(controllerA.isAttached());
        Assert.assertTrue(childController.isAttached());
        Assert.assertFalse(controllerB.isAttached());
        router.pushController(RouterTransaction.with(controllerB).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertFalse(controllerA.isAttached());
        Assert.assertFalse(childController.isAttached());
        Assert.assertTrue(controllerB.isAttached());
        activityProxy.rotate();
        router.rebindIfNeeded();
        Assert.assertFalse(controllerA.isAttached());
        Assert.assertFalse(childController.isAttached());
        Assert.assertTrue(childController.getNeedsAttach());
        Assert.assertTrue(controllerB.isAttached());
    }

    @Test
    public void testChildHandleBackOnOrientation() {
        final TestController controllerA = new TestController();
        final TestController controllerB = new TestController();
        final TestController childController = new TestController();
        router.pushController(RouterTransaction.with(controllerA).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertTrue(isAttached());
        Assert.assertFalse(isAttached());
        Assert.assertFalse(isAttached());
        router.pushController(RouterTransaction.with(controllerB).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Router childRouter = getChildRouter(((ViewGroup) (getView().findViewById(TestController.VIEW_ID))));
        childRouter.setPopsLastView(true);
        childRouter.pushController(RouterTransaction.with(childController).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        Assert.assertTrue(isAttached());
        activityProxy.rotate();
        router.rebindIfNeeded();
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        Assert.assertTrue(isAttached());
        router.handleBack();
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        Assert.assertFalse(isAttached());
        router.handleBack();
        Assert.assertTrue(isAttached());
        Assert.assertFalse(isAttached());
        Assert.assertFalse(isAttached());
    }

    // Attempt to test https://github.com/bluelinelabs/Conductor/issues/86#issuecomment-231381271
    @Test
    public void testReusedChildRouterHandleBackOnOrientation() {
        TestController controllerA = new TestController();
        TestController controllerB = new TestController();
        TestController childController = new TestController();
        router.pushController(RouterTransaction.with(controllerA).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertTrue(isAttached());
        Assert.assertFalse(isAttached());
        Assert.assertFalse(isAttached());
        router.pushController(RouterTransaction.with(controllerB).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Router childRouter = getChildRouter(((ViewGroup) (getView().findViewById(TestController.VIEW_ID))));
        childRouter.setPopsLastView(true);
        childRouter.pushController(RouterTransaction.with(childController).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        Assert.assertTrue(isAttached());
        router.handleBack();
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        Assert.assertFalse(isAttached());
        childController = new TestController();
        childRouter.pushController(RouterTransaction.with(childController).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        Assert.assertTrue(isAttached());
        activityProxy.rotate();
        router.rebindIfNeeded();
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        Assert.assertTrue(isAttached());
        router.handleBack();
        childController = new TestController();
        childRouter.pushController(RouterTransaction.with(childController).pushChangeHandler(MockChangeHandler.defaultHandler()).popChangeHandler(MockChangeHandler.defaultHandler()));
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        Assert.assertTrue(isAttached());
        router.handleBack();
        Assert.assertFalse(isAttached());
        Assert.assertTrue(isAttached());
        Assert.assertFalse(isAttached());
        router.handleBack();
        Assert.assertTrue(isAttached());
        Assert.assertFalse(isAttached());
        Assert.assertFalse(isAttached());
    }

    // Attempt to test https://github.com/bluelinelabs/Conductor/issues/367
    @Test
    public void testViewIsAttachedAfterStartedActivityIsRecreated() {
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        router.setRoot(RouterTransaction.with(controller1));
        Assert.assertTrue(controller1.isAttached());
        // Lock screen
        Bundle bundle = new Bundle();
        activityProxy.pause().saveInstanceState(bundle).stop(false);
        // Push a 2nd controller, which will rotate the screen once it unlocked
        router.pushController(RouterTransaction.with(controller2));
        Assert.assertTrue(controller2.isAttached());
        Assert.assertTrue(controller2.getNeedsAttach());
        // Unlock screen and rotate
        activityProxy.start();
        activityProxy.rotate();
        Assert.assertTrue(controller2.isAttached());
    }

    @Test
    public void testPopMiddleControllerAttaches() {
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        Controller controller3 = new TestController();
        router.setRoot(RouterTransaction.with(controller1));
        router.pushController(RouterTransaction.with(controller2));
        router.pushController(RouterTransaction.with(controller3));
        router.popController(controller2);
        Assert.assertFalse(controller1.isAttached());
        Assert.assertFalse(controller2.isAttached());
        Assert.assertTrue(controller3.isAttached());
        controller1 = new TestController();
        controller2 = new TestController();
        controller3 = new TestController();
        router.setRoot(RouterTransaction.with(controller1));
        router.pushController(RouterTransaction.with(controller2));
        router.pushController(RouterTransaction.with(controller3).pushChangeHandler(MockChangeHandler.noRemoveViewOnPushHandler()));
        router.popController(controller2);
        Assert.assertTrue(controller1.isAttached());
        Assert.assertFalse(controller2.isAttached());
        Assert.assertTrue(controller3.isAttached());
    }
}

