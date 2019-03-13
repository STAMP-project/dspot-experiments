package org.robolectric.shadows;


import android.R.attr.height;
import android.R.attr.id;
import android.R.attr.scrollbarFadeDuration;
import android.R.attr.title;
import android.R.attr.width;
import android.R.dimen;
import android.R.dimen.app_icon_size;
import android.R.id.text1;
import android.R.style.TextAppearance_Small;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import static ResourceTypes.ANDROID_NS;
import static ResourceTypes.AUTO_NS;
import static android.R.id.text1;
import static org.robolectric.R.attr.title;
import static org.robolectric.R.xml.xml_attrs;


@RunWith(AndroidJUnit4.class)
public class XmlPullParserTest {
    // emulator output:
    /* http://schemas.android.com/apk/res/android:id(resId=16842960) type=CDATA: value=@16908308 (resId=16908308)
    http://schemas.android.com/apk/res/android:height(resId=16843093) type=CDATA: value=1234.0px (resId=-1)
    http://schemas.android.com/apk/res/android:width(resId=16843097) type=CDATA: value=1234.0px (resId=-1)
    http://schemas.android.com/apk/res/android:title(resId=16843233) type=CDATA: value=Android Title (resId=-1)
    http://schemas.android.com/apk/res/android:scrollbarFadeDuration(resId=16843432) type=CDATA: value=1111 (resId=-1)
    http://schemas.android.com/apk/res-auto:title(resId=2130771971) type=CDATA: value=App Title (resId=-1)
    :style(resId=0) type=CDATA: value=@android:style/TextAppearance.Small (resId=16973894)
     */
    @Test
    public void xmlParser() throws IOException, XmlPullParserException {
        Resources resources = ApplicationProvider.getApplicationContext().getResources();
        XmlResourceParser parser = resources.getXml(xml_attrs);
        assertThat(parser).isNotNull();
        assertThat(parser.getAttributeCount()).isEqualTo((-1));
        assertThat(parser.next()).isEqualTo(XmlPullParser.START_DOCUMENT);
        assertThat(parser.next()).isEqualTo(XmlPullParser.START_TAG);
        assertThat(parser.getName()).isEqualTo("whatever");
        int attributeCount = parser.getAttributeCount();
        List<String> attrNames = new ArrayList<>();
        for (int i = 0; i < attributeCount; i++) {
            String namespace = parser.getAttributeNamespace(i);
            if (!("http://www.w3.org/2000/xmlns/".equals(namespace))) {
                attrNames.add(((namespace + ":") + (parser.getAttributeName(i))));
            }
        }
        assertThat(attrNames).containsExactly(((ANDROID_NS) + ":id"), ((ANDROID_NS) + ":height"), ((ANDROID_NS) + ":width"), ((ANDROID_NS) + ":title"), ((ANDROID_NS) + ":scrollbarFadeDuration"), ((AUTO_NS) + ":title"), ":style", ":class", ":id");
        if (!(RuntimeEnvironment.useLegacyResources())) {
            // doesn't work in legacy mode, sorry
            assertAttribute(parser, ResourceTypes.ANDROID_NS, "id", id, ("@" + (text1)), text1);
            assertAttribute(parser, ResourceTypes.ANDROID_NS, "height", height, ("@" + (dimen.app_icon_size)), app_icon_size);
            assertAttribute(parser, ResourceTypes.ANDROID_NS, "width", width, "1234.0px", (-1));
            assertAttribute(parser, "", "style", 0, "@android:style/TextAppearance.Small", TextAppearance_Small);
        }
        assertAttribute(parser, ResourceTypes.ANDROID_NS, "title", title, "Android Title", (-1));
        assertAttribute(parser, ResourceTypes.ANDROID_NS, "scrollbarFadeDuration", scrollbarFadeDuration, "1111", (-1));
        assertAttribute(parser, ResourceTypes.AUTO_NS, "title", title, "App Title", (-1));
        if (!(RuntimeEnvironment.useLegacyResources())) {
            // doesn't work in legacy mode, sorry
            assertThat(parser.getStyleAttribute()).isEqualTo(TextAppearance_Small);
        }
        assertThat(parser.getIdAttribute()).isEqualTo("@android:id/text2");
        assertThat(parser.getClassAttribute()).isEqualTo("none");
    }

    @Test
    public void buildAttrSet() throws Exception {
        XmlResourceParser parser = ((XmlResourceParser) (Robolectric.buildAttributeSet().addAttribute(width, "1234px").addAttribute(height, "@android:dimen/app_icon_size").addAttribute(scrollbarFadeDuration, "1111").addAttribute(title, "Android Title").addAttribute(title, "App Title").addAttribute(id, "@android:id/text1").setStyleAttribute("@android:style/TextAppearance.Small").setClassAttribute("none").setIdAttribute("@android:id/text2").build()));
        assertThat(parser.getName()).isEqualTo("dummy");
        int attributeCount = parser.getAttributeCount();
        List<String> attrNames = new ArrayList<>();
        for (int i = 0; i < attributeCount; i++) {
            attrNames.add((((parser.getAttributeNamespace(i)) + ":") + (parser.getAttributeName(i))));
        }
        assertThat(attrNames).containsExactly(((ANDROID_NS) + ":id"), ((ANDROID_NS) + ":height"), ((ANDROID_NS) + ":width"), ((ANDROID_NS) + ":title"), ((ANDROID_NS) + ":scrollbarFadeDuration"), ((AUTO_NS) + ":title"), ":style", ":class", ":id");
        assertAttribute(parser, ResourceTypes.ANDROID_NS, "id", id, ("@" + (text1)), text1);
        assertAttribute(parser, ResourceTypes.ANDROID_NS, "height", height, ("@" + (dimen.app_icon_size)), app_icon_size);
        assertAttribute(parser, ResourceTypes.ANDROID_NS, "width", width, "1234.0px", (-1));
        assertAttribute(parser, "", "style", 0, "@android:style/TextAppearance.Small", TextAppearance_Small);
        assertAttribute(parser, ResourceTypes.ANDROID_NS, "title", title, "Android Title", (-1));
        assertAttribute(parser, ResourceTypes.ANDROID_NS, "scrollbarFadeDuration", scrollbarFadeDuration, "1111", (-1));
        assertAttribute(parser, ResourceTypes.AUTO_NS, "title", title, "App Title", (-1));
        assertThat(parser.getStyleAttribute()).isEqualTo(TextAppearance_Small);
        assertThat(parser.getIdAttribute()).isEqualTo("@android:id/text2");
        assertThat(parser.getClassAttribute()).isEqualTo("none");
    }
}

