package org.robolectric.shadows;


import android.app.Application;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Shadows;

import static org.robolectric.R.id.title;
import static org.robolectric.R.layout.main;


@RunWith(AndroidJUnit4.class)
public class ShadowArrayAdapterTest {
    private ArrayAdapter<Integer> arrayAdapter;

    private Application context;

    @Test
    public void verifyContext() {
        assertThat(arrayAdapter.getContext()).isSameAs(context);
    }

    @Test
    @SuppressWarnings("BoxedPrimitiveConstructor")
    public void verifyListContent() {
        Assert.assertEquals(3, arrayAdapter.getCount());
        Assert.assertEquals(new Integer(1), arrayAdapter.getItem(0));
        Assert.assertEquals(new Integer(2), arrayAdapter.getItem(1));
        Assert.assertEquals(new Integer(3), arrayAdapter.getItem(2));
    }

    @Test
    public void usesTextViewResourceIdToSetTextWithinListItemView() throws Exception {
        ListView parent = new ListView(context);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(context, main, title, new String[]{ "first value" });
        View listItemView = arrayAdapter.getView(0, null, parent);
        TextView titleTextView = listItemView.findViewById(title);
        Assert.assertEquals("first value", titleTextView.getText().toString());
    }

    @Test
    public void hasTheCorrectConstructorResourceIDs() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(context, title, new String[]{ "first value" });
        // this assertion may look a little backwards since R.id.title is labeled
        // textViewResourceId in the constructor parameter list, but the output is correct.
        assertThat(Shadows.shadowOf(arrayAdapter).getResourceId()).isEqualTo(title);
        assertThat(Shadows.shadowOf(arrayAdapter).getTextViewResourceId()).isNotEqualTo(title);
        assertThat(Shadows.shadowOf(arrayAdapter).getTextViewResourceId()).isEqualTo(0);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter(context, title);
        // this assertion may look a little backwards since R.id.title is labeled
        // textViewResourceId in the constructor parameter list, but the output is correct.
        assertThat(Shadows.shadowOf(arrayAdapter2).getResourceId()).isEqualTo(title);
        assertThat(Shadows.shadowOf(arrayAdapter2).getTextViewResourceId()).isNotEqualTo(title);
        assertThat(Shadows.shadowOf(arrayAdapter2).getTextViewResourceId()).isEqualTo(0);
        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter(context, title, Arrays.asList(new String[]{ "first value" }));
        // this assertion may look a little backwards since R.id.title is labeled
        // textViewResourceId in the constructor parameter list, but the output is correct.
        assertThat(Shadows.shadowOf(arrayAdapter3).getResourceId()).isEqualTo(title);
        assertThat(Shadows.shadowOf(arrayAdapter3).getTextViewResourceId()).isNotEqualTo(title);
        assertThat(Shadows.shadowOf(arrayAdapter3).getTextViewResourceId()).isEqualTo(0);
    }

    @Test
    public void shouldClear() throws Exception {
        arrayAdapter.clear();
        Assert.assertEquals(0, arrayAdapter.getCount());
    }

    @Test
    @SuppressWarnings("BoxedPrimitiveConstructor")
    public void test_remove() throws Exception {
        Integer firstItem = arrayAdapter.getItem(0);
        Assert.assertEquals(3, arrayAdapter.getCount());
        Assert.assertEquals(new Integer(1), firstItem);
        arrayAdapter.remove(firstItem);
        Assert.assertEquals(2, arrayAdapter.getCount());
        Assert.assertEquals(new Integer(2), arrayAdapter.getItem(0));
        Assert.assertEquals(new Integer(3), arrayAdapter.getItem(1));
    }
}

