package com.bluelinelabs.conductor;


import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.bluelinelabs.conductor.Controller.LifecycleListener;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.util.MockChangeHandler;
import com.bluelinelabs.conductor.util.TestController;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RouterTests {
    private Router router;

    @Test
    public void testSetRoot() {
        String rootTag = "root";
        Controller rootController = new TestController();
        Assert.assertFalse(router.hasRootController());
        router.setRoot(RouterTransaction.with(rootController).tag(rootTag));
        Assert.assertTrue(router.hasRootController());
        Assert.assertEquals(rootController, router.getControllerWithTag(rootTag));
    }

    @Test
    public void testSetNewRoot() {
        String oldRootTag = "oldRoot";
        String newRootTag = "newRoot";
        Controller oldRootController = new TestController();
        Controller newRootController = new TestController();
        router.setRoot(RouterTransaction.with(oldRootController).tag(oldRootTag));
        router.setRoot(RouterTransaction.with(newRootController).tag(newRootTag));
        Assert.assertNull(router.getControllerWithTag(oldRootTag));
        Assert.assertEquals(newRootController, router.getControllerWithTag(newRootTag));
    }

    @Test
    public void testGetByInstanceId() {
        Controller controller = new TestController();
        router.pushController(RouterTransaction.with(controller));
        Assert.assertEquals(controller, router.getControllerWithInstanceId(controller.getInstanceId()));
        Assert.assertNull(router.getControllerWithInstanceId("fake id"));
    }

    @Test
    public void testGetByTag() {
        String controller1Tag = "controller1";
        String controller2Tag = "controller2";
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        router.pushController(RouterTransaction.with(controller1).tag(controller1Tag));
        router.pushController(RouterTransaction.with(controller2).tag(controller2Tag));
        Assert.assertEquals(controller1, router.getControllerWithTag(controller1Tag));
        Assert.assertEquals(controller2, router.getControllerWithTag(controller2Tag));
    }

    @Test
    public void testPushPopControllers() {
        String controller1Tag = "controller1";
        String controller2Tag = "controller2";
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        router.pushController(RouterTransaction.with(controller1).tag(controller1Tag));
        Assert.assertEquals(1, router.getBackstackSize());
        router.pushController(RouterTransaction.with(controller2).tag(controller2Tag));
        Assert.assertEquals(2, router.getBackstackSize());
        router.popCurrentController();
        Assert.assertEquals(1, router.getBackstackSize());
        Assert.assertEquals(controller1, router.getControllerWithTag(controller1Tag));
        Assert.assertNull(router.getControllerWithTag(controller2Tag));
        router.popCurrentController();
        Assert.assertEquals(0, router.getBackstackSize());
        Assert.assertNull(router.getControllerWithTag(controller1Tag));
        Assert.assertNull(router.getControllerWithTag(controller2Tag));
    }

    @Test
    public void testPopControllerConcurrentModificationException() {
        int step = 1;
        for (int i = 0; i < 10; i++ , step++) {
            router.pushController(RouterTransaction.with(new TestController()).tag("1"));
            router.pushController(RouterTransaction.with(new TestController()).tag("2"));
            router.pushController(RouterTransaction.with(new TestController()).tag("3"));
            String tag;
            if (step == 1) {
                tag = "1";
            } else
                if (step == 2) {
                    tag = "2";
                } else {
                    tag = "3";
                    step = 0;
                }

            Controller controller = router.getControllerWithTag(tag);
            if (controller != null) {
                router.popController(controller);
            }
            router.popToRoot();
        }
    }

    @Test
    public void testPopToTag() {
        String controller1Tag = "controller1";
        String controller2Tag = "controller2";
        String controller3Tag = "controller3";
        String controller4Tag = "controller4";
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        Controller controller3 = new TestController();
        Controller controller4 = new TestController();
        router.pushController(RouterTransaction.with(controller1).tag(controller1Tag));
        router.pushController(RouterTransaction.with(controller2).tag(controller2Tag));
        router.pushController(RouterTransaction.with(controller3).tag(controller3Tag));
        router.pushController(RouterTransaction.with(controller4).tag(controller4Tag));
        router.popToTag(controller2Tag);
        Assert.assertEquals(2, router.getBackstackSize());
        Assert.assertEquals(controller1, router.getControllerWithTag(controller1Tag));
        Assert.assertEquals(controller2, router.getControllerWithTag(controller2Tag));
        Assert.assertNull(router.getControllerWithTag(controller3Tag));
        Assert.assertNull(router.getControllerWithTag(controller4Tag));
    }

    @Test
    public void testPopNonCurrent() {
        String controller1Tag = "controller1";
        String controller2Tag = "controller2";
        String controller3Tag = "controller3";
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        Controller controller3 = new TestController();
        router.pushController(RouterTransaction.with(controller1).tag(controller1Tag));
        router.pushController(RouterTransaction.with(controller2).tag(controller2Tag));
        router.pushController(RouterTransaction.with(controller3).tag(controller3Tag));
        router.popController(controller2);
        Assert.assertEquals(2, router.getBackstackSize());
        Assert.assertEquals(controller1, router.getControllerWithTag(controller1Tag));
        Assert.assertNull(router.getControllerWithTag(controller2Tag));
        Assert.assertEquals(controller3, router.getControllerWithTag(controller3Tag));
    }

    @Test
    public void testSetBackstack() {
        RouterTransaction rootTransaction = RouterTransaction.with(new TestController());
        RouterTransaction middleTransaction = RouterTransaction.with(new TestController());
        RouterTransaction topTransaction = RouterTransaction.with(new TestController());
        List<RouterTransaction> backstack = Arrays.asList(rootTransaction, middleTransaction, topTransaction);
        router.setBackstack(backstack, null);
        Assert.assertEquals(3, router.getBackstackSize());
        List<RouterTransaction> fetchedBackstack = router.getBackstack();
        Assert.assertEquals(rootTransaction, fetchedBackstack.get(0));
        Assert.assertEquals(middleTransaction, fetchedBackstack.get(1));
        Assert.assertEquals(topTransaction, fetchedBackstack.get(2));
    }

    @Test
    public void testNewSetBackstack() {
        router.setRoot(RouterTransaction.with(new TestController()));
        Assert.assertEquals(1, router.getBackstackSize());
        RouterTransaction rootTransaction = RouterTransaction.with(new TestController());
        RouterTransaction middleTransaction = RouterTransaction.with(new TestController());
        RouterTransaction topTransaction = RouterTransaction.with(new TestController());
        List<RouterTransaction> backstack = Arrays.asList(rootTransaction, middleTransaction, topTransaction);
        router.setBackstack(backstack, null);
        Assert.assertEquals(3, router.getBackstackSize());
        List<RouterTransaction> fetchedBackstack = router.getBackstack();
        Assert.assertEquals(rootTransaction, fetchedBackstack.get(0));
        Assert.assertEquals(middleTransaction, fetchedBackstack.get(1));
        Assert.assertEquals(topTransaction, fetchedBackstack.get(2));
        Assert.assertEquals(router, rootTransaction.controller.getRouter());
        Assert.assertEquals(router, middleTransaction.controller.getRouter());
        Assert.assertEquals(router, topTransaction.controller.getRouter());
    }

    @Test
    public void testNewSetBackstackWithNoRemoveViewOnPush() {
        RouterTransaction oldRootTransaction = RouterTransaction.with(new TestController());
        RouterTransaction oldTopTransaction = RouterTransaction.with(new TestController()).pushChangeHandler(MockChangeHandler.noRemoveViewOnPushHandler());
        router.setRoot(oldRootTransaction);
        router.pushController(oldTopTransaction);
        Assert.assertEquals(2, router.getBackstackSize());
        Assert.assertTrue(oldRootTransaction.controller.isAttached());
        Assert.assertTrue(oldTopTransaction.controller.isAttached());
        RouterTransaction rootTransaction = RouterTransaction.with(new TestController());
        RouterTransaction middleTransaction = RouterTransaction.with(new TestController()).pushChangeHandler(MockChangeHandler.noRemoveViewOnPushHandler());
        RouterTransaction topTransaction = RouterTransaction.with(new TestController()).pushChangeHandler(MockChangeHandler.noRemoveViewOnPushHandler());
        List<RouterTransaction> backstack = Arrays.asList(rootTransaction, middleTransaction, topTransaction);
        router.setBackstack(backstack, null);
        Assert.assertEquals(3, router.getBackstackSize());
        List<RouterTransaction> fetchedBackstack = router.getBackstack();
        Assert.assertEquals(rootTransaction, fetchedBackstack.get(0));
        Assert.assertEquals(middleTransaction, fetchedBackstack.get(1));
        Assert.assertEquals(topTransaction, fetchedBackstack.get(2));
        Assert.assertFalse(oldRootTransaction.controller.isAttached());
        Assert.assertFalse(oldTopTransaction.controller.isAttached());
        Assert.assertTrue(rootTransaction.controller.isAttached());
        Assert.assertTrue(middleTransaction.controller.isAttached());
        Assert.assertTrue(topTransaction.controller.isAttached());
    }

    @Test
    public void testPopToRoot() {
        RouterTransaction rootTransaction = RouterTransaction.with(new TestController());
        RouterTransaction transaction1 = RouterTransaction.with(new TestController());
        RouterTransaction transaction2 = RouterTransaction.with(new TestController());
        List<RouterTransaction> backstack = Arrays.asList(rootTransaction, transaction1, transaction2);
        router.setBackstack(backstack, null);
        Assert.assertEquals(3, router.getBackstackSize());
        router.popToRoot();
        Assert.assertEquals(1, router.getBackstackSize());
        Assert.assertEquals(rootTransaction, router.getBackstack().get(0));
        Assert.assertTrue(rootTransaction.controller.isAttached());
        Assert.assertFalse(transaction1.controller.isAttached());
        Assert.assertFalse(transaction2.controller.isAttached());
    }

    @Test
    public void testPopToRootWithNoRemoveViewOnPush() {
        RouterTransaction rootTransaction = RouterTransaction.with(new TestController()).pushChangeHandler(new HorizontalChangeHandler(false));
        RouterTransaction transaction1 = RouterTransaction.with(new TestController()).pushChangeHandler(new HorizontalChangeHandler(false));
        RouterTransaction transaction2 = RouterTransaction.with(new TestController()).pushChangeHandler(new HorizontalChangeHandler(false));
        List<RouterTransaction> backstack = Arrays.asList(rootTransaction, transaction1, transaction2);
        router.setBackstack(backstack, null);
        Assert.assertEquals(3, router.getBackstackSize());
        router.popToRoot();
        Assert.assertEquals(1, router.getBackstackSize());
        Assert.assertEquals(rootTransaction, router.getBackstack().get(0));
        Assert.assertTrue(rootTransaction.controller.isAttached());
        Assert.assertFalse(transaction1.controller.isAttached());
        Assert.assertFalse(transaction2.controller.isAttached());
    }

    @Test
    public void testReplaceTopController() {
        RouterTransaction rootTransaction = RouterTransaction.with(new TestController());
        RouterTransaction topTransaction = RouterTransaction.with(new TestController());
        List<RouterTransaction> backstack = Arrays.asList(rootTransaction, topTransaction);
        router.setBackstack(backstack, null);
        Assert.assertEquals(2, router.getBackstackSize());
        List<RouterTransaction> fetchedBackstack = router.getBackstack();
        Assert.assertEquals(rootTransaction, fetchedBackstack.get(0));
        Assert.assertEquals(topTransaction, fetchedBackstack.get(1));
        RouterTransaction newTopTransaction = RouterTransaction.with(new TestController());
        router.replaceTopController(newTopTransaction);
        Assert.assertEquals(2, router.getBackstackSize());
        fetchedBackstack = router.getBackstack();
        Assert.assertEquals(rootTransaction, fetchedBackstack.get(0));
        Assert.assertEquals(newTopTransaction, fetchedBackstack.get(1));
    }

    @Test
    public void testReplaceTopControllerWithNoRemoveViewOnPush() {
        RouterTransaction rootTransaction = RouterTransaction.with(new TestController());
        RouterTransaction topTransaction = RouterTransaction.with(new TestController()).pushChangeHandler(MockChangeHandler.noRemoveViewOnPushHandler());
        List<RouterTransaction> backstack = Arrays.asList(rootTransaction, topTransaction);
        router.setBackstack(backstack, null);
        Assert.assertEquals(2, router.getBackstackSize());
        Assert.assertTrue(rootTransaction.controller.isAttached());
        Assert.assertTrue(topTransaction.controller.isAttached());
        List<RouterTransaction> fetchedBackstack = router.getBackstack();
        Assert.assertEquals(rootTransaction, fetchedBackstack.get(0));
        Assert.assertEquals(topTransaction, fetchedBackstack.get(1));
        RouterTransaction newTopTransaction = RouterTransaction.with(new TestController()).pushChangeHandler(MockChangeHandler.noRemoveViewOnPushHandler());
        router.replaceTopController(newTopTransaction);
        newTopTransaction.pushChangeHandler().completeImmediately();
        Assert.assertEquals(2, router.getBackstackSize());
        fetchedBackstack = router.getBackstack();
        Assert.assertEquals(rootTransaction, fetchedBackstack.get(0));
        Assert.assertEquals(newTopTransaction, fetchedBackstack.get(1));
        Assert.assertTrue(rootTransaction.controller.isAttached());
        Assert.assertFalse(topTransaction.controller.isAttached());
        Assert.assertTrue(newTopTransaction.controller.isAttached());
    }

    @Test
    public void testRearrangeTransactionBackstack() {
        RouterTransaction transaction1 = RouterTransaction.with(new TestController());
        RouterTransaction transaction2 = RouterTransaction.with(new TestController());
        List<RouterTransaction> backstack = Arrays.asList(transaction1, transaction2);
        router.setBackstack(backstack, null);
        Assert.assertEquals(1, transaction1.transactionIndex);
        Assert.assertEquals(2, transaction2.transactionIndex);
        backstack = Arrays.asList(transaction2, transaction1);
        router.setBackstack(backstack, null);
        Assert.assertEquals(1, transaction2.transactionIndex);
        Assert.assertEquals(2, transaction1.transactionIndex);
        router.handleBack();
        Assert.assertEquals(1, router.getBackstackSize());
        Assert.assertEquals(transaction2, router.getBackstack().get(0));
        router.handleBack();
        Assert.assertEquals(0, router.getBackstackSize());
    }

    @Test
    public void testChildRouterRearrangeTransactionBackstack() {
        Controller parent = new TestController();
        router.setRoot(RouterTransaction.with(parent));
        Router childRouter = parent.getChildRouter(((ViewGroup) (parent.getView().findViewById(TestController.CHILD_VIEW_ID_1))));
        RouterTransaction transaction1 = RouterTransaction.with(new TestController());
        RouterTransaction transaction2 = RouterTransaction.with(new TestController());
        List<RouterTransaction> backstack = Arrays.asList(transaction1, transaction2);
        childRouter.setBackstack(backstack, null);
        Assert.assertEquals(2, transaction1.transactionIndex);
        Assert.assertEquals(3, transaction2.transactionIndex);
        backstack = Arrays.asList(transaction2, transaction1);
        childRouter.setBackstack(backstack, null);
        Assert.assertEquals(2, transaction2.transactionIndex);
        Assert.assertEquals(3, transaction1.transactionIndex);
        childRouter.handleBack();
        Assert.assertEquals(1, childRouter.getBackstackSize());
        Assert.assertEquals(transaction2, childRouter.getBackstack().get(0));
        childRouter.handleBack();
        Assert.assertEquals(0, childRouter.getBackstackSize());
    }

    @Test
    public void testRemovesAllViewsOnDestroy() {
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        router.setRoot(RouterTransaction.with(controller1));
        router.pushController(RouterTransaction.with(controller2).pushChangeHandler(new FadeChangeHandler(false)));
        Assert.assertEquals(2, router.container.getChildCount());
        router.destroy(true);
        Assert.assertEquals(0, router.container.getChildCount());
    }

    @Test
    public void testIsBeingDestroyed() {
        final LifecycleListener lifecycleListener = new LifecycleListener() {
            @Override
            public void preDestroyView(@NonNull
            Controller controller, @NonNull
            View view) {
                Assert.assertTrue(controller.isBeingDestroyed());
            }
        };
        Controller controller1 = new TestController();
        Controller controller2 = new TestController();
        controller2.addLifecycleListener(lifecycleListener);
        router.setRoot(RouterTransaction.with(controller1));
        router.pushController(RouterTransaction.with(controller2));
        Assert.assertFalse(controller1.isBeingDestroyed());
        Assert.assertFalse(controller2.isBeingDestroyed());
        router.popCurrentController();
        Assert.assertFalse(controller1.isBeingDestroyed());
        Assert.assertTrue(controller2.isBeingDestroyed());
        Controller controller3 = new TestController();
        controller3.addLifecycleListener(lifecycleListener);
        router.pushController(RouterTransaction.with(controller3));
        Assert.assertFalse(controller1.isBeingDestroyed());
        Assert.assertFalse(controller3.isBeingDestroyed());
        router.popToRoot();
        Assert.assertFalse(controller1.isBeingDestroyed());
        Assert.assertTrue(controller3.isBeingDestroyed());
    }
}

