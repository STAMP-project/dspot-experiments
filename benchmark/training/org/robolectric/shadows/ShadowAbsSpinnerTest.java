package org.robolectric.shadows;


import android.R.layout.simple_spinner_item;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowAbsSpinnerTest {
    private Context context;

    private Spinner spinner;

    private ShadowAbsSpinner shadowSpinner;

    private ArrayAdapter<String> arrayAdapter;

    @Test
    public void checkSetAdapter() {
        spinner.setAdapter(arrayAdapter);
    }

    @Test
    public void getSelectedItemShouldReturnCorrectValue() {
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);
        assertThat(((String) (spinner.getSelectedItem()))).isEqualTo("foo");
        assertThat(((String) (spinner.getSelectedItem()))).isNotEqualTo("bar");
        spinner.setSelection(1);
        assertThat(((String) (spinner.getSelectedItem()))).isEqualTo("bar");
        assertThat(((String) (spinner.getSelectedItem()))).isNotEqualTo("foo");
    }

    @Test
    public void getSelectedItemShouldReturnNull_NoAdapterSet() {
        assertThat(spinner.getSelectedItem()).isNull();
    }

    @Test
    public void setSelectionWithAnimatedTransition() {
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0, true);
        assertThat(((String) (spinner.getSelectedItem()))).isEqualTo("foo");
        assertThat(((String) (spinner.getSelectedItem()))).isNotEqualTo("bar");
        assertThat(shadowSpinner.isAnimatedTransition()).isTrue();
    }

    private static class MyArrayAdapter extends ArrayAdapter<String> {
        public MyArrayAdapter(Context context, String[] testItems) {
            super(context, simple_spinner_item, testItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return new View(getContext());
        }
    }
}

