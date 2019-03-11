package jadx.tests.integration.names;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestReservedNames extends SmaliTest {
    /* public static class TestCls {

    public String do; // reserved name
    public String 0f; // invalid identifier

    public String try() {
    return this.do;
    }
    }
     */
    @Test
    public void test() {
        ClassNode cls = getClassNodeFromSmaliWithPath("names", "TestReservedNames");
        String code = cls.getCode().toString();
        Assert.assertThat(code, Matchers.not(Matchers.containsString("public String do;")));
    }
}

