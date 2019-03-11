package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestFinally extends IntegrationTest {
    public static class TestCls {
        private static final String DISPLAY_NAME = "name";

        String test(TestFinally.TestCls.Context context, Object uri) {
            TestFinally.TestCls.Cursor cursor = null;
            try {
                String[] projection = new String[]{ TestFinally.TestCls.DISPLAY_NAME };
                cursor = context.query(uri, projection);
                int columnIndex = cursor.getColumnIndexOrThrow(TestFinally.TestCls.DISPLAY_NAME);
                cursor.moveToFirst();
                return cursor.getString(columnIndex);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        private class Context {
            public TestFinally.TestCls.Cursor query(Object o, String[] s) {
                return null;
            }
        }

        private class Cursor {
            public void close() {
            }

            public void moveToFirst() {
            }

            public int getColumnIndexOrThrow(String s) {
                return 0;
            }

            public String getString(int i) {
                return null;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFinally.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("cursor.getString(columnIndex);"));
        Assert.assertThat(code, CoreMatchers.not(JadxMatchers.containsOne("String str = true;")));
    }
}

