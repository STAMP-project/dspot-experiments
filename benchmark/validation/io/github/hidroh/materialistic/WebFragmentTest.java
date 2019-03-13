package io.github.hidroh.materialistic;


import Base64.DEFAULT;
import FileDownloader.FileDownloaderCallback;
import R.id;
import R.id.button_back;
import R.id.button_clear;
import R.id.button_exit;
import R.id.button_find;
import R.id.button_forward;
import R.id.button_more;
import R.id.button_next;
import R.id.button_refresh;
import R.id.control_switcher;
import R.id.download_button;
import R.id.edittext;
import R.id.empty;
import R.id.nested_scroll_view;
import R.id.progress;
import R.id.toolbar_web;
import R.id.web_view;
import R.string.no_matches;
import RuntimeEnvironment.application;
import View.FOCUS_DOWN;
import View.FOCUS_UP;
import WebFragment.EXTRA_FULLSCREEN;
import WebFragment.PdfAndroidJavascriptBridge;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.util.Base64;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import io.github.hidroh.materialistic.data.FavoriteManager;
import io.github.hidroh.materialistic.data.ReadabilityClient;
import io.github.hidroh.materialistic.data.WebItem;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.WebActivity;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
import io.github.hidroh.materialistic.test.shadow.ShadowNestedScrollView;
import io.github.hidroh.materialistic.test.shadow.ShadowWebView;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import junit.framework.Assert;
import okio.Okio;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowPackageManager;
import org.robolectric.shadows.ShadowPopupMenu;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.ShadowLocalBroadcastManager;

import static WebFragment.ACTION_FULLSCREEN;


@SuppressWarnings("ConstantConditions")
@Config(shadows = { ShadowWebView.class })
@RunWith(TestRunner.class)
public class WebFragmentTest {
    private WebActivity activity;

    private ActivityController<WebActivity> controller;

    private WebItem item;

    @Inject
    FavoriteManager favoriteManager;

    @Inject
    ReadabilityClient readabilityClient;

    private Intent intent;

    @Test
    public void testProgressChanged() {
        ProgressBar progressBar = activity.findViewById(progress);
        WebView webView = activity.findViewById(web_view);
        Shadows.shadowOf(webView).getWebChromeClient().onProgressChanged(webView, 50);
        assertThat(progressBar).isVisible();
        Shadows.shadowOf(webView).getWebChromeClient().onProgressChanged(webView, 100);
        assertThat(progressBar).isNotVisible();
    }

    @Test
    public void testDownloadContent() {
        ResolveInfo resolverInfo = new ResolveInfo();
        resolverInfo.activityInfo = new ActivityInfo();
        resolverInfo.activityInfo.applicationInfo = new ApplicationInfo();
        resolverInfo.activityInfo.applicationInfo.packageName = ListActivity.class.getPackage().getName();
        resolverInfo.activityInfo.name = ListActivity.class.getName();
        ShadowPackageManager rpm = Shadows.shadowOf(application.getPackageManager());
        final String url = "http://example.com/file.doc";
        rpm.addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)), resolverInfo);
        WebView webView = activity.findViewById(web_view);
        ShadowWebView shadowWebView = Shadow.extract(webView);
        Mockito.when(item.getUrl()).thenReturn(url);
        shadowWebView.getDownloadListener().onDownloadStart(url, "", "", "", 0L);
        assertThat(((android.view.View) (activity.findViewById(empty)))).isVisible();
        activity.findViewById(download_button).performClick();
        Assert.assertNotNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testDownloadPdf() {
        ResolveInfo resolverInfo = new ResolveInfo();
        resolverInfo.activityInfo = new ActivityInfo();
        resolverInfo.activityInfo.applicationInfo = new ApplicationInfo();
        resolverInfo.activityInfo.applicationInfo.packageName = ListActivity.class.getPackage().getName();
        resolverInfo.activityInfo.name = ListActivity.class.getName();
        ShadowPackageManager rpm = Shadows.shadowOf(application.getPackageManager());
        Mockito.when(item.getUrl()).thenReturn("http://example.com/file.pdf");
        rpm.addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl())), resolverInfo);
        WebView webView = activity.findViewById(web_view);
        ShadowWebView shadowWebView = Shadow.extract(webView);
        WebFragment fragment = ((WebFragment) (getSupportFragmentManager().findFragmentByTag(WebFragment.class.getName())));
        shadowWebView.getDownloadListener().onDownloadStart(item.getUrl(), "", "", "application/pdf", 0L);
        getWebViewClient().onPageFinished(webView, WebFragment.PDF_LOADER_URL);
        Mockito.verify(fragment.mFileDownloader).downloadFile(ArgumentMatchers.eq(item.getUrl()), ArgumentMatchers.eq("application/pdf"), ArgumentMatchers.any(FileDownloaderCallback.class));
    }

    @Test
    public void testPdfAndroidJavascriptBridgeGetChunk() throws IOException {
        final String path = this.getClass().getClassLoader().getResource("file.txt").getPath();
        final File file = new File(path);
        final long size = file.length();
        final String expected = Base64.encodeToString(Okio.buffer(Okio.source(file)).readByteArray(), DEFAULT);
        final WebFragment.PdfAndroidJavascriptBridge bridge = new WebFragment.PdfAndroidJavascriptBridge(path, null);
        Assert.assertEquals(expected, bridge.getChunk(0, size));
    }

    @Test
    public void testPdfAndroidJavascriptBridgeGetSize() {
        final String path = this.getClass().getClassLoader().getResource("file.txt").getPath();
        final long expected = new File(path).length();
        final WebFragment.PdfAndroidJavascriptBridge bridge = new WebFragment.PdfAndroidJavascriptBridge(path, null);
        Assert.assertEquals(expected, bridge.getSize());
    }

    @Config(shadows = ShadowNestedScrollView.class)
    @Test
    public void testScrollToTop() {
        NestedScrollView scrollView = activity.findViewById(nested_scroll_view);
        scrollView.smoothScrollTo(0, 1);
        assertThat(CustomShadows.customShadowOf(scrollView).getSmoothScrollY()).isEqualTo(1);
        activity.fragment.scrollToTop();
        assertThat(CustomShadows.customShadowOf(scrollView).getSmoothScrollY()).isEqualTo(0);
    }

    @Test
    public void testFullscreenScrollToTop() {
        activity.findViewById(toolbar_web).performClick();
        Assert.assertEquals((-1), ((ShadowWebView) (Shadow.extract(activity.findViewById(web_view)))).getScrollY());
    }

    @Test
    public void testFullscreen() {
        ShadowLocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_FULLSCREEN).putExtra(EXTRA_FULLSCREEN, true));
        assertThat(((android.view.View) (activity.findViewById(control_switcher)))).isVisible();
        Shadows.shadowOf(activity).recreate();
        assertThat(((android.view.View) (activity.findViewById(control_switcher)))).isVisible();
        activity.findViewById(button_exit).performClick();
        assertThat(((android.view.View) (activity.findViewById(control_switcher)))).isNotVisible();
    }

    @Test
    public void testSearch() {
        ShadowLocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_FULLSCREEN).putExtra(EXTRA_FULLSCREEN, true));
        activity.findViewById(button_find).performClick();
        ViewSwitcher controlSwitcher = activity.findViewById(control_switcher);
        assertThat(controlSwitcher.getDisplayedChild()).isEqualTo(1);
        ShadowWebView shadowWebView = Shadow.extract(activity.findViewById(web_view));
        // no query
        EditText editText = activity.findViewById(edittext);
        Shadows.shadowOf(editText).getOnEditorActionListener().onEditorAction(null, 0, null);
        assertThat(((android.view.View) (activity.findViewById(button_next)))).isDisabled();
        // with results
        shadowWebView.setFindCount(1);
        editText.setText("abc");
        Shadows.shadowOf(editText).getOnEditorActionListener().onEditorAction(null, 0, null);
        assertThat(((android.view.View) (activity.findViewById(button_next)))).isEnabled();
        activity.findViewById(button_next).performClick();
        assertThat(shadowWebView.getFindIndex()).isEqualTo(1);
        activity.findViewById(button_clear).performClick();
        assertThat(editText).isEmpty();
        assertThat(controlSwitcher.getDisplayedChild()).isEqualTo(0);
        // with no results
        shadowWebView.setFindCount(0);
        editText.setText("abc");
        Shadows.shadowOf(editText).getOnEditorActionListener().onEditorAction(null, 0, null);
        assertThat(((android.view.View) (activity.findViewById(button_next)))).isDisabled();
        assertThat(ShadowToast.getTextOfLatestToast()).contains(activity.getString(no_matches));
    }

    @Test
    public void testRefresh() {
        ShadowLocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_FULLSCREEN).putExtra(EXTRA_FULLSCREEN, true));
        ShadowWebView.lastGlobalLoadedUrl = null;
        ShadowWebView shadowWebView = Shadow.extract(activity.findViewById(web_view));
        shadowWebView.setProgress(20);
        activity.findViewById(button_refresh).performClick();
        assertThat(ShadowWebView.getLastGlobalLoadedUrl()).isNullOrEmpty();
        shadowWebView.setProgress(100);
        activity.findViewById(button_refresh).performClick();
        assertThat(ShadowWebView.getLastGlobalLoadedUrl()).isEqualTo(ShadowWebView.RELOADED);
    }

    @SuppressLint("NewApi")
    @Test
    public void testWebControls() {
        ShadowLocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_FULLSCREEN).putExtra(EXTRA_FULLSCREEN, true));
        ShadowWebView shadowWebView = Shadow.extract(activity.findViewById(web_view));
        activity.findViewById(button_more).performClick();
        Shadows.shadowOf(ShadowPopupMenu.getLatestPopupMenu()).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_zoom_in));
        assertThat(shadowWebView.getZoomDegree()).isEqualTo(1);
        activity.findViewById(button_more).performClick();
        Shadows.shadowOf(ShadowPopupMenu.getLatestPopupMenu()).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_zoom_out));
        assertThat(shadowWebView.getZoomDegree()).isEqualTo(0);
        activity.findViewById(button_forward).performClick();
        assertThat(shadowWebView.getPageIndex()).isEqualTo(1);
        activity.findViewById(button_back).performClick();
        assertThat(shadowWebView.getPageIndex()).isEqualTo(0);
    }

    @Config(shadows = ShadowNestedScrollView.class)
    @Test
    public void testScroll() {
        ShadowNestedScrollView shadowScrollView = CustomShadows.customShadowOf(((NestedScrollView) (activity.findViewById(nested_scroll_view))));
        WebFragment fragment = ((WebFragment) (getSupportFragmentManager().findFragmentByTag(WebFragment.class.getName())));
        fragment.scrollToNext();
        assertThat(shadowScrollView.getLastScrollDirection()).isEqualTo(FOCUS_DOWN);
        fragment.scrollToPrevious();
        assertThat(shadowScrollView.getLastScrollDirection()).isEqualTo(FOCUS_UP);
        fragment.scrollToTop();
        assertThat(shadowScrollView.getSmoothScrollY()).isEqualTo(0);
    }

    @Test
    public void testFullScroll() {
        ShadowLocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_FULLSCREEN).putExtra(EXTRA_FULLSCREEN, true));
        ShadowWebView shadowWebView = Shadow.extract(activity.findViewById(web_view));
        WebFragment fragment = ((WebFragment) (getSupportFragmentManager().findFragmentByTag(WebFragment.class.getName())));
        fragment.scrollToTop();
        Assert.assertEquals(0, shadowWebView.getScrollY());
        fragment.scrollToNext();
        Assert.assertEquals(1, shadowWebView.getScrollY());
        fragment.scrollToPrevious();
        Assert.assertEquals(0, shadowWebView.getScrollY());
    }

    @Test
    public void testBackPressed() {
        WebView webView = activity.findViewById(web_view);
        getWebViewClient().onPageFinished(webView, "http://example.com");
        Shadows.shadowOf(webView).setCanGoBack(true);
        Assert.assertTrue(activity.fragment.onBackPressed());
        Shadows.shadowOf(webView).setCanGoBack(false);
        Assert.assertFalse(activity.fragment.onBackPressed());
    }

    @Test
    public void testReadabilityToggle() {
        activity.fragment.onOptionsItemSelected(new org.robolectric.fakes.RoboMenuItem(id.menu_readability));
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.any(), ArgumentMatchers.eq("http://example.com"), ArgumentMatchers.any());
    }
}

