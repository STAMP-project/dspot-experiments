package org.robolectric.shadows;


import android.widget.CheckedTextView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowCheckedTextViewTest {
    private CheckedTextView checkedTextView;

    @Test
    public void testToggle() {
        Assert.assertFalse(checkedTextView.isChecked());
        checkedTextView.toggle();
        Assert.assertTrue(checkedTextView.isChecked());
    }

    @Test
    public void testSetChecked() {
        Assert.assertFalse(checkedTextView.isChecked());
        checkedTextView.setChecked(true);
        Assert.assertTrue(checkedTextView.isChecked());
    }

    @Test
    public void toggle_shouldChangeCheckedness() throws Exception {
        CheckedTextView view = new CheckedTextView(ApplicationProvider.getApplicationContext());
        Assert.assertFalse(view.isChecked());
        view.toggle();
        Assert.assertTrue(view.isChecked());
        view.toggle();// Used to support performClick(), but Android doesn't. Sigh.

        Assert.assertFalse(view.isChecked());
    }
}

