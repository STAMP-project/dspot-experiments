package com.bruce.pickerview.popwindow;


import DatePickerPopWin.Builder;
import DatePickerPopWin.OnDatePickedListener;
import android.content.Context;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ DatePickerPopWin.class })
public class DatePickerPopWinBuilderTest {
    private static final int MIN_YEAR = 1950;

    private static final int MAX_YEAR = 2050;

    private static final String TEXT_CANCEL = "Cancel Text";

    private static final String TEXT_CONFIRM = "Confirm Text";

    private static final String DATE_CHOSE = "2011-11-11";

    private static final int COLOR_CANCEL = 11;

    private static final int COLOR_CONFIRM = 22;

    private static final int BTN_TEXT_SIZE = 14;

    private static final int VIEW_TEXT_SIZE = 16;

    private Builder builder;

    private Context mContext;

    private OnDatePickedListener mListener;

    @Test
    public void testConstructor() {
        Assert.assertEquals(mContext, Whitebox.getInternalState(builder, "context"));
        Assert.assertEquals(mListener, Whitebox.getInternalState(builder, "listener"));
    }

    @Test
    public void testMinYear() {
        // when
        DatePickerPopWin.Builder result = builder.minYear(DatePickerPopWinBuilderTest.MIN_YEAR);
        // then
        Assert.assertEquals(DatePickerPopWinBuilderTest.MIN_YEAR, Whitebox.getInternalState(builder, "minYear"));
        Assert.assertEquals(builder, result);
    }

    @Test
    public void testMaxYear() {
        // when
        DatePickerPopWin.Builder result = builder.maxYear(DatePickerPopWinBuilderTest.MAX_YEAR);
        // then
        Assert.assertEquals(DatePickerPopWinBuilderTest.MAX_YEAR, Whitebox.getInternalState(builder, "maxYear"));
        Assert.assertEquals(builder, result);
    }

    @Test
    public void testTextCancel() {
        // when
        DatePickerPopWin.Builder result = builder.textCancel(DatePickerPopWinBuilderTest.TEXT_CANCEL);
        // then
        Assert.assertEquals(DatePickerPopWinBuilderTest.TEXT_CANCEL, Whitebox.getInternalState(builder, "textCancel"));
        Assert.assertEquals(builder, result);
    }

    @Test
    public void testTextConfirm() {
        // when
        DatePickerPopWin.Builder result = builder.textConfirm(DatePickerPopWinBuilderTest.TEXT_CONFIRM);
        // then
        Assert.assertEquals(DatePickerPopWinBuilderTest.TEXT_CONFIRM, Whitebox.getInternalState(builder, "textConfirm"));
        Assert.assertEquals(builder, result);
    }

    @Test
    public void testDateChose() {
        // when
        DatePickerPopWin.Builder result = builder.dateChose(DatePickerPopWinBuilderTest.DATE_CHOSE);
        // then
        Assert.assertEquals(DatePickerPopWinBuilderTest.DATE_CHOSE, Whitebox.getInternalState(builder, "dateChose"));
        Assert.assertEquals(builder, result);
    }

    @Test
    public void testColorCancel() {
        // when
        DatePickerPopWin.Builder result = builder.colorCancel(DatePickerPopWinBuilderTest.COLOR_CANCEL);
        // then
        Assert.assertEquals(DatePickerPopWinBuilderTest.COLOR_CANCEL, Whitebox.getInternalState(builder, "colorCancel"));
        Assert.assertEquals(builder, result);
    }

    @Test
    public void testColorConfirm() {
        // when
        DatePickerPopWin.Builder result = builder.colorConfirm(DatePickerPopWinBuilderTest.COLOR_CONFIRM);
        // then
        Assert.assertEquals(DatePickerPopWinBuilderTest.COLOR_CONFIRM, Whitebox.getInternalState(builder, "colorConfirm"));
        Assert.assertEquals(builder, result);
    }

    @Test
    public void testBtnTextSize() {
        // when
        DatePickerPopWin.Builder result = builder.btnTextSize(DatePickerPopWinBuilderTest.BTN_TEXT_SIZE);
        // then
        Assert.assertEquals(DatePickerPopWinBuilderTest.BTN_TEXT_SIZE, Whitebox.getInternalState(builder, "btnTextSize"));
        Assert.assertEquals(builder, result);
    }

    @Test
    public void testViewTextSize() {
        // when
        DatePickerPopWin.Builder result = builder.viewTextSize(DatePickerPopWinBuilderTest.VIEW_TEXT_SIZE);
        // then
        Assert.assertEquals(DatePickerPopWinBuilderTest.VIEW_TEXT_SIZE, Whitebox.getInternalState(builder, "viewTextSize"));
        Assert.assertEquals(builder, result);
    }

    @Test
    public void testBuild() throws Exception {
        // given
        DatePickerPopWin mDatePickerPopWin = Mockito.mock(DatePickerPopWin.class);
        whenNew(DatePickerPopWin.class).withArguments(builder).thenReturn(mDatePickerPopWin);
        // when
        DatePickerPopWin result = builder.build();
        // then
        Assert.assertEquals(mDatePickerPopWin, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildIllegalParameters() throws Exception {
        // given
        builder.minYear(DatePickerPopWinBuilderTest.MAX_YEAR).maxYear(DatePickerPopWinBuilderTest.MIN_YEAR);
        DatePickerPopWin mDatePickerPopWin = Mockito.mock(DatePickerPopWin.class);
        whenNew(DatePickerPopWin.class).withArguments(builder).thenReturn(mDatePickerPopWin);
        // when
        builder.build();
        // then
        fail(("DatePickerPopWin.Builder should throw IllegalArgumentException " + "when minYear is greater then maxYear"));
    }
}

