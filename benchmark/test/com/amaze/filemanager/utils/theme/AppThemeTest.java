package com.amaze.filemanager.utils.theme;


import AppTheme.BLACK;
import AppTheme.BLACK_INDEX;
import AppTheme.DARK;
import AppTheme.DARK_INDEX;
import AppTheme.LIGHT;
import AppTheme.LIGHT_INDEX;
import AppTheme.TIMED;
import AppTheme.TIME_INDEX;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by yuhalyn on 2018-04-02.
 */
public class AppThemeTest {
    @Test
    public void getThemeLightTest() {
        AppTheme apptheme = AppTheme.getTheme(LIGHT_INDEX);
        Assert.assertEquals(LIGHT, apptheme);
    }

    @Test
    public void getThemeDARKTest() {
        AppTheme apptheme = AppTheme.getTheme(DARK_INDEX);
        Assert.assertEquals(DARK, apptheme);
    }

    @Test
    public void getThemeTIMEDTest() {
        AppTheme apptheme = AppTheme.getTheme(TIME_INDEX);
        Assert.assertEquals(TIMED, apptheme);
    }

    @Test
    public void getThemeBLACKTest() {
        AppTheme apptheme = AppTheme.getTheme(BLACK_INDEX);
        Assert.assertEquals(BLACK, apptheme);
    }

    @Test
    public void getMaterialDialogThemeLIGHTTest() {
        AppTheme apptheme = AppTheme.getTheme(LIGHT_INDEX);
        Assert.assertEquals(Theme.LIGHT, apptheme.getMaterialDialogTheme());
    }

    @Test
    public void getMaterialDialogThemeDARKTest() {
        AppTheme apptheme = AppTheme.getTheme(DARK_INDEX);
        Assert.assertEquals(Theme.DARK, apptheme.getMaterialDialogTheme());
    }

    @Test
    public void getMaterialDialogThemeTIMEDTest() {
        AppTheme apptheme = AppTheme.getTheme(TIME_INDEX);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if ((hour <= 6) || (hour >= 18)) {
            Assert.assertEquals(Theme.DARK, apptheme.getMaterialDialogTheme());
        } else
            Assert.assertEquals(Theme.LIGHT, apptheme.getMaterialDialogTheme());

    }

    @Test
    public void getMaterialDialogThemeBLACKTest() {
        AppTheme apptheme = AppTheme.getTheme(BLACK_INDEX);
        Assert.assertEquals(Theme.DARK, apptheme.getMaterialDialogTheme());
    }

    @Test
    public void getSimpleThemeLIGHTTest() {
        AppTheme apptheme = AppTheme.getTheme(LIGHT_INDEX);
        Assert.assertEquals(LIGHT, apptheme.getSimpleTheme());
    }

    @Test
    public void getSimpleThemeDARKTest() {
        AppTheme apptheme = AppTheme.getTheme(DARK_INDEX);
        Assert.assertEquals(DARK, apptheme.getSimpleTheme());
    }

    @Test
    public void getSimpleThemeTIMEDTest() {
        AppTheme apptheme = AppTheme.getTheme(TIME_INDEX);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if ((hour <= 6) || (hour >= 18)) {
            Assert.assertEquals(DARK, apptheme.getSimpleTheme());
        } else
            Assert.assertEquals(LIGHT, apptheme.getSimpleTheme());

    }

    @Test
    public void getSimpleThemeBLACKTest() {
        AppTheme apptheme = AppTheme.getTheme(BLACK_INDEX);
        Assert.assertEquals(BLACK, apptheme.getSimpleTheme());
    }

    @Test
    public void getIdLIGHTTest() {
        int index = 0;
        AppTheme apptheme = AppTheme.getTheme(index);
        Assert.assertEquals(index, apptheme.getId());
    }

    @Test
    public void getIdDARKTest() {
        int index = 1;
        AppTheme apptheme = AppTheme.getTheme(index);
        Assert.assertEquals(index, apptheme.getId());
    }

    @Test
    public void getIdTIMEDTest() {
        int index = 2;
        AppTheme apptheme = AppTheme.getTheme(index);
        Assert.assertEquals(index, apptheme.getId());
    }

    @Test
    public void getIdBLACKTest() {
        int index = 3;
        AppTheme apptheme = AppTheme.getTheme(index);
        Assert.assertEquals(index, apptheme.getId());
    }
}

