package trikita.anvil;


import org.junit.Assert;
import org.junit.Test;


public class IncrementalRenderTest extends Utils {
    private String fooValue = "a";

    private boolean showView = true;

    @Test
    public void testConstantsRenderedOnce() {
        Anvil.mount(container, new Anvil.Renderable() {
            public void view() {
                o(v(Utils.MockLayout.class), attr("text", "bar"));
            }
        });
        Assert.assertEquals(1, ((int) (createdViews.get(Utils.MockLayout.class))));
        Assert.assertEquals(1, ((int) (changedAttrs.get("text"))));
        Anvil.render();
        Assert.assertEquals(1, ((int) (createdViews.get(Utils.MockLayout.class))));
        Assert.assertEquals(1, ((int) (changedAttrs.get("text"))));
    }

    @Test
    public void testDynamicAttributeRenderedLazily() {
        Anvil.mount(container, new Anvil.Renderable() {
            public void view() {
                o(v(Utils.MockLayout.class), attr("text", fooValue));
            }
        });
        Assert.assertEquals(1, ((int) (changedAttrs.get("text"))));
        Anvil.render();
        Assert.assertEquals(1, ((int) (changedAttrs.get("text"))));
        fooValue = "b";
        Anvil.render();
        Assert.assertEquals(2, ((int) (changedAttrs.get("text"))));
        Anvil.render();
        Assert.assertEquals(2, ((int) (changedAttrs.get("text"))));
    }

    @Test
    public void testDynamicViewRenderedLazily() {
        Anvil.mount(container, new Anvil.Renderable() {
            public void view() {
                o(v(Utils.MockLayout.class), o(v(Utils.MockLayout.class)), (showView ? o(v(Utils.MockView.class)) : null));
            }
        });
        Utils.MockLayout layout = ((Utils.MockLayout) (container.getChildAt(0)));
        Assert.assertEquals(2, layout.getChildCount());
        Assert.assertEquals(1, ((int) (createdViews.get(Utils.MockView.class))));
        Anvil.render();
        Assert.assertEquals(1, ((int) (createdViews.get(Utils.MockView.class))));
        showView = false;
        Anvil.render();
        Assert.assertEquals(1, layout.getChildCount());
        Assert.assertEquals(1, ((int) (createdViews.get(Utils.MockView.class))));
        Anvil.render();
        Assert.assertEquals(1, ((int) (createdViews.get(Utils.MockView.class))));
        showView = true;
        Anvil.render();
        Assert.assertEquals(2, layout.getChildCount());
        Assert.assertEquals(2, ((int) (createdViews.get(Utils.MockView.class))));
        Anvil.render();
        Assert.assertEquals(2, ((int) (createdViews.get(Utils.MockView.class))));
    }

    private String firstMountValue = "foo";

    private String secondMountValue = "bar";

    @Test
    public void testRenderUpdatesAllMounts() {
        Utils.MockLayout rootA = new Utils.MockLayout(getContext());
        Utils.MockLayout rootB = new Utils.MockLayout(getContext());
        Anvil.mount(rootA, new Anvil.Renderable() {
            public void view() {
                attr("text", firstMountValue);
            }
        });
        Anvil.mount(rootB, new Anvil.Renderable() {
            public void view() {
                attr("tag", secondMountValue);
            }
        });
        Assert.assertEquals("foo", rootA.getText());
        Assert.assertEquals("bar", rootB.getTag());
        firstMountValue = "baz";
        secondMountValue = "qux";
        Anvil.render();
        Assert.assertEquals("baz", rootA.getText());
        Assert.assertEquals("qux", rootB.getTag());
    }
}

