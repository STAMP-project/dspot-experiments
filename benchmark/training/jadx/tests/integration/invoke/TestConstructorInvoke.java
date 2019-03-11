package jadx.tests.integration.invoke;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConstructorInvoke extends IntegrationTest {
    public class TestCls {
        void test(String root, String name) {
            TestConstructorInvoke.TestCls.ViewHolder viewHolder = new TestConstructorInvoke.TestCls.ViewHolder(root, name);
        }

        private final class ViewHolder {
            private int mElements = 0;

            private final String mRoot;

            private String mName;

            private ViewHolder(String root, String name) {
                this.mRoot = root;
                this.mName = name;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConstructorInvoke.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, Matchers.containsString("new ViewHolder(root, name);"));
    }
}

