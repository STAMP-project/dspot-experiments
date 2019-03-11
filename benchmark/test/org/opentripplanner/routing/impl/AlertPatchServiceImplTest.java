package org.opentripplanner.routing.impl;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.routing.alertpatch.AlertPatch;
import org.opentripplanner.routing.graph.Graph;


public class AlertPatchServiceImplTest {
    private class TestAlertPatch extends AlertPatch {
        private static final long serialVersionUID = 1L;

        @Override
        public void apply(Graph graph) {
            // NO-OP
        }

        @Override
        public void remove(Graph graph) {
            // NO-OP
        }
    }

    private AlertPatchServiceImplTest.TestAlertPatch[] alerts;

    private FeedScopedId testStop = new FeedScopedId("A", "A");

    private FeedScopedId testRoute = new FeedScopedId("B", "B");

    @Test
    public void testApplyAndExpire() {
        AlertPatchServiceImpl instance = getAlertPatchServiceImpl();
        instance.apply(alerts[0]);
        Assert.assertTrue(instance.getStopPatches(testStop).contains(alerts[0]));
        Assert.assertTrue(instance.getRoutePatches(testRoute).contains(alerts[0]));
        instance.expire(Collections.singleton(getId()));
        Assert.assertTrue(instance.getStopPatches(testStop).isEmpty());
        Assert.assertTrue(instance.getRoutePatches(testRoute).isEmpty());
    }

    @Test
    public void testExpire() {
        Set<String> purge = new HashSet<String>();
        AlertPatchServiceImpl instance = getAlertPatchServiceImpl();
        for (AlertPatchServiceImplTest.TestAlertPatch alert : alerts) {
            instance.apply(alert);
        }
        purge.add(getId());
        purge.add(getId());
        instance.expire(purge);
        Assert.assertEquals(2, instance.getAllAlertPatches().size());
        Assert.assertFalse(instance.getAllAlertPatches().contains(alerts[0]));
        Assert.assertFalse(instance.getAllAlertPatches().contains(alerts[1]));
        Assert.assertTrue(instance.getAllAlertPatches().contains(alerts[2]));
        Assert.assertTrue(instance.getAllAlertPatches().contains(alerts[3]));
    }

    @Test
    public void testExpireAll() {
        Set<String> purge = new HashSet<String>();
        AlertPatchServiceImpl instance = getAlertPatchServiceImpl();
        for (AlertPatchServiceImplTest.TestAlertPatch alert : alerts) {
            purge.add(getId());
            instance.apply(alert);
        }
        instance.expireAll();
        Assert.assertTrue(instance.getAllAlertPatches().isEmpty());
    }

    @Test
    public void testExpireAllExcept() {
        AlertPatchServiceImpl instance = getAlertPatchServiceImpl();
        for (AlertPatchServiceImplTest.TestAlertPatch alert : alerts) {
            instance.apply(alert);
        }
        instance.expireAllExcept(Collections.singleton(getId()));
        Assert.assertEquals(1, instance.getAllAlertPatches().size());
        Assert.assertTrue(instance.getAllAlertPatches().contains(alerts[0]));
    }
}

