package jadx.tests.integration.generics;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestGenerics extends IntegrationTest {
    public static class TestCls {
        class A {}

        public static void mthWildcard(List<?> list) {
        }

        public static void mthExtends(List<? extends TestGenerics.TestCls.A> list) {
        }

        public static void mthSuper(List<? super TestGenerics.TestCls.A> list) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestGenerics.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("mthWildcard(List<?> list)"));
        Assert.assertThat(code, CoreMatchers.containsString("mthExtends(List<? extends A> list)"));
        Assert.assertThat(code, CoreMatchers.containsString("mthSuper(List<? super A> list)"));
    }
}

