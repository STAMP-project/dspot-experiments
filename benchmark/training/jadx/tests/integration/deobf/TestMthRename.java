package jadx.tests.integration.deobf;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestMthRename extends IntegrationTest {
    public static class TestCls {
        public abstract static class TestAbstractCls {
            public abstract void a();
        }

        public void test(TestMthRename.TestCls.TestAbstractCls a) {
            a.a();
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        enableDeobfuscation();
        ClassNode cls = getClassNode(TestMthRename.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("public abstract void mo1a();"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("public abstract void a();")));
        Assert.assertThat(code, CoreMatchers.containsString(".mo1a();"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString(".a();")));
    }
}

