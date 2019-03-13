package jadx.tests.integration.generics;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestGenerics2 extends IntegrationTest {
    public static class TestCls {
        private static class ItemReference<V> extends WeakReference<V> {
            private Object id;

            public ItemReference(V item, Object id, ReferenceQueue<? super V> queue) {
                super(item, queue);
                this.id = id;
            }
        }

        public static class ItemReferences<V> {
            private Map<Object, TestGenerics2.TestCls.ItemReference<V>> items;

            public V get(Object id) {
                WeakReference<V> ref = this.items.get(id);
                return ref != null ? ref.get() : null;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestGenerics2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("public ItemReference(V item, Object id, ReferenceQueue<? super V> queue) {"));
        Assert.assertThat(code, CoreMatchers.containsString("public V get(Object id) {"));
        Assert.assertThat(code, CoreMatchers.containsString("WeakReference<V> ref = "));
        Assert.assertThat(code, CoreMatchers.containsString("return ref != null ? ref.get() : null;"));
    }
}

