package org.robolectric.shadows;


import ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import View.GONE;
import View.INVISIBLE;
import View.VISIBLE;
import android.R.id.icon;
import android.R.id.text1;
import android.R.layout.activity_list_item;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.CustomStateView;
import org.robolectric.android.CustomView;
import org.robolectric.android.CustomView2;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

import static org.robolectric.R.attr.stateFoo;
import static org.robolectric.R.drawable.an_image;
import static org.robolectric.R.drawable.an_other_image;
import static org.robolectric.R.drawable.fourth_image;
import static org.robolectric.R.drawable.image_background;
import static org.robolectric.R.drawable.third_image;
import static org.robolectric.R.id.button;
import static org.robolectric.R.id.custom_view;
import static org.robolectric.R.id.edit_text;
import static org.robolectric.R.id.icon;
import static org.robolectric.R.id.image;
import static org.robolectric.R.id.include_id;
import static org.robolectric.R.id.inner_text;
import static org.robolectric.R.id.invalid_onclick_button;
import static org.robolectric.R.id.landscape;
import static org.robolectric.R.id.list_view_with_enum_scrollbar;
import static org.robolectric.R.id.mipmapImage;
import static org.robolectric.R.id.outer_merge;
import static org.robolectric.R.id.portrait;
import static org.robolectric.R.id.snippet_text;
import static org.robolectric.R.id.subtitle;
import static org.robolectric.R.id.time;
import static org.robolectric.R.id.title;
import static org.robolectric.R.id.web_view;
import static org.robolectric.R.id.world;
import static org.robolectric.R.layout.activity_list_item;
import static org.robolectric.R.layout.custom_layout;
import static org.robolectric.R.layout.custom_layout2;
import static org.robolectric.R.layout.custom_layout3;
import static org.robolectric.R.layout.custom_layout4;
import static org.robolectric.R.layout.custom_layout5;
import static org.robolectric.R.layout.custom_layout6;
import static org.robolectric.R.layout.different_screen_sizes;
import static org.robolectric.R.layout.included_layout_parent;
import static org.robolectric.R.layout.inner_merge;
import static org.robolectric.R.layout.main;
import static org.robolectric.R.layout.main_layout;
import static org.robolectric.R.layout.media;
import static org.robolectric.R.layout.multi_orientation;
import static org.robolectric.R.layout.ordinal_scrollbar;
import static org.robolectric.R.layout.outer;
import static org.robolectric.R.layout.override_include;
import static org.robolectric.R.layout.request_focus;
import static org.robolectric.R.layout.snippet;
import static org.robolectric.R.layout.text_views;
import static org.robolectric.R.layout.webview_holder;
import static org.robolectric.R.layout.with_invalid_onclick;
import static org.robolectric.R.mipmap.robolectric;
import static org.robolectric.R.string.hello;


@RunWith(AndroidJUnit4.class)
public class ShadowLayoutInflaterTest {
    private Context context;

    @Test
    public void testCreatesCorrectClasses() throws Exception {
        ViewGroup view = inflate(media);
        assertThat(view).isInstanceOf(LinearLayout.class);
        Assert.assertSame(context, view.getContext());
    }

    @Test
    public void testChoosesLayoutBasedOnDefaultScreenSize() throws Exception {
        ViewGroup view = inflate(different_screen_sizes);
        TextView textView = view.findViewById(text1);
        assertThat(textView.getText().toString()).isEqualTo("default");
    }

    @Test
    @Config(qualifiers = "xlarge")
    public void testChoosesLayoutBasedOnScreenSize() throws Exception {
        ViewGroup view = inflate(different_screen_sizes);
        TextView textView = view.findViewById(text1);
        assertThat(textView.getText().toString()).isEqualTo("xlarge");
    }

    @Test
    @Config(qualifiers = "land")
    public void testChoosesLayoutBasedOnQualifiers() throws Exception {
        ViewGroup view = inflate(different_screen_sizes);
        TextView textView = view.findViewById(text1);
        assertThat(textView.getText().toString()).isEqualTo("land");
    }

    @Test
    public void testWebView() throws Exception {
        ViewGroup view = inflate(webview_holder);
        WebView webView = view.findViewById(web_view);
        webView.loadUrl("www.example.com");
        assertThat(Shadows.shadowOf(webView).getLastLoadedUrl()).isEqualTo("www.example.com");
    }

    @Test
    public void testAddsChildren() throws Exception {
        ViewGroup view = inflate(media);
        Assert.assertTrue(((view.getChildCount()) > 0));
        Assert.assertSame(context, view.getChildAt(0).getContext());
    }

    @Test
    public void testFindsChildrenById() throws Exception {
        ViewGroup mediaView = inflate(media);
        assertThat(mediaView.<TextView>findViewById(title)).isInstanceOf(TextView.class);
        ViewGroup mainView = inflate(main);
        assertThat(mainView.<View>findViewById(title)).isInstanceOf(View.class);
    }

    @Test
    public void testInflatingConflictingSystemAndLocalViewsWorks() throws Exception {
        ViewGroup view = inflate(activity_list_item);
        assertThat(view.<ImageView>findViewById(icon)).isInstanceOf(ImageView.class);
        view = inflate(activity_list_item);
        assertThat(view.<ImageView>findViewById(icon)).isInstanceOf(ImageView.class);
    }

    @Test
    public void testInclude() throws Exception {
        ViewGroup mediaView = inflate(media);
        assertThat(mediaView.<TextView>findViewById(include_id)).isInstanceOf(TextView.class);
    }

    @Test
    public void testIncludeShouldRetainAttributes() throws Exception {
        ViewGroup mediaView = inflate(media);
        assertThat(mediaView.findViewById(include_id).getVisibility()).isEqualTo(GONE);
    }

    @Test
    public void shouldOverwriteIdOnIncludedNonMerge() throws Exception {
        ViewGroup mediaView = inflate(media);
        Assert.assertNull(mediaView.findViewById(snippet_text));
    }

    @Test
    public void shouldRetainIdOnIncludedMergeWhenIncludeSpecifiesNoId() throws Exception {
        ViewGroup mediaView = inflate(override_include);
        assertThat(mediaView.<TextView>findViewById(inner_text)).isInstanceOf(TextView.class);
    }

    @Test
    public void shouldRetainIdOnIncludedNonMergeWhenIncludeSpecifiesNoId() throws Exception {
        ViewGroup mediaView = inflate(override_include);
        assertThat(mediaView.<TextView>findViewById(snippet_text)).isInstanceOf(TextView.class);
    }

    @Test
    public void testIncludedIdShouldNotBeFoundWhenIncludedIsMerge() throws Exception {
        ViewGroup overrideIncludeView = inflate(outer);
        assertThat(overrideIncludeView.<LinearLayout>findViewById(outer_merge)).isInstanceOf(LinearLayout.class);
        assertThat(overrideIncludeView.<TextView>findViewById(inner_text)).isInstanceOf(TextView.class);
        Assert.assertNull(overrideIncludeView.findViewById(include_id));
        Assert.assertEquals(1, overrideIncludeView.getChildCount());
    }

    @Test
    public void testIncludeShouldOverrideAttributesOfIncludedRootNode() throws Exception {
        ViewGroup overrideIncludeView = inflate(override_include);
        assertThat(overrideIncludeView.findViewById(snippet_text).getVisibility()).isEqualTo(INVISIBLE);
    }

    @Test
    public void shouldNotCountRequestFocusElementAsChild() throws Exception {
        ViewGroup viewGroup = inflate(request_focus);
        ViewGroup frameLayout = ((ViewGroup) (viewGroup.getChildAt(1)));
        Assert.assertEquals(0, frameLayout.getChildCount());
    }

    @Test
    public void focusRequest_shouldNotExplodeOnViewRootImpl() throws Exception {
        LinearLayout parent = new LinearLayout(context);
        Shadows.shadowOf(parent).setMyParent(ReflectionHelpers.createNullProxy(ViewParent.class));
        LayoutInflater.from(context).inflate(request_focus, parent);
    }

    @Test
    public void shouldGiveFocusToElementContainingRequestFocusElement() throws Exception {
        ViewGroup viewGroup = inflate(request_focus);
        EditText editText = viewGroup.findViewById(edit_text);
        Assert.assertFalse(editText.isFocused());
    }

    @Test
    public void testMerge() throws Exception {
        ViewGroup mediaView = inflate(outer);
        assertThat(mediaView.<TextView>findViewById(inner_text)).isInstanceOf(TextView.class);
    }

    @Test
    public void mergeIncludesShouldNotCreateAncestryLoops() throws Exception {
        ViewGroup mediaView = inflate(outer);
        mediaView.hasFocus();
    }

    @Test
    public void testViewGroupsLooksAtItsOwnId() throws Exception {
        TextView mediaView = inflate(snippet);
        Assert.assertSame(mediaView, mediaView.findViewById(snippet_text));
    }

    @Test
    public void shouldConstructCustomViewsWithAttributesConstructor() throws Exception {
        CustomView view = inflate(custom_layout);
        assertThat(view.attributeResourceValue).isEqualTo(hello);
    }

    @Test
    public void shouldConstructCustomViewsWithCustomState() throws Exception {
        CustomStateView view = inflate(custom_layout6);
        assertThat(getDrawableState()).asList().doesNotContain(stateFoo);
        view.extraAttribute = stateFoo;
        refreshDrawableState();
        assertThat(getDrawableState()).asList().contains(stateFoo);
    }

    @Test
    public void shouldConstructCustomViewsWithAttributesInResAutoNamespace() throws Exception {
        CustomView view = inflate(custom_layout5);
        assertThat(view.attributeResourceValue).isEqualTo(hello);
    }

    @Test
    public void shouldConstructCustomViewsWithAttributesWithURLEncodedNamespaces() throws Exception {
        CustomView view = inflate(custom_layout4).findViewById(custom_view);
        assertThat(view.namespacedResourceValue).isEqualTo(text_views);
    }

    @Test
    public void testViewVisibilityIsSet() throws Exception {
        View mediaView = inflate(media);
        assertThat(mediaView.findViewById(title).getVisibility()).isEqualTo(VISIBLE);
        assertThat(mediaView.findViewById(subtitle).getVisibility()).isEqualTo(GONE);
    }

    @Test
    public void testTextViewTextIsSet() throws Exception {
        View mediaView = inflate(main);
        assertThat(getText().toString()).isEqualTo("Main Layout");
        assertThat(getText().toString()).isEqualTo("Hello");
    }

    @Test
    public void testTextViewCompoundDrawablesAreSet() throws Exception {
        View mediaView = inflate(main);
        TextView view = mediaView.findViewById(title);
        Drawable[] drawables = view.getCompoundDrawables();
        assertThat(Shadows.shadowOf(drawables[0]).getCreatedFromResId()).isEqualTo(fourth_image);
        assertThat(Shadows.shadowOf(drawables[1]).getCreatedFromResId()).isEqualTo(an_image);
        assertThat(Shadows.shadowOf(drawables[2]).getCreatedFromResId()).isEqualTo(an_other_image);
        assertThat(Shadows.shadowOf(drawables[3]).getCreatedFromResId()).isEqualTo(third_image);
    }

    @Test
    public void testCheckBoxCheckedIsSet() throws Exception {
        View mediaView = inflate(main);
        assertThat(isChecked()).isTrue();
        assertThat(isChecked()).isFalse();
        assertThat(isChecked()).isFalse();
    }

    @Test
    public void testImageViewSrcIsSet() throws Exception {
        View mediaView = inflate(main);
        ImageView imageView = mediaView.findViewById(image);
        BitmapDrawable drawable = ((BitmapDrawable) (imageView.getDrawable()));
        assertThat(Shadows.shadowOf(drawable.getBitmap()).getCreatedFromResId()).isEqualTo(an_image);
    }

    @Test
    public void testImageViewSrcIsSetFromMipmap() throws Exception {
        View mediaView = inflate(main);
        ImageView imageView = mediaView.findViewById(mipmapImage);
        BitmapDrawable drawable = ((BitmapDrawable) (imageView.getDrawable()));
        assertThat(Shadows.shadowOf(drawable.getBitmap()).getCreatedFromResId()).isEqualTo(robolectric);
    }

    @Test
    public void shouldInflateMergeLayoutIntoParent() throws Exception {
        LinearLayout linearLayout = new LinearLayout(context);
        LayoutInflater.from(context).inflate(inner_merge, linearLayout);
        assertThat(linearLayout.getChildAt(0)).isInstanceOf(TextView.class);
    }

    @Test
    public void testMultiOrientation() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
        // Default screen orientation should be portrait.
        ViewGroup view = ((ViewGroup) (LayoutInflater.from(activity).inflate(multi_orientation, null)));
        assertThat(view).isInstanceOf(LinearLayout.class);
        assertThat(view.getId()).isEqualTo(portrait);
        Assert.assertSame(activity, view.getContext());
        // Confirm explicit "orientation = portrait" works.
        activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        int layoutResId = multi_orientation;
        view = ((ViewGroup) (LayoutInflater.from(activity).inflate(layoutResId, null)));
        assertThat(view).isInstanceOf(LinearLayout.class);
        assertThat(view.getId()).isEqualTo(portrait);
        Assert.assertSame(activity, view.getContext());
    }

    @Test
    @Config(qualifiers = "land")
    public void testMultiOrientation_explicitLandscape() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
        // Confirm explicit "orientation = landscape" works.
        activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup view = ((ViewGroup) (LayoutInflater.from(activity).inflate(multi_orientation, null)));
        assertThat(view.getId()).isEqualTo(landscape);
        assertThat(view).isInstanceOf(LinearLayout.class);
    }

    @Test
    @Config(qualifiers = "w0dp")
    public void testSetContentViewByItemResource() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        activity.setContentView(main_layout);
        TextView tv1 = activity.findViewById(R.id.hello);
        TextView tv2 = activity.findViewById(world);
        Assert.assertNotNull(tv1);
        Assert.assertNull(tv2);
    }

    @Test
    @Config(qualifiers = "w820dp")
    public void testSetContentViewByItemResourceWithW820dp() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        activity.setContentView(main_layout);
        TextView tv1 = activity.findViewById(R.id.hello);
        TextView tv2 = activity.findViewById(world);
        Assert.assertNotNull(tv1);
        Assert.assertNotNull(tv2);
    }

    @Test
    public void testViewEnabled() throws Exception {
        View mediaView = inflate(main);
        assertThat(mediaView.findViewById(time).isEnabled()).isFalse();
    }

    @Test
    public void testContentDescriptionIsSet() throws Exception {
        View mediaView = inflate(main);
        assertThat(mediaView.findViewById(time).getContentDescription().toString()).isEqualTo("Howdy");
    }

    @Test
    public void testAlphaIsSet() throws Exception {
        View mediaView = inflate(main);
        assertThat(mediaView.findViewById(time).getAlpha()).isEqualTo(0.3F);
    }

    @Test
    public void testViewBackgroundIdIsSet() throws Exception {
        View mediaView = inflate(main);
        ImageView imageView = mediaView.findViewById(image);
        assertThat(Shadows.shadowOf(imageView.getBackground()).getCreatedFromResId()).isEqualTo(image_background);
    }

    @Test
    public void testOnClickAttribute() throws Exception {
        ShadowLayoutInflaterTest.ClickActivity activity = Robolectric.buildActivity(ShadowLayoutInflaterTest.ClickActivity.class).create().get();
        assertThat(activity.clicked).isFalse();
        Button button = findViewById(button);
        button.performClick();
        assertThat(activity.clicked).isTrue();
    }

    @Test
    public void testInvalidOnClickAttribute() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        activity.setContentView(with_invalid_onclick);
        Button button = activity.findViewById(invalid_onclick_button);
        IllegalStateException exception = null;
        try {
            button.performClick();
        } catch (IllegalStateException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        assertThat(exception.getMessage()).named("The error message should contain the id name of the faulty button").contains("invalid_onclick_button");
    }

    @Test
    public void shouldInvokeOnFinishInflate() throws Exception {
        int layoutResId = custom_layout2;
        CustomView2 outerCustomView = inflate(layoutResId);
        CustomView2 innerCustomView = ((CustomView2) (getChildAt(0)));
        assertThat(outerCustomView.childCountAfterInflate).isEqualTo(1);
        assertThat(innerCustomView.childCountAfterInflate).isEqualTo(3);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class CustomView3 extends TextView {
        public CustomView3(Context context) {
            super(context);
        }

        public CustomView3(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomView3(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
    }

    @Test
    public void shouldInflateViewsWithClassAttr() throws Exception {
        ShadowLayoutInflaterTest.CustomView3 outerCustomView = inflate(custom_layout3);
        assertThat(getText().toString()).isEqualTo("Hello bonjour");
    }

    @Test
    public void testIncludesLinearLayoutsOnlyOnce() throws Exception {
        ViewGroup parentView = inflate(included_layout_parent);
        Assert.assertEquals(1, parentView.getChildCount());
    }

    @Test
    public void testConverterAcceptsEnumOrdinal() throws Exception {
        ViewGroup view = inflate(ordinal_scrollbar);
        assertThat(view).isInstanceOf(RelativeLayout.class);
        ListView listView = view.findViewById(list_view_with_enum_scrollbar);
        assertThat(listView).isInstanceOf(ListView.class);
    }

    public static class ClickActivity extends Activity {
        public boolean clicked = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(main);
        }

        public void onButtonClick(View v) {
            clicked = true;
        }
    }
}

