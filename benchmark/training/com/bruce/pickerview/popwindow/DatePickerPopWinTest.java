package com.bruce.pickerview.popwindow;


import DatePickerPopWin.Builder;
import com.brucetoo.pickview.BuildConfig;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "app/src/main/AndroidManifest.xml")
public class DatePickerPopWinTest {
    private DatePickerPopWin datePickerPopWin;

    private Builder builder;

    @Test
    public void testConstructor() {
        Assert.assertEquals(Whitebox.getInternalState(builder, "minYear"), Whitebox.getInternalState(datePickerPopWin, "minYear"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "maxYear"), Whitebox.getInternalState(datePickerPopWin, "maxYear"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "textCancel"), Whitebox.getInternalState(datePickerPopWin, "textCancel"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "textConfirm"), Whitebox.getInternalState(datePickerPopWin, "textConfirm"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "context"), Whitebox.getInternalState(datePickerPopWin, "mContext"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "listener"), Whitebox.getInternalState(datePickerPopWin, "mListener"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "colorCancel"), Whitebox.getInternalState(datePickerPopWin, "colorCancel"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "colorConfirm"), Whitebox.getInternalState(datePickerPopWin, "colorConfirm"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "btnTextSize"), Whitebox.getInternalState(datePickerPopWin, "btnTextsize"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "viewTextSize"), Whitebox.getInternalState(datePickerPopWin, "viewTextSize"));
        Assert.assertEquals(Whitebox.getInternalState(builder, "textCancel"), datePickerPopWin.cancelBtn.getText());
        Assert.assertEquals(Whitebox.getInternalState(builder, "textConfirm"), datePickerPopWin.confirmBtn.getText());
        Assert.assertEquals(Whitebox.getInternalState(builder, "btnTextSize"), ((int) (datePickerPopWin.cancelBtn.getTextSize())));
        Assert.assertEquals(Whitebox.getInternalState(builder, "btnTextSize"), ((int) (datePickerPopWin.confirmBtn.getTextSize())));
    }
}

