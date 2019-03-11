package jadx.tests.integration.variables;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestVariablesIfElseChain extends IntegrationTest {
    public static class TestCls {
        String used;

        public String test(int a) {
            if (a == 0) {
                use("zero");
            } else
                if (a == 1) {
                    String r = m(a);
                    if (r != null) {
                        use(r);
                    }
                } else
                    if (a == 2) {
                        String r = m(a);
                        if (r != null) {
                            use(r);
                        }
                    } else {
                        return "miss";
                    }


            return null;
        }

        public String m(int a) {
            return "hit" + a;
        }

        public void use(String s) {
            used = s;
        }

        public void check() {
            test(0);
            Assert.assertThat(used, Matchers.is("zero"));
            test(1);
            Assert.assertThat(used, Matchers.is("hit1"));
            test(2);
            Assert.assertThat(used, Matchers.is("hit2"));
            Assert.assertThat(test(5), Matchers.is("miss"));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestVariablesIfElseChain.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("return \"miss\";"));
        // and compilable
    }
}

