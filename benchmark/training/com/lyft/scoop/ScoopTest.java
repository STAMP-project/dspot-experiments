package com.lyft.scoop;


import android.content.Context;
import android.view.View;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ScoopTest {
    private static final Scoop TEST_SCOOP = new Scoop.Builder("root").build();

    @Test
    public void createRootScoop() {
        ScoopTest.FooService service = new ScoopTest.FooService();
        Scoop scoop = build();
        Assert.assertEquals("root", scoop.getName());
        Assert.assertNull(scoop.getParent());
        Assert.assertEquals(service, scoop.findService("foo_service"));
    }

    @Test
    public void createChildScoop() {
        Scoop rootScoop = new Scoop.Builder("root").build();
        Scoop childScoop = build();
        Assert.assertEquals("child", childScoop.getName());
        Assert.assertEquals(rootScoop, childScoop.getParent());
        Assert.assertEquals(childScoop, rootScoop.findChild("child"));
    }

    @Test
    public void findService() {
        ScoopTest.FooService service = new ScoopTest.FooService();
        Scoop rootScoop = build();
        Assert.assertEquals(service, rootScoop.findService("foo_service"));
    }

    @Test
    public void findServiceInParent() {
        ScoopTest.FooService service = new ScoopTest.FooService();
        Scoop rootScoop = build();
        Scoop childScoop = build();
        Assert.assertEquals(service, childScoop.findService("foo_service"));
    }

    @Test
    public void destroyChildScoop() {
        ScoopTest.FooService service1 = new ScoopTest.FooService();
        Scoop rootScoop = build();
        ScoopTest.FooService service2 = new ScoopTest.FooService();
        Scoop childScoop = build();
        ScoopTest.FooService service3 = new ScoopTest.FooService();
        Scoop grandChildScoop = build();
        childScoop.destroy();
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertNull(rootScoop.findChild("child"));
        Assert.assertNotNull(rootScoop.findService("foo_service_1"));
        Assert.assertTrue(childScoop.isDestroyed());
        Assert.assertNotNull(childScoop.getParent());
        Assert.assertNull(childScoop.findChild("grand_child"));
        Assert.assertNotNull(childScoop.findService("foo_service_1"));
        Assert.assertNotNull(childScoop.findService("foo_service_2"));
        Assert.assertTrue(grandChildScoop.isDestroyed());
        Assert.assertNotNull(grandChildScoop.getParent());
        Assert.assertNotNull(grandChildScoop.findService("foo_service_1"));
        Assert.assertNotNull(grandChildScoop.findService("foo_service_2"));
        Assert.assertNotNull(grandChildScoop.findService("foo_service_3"));
    }

    @Test
    public void fromNullView() {
        Assert.assertNull(Scoop.fromView(null));
    }

    @Test
    public void fromViewNoScoop() {
        try {
            Scoop.fromView(new ScoopTest.TestView(RuntimeEnvironment.application));
            Assert.fail("Should have thrown a RuntimeException if there is no scoop in the view's context.");
        } catch (RuntimeException e) {
            // Expected result
        }
    }

    static class FooService {}

    static class TestView extends View {
        public TestView(Context context) {
            super(context);
        }
    }
}

