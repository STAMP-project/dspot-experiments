package org.robolectric.shadows.support.v4;


import ViewPager.SimpleOnPageChangeListener;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.TestRunnerWithManifest;


@RunWith(TestRunnerWithManifest.class)
public class ShadowViewPagerTest {
    private ViewPager pager;

    private ShadowViewPagerTest.TestPagerAdapter adapter;

    @Test
    public void shouldSetAndGetAdapter() throws Exception {
        Assert.assertNull(pager.getAdapter());
        pager.setAdapter(adapter);
        Assert.assertSame(adapter, pager.getAdapter());
    }

    @Test
    public void test_getAndSetCurrentItem() throws Exception {
        pager.setAdapter(adapter);
        pager.setCurrentItem(2);
        Assert.assertEquals(2, pager.getCurrentItem());
    }

    @Test
    public void setCurrentItem_shouldInvokeListener() throws Exception {
        pager.setAdapter(adapter);
        ShadowViewPagerTest.TestOnPageChangeListener listener = new ShadowViewPagerTest.TestOnPageChangeListener();
        pager.setOnPageChangeListener(listener);
        Assert.assertFalse(listener.onPageSelectedCalled);
        pager.setCurrentItem(2);
        Assert.assertTrue(listener.onPageSelectedCalled);
    }

    @Test
    public void setCurrentItem_shouldntInvokeListenerWhenSettingRedundantly() throws Exception {
        ShadowViewPagerTest.TestOnPageChangeListener listener = new ShadowViewPagerTest.TestOnPageChangeListener();
        pager.setOnPageChangeListener(listener);
        Assert.assertFalse(listener.onPageSelectedCalled);
        pager.setCurrentItem(pager.getCurrentItem());
        Assert.assertFalse(listener.onPageSelectedCalled);
    }

    private static class TestPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }
    }

    private static class TestOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        public boolean onPageSelectedCalled;

        @Override
        public void onPageSelected(int position) {
            onPageSelectedCalled = true;
        }
    }
}

