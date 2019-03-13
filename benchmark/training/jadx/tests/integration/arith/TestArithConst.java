package jadx.tests.integration.arith;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestArithConst extends SmaliTest {
    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNodeFromSmaliWithPath("arith", "TestArithConst");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("return i + CONST_INT;"));
    }
}

