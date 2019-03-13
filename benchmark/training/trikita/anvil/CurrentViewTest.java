package trikita.anvil;


import android.view.ViewGroup;
import org.junit.Assert;
import org.junit.Test;


public class CurrentViewTest extends Utils {
    @Test
    public void testCurrentView() {
        Assert.assertNull(Anvil.currentView());
        Anvil.mount(container, new Anvil.Renderable() {
            public void view() {
                Assert.assertTrue(((Anvil.currentView()) instanceof ViewGroup));
                v(Utils.MockLayout.class, new Anvil.Renderable() {
                    public void view() {
                        Assert.assertTrue(((Anvil.currentView()) instanceof Utils.MockLayout));
                        v(Utils.MockView.class, new Anvil.Renderable() {
                            public void view() {
                                Assert.assertTrue(((Anvil.currentView()) instanceof Utils.MockView));
                                attr("text", "bar");
                                Utils.MockView view = Anvil.currentView();// should cast automatically

                                Assert.assertEquals("bar", view.getText());
                            }
                        });
                        Assert.assertTrue(((Anvil.currentView()) instanceof Utils.MockLayout));
                    }
                });
                Assert.assertTrue(((Anvil.currentView()) instanceof ViewGroup));
            }
        });
        Assert.assertNull(Anvil.currentView());
    }
}

