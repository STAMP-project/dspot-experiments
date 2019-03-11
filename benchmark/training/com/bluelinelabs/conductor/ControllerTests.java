package com.bluelinelabs.conductor;


import Activity.RESULT_OK;
import RetainViewMode.RELEASE_DETACH;
import RetainViewMode.RETAIN_DETACH;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.bluelinelabs.conductor.util.ActivityProxy;
import com.bluelinelabs.conductor.util.CallState;
import com.bluelinelabs.conductor.util.TestController;
import com.bluelinelabs.conductor.util.ViewUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ControllerTests {
    private ActivityProxy activityProxy;

    private Router router;

    @Test
    public void testViewRetention() {
        Controller controller = new TestController();
        controller.setRouter(router);
        // Test View getting released w/ RELEASE_DETACH
        controller.setRetainViewMode(RELEASE_DETACH);
        Assert.assertNull(controller.getView());
        View view = controller.inflate(router.container);
        Assert.assertNotNull(controller.getView());
        ViewUtils.reportAttached(view, true);
        Assert.assertNotNull(controller.getView());
        ViewUtils.reportAttached(view, false);
        Assert.assertNull(controller.getView());
        // Test View getting retained w/ RETAIN_DETACH
        controller.setRetainViewMode(RETAIN_DETACH);
        view = controller.inflate(router.container);
        Assert.assertNotNull(controller.getView());
        ViewUtils.reportAttached(view, true);
        Assert.assertNotNull(controller.getView());
        ViewUtils.reportAttached(view, false);
        Assert.assertNotNull(controller.getView());
        // Ensure re-setting RELEASE_DETACH releases
        controller.setRetainViewMode(RELEASE_DETACH);
        Assert.assertNull(controller.getView());
    }

    @Test
    public void testActivityResult() {
        TestController controller = new TestController();
        CallState expectedCallState = new CallState(true);
        router.pushController(RouterTransaction.with(controller));
        // Ensure that calling onActivityResult w/o requesting a result doesn't do anything
        router.onActivityResult(1, RESULT_OK, null);
        assertCalls(expectedCallState, controller);
        // Ensure starting an activity for result gets us the result back
        controller.startActivityForResult(new Intent("action"), 1);
        router.onActivityResult(1, RESULT_OK, null);
        (expectedCallState.onActivityResultCalls)++;
        assertCalls(expectedCallState, controller);
        // Ensure requesting a result w/o calling startActivityForResult works
        registerForActivityResult(2);
        router.onActivityResult(2, RESULT_OK, null);
        (expectedCallState.onActivityResultCalls)++;
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testActivityResultForChild() {
        TestController parent = new TestController();
        TestController child = new TestController();
        router.pushController(RouterTransaction.with(parent));
        getChildRouter(((android.view.ViewGroup) (getView().findViewById(TestController.VIEW_ID)))).setRoot(RouterTransaction.with(child));
        CallState childExpectedCallState = new CallState(true);
        CallState parentExpectedCallState = new CallState(true);
        // Ensure that calling onActivityResult w/o requesting a result doesn't do anything
        router.onActivityResult(1, RESULT_OK, null);
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
        // Ensure starting an activity for result gets us the result back
        child.startActivityForResult(new Intent("action"), 1);
        router.onActivityResult(1, RESULT_OK, null);
        (childExpectedCallState.onActivityResultCalls)++;
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
        // Ensure requesting a result w/o calling startActivityForResult works
        registerForActivityResult(2);
        router.onActivityResult(2, RESULT_OK, null);
        (childExpectedCallState.onActivityResultCalls)++;
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
    }

    @Test
    public void testPermissionResult() {
        final String[] requestedPermissions = new String[]{ "test" };
        TestController controller = new TestController();
        CallState expectedCallState = new CallState(true);
        router.pushController(RouterTransaction.with(controller));
        // Ensure that calling handleRequestedPermission w/o requesting a result doesn't do anything
        router.onRequestPermissionsResult("anotherId", 1, requestedPermissions, new int[]{ 1 });
        assertCalls(expectedCallState, controller);
        // Ensure requesting the permission gets us the result back
        try {
            requestPermissions(requestedPermissions, 1);
        } catch (NoSuchMethodError ignored) {
        }
        router.onRequestPermissionsResult(getInstanceId(), 1, requestedPermissions, new int[]{ 1 });
        (expectedCallState.onRequestPermissionsResultCalls)++;
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testPermissionResultForChild() {
        final String[] requestedPermissions = new String[]{ "test" };
        TestController parent = new TestController();
        TestController child = new TestController();
        router.pushController(RouterTransaction.with(parent));
        getChildRouter(((android.view.ViewGroup) (getView().findViewById(TestController.VIEW_ID)))).setRoot(RouterTransaction.with(child));
        CallState childExpectedCallState = new CallState(true);
        CallState parentExpectedCallState = new CallState(true);
        // Ensure that calling handleRequestedPermission w/o requesting a result doesn't do anything
        router.onRequestPermissionsResult("anotherId", 1, requestedPermissions, new int[]{ 1 });
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
        // Ensure requesting the permission gets us the result back
        try {
            requestPermissions(requestedPermissions, 1);
        } catch (NoSuchMethodError ignored) {
        }
        router.onRequestPermissionsResult(getInstanceId(), 1, requestedPermissions, new int[]{ 1 });
        (childExpectedCallState.onRequestPermissionsResultCalls)++;
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
    }

    @Test
    public void testOptionsMenu() {
        TestController controller = new TestController();
        CallState expectedCallState = new CallState(true);
        router.pushController(RouterTransaction.with(controller));
        // Ensure that calling onCreateOptionsMenu w/o declaring that we have one doesn't do anything
        router.onCreateOptionsMenu(null, null);
        assertCalls(expectedCallState, controller);
        // Ensure calling onCreateOptionsMenu with a menu works
        setHasOptionsMenu(true);
        // Ensure it'll still get called back next time onCreateOptionsMenu is called
        router.onCreateOptionsMenu(null, null);
        (expectedCallState.createOptionsMenuCalls)++;
        assertCalls(expectedCallState, controller);
        // Ensure we stop getting them when we hide it
        setOptionsMenuHidden(true);
        router.onCreateOptionsMenu(null, null);
        assertCalls(expectedCallState, controller);
        // Ensure we get the callback them when we un-hide it
        setOptionsMenuHidden(false);
        router.onCreateOptionsMenu(null, null);
        (expectedCallState.createOptionsMenuCalls)++;
        assertCalls(expectedCallState, controller);
        // Ensure we don't get the callback when we no longer have a menu
        setHasOptionsMenu(false);
        router.onCreateOptionsMenu(null, null);
        assertCalls(expectedCallState, controller);
    }

    @Test
    public void testOptionsMenuForChild() {
        TestController parent = new TestController();
        TestController child = new TestController();
        router.pushController(RouterTransaction.with(parent));
        getChildRouter(((android.view.ViewGroup) (getView().findViewById(TestController.VIEW_ID)))).setRoot(RouterTransaction.with(child));
        CallState childExpectedCallState = new CallState(true);
        CallState parentExpectedCallState = new CallState(true);
        // Ensure that calling onCreateOptionsMenu w/o declaring that we have one doesn't do anything
        router.onCreateOptionsMenu(null, null);
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
        // Ensure calling onCreateOptionsMenu with a menu works
        setHasOptionsMenu(true);
        // Ensure it'll still get called back next time onCreateOptionsMenu is called
        router.onCreateOptionsMenu(null, null);
        (childExpectedCallState.createOptionsMenuCalls)++;
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
        // Ensure we stop getting them when we hide it
        setOptionsMenuHidden(true);
        router.onCreateOptionsMenu(null, null);
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
        // Ensure we get the callback them when we un-hide it
        setOptionsMenuHidden(false);
        router.onCreateOptionsMenu(null, null);
        (childExpectedCallState.createOptionsMenuCalls)++;
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
        // Ensure we don't get the callback when we no longer have a menu
        setHasOptionsMenu(false);
        router.onCreateOptionsMenu(null, null);
        assertCalls(childExpectedCallState, child);
        assertCalls(parentExpectedCallState, parent);
    }

    @Test
    public void testAddRemoveChildControllers() {
        TestController parent = new TestController();
        TestController child1 = new TestController();
        TestController child2 = new TestController();
        router.pushController(RouterTransaction.with(parent));
        Assert.assertEquals(0, getChildRouters().size());
        Assert.assertNull(getParentController());
        Assert.assertNull(getParentController());
        Router childRouter = parent.getChildRouter(((android.view.ViewGroup) (getView().findViewById(TestController.VIEW_ID))));
        childRouter.setPopsLastView(true);
        childRouter.setRoot(RouterTransaction.with(child1));
        Assert.assertEquals(1, getChildRouters().size());
        Assert.assertEquals(childRouter, getChildRouters().get(0));
        Assert.assertEquals(1, childRouter.getBackstackSize());
        Assert.assertEquals(child1, childRouter.getControllers().get(0));
        Assert.assertEquals(parent, getParentController());
        Assert.assertNull(getParentController());
        childRouter = getChildRouter(((android.view.ViewGroup) (getView().findViewById(TestController.VIEW_ID))));
        childRouter.pushController(RouterTransaction.with(child2));
        Assert.assertEquals(1, getChildRouters().size());
        Assert.assertEquals(childRouter, getChildRouters().get(0));
        Assert.assertEquals(2, childRouter.getBackstackSize());
        Assert.assertEquals(child1, childRouter.getControllers().get(0));
        Assert.assertEquals(child2, childRouter.getControllers().get(1));
        Assert.assertEquals(parent, getParentController());
        Assert.assertEquals(parent, getParentController());
        childRouter.popController(child2);
        Assert.assertEquals(1, getChildRouters().size());
        Assert.assertEquals(childRouter, getChildRouters().get(0));
        Assert.assertEquals(1, childRouter.getBackstackSize());
        Assert.assertEquals(child1, childRouter.getControllers().get(0));
        Assert.assertEquals(parent, getParentController());
        Assert.assertNull(getParentController());
        childRouter.popController(child1);
        Assert.assertEquals(1, getChildRouters().size());
        Assert.assertEquals(childRouter, getChildRouters().get(0));
        Assert.assertEquals(0, childRouter.getBackstackSize());
        Assert.assertNull(getParentController());
        Assert.assertNull(getParentController());
    }

    @Test
    public void testAddRemoveChildRouters() {
        TestController parent = new TestController();
        TestController child1 = new TestController();
        TestController child2 = new TestController();
        router.pushController(RouterTransaction.with(parent));
        Assert.assertEquals(0, getChildRouters().size());
        Assert.assertNull(getParentController());
        Assert.assertNull(getParentController());
        Router childRouter1 = parent.getChildRouter(((android.view.ViewGroup) (getView().findViewById(TestController.CHILD_VIEW_ID_1))));
        Router childRouter2 = parent.getChildRouter(((android.view.ViewGroup) (getView().findViewById(TestController.CHILD_VIEW_ID_2))));
        childRouter1.setRoot(RouterTransaction.with(child1));
        childRouter2.setRoot(RouterTransaction.with(child2));
        Assert.assertEquals(2, getChildRouters().size());
        Assert.assertEquals(childRouter1, getChildRouters().get(0));
        Assert.assertEquals(childRouter2, getChildRouters().get(1));
        Assert.assertEquals(1, childRouter1.getBackstackSize());
        Assert.assertEquals(1, childRouter2.getBackstackSize());
        Assert.assertEquals(child1, childRouter1.getControllers().get(0));
        Assert.assertEquals(child2, childRouter2.getControllers().get(0));
        Assert.assertEquals(parent, getParentController());
        Assert.assertEquals(parent, getParentController());
        parent.removeChildRouter(childRouter2);
        Assert.assertEquals(1, getChildRouters().size());
        Assert.assertEquals(childRouter1, getChildRouters().get(0));
        Assert.assertEquals(1, childRouter1.getBackstackSize());
        Assert.assertEquals(0, childRouter2.getBackstackSize());
        Assert.assertEquals(child1, childRouter1.getControllers().get(0));
        Assert.assertEquals(parent, getParentController());
        Assert.assertNull(getParentController());
        parent.removeChildRouter(childRouter1);
        Assert.assertEquals(0, getChildRouters().size());
        Assert.assertEquals(0, childRouter1.getBackstackSize());
        Assert.assertEquals(0, childRouter2.getBackstackSize());
        Assert.assertNull(getParentController());
        Assert.assertNull(getParentController());
    }

    @Test
    public void testRestoredChildRouterBackstack() {
        TestController parent = new TestController();
        router.pushController(RouterTransaction.with(parent));
        ViewUtils.reportAttached(getView(), true);
        RouterTransaction childTransaction1 = RouterTransaction.with(new TestController());
        RouterTransaction childTransaction2 = RouterTransaction.with(new TestController());
        Router childRouter = parent.getChildRouter(((android.view.ViewGroup) (getView().findViewById(TestController.CHILD_VIEW_ID_1))));
        childRouter.setPopsLastView(true);
        childRouter.setRoot(childTransaction1);
        childRouter.pushController(childTransaction2);
        Bundle savedState = new Bundle();
        childRouter.saveInstanceState(savedState);
        parent.removeChildRouter(childRouter);
        childRouter = getChildRouter(((android.view.ViewGroup) (getView().findViewById(TestController.CHILD_VIEW_ID_1))));
        Assert.assertEquals(0, childRouter.getBackstackSize());
        childRouter.restoreInstanceState(savedState);
        childRouter.rebindIfNeeded();
        Assert.assertEquals(2, childRouter.getBackstackSize());
        RouterTransaction restoredChildTransaction1 = childRouter.getBackstack().get(0);
        RouterTransaction restoredChildTransaction2 = childRouter.getBackstack().get(1);
        Assert.assertEquals(childTransaction1.transactionIndex, restoredChildTransaction1.transactionIndex);
        Assert.assertEquals(childTransaction1.controller.getInstanceId(), restoredChildTransaction1.controller.getInstanceId());
        Assert.assertEquals(childTransaction2.transactionIndex, restoredChildTransaction2.transactionIndex);
        Assert.assertEquals(childTransaction2.controller.getInstanceId(), restoredChildTransaction2.controller.getInstanceId());
        Assert.assertTrue(handleBack());
        Assert.assertEquals(1, childRouter.getBackstackSize());
        Assert.assertEquals(restoredChildTransaction1, childRouter.getBackstack().get(0));
        Assert.assertTrue(handleBack());
        Assert.assertEquals(0, childRouter.getBackstackSize());
    }
}

