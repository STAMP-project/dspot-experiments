package jadx.tests.integration.generics;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestGenerics3 extends IntegrationTest {
    public static class TestCls {
        public static void mthExtendsArray(List<? extends byte[]> list) {
        }

        public static void mthSuperArray(List<? super int[]> list) {
        }

        public static void mthSuperInteger(List<? super Integer> list) {
        }

        public static void mthExtendsString(List<? super String> list) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestGenerics3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("mthExtendsArray(List<? extends byte[]> list)"));
        Assert.assertThat(code, CoreMatchers.containsString("mthSuperArray(List<? super int[]> list)"));
        Assert.assertThat(code, CoreMatchers.containsString("mthSuperInteger(List<? super Integer> list)"));
        Assert.assertThat(code, CoreMatchers.containsString("mthExtendsString(List<? super String> list)"));
    }
}

