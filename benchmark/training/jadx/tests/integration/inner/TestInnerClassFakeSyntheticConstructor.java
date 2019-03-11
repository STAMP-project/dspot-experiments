package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInnerClassFakeSyntheticConstructor extends SmaliTest {
    // public class TestCls {
    // public /* synthetic */ TestCls(String a) {
    // this(a, true);
    // }
    // 
    // public TestCls(String a, boolean b) {
    // }
    // 
    // public static TestCls build(String str) {
    // return new TestCls(str);
    // }
    // }
    @Test
    public void test() {
        ClassNode cls = getClassNodeFromSmali("inner/TestInnerClassFakeSyntheticConstructor", "jadx.tests.inner.TestCls");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("TestCls(String a) {"));
        // and must compile
    }
}

