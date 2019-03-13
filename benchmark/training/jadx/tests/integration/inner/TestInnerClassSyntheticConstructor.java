package jadx.tests.integration.inner;


import jadx.tests.api.IntegrationTest;
import org.junit.Test;


public class TestInnerClassSyntheticConstructor extends IntegrationTest {
    private class TestCls {
        private int mth() {
            return 1;
        }
    }

    @Test
    public void test() {
        getClassNode(TestInnerClassSyntheticConstructor.class);
        // must compile, no usage of removed synthetic empty class
    }
}

