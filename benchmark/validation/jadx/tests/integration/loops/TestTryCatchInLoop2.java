package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatchInLoop2 extends IntegrationTest {
    public static class TestCls<T extends String> {
        private static class MyItem {
            int idx;

            String name;
        }

        private final Map<Integer, TestTryCatchInLoop2.TestCls.MyItem> mCache = new HashMap<>();

        void test(TestTryCatchInLoop2.TestCls.MyItem[] items) {
            synchronized(this.mCache) {
                for (int i = 0; i < (items.length); ++i) {
                    TestTryCatchInLoop2.TestCls.MyItem existingItem = mCache.get(items[i].idx);
                    if (null == existingItem) {
                        mCache.put(items[i].idx, items[i]);
                    } else {
                        existingItem.name = items[i].name;
                    }
                }
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatchInLoop2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("synchronized (this.mCache) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("for (int i = 0; i < items.length; i++) {"));
    }
}

