package jadx.tests.integration.others;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestFieldInit extends IntegrationTest {
    public static class TestCls {
        public class A {}

        private static List<String> s = new ArrayList<>();

        private TestFieldInit.TestCls.A a = new TestFieldInit.TestCls.A();

        private int i = 1 + (Random.class.getSimpleName().length());

        private int n = 0;

        public TestCls(int z) {
            this.n = z;
            this.n = 0;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFieldInit.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("List<String> s = new ArrayList"));
        Assert.assertThat(code, JadxMatchers.containsOne("A a = new A();"));
        Assert.assertThat(code, JadxMatchers.containsOne("int i = (Random.class.getSimpleName().length() + 1);"));
        Assert.assertThat(code, JadxMatchers.containsOne("int n = 0;"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("static {")));
        Assert.assertThat(code, JadxMatchers.containsOne("this.n = z;"));
        Assert.assertThat(code, JadxMatchers.containsOne("this.n = 0;"));
    }
}

