package org.robolectric.shadows;


import Build.VERSION_CODES;
import Resources.Theme;
import TypedValue.TYPE_FIRST_COLOR_INT;
import TypedValue.TYPE_LAST_COLOR_INT;
import TypedValue.TYPE_REFERENCE;
import android.R.attr;
import android.R.attr.height;
import android.R.attr.id;
import android.R.attr.title;
import android.R.attr.viewportHeight;
import android.R.attr.viewportWidth;
import android.R.attr.width;
import android.R.attr.windowBackground;
import android.R.color.black;
import android.R.id.mask;
import android.R.layout.list_content;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.common.collect.Range;
import java.io.InputStream;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.android.XmlResourceParserImpl;
import org.robolectric.annotation.Config;
import org.xmlpull.v1.XmlPullParser;

import static org.robolectric.R.attr.styleReference;
import static org.robolectric.R.drawable.an_image;
import static org.robolectric.R.raw.raw_resource;
import static org.robolectric.R.string.hello;
import static org.robolectric.R.style.MyBlackTheme;
import static org.robolectric.R.xml.preferences;
import static org.robolectric.R.xml.shortcuts;
import static org.robolectric.R.xml.xml_attrs;


@RunWith(AndroidJUnit4.class)
public class ShadowResourcesTest {
    private Resources resources;

    @Test
    @Config(qualifiers = "fr")
    public void testGetValuesResFromSpecificQualifiers() {
        assertThat(resources.getString(hello)).isEqualTo("Bonjour");
    }

    /**
     * Public framework symbols are defined here: https://android.googlesource.com/platform/frameworks/base/+/master/core/res/res/values/public.xml
     * Private framework symbols are defined here: https://android.googlesource.com/platform/frameworks/base/+/master/core/res/res/values/symbols.xml
     *
     * These generate android.R and com.android.internal.R respectively, when Framework Java code does not need to reference a framework resource
     * it will not have an R value generated. Robolectric is then missing an identifier for this resource so we must generate a placeholder ourselves.
     */
    // android:color/secondary_text_material_dark was added in API 21
    @Test
    @Config(sdk = VERSION_CODES.LOLLIPOP)
    public void shouldGenerateIdsForResourcesThatAreMissingRValues() throws Exception {
        int identifier_missing_from_r_file = resources.getIdentifier("secondary_text_material_dark", "color", "android");
        // We expect Robolectric to generate a placeholder identifier where one was not generated in the android R files.
        assertThat(identifier_missing_from_r_file).isNotEqualTo(0);
        // We expect to be able to successfully android:color/secondary_text_material_dark to a ColorStateList.
        assertThat(resources.getColorStateList(identifier_missing_from_r_file)).isNotNull();
    }

    @Test
    @Config(qualifiers = "fr")
    public void openRawResource_shouldLoadDrawableWithQualifiers() throws Exception {
        InputStream resourceStream = resources.openRawResource(an_image);
        Bitmap bitmap = BitmapFactory.decodeStream(resourceStream);
        assertThat(bitmap.getHeight()).isEqualTo(100);
        assertThat(bitmap.getWidth()).isEqualTo(100);
    }

    @Test
    public void openRawResourceFd_returnsNull_todo_FIX() throws Exception {
        if (ShadowAssetManager.useLegacy()) {
            assertThat(resources.openRawResourceFd(raw_resource)).isNull();
        } else {
            assertThat(resources.openRawResourceFd(raw_resource)).isNotNull();
        }
    }

    @Test
    @Config
    public void themeResolveAttribute_shouldSupportDereferenceResource() {
        TypedValue out = new TypedValue();
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(MyBlackTheme, false);
        theme.resolveAttribute(windowBackground, out, true);
        assertThat(out.type).isNotEqualTo(TYPE_REFERENCE);
        assertThat(out.type).isIn(Range.closed(TYPE_FIRST_COLOR_INT, TYPE_LAST_COLOR_INT));
        int value = resources.getColor(black);
        assertThat(out.data).isEqualTo(value);
    }

    @Test
    public void themeResolveAttribute_shouldSupportNotDereferencingResource() {
        TypedValue out = new TypedValue();
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(MyBlackTheme, false);
        theme.resolveAttribute(windowBackground, out, false);
        assertThat(out.type).isEqualTo(TYPE_REFERENCE);
        assertThat(out.data).isEqualTo(black);
    }

    // todo: port to ResourcesTest
    @Test
    public void obtainAttributes_shouldUseReferencedIdFromAttributeSet() throws Exception {
        // android:id/mask was introduced in API 21, but it's still possible for apps built against API 21 to refer to it
        // in older runtimes because referenced resource ids are compiled (by aapt) into the binary XML format.
        AttributeSet attributeSet = Robolectric.buildAttributeSet().addAttribute(id, "@android:id/mask").build();
        TypedArray typedArray = resources.obtainAttributes(attributeSet, new int[]{ attr.id });
        assertThat(typedArray.getResourceId(0, (-9))).isEqualTo(mask);
    }

    // todo: port to ResourcesTest
    @Test
    public void obtainAttributes() {
        TypedArray typedArray = resources.obtainAttributes(Robolectric.buildAttributeSet().addAttribute(styleReference, "@xml/shortcuts").build(), new int[]{ styleReference });
        assertThat(typedArray).isNotNull();
        assertThat(typedArray.peekValue(0).resourceId).isEqualTo(shortcuts);
    }

    // todo: port to ResourcesTest
    @Test
    public void obtainAttributes_shouldReturnValuesFromAttributeSet() throws Exception {
        AttributeSet attributes = Robolectric.buildAttributeSet().addAttribute(title, "A title!").addAttribute(width, "12px").addAttribute(height, "1in").build();
        TypedArray typedArray = resources.obtainAttributes(attributes, new int[]{ attr.height, attr.width, attr.title });
        assertThat(typedArray.getDimension(0, 0)).isEqualTo(160.0F);
        assertThat(typedArray.getDimension(1, 0)).isEqualTo(12.0F);
        assertThat(typedArray.getString(2)).isEqualTo("A title!");
        typedArray.recycle();
    }

    // todo: port to ResourcesTest
    @Test
    public void obtainAttributes_shouldReturnValuesFromResources() throws Exception {
        XmlPullParser parser = resources.getXml(xml_attrs);
        parser.next();
        parser.next();
        AttributeSet attributes = Xml.asAttributeSet(parser);
        TypedArray typedArray = resources.obtainAttributes(attributes, new int[]{ attr.title, attr.scrollbarFadeDuration });
        assertThat(typedArray.getString(0)).isEqualTo("Android Title");
        assertThat(typedArray.getInt(1, 0)).isEqualTo(1111);
        typedArray.recycle();
    }

    @Test
    public void obtainStyledAttributes_shouldCheckXmlFirst_fromAttributeSetBuilder() throws Exception {
        // This simulates a ResourceProvider built from a 21+ SDK as viewportHeight / viewportWidth were introduced in API 21
        // but the public ID values they are assigned clash with private com.android.internal.R values on older SDKs. This
        // test ensures that even on older SDKs, on calls to obtainStyledAttributes() Robolectric will first check for matching
        // resource ID values in the AttributeSet before checking the theme.
        AttributeSet attributes = Robolectric.buildAttributeSet().addAttribute(viewportWidth, "12.0").addAttribute(viewportHeight, "24.0").build();
        TypedArray typedArray = ApplicationProvider.getApplicationContext().getTheme().obtainStyledAttributes(attributes, new int[]{ attr.viewportWidth, attr.viewportHeight }, 0, 0);
        assertThat(typedArray.getFloat(0, 0)).isEqualTo(12.0F);
        assertThat(typedArray.getFloat(1, 0)).isEqualTo(24.0F);
        typedArray.recycle();
    }

    // todo: port to ResourcesTest
    @Test
    public void obtainStyledAttributesShouldCheckXmlFirst_andFollowReferences() throws Exception {
        // TODO: investigate failure with binary resources
        Assume.assumeTrue(ShadowAssetManager.useLegacy());
        // This simulates a ResourceProvider built from a 21+ SDK as viewportHeight / viewportWidth were introduced in API 21
        // but the public ID values they are assigned clash with private com.android.internal.R values on older SDKs. This
        // test ensures that even on older SDKs, on calls to obtainStyledAttributes() Robolectric will first check for matching
        // resource ID values in the AttributeSet before checking the theme.
        AttributeSet attributes = Robolectric.buildAttributeSet().addAttribute(viewportWidth, "@dimen/dimen20px").addAttribute(viewportHeight, "@dimen/dimen30px").build();
        TypedArray typedArray = ApplicationProvider.getApplicationContext().getTheme().obtainStyledAttributes(attributes, new int[]{ attr.viewportWidth, attr.viewportHeight }, 0, 0);
        assertThat(typedArray.getDimension(0, 0)).isEqualTo(20.0F);
        assertThat(typedArray.getDimension(1, 0)).isEqualTo(30.0F);
        typedArray.recycle();
    }

    @Test
    public void getXml_shouldHavePackageContextForReferenceResolution() throws Exception {
        if (!(ShadowAssetManager.useLegacy())) {
            return;
        }
        XmlResourceParserImpl xmlResourceParser = ((XmlResourceParserImpl) (resources.getXml(preferences)));
        assertThat(xmlResourceParser.qualify("?ref")).isEqualTo("?org.robolectric:attr/ref");
        xmlResourceParser = ((XmlResourceParserImpl) (resources.getXml(list_content)));
        assertThat(xmlResourceParser.qualify("?ref")).isEqualTo("?android:attr/ref");
    }
}

