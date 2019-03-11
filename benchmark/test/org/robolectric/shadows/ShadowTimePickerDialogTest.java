package org.robolectric.shadows;


import android.app.TimePickerDialog;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowTimePickerDialogTest {
    @Test
    public void returnsTheIntialHourAndMinutePassedIntoTheTimePickerDialog() throws Exception {
        TimePickerDialog timePickerDialog = new TimePickerDialog(ApplicationProvider.getApplicationContext(), 0, null, 6, 55, false);
        ShadowTimePickerDialog shadow = Shadows.shadowOf(timePickerDialog);
        assertThat(shadow.getHourOfDay()).isEqualTo(6);
        assertThat(shadow.getMinute()).isEqualTo(55);
    }
}

