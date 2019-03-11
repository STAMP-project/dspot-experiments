package trikita.anvil;


import Anvil.Renderable;
import android.view.View;
import java.lang.ref.WeakReference;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MountTest extends Utils {
    Renderable testLayout = new Anvil.Renderable() {
        public void view() {
            v(Utils.MockView.class, new Anvil.Renderable() {
                public void view() {
                    attr("text", "bar");
                }
            });
        }
    };

    @Test
    public void testMountReturnsMountPoint() {
        Assert.assertEquals(container, Anvil.mount(container, empty));
    }

    @Test
    public void testMountRendersViews() {
        Anvil.mount(container, testLayout);
        Assert.assertEquals(1, container.getChildCount());
        Assert.assertTrue(((container.getChildAt(0)) instanceof Utils.MockView));
        Utils.MockView v = ((Utils.MockView) (container.getChildAt(0)));
        Assert.assertEquals("bar", v.getText());
    }

    @Test
    public void testUnmountRemovesViews() {
        Anvil.mount(container, testLayout);
        Assert.assertEquals(1, container.getChildCount());
        Anvil.unmount(container);
        Assert.assertEquals(0, container.getChildCount());
    }

    @Test
    public void testMountReplacesViews() {
        Anvil.mount(container, testLayout);
        Assert.assertEquals(1, container.getChildCount());
        Anvil.mount(container, empty);
        Assert.assertEquals(0, container.getChildCount());
        Anvil.mount(container, testLayout);
        Assert.assertEquals(1, container.getChildCount());
    }

    @Test
    public void testMountInfoView() {
        Utils.MockView v = Anvil.mount(new Utils.MockView(getContext()), new Anvil.Renderable() {
            public void view() {
                attr("id", 100);
                attr("text", "bar");
                attr("tag", "foo");
            }
        });
        Assert.assertEquals(100, v.getId());
        Assert.assertEquals("foo", v.getTag());
        Assert.assertEquals("bar", v.getText());
    }

    @Test
    public void testMountGC() {
        Anvil.Renderable layout = Mockito.spy(testLayout);
        Anvil.mount(container, layout);
        Mockito.verify(layout, Mockito.times(1)).view();
        Assert.assertEquals(1, container.getChildCount());
        // Once the container is garbage collection all other views should be removed, too
        WeakReference<View> ref = new WeakReference(container.getChildAt(0));
        container = null;
        System.gc();
        Assert.assertEquals(null, ref.get());
        // Ensure that the associated renderable is no longer called
        Anvil.render();
        Mockito.verify(layout, Mockito.times(1)).view();
    }
}

