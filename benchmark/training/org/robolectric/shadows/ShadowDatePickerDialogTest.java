package org.robolectric.shadows;


import DatePickerDialog.OnDateSetListener;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowDatePickerDialogTest {
    @Test
    public void returnsTheInitialYearMonthAndDayPassedIntoTheDatePickerDialog() throws Exception {
        Locale.setDefault(Locale.US);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ApplicationProvider.getApplicationContext(), null, 2012, 6, 7);
        assertThat(Shadows.shadowOf(datePickerDialog).getYear()).isEqualTo(2012);
        assertThat(Shadows.shadowOf(datePickerDialog).getMonthOfYear()).isEqualTo(6);
        assertThat(Shadows.shadowOf(datePickerDialog).getDayOfMonth()).isEqualTo(7);
    }

    @Test
    public void savesTheCallback() {
        DatePickerDialog.OnDateSetListener expectedDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                // ignored
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(ApplicationProvider.getApplicationContext(), expectedDateSetListener, 2012, 6, 7);
        ShadowDatePickerDialog shadowDatePickerDialog = Shadows.shadowOf(datePickerDialog);
        assertThat(shadowDatePickerDialog.getOnDateSetListenerCallback()).isEqualTo(expectedDateSetListener);
    }
}

