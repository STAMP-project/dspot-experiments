package io.github.hidroh.materialistic;


import ConnectivityManager.TYPE_MOBILE;
import ConnectivityManager.TYPE_WIFI;
import Context.CONNECTIVITY_SERVICE;
import HackerNewsClient.WEB_ITEM_PATH;
import MotionEvent.ACTION_DOWN;
import MotionEvent.ACTION_UP;
import Navigable.DIRECTION_DOWN;
import Navigable.DIRECTION_LEFT;
import Navigable.DIRECTION_RIGHT;
import Navigable.DIRECTION_UP;
import OfflineWebActivity.EXTRA_URL;
import R.attr;
import R.string.pref_custom_tab;
import R.style;
import R.style.AppTextSize;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.test.TestListActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.widget.PopupMenu;
import javax.inject.Inject;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowAccountManager;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowNetworkInfo;

import static BuildConfig.APPLICATION_ID;
import static org.junit.Assert.assertNull;


@RunWith(TestRunner.class)
public class AppUtilsTest {
    @Inject
    AlertDialogBuilder alertDialogBuilder;

    private Activity context;

    @Test
    public void testSetTextWithLinks() {
        TestApplication.addResolver(new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com")));
        Preferences.set(context, pref_custom_tab, false);
        TextView textView = new TextView(context);
        AppUtils.setTextWithLinks(textView, AppUtils.fromHtml("<a href=\"http://example.com\">http://example.com</a>"));
        MotionEvent event = Mockito.mock(MotionEvent.class);
        Mockito.when(event.getAction()).thenReturn(ACTION_DOWN);
        Mockito.when(event.getX()).thenReturn(0.0F);
        Mockito.when(event.getY()).thenReturn(0.0F);
        Assert.assertTrue(Shadows.shadowOf(textView).getOnTouchListener().onTouch(textView, event));
        Mockito.when(event.getAction()).thenReturn(ACTION_UP);
        Mockito.when(event.getX()).thenReturn(0.0F);
        Mockito.when(event.getY()).thenReturn(0.0F);
        Assert.assertTrue(Shadows.shadowOf(textView).getOnTouchListener().onTouch(textView, event));
        Assert.assertNotNull(ShadowApplication.getInstance().getNextStartedActivity());
    }

    @Test
    public void testSetTextWithLinksOpenChromeCustomTabs() {
        TestApplication.addResolver(new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com")));
        TextView textView = new TextView(new android.view.ContextThemeWrapper(context, style.AppTheme));
        AppUtils.setTextWithLinks(textView, AppUtils.fromHtml("<a href=\"http://example.com\">http://example.com</a>"));
        MotionEvent event = Mockito.mock(MotionEvent.class);
        Mockito.when(event.getAction()).thenReturn(ACTION_DOWN);
        Mockito.when(event.getX()).thenReturn(0.0F);
        Mockito.when(event.getY()).thenReturn(0.0F);
        Assert.assertTrue(Shadows.shadowOf(textView).getOnTouchListener().onTouch(textView, event));
        Mockito.when(event.getAction()).thenReturn(ACTION_UP);
        Mockito.when(event.getX()).thenReturn(0.0F);
        Mockito.when(event.getY()).thenReturn(0.0F);
        Assert.assertTrue(Shadows.shadowOf(textView).getOnTouchListener().onTouch(textView, event));
        Assert.assertNotNull(ShadowApplication.getInstance().getNextStartedActivity());
    }

    @Test
    public void testDefaultTextSize() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        float expected = activity.getTheme().obtainStyledAttributes(AppTextSize, new int[]{ attr.contentTextSize }).getDimension(0, 0);
        float actual = activity.getTheme().obtainStyledAttributes(new int[]{ attr.contentTextSize }).getDimension(0, 0);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testGetAbbreviatedTimeSpan() {
        Assert.assertEquals("0m", AppUtils.getAbbreviatedTimeSpan(((System.currentTimeMillis()) + (DateUtils.SECOND_IN_MILLIS))));
        Assert.assertEquals("0m", AppUtils.getAbbreviatedTimeSpan(System.currentTimeMillis()));
        Assert.assertEquals("5m", AppUtils.getAbbreviatedTimeSpan((((System.currentTimeMillis()) - (5 * (DateUtils.MINUTE_IN_MILLIS))) - (10 * (DateUtils.SECOND_IN_MILLIS)))));
        Assert.assertEquals("1h", AppUtils.getAbbreviatedTimeSpan((((System.currentTimeMillis()) - (DateUtils.HOUR_IN_MILLIS)) - (DateUtils.MINUTE_IN_MILLIS))));
        Assert.assertEquals("6d", AppUtils.getAbbreviatedTimeSpan((((System.currentTimeMillis()) - (DateUtils.WEEK_IN_MILLIS)) + (DateUtils.MINUTE_IN_MILLIS))));
        Assert.assertEquals("1w", AppUtils.getAbbreviatedTimeSpan((((System.currentTimeMillis()) - (DateUtils.WEEK_IN_MILLIS)) - (DateUtils.MINUTE_IN_MILLIS))));
        Assert.assertEquals("10y", AppUtils.getAbbreviatedTimeSpan((((System.currentTimeMillis()) - (10 * (DateUtils.YEAR_IN_MILLIS))) - (DateUtils.MINUTE_IN_MILLIS))));
    }

    @Test
    public void testNoActiveNetwork() {
        Shadows.shadowOf(((ConnectivityManager) (context.getSystemService(CONNECTIVITY_SERVICE)))).setActiveNetworkInfo(null);
        Assert.assertFalse(AppUtils.isOnWiFi(context));
    }

    @Test
    public void testDisconnectedNetwork() {
        Shadows.shadowOf(((ConnectivityManager) (context.getSystemService(CONNECTIVITY_SERVICE)))).setActiveNetworkInfo(ShadowNetworkInfo.newInstance(null, 0, 0, false, false));
        Assert.assertFalse(AppUtils.isOnWiFi(context));
    }

    @Test
    public void testNonWiFiNetwork() {
        Shadows.shadowOf(((ConnectivityManager) (context.getSystemService(CONNECTIVITY_SERVICE)))).setActiveNetworkInfo(ShadowNetworkInfo.newInstance(null, TYPE_MOBILE, 0, true, true));
        Assert.assertFalse(AppUtils.isOnWiFi(context));
    }

    @Test
    public void testWiFiNetwork() {
        Shadows.shadowOf(((ConnectivityManager) (context.getSystemService(CONNECTIVITY_SERVICE)))).setActiveNetworkInfo(ShadowNetworkInfo.newInstance(null, TYPE_WIFI, 0, true, true));
        Assert.assertTrue(AppUtils.isOnWiFi(context));
    }

    @Test
    public void testRemoveAccount() {
        Preferences.setUsername(context, "olduser");
        AppUtils.registerAccountsUpdatedListener(context);
        Shadows.shadowOf(AccountManager.get(context)).addAccount(new android.accounts.Account("newuser", APPLICATION_ID));
        assertNull(Preferences.getUsername(context));
        Preferences.setUsername(context, "newuser");
        Shadows.shadowOf(AccountManager.get(context)).addAccount(new android.accounts.Account("olduser", APPLICATION_ID));
        Assert.assertEquals("newuser", Preferences.getUsername(context));
    }

    @Test
    public void testShareComment() {
        AppUtils.share(context, Mockito.mock(PopupMenu.class), new View(context), new TestHnItem(1));
        assertNull(ShadowAlertDialog.getLatestAlertDialog());
        AppUtils.share(context, Mockito.mock(PopupMenu.class), new View(context), new TestHnItem(1) {
            @Override
            public String getUrl() {
                return String.format(WEB_ITEM_PATH, "1");
            }
        });
        assertNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void testOpenExternalComment() {
        ActivityController<TestListActivity> controller = Robolectric.buildActivity(TestListActivity.class);
        TestListActivity activity = controller.create().start().resume().get();
        AppUtils.openExternal(activity, Mockito.mock(PopupMenu.class), new View(activity), new TestHnItem(1), null);
        assertNull(ShadowAlertDialog.getLatestAlertDialog());
        AppUtils.openExternal(activity, Mockito.mock(PopupMenu.class), new View(activity), new TestHnItem(1) {
            @Override
            public String getUrl() {
                return String.format(WEB_ITEM_PATH, "1");
            }
        }, null);
        assertNull(ShadowAlertDialog.getLatestAlertDialog());
        controller.destroy();
    }

    @Test
    public void testLoginNoAccounts() {
        AppUtils.showLogin(context, null);
        assertThat(Shadows.shadowOf(context).getNextStartedActivity()).hasComponent(context, LoginActivity.class);
    }

    @Test
    public void testLoginStaleAccount() {
        Preferences.setUsername(context, "username");
        Shadows.shadowOf(ShadowAccountManager.get(context)).addAccount(new android.accounts.Account("username", APPLICATION_ID));
        AppUtils.showLogin(context, null);
        assertThat(Shadows.shadowOf(context).getNextStartedActivity()).hasComponent(context, LoginActivity.class);
    }

    @Test
    public void testLoginShowChooser() {
        TestApplication.applicationGraph.inject(this);
        Shadows.shadowOf(ShadowAccountManager.get(context)).addAccount(new android.accounts.Account("username", APPLICATION_ID));
        AppUtils.showLogin(context, alertDialogBuilder);
        Assert.assertNotNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void testTrimHtmlWhitespaces() {
        TextView textView = new TextView(context);
        textView.setText(AppUtils.fromHtml("<p>paragraph</p><p><br/><br/><br/></p>"));
        assertThat(textView).hasTextString("paragraph");
        textView.setText(AppUtils.fromHtml(""));
        assertThat(textView).hasTextString("");
        textView.setText(AppUtils.fromHtml("paragraph"));
        assertThat(textView).hasTextString("paragraph");
    }

    @Test
    public void testOpenExternalUrlNoConnection() {
        Shadows.shadowOf(((ConnectivityManager) (context.getSystemService(CONNECTIVITY_SERVICE)))).setActiveNetworkInfo(null);
        AppUtils.openWebUrlExternal(context, new TestHnItem(1L) {
            @Override
            public String getUrl() {
                return "http://example.com";
            }
        }, "http://example.com", null);
        assertThat(Shadows.shadowOf(context).getNextStartedActivity()).hasComponent(context, OfflineWebActivity.class).hasExtra(EXTRA_URL, "http://example.com");
    }

    @Test
    public void testFullscreenButton() {
        ActivityController<TestListActivity> controller = Robolectric.buildActivity(TestListActivity.class);
        TestListActivity activity = controller.create().start().resume().get();
        FloatingActionButton fab = new FloatingActionButton(activity);
        AppUtils.toggleFabAction(fab, null, false);
        fab.performClick();
        assertThat(Shadows.shadowOf(LocalBroadcastManager.getInstance(activity)).getSentBroadcastIntents()).isNotEmpty();
        controller.destroy();
    }

    @Test
    public void testNavigate() {
        AppBarLayout appBar = Mockito.mock(AppBarLayout.class);
        Mockito.when(appBar.getBottom()).thenReturn(1);
        Navigable navigable = Mockito.mock(Navigable.class);
        AppUtils.navigate(DIRECTION_DOWN, appBar, navigable);
        Mockito.verify(appBar).setExpanded(ArgumentMatchers.eq(false), ArgumentMatchers.anyBoolean());
        Mockito.verify(navigable, Mockito.never()).onNavigate(ArgumentMatchers.anyInt());
        Mockito.when(appBar.getBottom()).thenReturn(0);
        AppUtils.navigate(DIRECTION_DOWN, appBar, navigable);
        Mockito.verify(navigable).onNavigate(ArgumentMatchers.eq(DIRECTION_DOWN));
        AppUtils.navigate(DIRECTION_RIGHT, appBar, navigable);
        Mockito.verify(navigable).onNavigate(ArgumentMatchers.eq(DIRECTION_RIGHT));
        AppUtils.navigate(DIRECTION_UP, appBar, navigable);
        Mockito.verify(navigable).onNavigate(ArgumentMatchers.eq(DIRECTION_UP));
        AppUtils.navigate(DIRECTION_LEFT, appBar, navigable);
        Mockito.verify(navigable).onNavigate(ArgumentMatchers.eq(DIRECTION_LEFT));
    }
}

