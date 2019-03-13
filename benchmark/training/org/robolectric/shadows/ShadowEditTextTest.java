package org.robolectric.shadows;


import android.R.attr.maxLength;
import android.app.Application;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;

import static org.robolectric.R.layout.edit_text;


@RunWith(AndroidJUnit4.class)
public class ShadowEditTextTest {
    private EditText editText;

    private Application context;

    @Test
    public void shouldRespectMaxLength() throws Exception {
        editText.setText("0123456678");
        assertThat(editText.getText().toString()).isEqualTo("01234");
    }

    @Test
    public void shouldAcceptNullStrings() {
        editText.setText(null);
        assertThat(editText.getText().toString()).isEqualTo("");
    }

    @Test
    public void givenInitializingWithAttributeSet_whenMaxLengthDefined_thenRestrictTextLengthToMaxLength() {
        int maxLength = anyInteger();
        AttributeSet attrs = Robolectric.buildAttributeSet().addAttribute(maxLength, (maxLength + "")).build();
        EditText editText = new EditText(context, attrs);
        String excessiveInput = stringOfLength((maxLength * 2));
        editText.setText(excessiveInput);
        assertThat(((CharSequence) (editText.getText().toString()))).isEqualTo(excessiveInput.subSequence(0, maxLength));
    }

    @Test
    public void givenInitializingWithAttributeSet_whenMaxLengthNotDefined_thenTextLengthShouldHaveNoRestrictions() {
        AttributeSet attrs = Robolectric.buildAttributeSet().build();
        EditText editText = new EditText(context, attrs);
        String input = anyString();
        editText.setText(input);
        assertThat(editText.getText().toString()).isEqualTo(input);
    }

    @Test
    public void whenInitializingWithoutAttributeSet_thenTextLengthShouldHaveNoRestrictions() {
        EditText editText = new EditText(context);
        String input = anyString();
        editText.setText(input);
        assertThat(editText.getText().toString()).isEqualTo(input);
    }

    @Test
    public void testSelectAll() {
        EditText editText = new EditText(context);
        editText.setText("foo");
        editText.selectAll();
        assertThat(editText.getSelectionStart()).isEqualTo(0);
        assertThat(editText.getSelectionEnd()).isEqualTo(3);
    }

    @Test
    public void shouldGetHintFromXml() {
        LayoutInflater inflater = LayoutInflater.from(context);
        EditText editText = ((EditText) (inflater.inflate(edit_text, null)));
        assertThat(editText.getHint().toString()).isEqualTo("Hello, Hint");
    }
}

