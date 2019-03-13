package cucumber.runtime;


import org.junit.Assert;
import org.junit.Test;


public class EnvTest {
    private Env env = new Env("env-test");

    @Test
    public void looks_up_value_from_environment() {
        Assert.assertNotNull(env.get("PATH"));
    }

    @Test
    public void returns_null_for_absent_key() {
        Assert.assertNull(env.get("pxfj54#"));
    }

    @Test
    public void looks_up_dotted_value_from_resource_bundle_with_dots() {
        Assert.assertEquals("a.b", env.get("a.b"));
    }

    @Test
    public void looks_up_dotted_value_from_resource_bundle_with_underscores() {
        Assert.assertEquals("a.b", env.get("A_B"));
    }

    @Test
    public void looks_up_underscored_value_from_resource_bundle_with_dots() {
        Assert.assertEquals("B_C", env.get("b.c"));
    }

    @Test
    public void looks_up_underscored_value_from_resource_bundle_with_underscores() {
        Assert.assertEquals("B_C", env.get("B_C"));
    }

    @Test
    public void looks_up_value_by_exact_case_keuy() {
        Assert.assertEquals("C_D", env.get("c.D"));
    }
}

