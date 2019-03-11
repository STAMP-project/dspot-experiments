package jadx.tests.integration.others;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


public class TestIssue13a extends IntegrationTest {
    public static class TestCls {
        private static final String TAG = "Parcel";

        private static final HashMap<ClassLoader, HashMap<String, TestIssue13a.TestCls.Parcelable.Creator<?>>> mCreators = new HashMap<>();

        @SuppressWarnings("unchecked")
        public final <T extends TestIssue13a.TestCls.Parcelable> T test(ClassLoader loader) {
            String name = readString();
            if (name == null) {
                return null;
            }
            TestIssue13a.TestCls.Parcelable.Creator<T> creator;
            synchronized(TestIssue13a.TestCls.mCreators) {
                HashMap<String, TestIssue13a.TestCls.Parcelable.Creator<?>> map = TestIssue13a.TestCls.mCreators.get(loader);
                if (map == null) {
                    map = new HashMap<>();
                    TestIssue13a.TestCls.mCreators.put(loader, map);
                }
                creator = ((TestIssue13a.TestCls.Parcelable.Creator<T>) (map.get(name)));
                if (creator == null) {
                    try {
                        Class<?> c = (loader == null) ? Class.forName(name) : Class.forName(name, true, loader);
                        Field f = c.getField("CREATOR");
                        creator = ((TestIssue13a.TestCls.Parcelable.Creator) (f.get(null)));
                    } catch (IllegalAccessException e) {
                        TestIssue13a.TestCls.Log.e(TestIssue13a.TestCls.TAG, ((("1" + name) + ", e: ") + e));
                        throw new RuntimeException(("2" + name));
                    } catch (ClassNotFoundException e) {
                        TestIssue13a.TestCls.Log.e(TestIssue13a.TestCls.TAG, ((("3" + name) + ", e: ") + e));
                        throw new RuntimeException(("4" + name));
                    } catch (ClassCastException e) {
                        throw new RuntimeException(("5" + name));
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(("6" + name));
                    }
                    if (creator == null) {
                        throw new RuntimeException(("7" + name));
                    }
                    map.put(name, creator);
                }
            }
            if (creator instanceof TestIssue13a.TestCls.Parcelable.ClassLoaderCreator<?>) {
                return ((TestIssue13a.TestCls.Parcelable.ClassLoaderCreator<T>) (creator)).createFromParcel(this, loader);
            }
            return creator.createFromParcel(this);
        }

        private String readString() {
            return "";
        }

        private class Parcelable {
            public class Creator<T> {
                public T createFromParcel(TestIssue13a.TestCls testCls) {
                    return null;
                }
            }

            public class ClassLoaderCreator<T> extends TestIssue13a.TestCls.Parcelable.Creator<T> {
                public T createFromParcel(TestIssue13a.TestCls testCls, ClassLoader loader) {
                    return null;
                }
            }
        }

        private static class Log {
            public static void e(String tag, String s) {
            }
        }
    }

    @Test
    public void test() {
        disableCompilation();
        ClassNode cls = getClassNode(TestIssue13a.TestCls.class);
        String code = cls.getCode().toString();
        for (int i = 1; i <= 7; i++) {
            Assert.assertThat(code, JadxMatchers.containsOne((("\"" + i) + "\"")));
        }
        // TODO: add additional checks
        // assertThat(code, not(containsString("Throwable")));
    }
}

