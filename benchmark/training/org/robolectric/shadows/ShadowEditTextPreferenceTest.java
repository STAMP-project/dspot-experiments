package org.robolectric.shadows;


import android.content.Context;
import android.preference.EditTextPreference;
import android.widget.EditText;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowEditTextPreferenceTest {
    private static final String SOME_TEXT = "some text";

    private EditTextPreference preference;

    private Context context;

    @Test
    public void testConstructor() {
        preference = new EditTextPreference(context);
        Assert.assertNotNull(preference.getEditText());
    }

    @Test
    public void setTextInEditTextShouldStoreText() {
        final EditText editText = preference.getEditText();
        editText.setText(ShadowEditTextPreferenceTest.SOME_TEXT);
        assertThat(editText.getText().toString()).isEqualTo(ShadowEditTextPreferenceTest.SOME_TEXT);
    }

    @Test
    public void setTextShouldStoreText() {
        preference.setText("some other text");
        assertThat(preference.getText()).isEqualTo("some other text");
    }

    @Test
    public void setTextShouldStoreNull() {
        preference.setText(null);
        Assert.assertNull(preference.getText());
    }
}

