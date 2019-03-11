package org.robolectric.shadows;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;

import static org.robolectric.R.id.subtitle;
import static org.robolectric.R.layout.main;
import static org.robolectric.R.layout.media;


@RunWith(AndroidJUnit4.class)
public class ShadowAppWidgetManagerTest {
    private AppWidgetManager appWidgetManager;

    private ShadowAppWidgetManager shadowAppWidgetManager;

    @Test
    public void createWidget_shouldInflateViewAndAssignId() throws Exception {
        int widgetId = shadowAppWidgetManager.createWidget(ShadowAppWidgetManagerTest.SpanishTestAppWidgetProvider.class, main);
        View widgetView = shadowAppWidgetManager.getViewFor(widgetId);
        Assert.assertEquals("Hola", getText());
    }

    @Test
    public void getViewFor_shouldReturnSameViewEveryTimeForGivenWidgetId() throws Exception {
        int widgetId = shadowAppWidgetManager.createWidget(ShadowAppWidgetManagerTest.SpanishTestAppWidgetProvider.class, main);
        View widgetView = shadowAppWidgetManager.getViewFor(widgetId);
        Assert.assertNotNull(widgetView);
        Assert.assertSame(widgetView, shadowAppWidgetManager.getViewFor(widgetId));
    }

    @Test
    public void createWidget_shouldAllowForMultipleInstancesOfWidgets() throws Exception {
        int widgetId = shadowAppWidgetManager.createWidget(ShadowAppWidgetManagerTest.SpanishTestAppWidgetProvider.class, main);
        View widgetView = shadowAppWidgetManager.getViewFor(widgetId);
        Assert.assertNotSame(widgetId, shadowAppWidgetManager.createWidget(ShadowAppWidgetManagerTest.SpanishTestAppWidgetProvider.class, main));
        Assert.assertNotSame(widgetView, shadowAppWidgetManager.getViewFor(shadowAppWidgetManager.createWidget(ShadowAppWidgetManagerTest.SpanishTestAppWidgetProvider.class, main)));
    }

    @Test
    public void shouldReplaceLayoutIfAndOnlyIfLayoutIdIsDifferent() throws Exception {
        int widgetId = shadowAppWidgetManager.createWidget(ShadowAppWidgetManagerTest.SpanishTestAppWidgetProvider.class, main);
        View originalWidgetView = shadowAppWidgetManager.getViewFor(widgetId);
        assertContains("Main Layout", originalWidgetView);
        appWidgetManager.updateAppWidget(widgetId, new RemoteViews(ApplicationProvider.getApplicationContext().getPackageName(), main));
        Assert.assertSame(originalWidgetView, shadowAppWidgetManager.getViewFor(widgetId));
        appWidgetManager.updateAppWidget(widgetId, new RemoteViews(ApplicationProvider.getApplicationContext().getPackageName(), media));
        Assert.assertNotSame(originalWidgetView, shadowAppWidgetManager.getViewFor(widgetId));
        View mediaWidgetView = shadowAppWidgetManager.getViewFor(widgetId);
        assertContains("Media Layout", mediaWidgetView);
    }

    @Test
    public void getAppWidgetIds() {
        int expectedWidgetId = shadowAppWidgetManager.createWidget(ShadowAppWidgetManagerTest.SpanishTestAppWidgetProvider.class, main);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(ShadowAppWidgetManagerTest.SpanishTestAppWidgetProvider.class.getPackage().getName(), ShadowAppWidgetManagerTest.SpanishTestAppWidgetProvider.class.getName()));
        Assert.assertEquals(1, appWidgetIds.length);
        Assert.assertEquals(expectedWidgetId, appWidgetIds[0]);
    }

    @Test
    public void getAppWidgetInfo_shouldReturnSpecifiedAppWidgetInfo() throws Exception {
        AppWidgetProviderInfo expectedWidgetInfo = new AppWidgetProviderInfo();
        shadowAppWidgetManager.addBoundWidget(26, expectedWidgetInfo);
        Assert.assertEquals(expectedWidgetInfo, appWidgetManager.getAppWidgetInfo(26));
        Assert.assertEquals(null, appWidgetManager.getAppWidgetInfo(27));
    }

    @Test
    public void bindAppWidgetIdIfAllowed_shouldReturnThePresetBoolean() throws Exception {
        shadowAppWidgetManager.setAllowedToBindAppWidgets(false);
        Assert.assertEquals(shadowAppWidgetManager.bindAppWidgetIdIfAllowed(12345, new ComponentName("", "")), false);
        shadowAppWidgetManager.setAllowedToBindAppWidgets(true);
        Assert.assertEquals(shadowAppWidgetManager.bindAppWidgetIdIfAllowed(12345, new ComponentName("", "")), true);
    }

    @Test
    public void bindAppWidgetIdIfAllowed_shouldRecordTheBinding() throws Exception {
        ComponentName provider = new ComponentName("A", "B");
        appWidgetManager.bindAppWidgetIdIfAllowed(789, provider);
        Assert.assertArrayEquals(new int[]{ 789 }, appWidgetManager.getAppWidgetIds(provider));
    }

    @Test
    public void bindAppWidgetId_shouldRecordAppWidgetInfo() throws Exception {
        ComponentName provider = new ComponentName("abc", "123");
        AppWidgetProviderInfo providerInfo = new AppWidgetProviderInfo();
        providerInfo.provider = provider;
        shadowAppWidgetManager.addInstalledProvider(providerInfo);
        appWidgetManager.bindAppWidgetIdIfAllowed(90210, provider);
        Assert.assertSame(providerInfo, appWidgetManager.getAppWidgetInfo(90210));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bindAppWidgetIdIfAllowed_shouldThrowIllegalArgumentExceptionWhenPrompted() throws Exception {
        shadowAppWidgetManager.setValidWidgetProviderComponentName(false);
        shadowAppWidgetManager.bindAppWidgetIdIfAllowed(12345, new ComponentName("", ""));
    }

    @Test
    public void getInstalledProviders_returnsWidgetList() throws Exception {
        AppWidgetProviderInfo info1 = new AppWidgetProviderInfo();
        info1.label = "abc";
        AppWidgetProviderInfo info2 = new AppWidgetProviderInfo();
        info2.label = "def";
        shadowAppWidgetManager.addInstalledProvider(info1);
        shadowAppWidgetManager.addInstalledProvider(info2);
        List<AppWidgetProviderInfo> installedProviders = appWidgetManager.getInstalledProviders();
        Assert.assertEquals(2, installedProviders.size());
        Assert.assertEquals(info1, installedProviders.get(0));
        Assert.assertEquals(info2, installedProviders.get(1));
    }

    public static class SpanishTestAppWidgetProvider extends AppWidgetProvider {
        @Override
        public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), main);
            remoteViews.setTextViewText(subtitle, "Hola");
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }
    }
}

