package trikita.anvil;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class InitTest extends Utils {
    Map<String, Boolean> called = new HashMap<>();

    @Test
    public void testInit() {
        System.out.println("============================");
        Anvil.mount(container, new Anvil.Renderable() {
            public void view() {
                init(makeFunc("once"));
                v(Utils.MockView.class, new Anvil.Renderable() {
                    public void view() {
                        init(makeFunc("setUpView"));
                    }
                });
            }
        });
        System.out.println("============================");
        Assert.assertTrue(called.get("once"));
        Assert.assertTrue(called.get("setUpView"));
        called.clear();
        Anvil.render();
        Assert.assertFalse(called.containsKey("once"));
        Assert.assertFalse(called.containsKey("setUpView"));
    }
}

