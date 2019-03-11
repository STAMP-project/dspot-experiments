package org.robolectric.shadows;


import AdapterView.INVALID_POSITION;
import View.GONE;
import View.VISIBLE;
import android.view.View;
import android.widget.AdapterView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public abstract class AdapterViewBehavior {
    private AdapterView adapterView;

    @Test
    public void shouldIgnoreSetSelectionCallsWithInvalidPosition() {
        final List<String> transcript = new ArrayList<>();
        adapterView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transcript.add("onItemSelected fired");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ShadowLooper.idleMainLooper();
        assertThat(transcript).isEmpty();
        adapterView.setSelection(INVALID_POSITION);
        ShadowLooper.idleMainLooper();
        assertThat(transcript).isEmpty();
    }

    @Test
    public void testSetAdapter_ShouldCauseViewsToBeRenderedAsynchronously() throws Exception {
        adapterView.setAdapter(new ShadowCountingAdapter(2));
        assertThat(adapterView.getCount()).isEqualTo(2);
        assertThat(adapterView.getChildCount()).isEqualTo(0);
        Shadows.shadowOf(adapterView).populateItems();
        assertThat(adapterView.getChildCount()).isEqualTo(2);
        assertThat(getText()).isEqualTo("Item 0");
        assertThat(getText()).isEqualTo("Item 1");
    }

    @Test
    public void testSetEmptyView_ShouldHideAdapterViewIfAdapterIsNull() throws Exception {
        adapterView.setAdapter(null);
        View emptyView = new View(adapterView.getContext());
        adapterView.setEmptyView(emptyView);
        assertThat(adapterView.getVisibility()).isEqualTo(GONE);
        assertThat(emptyView.getVisibility()).isEqualTo(VISIBLE);
    }

    @Test
    public void testSetEmptyView_ShouldHideAdapterViewIfAdapterViewIsEmpty() throws Exception {
        adapterView.setAdapter(new ShadowCountingAdapter(0));
        View emptyView = new View(adapterView.getContext());
        adapterView.setEmptyView(emptyView);
        assertThat(adapterView.getVisibility()).isEqualTo(GONE);
        assertThat(emptyView.getVisibility()).isEqualTo(VISIBLE);
    }

    @Test
    public void testSetEmptyView_ShouldHideEmptyViewIfAdapterViewIsNotEmpty() throws Exception {
        adapterView.setAdapter(new ShadowCountingAdapter(1));
        View emptyView = new View(adapterView.getContext());
        adapterView.setEmptyView(emptyView);
        assertThat(adapterView.getVisibility()).isEqualTo(VISIBLE);
        assertThat(emptyView.getVisibility()).isEqualTo(GONE);
    }

    @Test
    public void testSetEmptyView_ShouldHideEmptyViewWhenAdapterGetsNewItem() throws Exception {
        ShadowCountingAdapter adapter = new ShadowCountingAdapter(0);
        adapterView.setAdapter(adapter);
        View emptyView = new View(adapterView.getContext());
        adapterView.setEmptyView(emptyView);
        assertThat(adapterView.getVisibility()).isEqualTo(GONE);
        assertThat(emptyView.getVisibility()).isEqualTo(VISIBLE);
        adapter.setCount(1);
        ShadowLooper.idleMainLooper();
        assertThat(adapterView.getVisibility()).isEqualTo(VISIBLE);
        assertThat(emptyView.getVisibility()).isEqualTo(GONE);
    }

    @Test
    public void testSetEmptyView_ShouldHideAdapterViewWhenAdapterBecomesEmpty() throws Exception {
        ShadowCountingAdapter adapter = new ShadowCountingAdapter(1);
        adapterView.setAdapter(adapter);
        View emptyView = new View(adapterView.getContext());
        adapterView.setEmptyView(emptyView);
        assertThat(adapterView.getVisibility()).isEqualTo(VISIBLE);
        assertThat(emptyView.getVisibility()).isEqualTo(GONE);
        adapter.setCount(0);
        ShadowLooper.idleMainLooper();
        assertThat(adapterView.getVisibility()).isEqualTo(GONE);
        assertThat(emptyView.getVisibility()).isEqualTo(VISIBLE);
    }
}

