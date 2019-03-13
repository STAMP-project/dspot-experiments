package cucumber.runtime;


import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;


public class UtilsTest {
    @Test
    public void public_non_static_inner_classes_are_not_instantiable() {
        Assert.assertFalse(Utils.isInstantiable(UtilsTest.NonStaticInnerClass.class));
    }

    @Test
    public void public_static_inner_classes_are_instantiable() {
        Assert.assertTrue(Utils.isInstantiable(UtilsTest.StaticInnerClass.class));
    }

    public class NonStaticInnerClass {}

    public static class StaticInnerClass {}

    @Test
    public void test_url() throws MalformedURLException {
        URL dotCucumber = Utils.toURL("foo/bar/.cucumber");
        URL url = new URL(dotCucumber, "stepdefs.json");
        Assert.assertEquals(new URL("file:foo/bar/.cucumber/stepdefs.json"), url);
    }
}

