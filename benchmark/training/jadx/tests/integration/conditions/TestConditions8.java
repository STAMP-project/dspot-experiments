package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions8 extends IntegrationTest {
    public static class TestCls {
        private TestConditions8.TestCls pager;

        private TestConditions8.TestCls listView;

        public void test(TestConditions8.TestCls view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!(isUsable())) {
                return;
            }
            if (!(pager.hasMore())) {
                return;
            }
            if (getLoaderManager().hasRunningLoaders()) {
                return;
            }
            if (((listView) != null) && ((listView.getLastVisiblePosition()) >= (pager.size()))) {
                showMore();
            }
        }

        private void showMore() {
        }

        private int size() {
            return 0;
        }

        private int getLastVisiblePosition() {
            return 0;
        }

        private boolean hasRunningLoaders() {
            return false;
        }

        private TestConditions8.TestCls getLoaderManager() {
            return null;
        }

        private boolean hasMore() {
            return false;
        }

        private boolean isUsable() {
            return false;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions8.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("showMore();"));
    }
}

