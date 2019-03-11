package com.bluelinelabs.conductor;


import android.view.View;
import com.bluelinelabs.conductor.util.ChangeHandlerHistory;
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
public class RouterChangeHandlerTests {
    private Router router;

    @Test
    public void testSetRootHandler() {
        MockChangeHandler handler = MockChangeHandler.taggedHandler("root", true);
        TestController rootController = new TestController();
        router.setRoot(RouterTransaction.with(rootController).pushChangeHandler(handler));
        Assert.assertTrue(rootController.changeHandlerHistory.isValidHistory);
        Assert.assertNull(rootController.changeHandlerHistory.latestFromView());
        Assert.assertNotNull(rootController.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), rootController.changeHandlerHistory.latestToView());
        Assert.assertTrue(rootController.changeHandlerHistory.latestIsPush());
        Assert.assertEquals(handler.tag, rootController.changeHandlerHistory.latestChangeHandler().tag);
    }

    @Test
    public void testPushPopHandlers() {
        TestController rootController = new TestController();
        router.setRoot(RouterTransaction.with(rootController).pushChangeHandler(MockChangeHandler.defaultHandler()));
        View rootView = rootController.getView();
        MockChangeHandler pushHandler = MockChangeHandler.taggedHandler("push", true);
        MockChangeHandler popHandler = MockChangeHandler.taggedHandler("pop", true);
        TestController pushController = new TestController();
        router.pushController(RouterTransaction.with(pushController).pushChangeHandler(pushHandler).popChangeHandler(popHandler));
        Assert.assertTrue(rootController.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(pushController.changeHandlerHistory.isValidHistory);
        Assert.assertNotNull(pushController.changeHandlerHistory.latestFromView());
        Assert.assertNotNull(pushController.changeHandlerHistory.latestToView());
        Assert.assertEquals(rootView, pushController.changeHandlerHistory.latestFromView());
        Assert.assertEquals(getView(), pushController.changeHandlerHistory.latestToView());
        Assert.assertTrue(pushController.changeHandlerHistory.latestIsPush());
        Assert.assertEquals(pushHandler.tag, pushController.changeHandlerHistory.latestChangeHandler().tag);
        View pushView = pushController.getView();
        router.popController(pushController);
        Assert.assertNotNull(pushController.changeHandlerHistory.latestFromView());
        Assert.assertNotNull(pushController.changeHandlerHistory.latestToView());
        Assert.assertEquals(pushView, pushController.changeHandlerHistory.fromViewAt(1));
        Assert.assertEquals(getView(), pushController.changeHandlerHistory.latestToView());
        Assert.assertFalse(pushController.changeHandlerHistory.latestIsPush());
        Assert.assertEquals(popHandler.tag, pushController.changeHandlerHistory.latestChangeHandler().tag);
    }

    @Test
    public void testResetRootHandlers() {
        TestController initialController1 = new TestController();
        MockChangeHandler initialPushHandler1 = MockChangeHandler.taggedHandler("initialPush1", true);
        MockChangeHandler initialPopHandler1 = MockChangeHandler.taggedHandler("initialPop1", true);
        router.setRoot(RouterTransaction.with(initialController1).pushChangeHandler(initialPushHandler1).popChangeHandler(initialPopHandler1));
        TestController initialController2 = new TestController();
        MockChangeHandler initialPushHandler2 = MockChangeHandler.taggedHandler("initialPush2", false);
        MockChangeHandler initialPopHandler2 = MockChangeHandler.taggedHandler("initialPop2", false);
        router.pushController(RouterTransaction.with(initialController2).pushChangeHandler(initialPushHandler2).popChangeHandler(initialPopHandler2));
        View initialView1 = initialController1.getView();
        View initialView2 = initialController2.getView();
        TestController newRootController = new TestController();
        MockChangeHandler newRootHandler = MockChangeHandler.taggedHandler("newRootHandler", true);
        router.setRoot(RouterTransaction.with(newRootController).pushChangeHandler(newRootHandler));
        Assert.assertTrue(initialController1.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(initialController2.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(newRootController.changeHandlerHistory.isValidHistory);
        Assert.assertEquals(3, initialController1.changeHandlerHistory.size());
        Assert.assertEquals(2, initialController2.changeHandlerHistory.size());
        Assert.assertEquals(1, newRootController.changeHandlerHistory.size());
        Assert.assertNotNull(initialController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), initialController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView1, initialController1.changeHandlerHistory.latestFromView());
        Assert.assertEquals(newRootHandler.tag, initialController1.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(initialController1.changeHandlerHistory.latestIsPush());
        Assert.assertNull(initialController2.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView2, initialController2.changeHandlerHistory.latestFromView());
        Assert.assertEquals(newRootHandler.tag, initialController2.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(initialController2.changeHandlerHistory.latestIsPush());
        Assert.assertNotNull(newRootController.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), newRootController.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView1, newRootController.changeHandlerHistory.latestFromView());
        Assert.assertEquals(newRootHandler.tag, newRootController.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(newRootController.changeHandlerHistory.latestIsPush());
    }

    @Test
    public void testSetBackstackHandlers() {
        TestController initialController1 = new TestController();
        MockChangeHandler initialPushHandler1 = MockChangeHandler.taggedHandler("initialPush1", true);
        MockChangeHandler initialPopHandler1 = MockChangeHandler.taggedHandler("initialPop1", true);
        router.setRoot(RouterTransaction.with(initialController1).pushChangeHandler(initialPushHandler1).popChangeHandler(initialPopHandler1));
        TestController initialController2 = new TestController();
        MockChangeHandler initialPushHandler2 = MockChangeHandler.taggedHandler("initialPush2", false);
        MockChangeHandler initialPopHandler2 = MockChangeHandler.taggedHandler("initialPop2", false);
        router.pushController(RouterTransaction.with(initialController2).pushChangeHandler(initialPushHandler2).popChangeHandler(initialPopHandler2));
        View initialView1 = initialController1.getView();
        View initialView2 = initialController2.getView();
        TestController newController1 = new TestController();
        TestController newController2 = new TestController();
        MockChangeHandler setBackstackHandler = MockChangeHandler.taggedHandler("setBackstackHandler", true);
        List<RouterTransaction> newBackstack = Arrays.asList(RouterTransaction.with(newController1), RouterTransaction.with(newController2));
        router.setBackstack(newBackstack, setBackstackHandler);
        Assert.assertTrue(initialController1.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(initialController2.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(newController1.changeHandlerHistory.isValidHistory);
        Assert.assertEquals(3, initialController1.changeHandlerHistory.size());
        Assert.assertEquals(2, initialController2.changeHandlerHistory.size());
        Assert.assertEquals(0, newController1.changeHandlerHistory.size());
        Assert.assertEquals(1, newController2.changeHandlerHistory.size());
        Assert.assertNotNull(initialController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), initialController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView1, initialController1.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, initialController1.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(initialController1.changeHandlerHistory.latestIsPush());
        Assert.assertNull(initialController2.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView2, initialController2.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, initialController2.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(initialController2.changeHandlerHistory.latestIsPush());
        Assert.assertNotNull(newController2.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), newController2.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView1, newController2.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, newController2.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(newController2.changeHandlerHistory.latestIsPush());
    }

    @Test
    public void testSetBackstackWithTwoVisibleHandlers() {
        TestController initialController1 = new TestController();
        MockChangeHandler initialPushHandler1 = MockChangeHandler.taggedHandler("initialPush1", true);
        MockChangeHandler initialPopHandler1 = MockChangeHandler.taggedHandler("initialPop1", true);
        router.setRoot(RouterTransaction.with(initialController1).pushChangeHandler(initialPushHandler1).popChangeHandler(initialPopHandler1));
        TestController initialController2 = new TestController();
        MockChangeHandler initialPushHandler2 = MockChangeHandler.taggedHandler("initialPush2", false);
        MockChangeHandler initialPopHandler2 = MockChangeHandler.taggedHandler("initialPop2", false);
        router.pushController(RouterTransaction.with(initialController2).pushChangeHandler(initialPushHandler2).popChangeHandler(initialPopHandler2));
        View initialView1 = initialController1.getView();
        View initialView2 = initialController2.getView();
        TestController newController1 = new TestController();
        TestController newController2 = new TestController();
        MockChangeHandler setBackstackHandler = MockChangeHandler.taggedHandler("setBackstackHandler", true);
        MockChangeHandler pushController2Handler = MockChangeHandler.noRemoveViewOnPushHandler("pushController2");
        List<RouterTransaction> newBackstack = Arrays.asList(RouterTransaction.with(newController1), RouterTransaction.with(newController2).pushChangeHandler(pushController2Handler));
        router.setBackstack(newBackstack, setBackstackHandler);
        Assert.assertTrue(initialController1.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(initialController2.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(newController1.changeHandlerHistory.isValidHistory);
        Assert.assertEquals(3, initialController1.changeHandlerHistory.size());
        Assert.assertEquals(2, initialController2.changeHandlerHistory.size());
        Assert.assertEquals(2, newController1.changeHandlerHistory.size());
        Assert.assertEquals(1, newController2.changeHandlerHistory.size());
        Assert.assertNotNull(initialController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), initialController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView1, initialController1.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, initialController1.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(initialController1.changeHandlerHistory.latestIsPush());
        Assert.assertNull(initialController2.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView2, initialController2.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, initialController2.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(initialController2.changeHandlerHistory.latestIsPush());
        Assert.assertNotNull(newController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), newController1.changeHandlerHistory.toViewAt(0));
        Assert.assertEquals(getView(), newController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView1, newController1.changeHandlerHistory.fromViewAt(0));
        Assert.assertEquals(getView(), newController1.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, newController1.changeHandlerHistory.changeHandlerAt(0).tag);
        Assert.assertEquals(pushController2Handler.tag, newController1.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(newController1.changeHandlerHistory.latestIsPush());
        Assert.assertNotNull(newController2.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), newController2.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), newController2.changeHandlerHistory.latestFromView());
        Assert.assertEquals(pushController2Handler.tag, newController2.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(newController2.changeHandlerHistory.latestIsPush());
    }

    @Test
    public void testSetBackstackForPushHandlers() {
        TestController initialController = new TestController();
        MockChangeHandler initialPushHandler = MockChangeHandler.taggedHandler("initialPush1", true);
        MockChangeHandler initialPopHandler = MockChangeHandler.taggedHandler("initialPop1", true);
        RouterTransaction initialTransaction = RouterTransaction.with(initialController).pushChangeHandler(initialPushHandler).popChangeHandler(initialPopHandler);
        router.setRoot(initialTransaction);
        View initialView = initialController.getView();
        TestController newController = new TestController();
        MockChangeHandler setBackstackHandler = MockChangeHandler.taggedHandler("setBackstackHandler", true);
        List<RouterTransaction> newBackstack = Arrays.asList(initialTransaction, RouterTransaction.with(newController));
        router.setBackstack(newBackstack, setBackstackHandler);
        Assert.assertTrue(initialController.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(newController.changeHandlerHistory.isValidHistory);
        Assert.assertEquals(2, initialController.changeHandlerHistory.size());
        Assert.assertEquals(1, newController.changeHandlerHistory.size());
        Assert.assertNotNull(initialController.changeHandlerHistory.latestToView());
        Assert.assertEquals(getView(), initialController.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView, initialController.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, initialController.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(initialController.changeHandlerHistory.latestIsPush());
        Assert.assertTrue(newController.changeHandlerHistory.latestIsPush());
    }

    @Test
    public void testSetBackstackForInvertHandlersWithRemovesView() {
        TestController initialController1 = new TestController();
        MockChangeHandler initialPushHandler1 = MockChangeHandler.taggedHandler("initialPush1", true);
        MockChangeHandler initialPopHandler1 = MockChangeHandler.taggedHandler("initialPop1", true);
        RouterTransaction initialTransaction1 = RouterTransaction.with(initialController1).pushChangeHandler(initialPushHandler1).popChangeHandler(initialPopHandler1);
        router.setRoot(initialTransaction1);
        TestController initialController2 = new TestController();
        MockChangeHandler initialPushHandler2 = MockChangeHandler.taggedHandler("initialPush2", true);
        MockChangeHandler initialPopHandler2 = MockChangeHandler.taggedHandler("initialPop2", true);
        RouterTransaction initialTransaction2 = RouterTransaction.with(initialController2).pushChangeHandler(initialPushHandler2).popChangeHandler(initialPopHandler2);
        router.pushController(initialTransaction2);
        View initialView2 = initialController2.getView();
        MockChangeHandler setBackstackHandler = MockChangeHandler.taggedHandler("setBackstackHandler", true);
        List<RouterTransaction> newBackstack = Arrays.asList(initialTransaction2, initialTransaction1);
        router.setBackstack(newBackstack, setBackstackHandler);
        Assert.assertTrue(initialController1.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(initialController2.changeHandlerHistory.isValidHistory);
        Assert.assertEquals(3, initialController1.changeHandlerHistory.size());
        Assert.assertEquals(2, initialController2.changeHandlerHistory.size());
        Assert.assertNotNull(initialController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView2, initialController1.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, initialController1.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertFalse(initialController1.changeHandlerHistory.latestIsPush());
        Assert.assertNotNull(initialController2.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView2, initialController2.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, initialController2.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertFalse(initialController2.changeHandlerHistory.latestIsPush());
    }

    @Test
    public void testSetBackstackForInvertHandlersWithoutRemovesView() {
        TestController initialController1 = new TestController();
        MockChangeHandler initialPushHandler1 = MockChangeHandler.taggedHandler("initialPush1", true);
        MockChangeHandler initialPopHandler1 = MockChangeHandler.taggedHandler("initialPop1", true);
        RouterTransaction initialTransaction1 = RouterTransaction.with(initialController1).pushChangeHandler(initialPushHandler1).popChangeHandler(initialPopHandler1);
        router.setRoot(initialTransaction1);
        TestController initialController2 = new TestController();
        MockChangeHandler initialPushHandler2 = MockChangeHandler.taggedHandler("initialPush2", false);
        MockChangeHandler initialPopHandler2 = MockChangeHandler.taggedHandler("initialPop2", false);
        RouterTransaction initialTransaction2 = RouterTransaction.with(initialController2).pushChangeHandler(initialPushHandler2).popChangeHandler(initialPopHandler2);
        router.pushController(initialTransaction2);
        View initialView1 = initialController1.getView();
        View initialView2 = initialController2.getView();
        MockChangeHandler setBackstackHandler = MockChangeHandler.taggedHandler("setBackstackHandler", true);
        List<RouterTransaction> newBackstack = Arrays.asList(initialTransaction2, initialTransaction1);
        router.setBackstack(newBackstack, setBackstackHandler);
        Assert.assertTrue(initialController1.changeHandlerHistory.isValidHistory);
        Assert.assertTrue(initialController2.changeHandlerHistory.isValidHistory);
        Assert.assertEquals(2, initialController1.changeHandlerHistory.size());
        Assert.assertEquals(2, initialController2.changeHandlerHistory.size());
        Assert.assertNotNull(initialController1.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView1, initialController1.changeHandlerHistory.latestFromView());
        Assert.assertEquals(initialPushHandler2.tag, initialController1.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertTrue(initialController1.changeHandlerHistory.latestIsPush());
        Assert.assertNull(initialController2.changeHandlerHistory.latestToView());
        Assert.assertEquals(initialView2, initialController2.changeHandlerHistory.latestFromView());
        Assert.assertEquals(setBackstackHandler.tag, initialController2.changeHandlerHistory.latestChangeHandler().tag);
        Assert.assertFalse(initialController2.changeHandlerHistory.latestIsPush());
    }
}

