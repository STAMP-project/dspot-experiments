package io.github.hidroh.materialistic;


import HackerNewsClient.WEB_ITEM_PATH;
import ItemManager.MODE_DEFAULT;
import R.id;
import R.id.web_view;
import R.string.pref_readability_font;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.webkit.WebView;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.ResponseListener;
import io.github.hidroh.materialistic.test.TestItem;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.TestWebItem;
import io.github.hidroh.materialistic.test.WebActivity;
import io.github.hidroh.materialistic.test.shadow.ShadowPreferenceFragmentCompat;
import javax.inject.Inject;
import javax.inject.Named;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowWebView;

import static ActivityModule.HN;


@SuppressWarnings("ConstantConditions")
@Config(shadows = { ShadowPreferenceFragmentCompat.class })
@RunWith(TestRunner.class)
public class WebFragmentLocalTest {
    private ActivityController<WebActivity> controller;

    private WebActivity activity;

    @Inject
    @Named(HN)
    ItemManager itemManager;

    @Captor
    ArgumentCaptor<ResponseListener<Item>> listener;

    @Test
    public void testStory() {
        TestWebItem item = new TestWebItem() {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }

            @Override
            public String getId() {
                return "1";
            }

            @Override
            public String getUrl() {
                return String.format(WEB_ITEM_PATH, "1");
            }

            @Override
            public String getDisplayedTitle() {
                return "Ask HN";
            }
        };
        Intent intent = new Intent();
        intent.putExtra(WebActivity.EXTRA_ITEM, item);
        controller = Robolectric.buildActivity(WebActivity.class, intent);
        controller.create().start().resume().visible();
        activity = controller.get();
        Mockito.verify(itemManager).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), listener.capture());
        listener.getValue().onResponse(new TestItem() {
            @Override
            public String getText() {
                return "text";
            }
        });
        WebView webView = activity.findViewById(web_view);
        ShadowWebView shadowWebView = Shadows.shadowOf(webView);
        shadowWebView.getWebViewClient().onPageFinished(webView, "about:blank");
        assertThat(shadowWebView.getLastLoadDataWithBaseURL().data).contains("text");
    }

    @Test
    public void testComment() {
        TestItem item = new TestItem() {
            @NonNull
            @Override
            public String getType() {
                return COMMENT_TYPE;
            }

            @Override
            public String getId() {
                return "1";
            }

            @Override
            public String getUrl() {
                return String.format(WEB_ITEM_PATH, "1");
            }

            @Override
            public String getText() {
                return "comment";
            }
        };
        Intent intent = new Intent();
        intent.putExtra(WebActivity.EXTRA_ITEM, item);
        controller = Robolectric.buildActivity(WebActivity.class, intent);
        controller.create().start().resume().visible();
        activity = controller.get();
        WebView webView = activity.findViewById(web_view);
        ShadowWebView shadowWebView = Shadows.shadowOf(webView);
        shadowWebView.getWebViewClient().onPageFinished(webView, "about:blank");
        assertThat(shadowWebView.getLastLoadDataWithBaseURL().data).contains("comment");
    }

    @Test
    public void testMenu() {
        TestWebItem item = new TestWebItem() {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }

            @Override
            public String getId() {
                return "1";
            }

            @Override
            public String getUrl() {
                return String.format(WEB_ITEM_PATH, "1");
            }

            @Override
            public String getDisplayedTitle() {
                return "Ask HN";
            }
        };
        Intent intent = new Intent();
        intent.putExtra(WebActivity.EXTRA_ITEM, item);
        controller = Robolectric.buildActivity(WebActivity.class, intent);
        controller.create().start().resume().visible();
        activity = controller.get();
        Mockito.verify(itemManager).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), listener.capture());
        listener.getValue().onResponse(new TestItem() {
            @Override
            public String getText() {
                return "text";
            }
        });
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(WebFragment.class.getName());
        Assert.assertTrue(fragment.hasOptionsMenu());
        fragment.onOptionsItemSelected(new org.robolectric.fakes.RoboMenuItem(id.menu_font_options));
        Assert.assertNotNull(ShadowDialog.getLatestDialog());
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_readability_font), "DroidSans.ttf").apply();
        WebView webView = activity.findViewById(web_view);
        ShadowWebView shadowWebView = Shadows.shadowOf(webView);
        shadowWebView.getWebViewClient().onPageFinished(webView, "about:blank");
        assertThat(shadowWebView.getLastLoadDataWithBaseURL().data).contains("text").contains("DroidSans.ttf");
    }
}

