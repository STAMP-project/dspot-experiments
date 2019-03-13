package org.robolectric.res;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.res.android.ResTable_config;


@RunWith(JUnit4.class)
public class StyleResourceLoaderTest {
    private PackageResourceTable resourceTable;

    @Test
    public void testStyleDataIsLoadedCorrectly() throws Exception {
        TypedResource typedResource = resourceTable.getValue(new ResName("android", "style", "Theme_Holo"), new ResTable_config());
        StyleData styleData = ((StyleData) (typedResource.getData()));
        assertThat(styleData.getName()).isEqualTo("Theme_Holo");
        assertThat(styleData.getParent()).isEqualTo("Theme");
        assertThat(styleData.getPackageName()).isEqualTo("android");
        assertThat(styleData.getAttrValue(new ResName("android", "attr", "colorForeground")).value).isEqualTo("@android:color/bright_foreground_holo_dark");
    }
}

