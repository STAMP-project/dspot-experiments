package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;


public class TestConditions2 extends IntegrationTest {
    public static class TestCls {
        int c;

        String d;

        String f;

        public void testComplexIf(String a, int b) {
            if (((d) == null) || ((((c) == 0) && (b != (-1))) && ((d.length()) == 0))) {
                c = a.codePointAt(c);
            } else {
                if ((a.hashCode()) != 3294) {
                    c = f.compareTo(a);
                }
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions2.TestCls.class);
        String code = cls.getCode().toString();
        // assertThat(code, containsString("return;"));
        // assertThat(code, not(containsString("else")));
    }
}

