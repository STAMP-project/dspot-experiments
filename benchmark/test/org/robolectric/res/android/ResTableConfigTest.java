package org.robolectric.res.android;


import ResTable_config.SCREENLAYOUT_LAYOUTDIR_LTR;
import ResTable_config.SCREENLAYOUT_LAYOUTDIR_RTL;
import ResTable_config.kEnglish;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ResTableConfigTest {
    public static final int MCC_US_CARRIER = 310;

    public static final int MCC_US_VERIZON = 4;

    public static final byte[] LANGUAGE_FRENCH = new byte[]{ 'f', 'r' };

    private static final byte[] LANGUAGE_SPANISH = new byte[]{ 'e', 's' };

    @Test
    public void isBetterThan_emptyConfig() {
        // When a configuration is not specified the result is always false
        assertThat(ResTableConfigTest.newBuilder().build().isBetterThan(ResTableConfigTest.newBuilder().build(), ResTableConfigTest.newBuilder().build())).isFalse();
    }

    /**
     * https://developer.android.com/guide/topics/resources/providing-resources.html#MccQualifier
     *
     * @see <a href="http://mcc-mnc.com/">http://mcc-mnc.com/</a>
     */
    @Test
    public void isBetterThan_mcc() {
        // When requested is less of a match
        assertThat(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).build().isBetterThan(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).build(), ResTableConfigTest.newBuilder().build())).isFalse();
        // When requested is a better match
        assertThat(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).build().isBetterThan(ResTableConfigTest.newBuilder().build(), ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).build())).isTrue();
    }

    /**
     * https://developer.android.com/guide/topics/resources/providing-resources.html#MccQualifier
     *
     * @see <a href="http://mcc-mnc.com/">http://mcc-mnc.com/</a>
     */
    @Test
    public void isBetterThan_mnc() {
        // When a configuration is not specified the result is always false
        assertThat(ResTableConfigTest.newBuilder().build().isBetterThan(ResTableConfigTest.newBuilder().build(), ResTableConfigTest.newBuilder().build())).isFalse();
        // When requested is less of a match
        assertThat(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).setMnc(ResTableConfigTest.MCC_US_VERIZON).build().isBetterThan(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).build(), ResTableConfigTest.newBuilder().build())).isFalse();
        // When requested is a better match - any US Carrier is a better match to US + Verizon
        assertThat(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).setMnc(ResTableConfigTest.MCC_US_VERIZON).build().isBetterThan(ResTableConfigTest.newBuilder().build(), ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).build())).isTrue();
        // When requested is a better match - any US Carrier is a better match to US + Verizon
        assertThat(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).setMnc(ResTableConfigTest.MCC_US_VERIZON).build().isBetterThan(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).build(), ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).setMnc(ResTableConfigTest.MCC_US_VERIZON).build())).isTrue();
        // When requested is a better match - any US Carrier is not a better match to US + Verizon
        assertThat(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).setMnc(ResTableConfigTest.MCC_US_VERIZON).build().isBetterThan(ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).setMnc(ResTableConfigTest.MCC_US_VERIZON).build(), ResTableConfigTest.newBuilder().setMcc(ResTableConfigTest.MCC_US_CARRIER).build())).isFalse();
    }

    @Test
    public void isBetterThan_language() {
        // When requested has no language, is not a better match
        assertThat(ResTableConfigTest.newBuilder().setLanguage(ResTableConfigTest.LANGUAGE_FRENCH).build().isBetterThan(ResTableConfigTest.newBuilder().setLanguage(ResTableConfigTest.LANGUAGE_FRENCH).build(), ResTableConfigTest.newBuilder().build())).isFalse();
    }

    @Test
    public void isBetterThan_language_comparedNotSame_requestedEnglish() {
        // When requested has no language, is not a better match
        assertThat(ResTableConfigTest.newBuilder().setLanguage(ResTableConfigTest.LANGUAGE_FRENCH).build().isBetterThan(ResTableConfigTest.newBuilder().setLanguage(ResTableConfigTest.LANGUAGE_SPANISH).build(), ResTableConfigTest.newBuilder().setLanguage(kEnglish).build())).isTrue();
    }

    @Test
    public void isBetterThan_language_comparedNotSame_requestedEnglishUS() {
        // When requested has no language, is not a better match
        assertThat(ResTableConfigTest.newBuilder().setLanguage(ResTableConfigTest.LANGUAGE_FRENCH).build().isBetterThan(ResTableConfigTest.newBuilder().setLanguage(ResTableConfigTest.LANGUAGE_SPANISH).build(), ResTableConfigTest.newBuilder().setLanguage(kEnglish).build())).isTrue();
    }

    @Test
    public void isBetterThan_layoutDirection_() {
        // Requested matches this configuration
        assertThat(ResTableConfigTest.newBuilder().setLayoutDirection(SCREENLAYOUT_LAYOUTDIR_RTL).build().isBetterThan(ResTableConfigTest.newBuilder().setLayoutDirection(SCREENLAYOUT_LAYOUTDIR_LTR).build(), ResTableConfigTest.newBuilder().setLayoutDirection(SCREENLAYOUT_LAYOUTDIR_RTL).build())).isTrue();
        // Requested matches this configuration
        assertThat(ResTableConfigTest.newBuilder().setLayoutDirection(SCREENLAYOUT_LAYOUTDIR_LTR).build().isBetterThan(ResTableConfigTest.newBuilder().setLayoutDirection(SCREENLAYOUT_LAYOUTDIR_RTL).build(), ResTableConfigTest.newBuilder().setLayoutDirection(SCREENLAYOUT_LAYOUTDIR_RTL).build())).isFalse();
    }

    private static class ResTableConfigBuilder {
        int mcc;

        int mnc;

        byte[] language = new byte[2];

        byte[] region = new byte[2];

        int orientation;

        int touchscreen;

        int density;

        int keyboard;

        int navigation;

        int inputFlags;

        int screenWidth;

        int screenHeight;

        int sdkVersion;

        int minorVersion;

        int screenLayout;

        int uiMode;

        int smallestScreenWidthDp;

        int screenWidthDp;

        int screenHeightDp;

        byte[] localeScript = new byte[4];

        byte[] localeVariant = new byte[8];

        byte screenLayout2;

        byte screenConfigPad1;

        short screenConfigPad2;

        ResTable_config build() {
            return new ResTable_config(0, mcc, mnc, language, region, orientation, touchscreen, density, keyboard, navigation, inputFlags, screenWidth, screenHeight, sdkVersion, minorVersion, screenLayout, uiMode, smallestScreenWidthDp, screenWidthDp, screenHeightDp, localeScript, localeVariant, screenLayout2, screenConfigPad1, screenConfigPad2, null);
        }

        public ResTableConfigTest.ResTableConfigBuilder setMcc(int mcc) {
            this.mcc = mcc;
            return this;
        }

        public ResTableConfigTest.ResTableConfigBuilder setMnc(int mnc) {
            this.mnc = mnc;
            return this;
        }

        public ResTableConfigTest.ResTableConfigBuilder setLanguage(byte[] language) {
            this.language = language;
            return this;
        }

        public ResTableConfigTest.ResTableConfigBuilder setLayoutDirection(int layoutDirection) {
            screenLayout = layoutDirection;
            return this;
        }
    }
}

