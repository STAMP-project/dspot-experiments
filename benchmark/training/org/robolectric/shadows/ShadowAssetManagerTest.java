package org.robolectric.shadows;


import Resources.NotFoundException;
import Resources.Theme;
import TypedValue.TYPE_INT_BOOLEAN;
import TypedValue.TYPE_INT_DEC;
import TypedValue.TYPE_INT_HEX;
import TypedValue.TYPE_STRING;
import android.R.attr.windowBackground;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static android.R.attr.windowBackground;
import static org.robolectric.R.attr.multiformat;
import static org.robolectric.R.attr.string1;
import static org.robolectric.R.style.Theme_Robolectric;


@RunWith(AndroidJUnit4.class)
public class ShadowAssetManagerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AssetManager assetManager;

    private Resources resources;

    @Test
    public void openFd_shouldProvideFileDescriptorForDeflatedAsset() throws Exception {
        Assume.assumeTrue((!(ShadowAssetManager.useLegacy())));
        expectedException.expect(FileNotFoundException.class);
        expectedException.expectMessage("This file can not be opened as a file descriptor; it is probably compressed");
        assetManager.openFd("deflatedAsset.xml");
    }

    @Test
    public void openNonAssetShouldOpenRealAssetFromResources() throws IOException {
        InputStream inputStream = assetManager.openNonAsset(0, "res/drawable/an_image.png", 0);
        // expect different sizes in binary vs file resources
        int expectedFileSize = (ShadowAssetManager.useLegacy()) ? 6559 : 5138;
        int bytes = ShadowAssetManagerTest.countBytes(inputStream);
        if ((bytes != 6559) && (bytes != 5138)) {
            Assert.fail(("Expected 5138 or 6559 bytes for image but got " + bytes));
        }
    }

    @Test
    public void openNonAssetShouldOpenFileFromAndroidJar() throws IOException {
        String fileName = "res/raw/fallbackring.ogg";
        if (ShadowAssetManager.useLegacy()) {
            // Not the real full path (it's in .m2/repository), but it only cares about the last folder and file name;
            // retrieves the uncompressed, un-version-qualified file from raw-res/...
            fileName = "jar:" + fileName;
        }
        InputStream inputStream = assetManager.openNonAsset(0, fileName, 0);
        assertThat(ShadowAssetManagerTest.countBytes(inputStream)).isEqualTo(14611);
    }

    @Test
    public void openNonAssetShouldThrowExceptionWhenFileDoesNotExist() throws IOException {
        Assume.assumeTrue(ShadowAssetManager.useLegacy());
        expectedException.expect(IOException.class);
        expectedException.expectMessage("res/drawable/does_not_exist.png");
        assetManager.openNonAsset(0, "res/drawable/does_not_exist.png", 0);
    }

    @Test
    public void unknownResourceIdsShouldReportPackagesSearched() throws IOException {
        Assume.assumeTrue(ShadowAssetManager.useLegacy());
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Resource ID #0xffffffff");
        resources.newTheme().applyStyle((-1), false);
        assetManager.openNonAsset(0, "res/drawable/does_not_exist.png", 0);
    }

    @Test
    public void forSystemResources_unknownResourceIdsShouldReportPackagesSearched() throws IOException {
        if (!(ShadowAssetManager.useLegacy()))
            return;

        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Resource ID #0xffffffff");
        Resources.getSystem().newTheme().applyStyle((-1), false);
        assetManager.openNonAsset(0, "res/drawable/does_not_exist.png", 0);
    }

    @Test
    @Config(qualifiers = "mdpi")
    public void openNonAssetShouldOpenCorrectAssetBasedOnQualifierMdpi() throws IOException {
        if (!(ShadowAssetManager.useLegacy()))
            return;

        InputStream inputStream = assetManager.openNonAsset(0, "res/drawable/robolectric.png", 0);
        assertThat(ShadowAssetManagerTest.countBytes(inputStream)).isEqualTo(8141);
    }

    @Test
    @Config(qualifiers = "hdpi")
    public void openNonAssetShouldOpenCorrectAssetBasedOnQualifierHdpi() throws IOException {
        if (!(ShadowAssetManager.useLegacy()))
            return;

        InputStream inputStream = assetManager.openNonAsset(0, "res/drawable/robolectric.png", 0);
        assertThat(ShadowAssetManagerTest.countBytes(inputStream)).isEqualTo(23447);
    }

    // todo: port to ResourcesTest
    @Test
    public void multiFormatAttributes_integerDecimalValue() {
        AttributeSet attributeSet = Robolectric.buildAttributeSet().addAttribute(multiformat, "16").build();
        TypedArray typedArray = resources.obtainAttributes(attributeSet, new int[]{ multiformat });
        TypedValue outValue = new TypedValue();
        typedArray.getValue(0, outValue);
        assertThat(outValue.type).isEqualTo(TYPE_INT_DEC);
    }

    // todo: port to ResourcesTest
    @Test
    public void multiFormatAttributes_integerHexValue() {
        AttributeSet attributeSet = Robolectric.buildAttributeSet().addAttribute(multiformat, "0x10").build();
        TypedArray typedArray = resources.obtainAttributes(attributeSet, new int[]{ multiformat });
        TypedValue outValue = new TypedValue();
        typedArray.getValue(0, outValue);
        assertThat(outValue.type).isEqualTo(TYPE_INT_HEX);
    }

    // todo: port to ResourcesTest
    @Test
    public void multiFormatAttributes_stringValue() {
        AttributeSet attributeSet = Robolectric.buildAttributeSet().addAttribute(multiformat, "Hello World").build();
        TypedArray typedArray = resources.obtainAttributes(attributeSet, new int[]{ multiformat });
        TypedValue outValue = new TypedValue();
        typedArray.getValue(0, outValue);
        assertThat(outValue.type).isEqualTo(TYPE_STRING);
    }

    // todo: port to ResourcesTest
    @Test
    public void multiFormatAttributes_booleanValue() {
        AttributeSet attributeSet = Robolectric.buildAttributeSet().addAttribute(multiformat, "true").build();
        TypedArray typedArray = resources.obtainAttributes(attributeSet, new int[]{ multiformat });
        TypedValue outValue = new TypedValue();
        typedArray.getValue(0, outValue);
        assertThat(outValue.type).isEqualTo(TYPE_INT_BOOLEAN);
    }

    @Test
    public void attrsToTypedArray_shouldAllowMockedAttributeSets() throws Exception {
        if (!(ShadowAssetManager.useLegacy()))
            return;

        AttributeSet mockAttributeSet = Mockito.mock(AttributeSet.class);
        Mockito.when(mockAttributeSet.getAttributeCount()).thenReturn(1);
        Mockito.when(mockAttributeSet.getAttributeNameResource(0)).thenReturn(windowBackground);
        Mockito.when(mockAttributeSet.getAttributeName(0)).thenReturn("android:windowBackground");
        Mockito.when(mockAttributeSet.getAttributeValue(0)).thenReturn("value");
        resources.obtainAttributes(mockAttributeSet, new int[]{ windowBackground });
    }

    @Test
    public void whenStyleAttrResolutionFails_attrsToTypedArray_returnsNiceErrorMessage() throws Exception {
        if (!(ShadowAssetManager.useLegacy()))
            return;

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(("no value for org.robolectric:attr/styleNotSpecifiedInAnyTheme " + "in theme with applied styles: [Style org.robolectric:Theme.Robolectric (and parents)]"));
        Resources.Theme theme = resources.newTheme();
        theme.applyStyle(Theme_Robolectric, false);
        ShadowAssetManager.legacyShadowOf(assetManager).attrsToTypedArray(resources, Robolectric.buildAttributeSet().setStyleAttribute("?attr/styleNotSpecifiedInAnyTheme").build(), new int[]{ string1 }, 0, getNativePtr(), 0);
    }
}

