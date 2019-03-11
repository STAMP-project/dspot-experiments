package jadx.tests.smali;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInlineVarArg extends SmaliTest {
    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNodeFromSmali("TestInlineVarArg");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("f(\"a\", \"b\", \"c\");"));
    }
}

