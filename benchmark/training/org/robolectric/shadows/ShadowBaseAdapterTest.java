package org.robolectric.shadows;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowBaseAdapterTest {
    @Test
    public void shouldRecordNotifyDataSetChanged() throws Exception {
        BaseAdapter adapter = new ShadowBaseAdapterTest.TestBaseAdapter();
        adapter.notifyDataSetChanged();
        Assert.assertTrue(Shadows.shadowOf(adapter).wasNotifyDataSetChangedCalled());
    }

    @Test
    public void canResetNotifyDataSetChangedFlag() throws Exception {
        BaseAdapter adapter = new ShadowBaseAdapterTest.TestBaseAdapter();
        adapter.notifyDataSetChanged();
        Shadows.shadowOf(adapter).clearWasDataSetChangedCalledFlag();
        Assert.assertFalse(Shadows.shadowOf(adapter).wasNotifyDataSetChangedCalled());
    }

    private static class TestBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}

