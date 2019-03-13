package org.robolectric.shadows.support.v4;


import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.TestRunnerWithManifest;


@RunWith(TestRunnerWithManifest.class)
public class ShadowPagerAdapterTest {
    @Test
    public void shouldNotifyDataSetChanged() throws Exception {
        ShadowPagerAdapterTest.NullPagerAdapter pagerAdapter = new ShadowPagerAdapterTest.NullPagerAdapter();
        ShadowPagerAdapterTest.TestDataSetObserver datasetObserver = new ShadowPagerAdapterTest.TestDataSetObserver();
        Method method = PagerAdapter.class.getDeclaredMethod("registerDataSetObserver", DataSetObserver.class);
        method.setAccessible(true);
        method.invoke(pagerAdapter, datasetObserver);
        notifyDataSetChanged();
        Assert.assertTrue(datasetObserver.onChangedWasCalled);
    }

    private static class NullPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }
    }

    private static class TestDataSetObserver extends DataSetObserver {
        boolean onChangedWasCalled;

        @Override
        public void onChanged() {
            onChangedWasCalled = true;
        }
    }
}

