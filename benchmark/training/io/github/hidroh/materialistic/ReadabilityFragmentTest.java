package io.github.hidroh.materialistic;


import Preferences.Theme;
import R.id;
import R.id.button_more;
import R.id.menu_font_options;
import R.id.nested_scroll_view;
import R.id.progress;
import R.id.web_view;
import R.string.pref_readability_font;
import R.string.pref_readability_text_size;
import R.string.readability_failed;
import R.style.AppTextSize_XLarge;
import WebFragment.EXTRA_FULLSCREEN;
import android.annotation.SuppressLint;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
import android.webkit.WebView;
import io.github.hidroh.materialistic.data.ReadabilityClient;
import io.github.hidroh.materialistic.test.TestReadabilityActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
import io.github.hidroh.materialistic.test.shadow.ShadowNestedScrollView;
import io.github.hidroh.materialistic.test.shadow.ShadowPreferenceFragmentCompat;
import io.github.hidroh.materialistic.test.shadow.ShadowWebView;
import javax.inject.Inject;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPopupMenu;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.ShadowLocalBroadcastManager;

import static WebFragment.ACTION_FULLSCREEN;


@SuppressWarnings("ConstantConditions")
@Config(shadows = { ShadowWebView.class, ShadowPreferenceFragmentCompat.class })
@RunWith(TestRunner.class)
public class ReadabilityFragmentTest {
    private TestReadabilityActivity activity;

    private ActivityController<TestReadabilityActivity> controller;

    @Inject
    ReadabilityClient readabilityClient;

    @Captor
    ArgumentCaptor<ReadabilityClient.Callback> callback;

    private WebFragment fragment;

    @Test
    public void testParseAndBind() {
        assertThat(((android.view.View) (activity.findViewById(progress)))).isVisible();
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.eq("1"), ArgumentMatchers.eq("http://example.com/article.html"), callback.capture());
        callback.getValue().onResponse("<div>content</div>");
        WebView webView = ((WebView) (activity.findViewById(web_view)));
        Shadows.shadowOf(webView).getWebViewClient().onPageFinished(webView, "about:blank");
        assertThat(Shadows.shadowOf(webView).getLastLoadDataWithBaseURL().data).contains("content");
        Shadows.shadowOf(activity).recreate();
        webView = ((WebView) (activity.findViewById(web_view)));
        Shadows.shadowOf(webView).getWebViewClient().onPageFinished(webView, "about:blank");
        assertThat(Shadows.shadowOf(webView).getLastLoadDataWithBaseURL().data).contains("content");
        controller.pause().stop().destroy();
    }

    @Test
    public void testParseFailed() {
        assertThat(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_font_options)).isNotVisible();
        assertThat(((android.view.View) (activity.findViewById(progress)))).isVisible();
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.eq("1"), ArgumentMatchers.eq("http://example.com/article.html"), callback.capture());
        callback.getValue().onResponse(null);
        Mockito.reset(readabilityClient);
        assertThat(ShadowToast.getTextOfLatestToast()).contains(activity.getString(readability_failed));
        WebView webView = ((WebView) (activity.findViewById(web_view)));
        Shadows.shadowOf(webView).getWebViewClient().onPageFinished(webView, "about:blank");
        assertThat(ShadowWebView.getLastGlobalLoadedUrl()).contains("http://example.com/article.html");
        assertThat(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_font_options)).isNotVisible();
        controller.pause().stop().destroy();
    }

    @Config(shadows = ShadowNestedScrollView.class)
    @Test
    public void testScrollToTop() {
        NestedScrollView scrollView = ((NestedScrollView) (activity.findViewById(nested_scroll_view)));
        scrollView.smoothScrollTo(0, 1);
        assertThat(CustomShadows.customShadowOf(scrollView).getSmoothScrollY()).isEqualTo(1);
        fragment.scrollToTop();
        assertThat(CustomShadows.customShadowOf(scrollView).getSmoothScrollY()).isEqualTo(0);
        controller.pause().stop().destroy();
    }

    @Test
    public void testFontSizeMenu() {
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.eq("1"), ArgumentMatchers.eq("http://example.com/article.html"), callback.capture());
        callback.getValue().onResponse("<div>content</div>");
        fragment.onOptionsItemSelected(new org.robolectric.fakes.RoboMenuItem(id.menu_font_options));
        assertThat(fragment.getFragmentManager()).hasFragmentWithTag(PopupSettingsFragment.class.getName());
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_readability_text_size), "3").apply();
        WebView webView = ((WebView) (activity.findViewById(web_view)));
        Shadows.shadowOf(webView).getWebViewClient().onPageFinished(webView, "about:blank");
        assertThat(Shadows.shadowOf(webView).getLastLoadDataWithBaseURL().data).contains("20");
        Assert.assertEquals(AppTextSize_XLarge, Theme.resolvePreferredReadabilityTextSize(activity));
        controller.pause().stop().destroy();
    }

    @Test
    public void testFontMenu() {
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.eq("1"), ArgumentMatchers.eq("http://example.com/article.html"), callback.capture());
        callback.getValue().onResponse("<div>content</div>");
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_readability_font), "DroidSans.ttf").apply();
        WebView webView = ((WebView) (activity.findViewById(web_view)));
        Shadows.shadowOf(webView).getWebViewClient().onPageFinished(webView, "about:blank");
        assertThat(Shadows.shadowOf(webView).getLastLoadDataWithBaseURL().data).contains("DroidSans.ttf");
        Assert.assertEquals("DroidSans.ttf", Theme.getReadabilityTypeface(activity));
        controller.pause().stop().destroy();
    }

    @Test
    public void testWebToggle() {
        fragment.onOptionsItemSelected(new org.robolectric.fakes.RoboMenuItem(id.menu_readability));
        WebView webView = ((WebView) (activity.findViewById(web_view)));
        Shadows.shadowOf(webView).getWebViewClient().onPageFinished(webView, "about:blank");
        assertThat(Shadows.shadowOf(webView).getLastLoadedUrl()).isEqualTo("http://example.com/article.html");
    }

    @SuppressLint("NewApi")
    @Test
    public void testFullscreenMenu() {
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.eq("1"), ArgumentMatchers.eq("http://example.com/article.html"), callback.capture());
        callback.getValue().onResponse("<div>content</div>");
        ShadowLocalBroadcastManager.getInstance(activity).sendBroadcast(new android.content.Intent(ACTION_FULLSCREEN).putExtra(EXTRA_FULLSCREEN, true));
        activity.findViewById(button_more).performClick();
        Shadows.shadowOf(ShadowPopupMenu.getLatestPopupMenu()).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_font_options));
        assertThat(fragment.getFragmentManager()).hasFragmentWithTag(PopupSettingsFragment.class.getName());
    }

    @Test
    public void testBindAfterDetached() {
        assertThat(((android.view.View) (activity.findViewById(progress)))).isVisible();
        controller.pause().stop().destroy();
        Mockito.verify(readabilityClient).parse(ArgumentMatchers.eq("1"), ArgumentMatchers.eq("http://example.com/article.html"), callback.capture());
        callback.getValue().onResponse("<div>content</div>");
    }
}

