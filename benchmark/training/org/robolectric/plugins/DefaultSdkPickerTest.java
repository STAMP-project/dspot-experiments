package org.robolectric.plugins;


import Config.ALL_SDKS;
import Config.NEWEST_SDK;
import Config.OLDEST_SDK;
import Config.TARGET_SDK;
import VERSION_CODES.JELLY_BEAN_MR1;
import VERSION_CODES.JELLY_BEAN_MR2;
import VERSION_CODES.KITKAT;
import VERSION_CODES.LOLLIPOP;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.ConfigUtils;
import org.robolectric.pluginapi.SdkPicker;
import org.robolectric.pluginapi.UsesSdk;


@RunWith(JUnit4.class)
public class DefaultSdkPickerTest {
    private static final int[] sdkInts = new int[]{ 16, 17, 18, 19, 21, 22, 23 };

    private SdkCollection sdkCollection;

    private UsesSdk usesSdk;

    private SdkPicker sdkPicker;

    @Test
    public void withDefaultSdk_shouldUseTargetSdkFromAndroidManifest() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(22);
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder()), usesSdk)).containsExactly(sdkCollection.getSdk(22));
    }

    @Test
    public void withAllSdksConfig_shouldUseFullSdkRangeFromAndroidManifest() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(22);
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(19);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(23);
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(ALL_SDKS)), usesSdk)).containsExactly(sdkCollection.getSdk(19), sdkCollection.getSdk(21), sdkCollection.getSdk(22), sdkCollection.getSdk(23));
    }

    @Test
    public void withAllSdksConfigAndNoMinSdkVersion_shouldUseFullSdkRangeFromAndroidManifest() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(22);
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(1);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(22);
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(ALL_SDKS)), usesSdk)).containsExactly(sdkCollection.getSdk(16), sdkCollection.getSdk(17), sdkCollection.getSdk(18), sdkCollection.getSdk(19), sdkCollection.getSdk(21), sdkCollection.getSdk(22));
    }

    @Test
    public void withAllSdksConfigAndNoMaxSdkVersion_shouldUseFullSdkRangeFromAndroidManifest() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(22);
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(19);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(null);
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(ALL_SDKS)), usesSdk)).containsExactly(sdkCollection.getSdk(19), sdkCollection.getSdk(21), sdkCollection.getSdk(22), sdkCollection.getSdk(23));
    }

    @Test
    public void withMinSdkHigherThanSupportedRange_shouldReturnNone() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(23);
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(1);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(null);
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setMinSdk(24)), usesSdk)).isEmpty();
    }

    @Test
    public void withMinSdkHigherThanMaxSdk_shouldThrowError() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(23);
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(1);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(null);
        try {
            sdkPicker.selectSdks(buildConfig(new Config.Builder().setMinSdk(22).setMaxSdk(21)), usesSdk);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().contains("minSdk may not be larger than maxSdk (minSdk=22, maxSdk=21)");
        }
    }

    @Test
    public void withTargetSdkLessThanMinSdk_shouldThrowError() throws Exception {
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(23);
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(22);
        try {
            sdkPicker.selectSdks(buildConfig(new Config.Builder()), usesSdk);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().contains("Package targetSdkVersion=22 < minSdkVersion=23");
        }
    }

    @Test
    public void withTargetSdkGreaterThanMaxSdk_shouldThrowError() throws Exception {
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(21);
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(22);
        try {
            sdkPicker.selectSdks(buildConfig(new Config.Builder()), usesSdk);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().contains("Package targetSdkVersion=22 > maxSdkVersion=21");
        }
    }

    @Test
    public void shouldClipSdkRangeFromAndroidManifest() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(1);
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(1);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(null);
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder()), usesSdk)).containsExactly(sdkCollection.getSdk(16));
    }

    @Test
    public void withMinSdk_shouldClipSdkRangeFromAndroidManifest() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(22);
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(19);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(23);
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setMinSdk(21)), usesSdk)).containsExactly(sdkCollection.getSdk(21), sdkCollection.getSdk(22), sdkCollection.getSdk(23));
    }

    @Test
    public void withMaxSdk_shouldUseSdkRangeFromAndroidManifest() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(22);
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(19);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(23);
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setMaxSdk(21)), usesSdk)).containsExactly(sdkCollection.getSdk(19), sdkCollection.getSdk(21));
    }

    @Test
    public void withExplicitSdk_selectSdks() throws Exception {
        Mockito.when(usesSdk.getTargetSdkVersion()).thenReturn(21);
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(19);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(22);
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(21)), usesSdk)).containsExactly(sdkCollection.getSdk(21));
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(OLDEST_SDK)), usesSdk)).containsExactly(sdkCollection.getSdk(19));
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(TARGET_SDK)), usesSdk)).containsExactly(sdkCollection.getSdk(21));
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(NEWEST_SDK)), usesSdk)).containsExactly(sdkCollection.getSdk(22));
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(16)), usesSdk)).containsExactly(sdkCollection.getSdk(16));
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(23)), usesSdk)).containsExactly(sdkCollection.getSdk(23));
    }

    @Test
    public void withEnabledSdks_shouldRestrictAsSpecified() throws Exception {
        Mockito.when(usesSdk.getMinSdkVersion()).thenReturn(16);
        Mockito.when(usesSdk.getMaxSdkVersion()).thenReturn(23);
        sdkPicker = new DefaultSdkPicker(sdkCollection, "17,18");
        assertThat(sdkPicker.selectSdks(buildConfig(new Config.Builder().setSdk(ALL_SDKS)), usesSdk)).containsExactly(sdkCollection.getSdk(17), sdkCollection.getSdk(18));
    }

    @Test
    public void shouldParseSdkSpecs() throws Exception {
        assertThat(ConfigUtils.parseSdkArrayProperty("17,18")).asList().containsExactly(JELLY_BEAN_MR1, JELLY_BEAN_MR2);
        assertThat(ConfigUtils.parseSdkArrayProperty("KITKAT, LOLLIPOP")).asList().containsExactly(KITKAT, LOLLIPOP);
    }
}

