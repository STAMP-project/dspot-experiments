package org.robolectric.shadows;


import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;

import static org.robolectric.R.drawable.an_image;
import static org.robolectric.Shadows.shadowOf;


@RunWith(AndroidJUnit4.class)
public class ShadowRadioButtonTest {
    private Application context;

    private RadioButton radioButton;

    @Test
    public void canBeExplicitlyChecked() throws Exception {
        Assert.assertFalse(radioButton.isChecked());
        radioButton.setChecked(true);
        Assert.assertTrue(radioButton.isChecked());
        radioButton.setChecked(false);
        Assert.assertFalse(radioButton.isChecked());
    }

    @Test
    public void canBeToggledBetweenCheckedState() throws Exception {
        Assert.assertFalse(radioButton.isChecked());
        radioButton.toggle();
        Assert.assertTrue(radioButton.isChecked());
        radioButton.toggle();
        Assert.assertTrue(radioButton.isChecked());// radio buttons can't be turned off again with a click

    }

    @Test
    public void canBeClickedToToggleCheckedState() throws Exception {
        Assert.assertFalse(radioButton.isChecked());
        radioButton.performClick();
        Assert.assertTrue(radioButton.isChecked());
        radioButton.performClick();
        Assert.assertTrue(radioButton.isChecked());// radio buttons can't be turned off again with a click

    }

    @Test
    public void shouldInformRadioGroupThatItIsChecked() throws Exception {
        RadioButton radioButton1 = new RadioButton(context);
        radioButton1.setId(99);
        RadioButton radioButton2 = new RadioButton(context);
        radioButton2.setId(100);
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.addView(radioButton1);
        radioGroup.addView(radioButton2);
        radioButton1.setChecked(true);
        assertThat(radioGroup.getCheckedRadioButtonId()).isEqualTo(radioButton1.getId());
        radioButton2.setChecked(true);
        assertThat(radioGroup.getCheckedRadioButtonId()).isEqualTo(radioButton2.getId());
    }

    @Test
    public void getButtonDrawableId() {
        radioButton.setButtonDrawable(an_image);
        assertThat(shadowOf(radioButton).getButtonDrawableId()).isEqualTo(an_image);
    }

    @Test
    public void getButtonDrawable() {
        ColorDrawable drawable = new ColorDrawable();
        radioButton.setButtonDrawable(drawable);
        assertThat(shadowOf(radioButton).getButtonDrawable()).isEqualTo(drawable);
    }
}

