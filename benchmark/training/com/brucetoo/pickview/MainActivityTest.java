package com.brucetoo.pickview;


import R.id.date;
import R.id.province;
import android.widget.Button;
import android.widget.PopupWindow;
import com.bruce.pickerview.popwindow.DatePickerPopWin;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "app/src/main/AndroidManifest.xml")
public class MainActivityTest {
    private static final String TITLE = "PickView";

    private static final String IN_PROGRESS = "Working on...";

    private static final String DEFAULT_DATE = "2013-11-11";

    private static final float BTN_TEXT_SIZE = 16.0F;

    private static final String CONFIRM_BTN_TEXT = "CONFIRM";

    private static final int CONFIRM_BTN_COLOR = ((int) (281474959972608L));

    private static final String CANCEL_BTN_TEXT = "CANCEL";

    private static final int CANCEL_BTN_COLOR = ((int) (281474969999769L));

    private MainActivity mainActivity;

    @Test
    public void testConstructor() {
        Assert.assertEquals(MainActivityTest.TITLE, mainActivity.getTitle());
        Assert.assertNotNull(mainActivity.findViewById(date));
        Assert.assertNotNull(mainActivity.findViewById(province));
    }

    @Test
    public void testDatePickerPopWindow() {
        // given
        Button dateButton = ((Button) (mainActivity.findViewById(date)));
        // when
        boolean result = dateButton.performClick();
        // then
        Assert.assertTrue(result);
        PopupWindow popupWindow = shadowOf(RuntimeEnvironment.application).getLatestPopupWindow();
        Assert.assertNotNull(popupWindow);
        Assert.assertTrue((popupWindow instanceof DatePickerPopWin));
        DatePickerPopWin datePickerpopupWindow = ((DatePickerPopWin) (popupWindow));
        Assert.assertEquals(MainActivityTest.CONFIRM_BTN_TEXT, datePickerpopupWindow.confirmBtn.getText());
        Assert.assertEquals(MainActivityTest.BTN_TEXT_SIZE, datePickerpopupWindow.confirmBtn.getTextSize());
        Assert.assertEquals(MainActivityTest.CONFIRM_BTN_COLOR, datePickerpopupWindow.confirmBtn.getCurrentTextColor());
        Assert.assertEquals(MainActivityTest.CANCEL_BTN_TEXT, datePickerpopupWindow.cancelBtn.getText());
        Assert.assertEquals(MainActivityTest.BTN_TEXT_SIZE, datePickerpopupWindow.cancelBtn.getTextSize());
        Assert.assertEquals(MainActivityTest.CANCEL_BTN_COLOR, datePickerpopupWindow.cancelBtn.getCurrentTextColor());
    }

    @Test
    public void testDatePickerPopWindowPostProcessor() {
        // given
        Button dateButton = ((Button) (mainActivity.findViewById(date)));
        dateButton.performClick();
        DatePickerPopWin datePickerpopupWindow = ((DatePickerPopWin) (shadowOf(RuntimeEnvironment.application).getLatestPopupWindow()));
        // when
        boolean result = datePickerpopupWindow.confirmBtn.performClick();
        // then
        Assert.assertTrue(result);
        Assert.assertEquals(MainActivityTest.DEFAULT_DATE, ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testProvincePickerPopWindow() {
        // given
        Button provinceButton = ((Button) (mainActivity.findViewById(province)));
        // when
        boolean result = provinceButton.performClick();
        // then
        Assert.assertTrue(result);
        Assert.assertEquals(MainActivityTest.IN_PROGRESS, ShadowToast.getTextOfLatestToast());
    }
}

