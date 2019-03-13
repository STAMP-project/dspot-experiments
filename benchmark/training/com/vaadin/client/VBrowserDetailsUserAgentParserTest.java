package com.vaadin.client;


import com.vaadin.shared.VBrowserDetails;
import org.junit.Assert;
import org.junit.Test;


public class VBrowserDetailsUserAgentParserTest {
    private static final String FIREFOX30_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.0.6) Gecko/2009011913 Firefox/3.0.6";

    private static final String FIREFOX30_LINUX = "Mozilla/5.0 (X11; U; Linux x86_64; es-ES; rv:1.9.0.12) Gecko/2009070811 Ubuntu/9.04 (jaunty) Firefox/3.0.12";

    private static final String FIREFOX33_ANDROID = "Mozilla/5.0 (Android; Tablet; rv:33.0) Gecko/33.0 Firefox/33.0";

    private static final String FIREFOX57_ANDROID = "Mozilla/5.0 (Android 8.1.0; Mobile; rv:57.0) Gecko/57.0 Firefox/57.0";

    private static final String FIREFOX35_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.8) Gecko/20100202 Firefox/3.5.8 (.NET CLR 3.5.30729) FirePHP/0.4";

    private static final String FIREFOX36_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6 (.NET CLR 3.5.30729)";

    private static final String FIREFOX36B_MAC = "UAString mozilla/5.0 (macintosh; u; intel mac os x 10.6; en-us; rv:1.9.2) gecko/20100115 firefox/3.6";

    private static final String FIREFOX_30B5_MAC = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9b5) Gecko/2008032619 Firefox/3.0b5";

    private static final String FIREFOX_40B7_WIN = "Mozilla/5.0 (Windows NT 5.1; rv:2.0b7) Gecko/20100101 Firefox/4.0b7";

    private static final String FIREFOX_40B11_WIN = "Mozilla/5.0 (Windows NT 5.1; rv:2.0b11) Gecko/20100101 Firefox/4.0b11";

    private static final String FIREFOX_SUPPORTED = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:45.0) Gecko/20100101 Firefox/45.0";

    private static final String KONQUEROR_LINUX = "Mozilla/5.0 (compatible; Konqueror/3.5; Linux) KHTML/3.5.5 (like Gecko) (Exabot-Thumbnails)";

    private static final String IE11_WINDOWS_7 = "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; rv:11.0) like Gecko";

    private static final String IE11_WINDOWS_7_COMPATIBILITY_VIEW_IE7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)";

    private static final String IE11_WINDOWS_10_COMPATIBILITY_VIEW_IE7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E)";

    private static final String IE11_INITIAL_WINDOWS_10_COMPATIBILITY_VIEW_IE7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 10.0; WOW64; Trident/8.0; .NET4.0C; .NET4.0E)";

    private static final String IE11_WINDOWS_PHONE_8_1_UPDATE = "Mozilla/5.0 (Mobile; Windows Phone 8.1; Android 4.0; ARM; Trident/7.0; Touch; rv:11.0; IEMobile/11.0; NOKIA; Lumia 920) Like iPhone OS 7_0_3 Mac OS X AppleWebKit/537 (KHTML, like Gecko) Mobile Safari/537";

    // "Version/" was added in 10.00
    private static final String OPERA964_WINDOWS = "Opera/9.64(Windows NT 5.1; U; en) Presto/2.1.1";

    private static final String OPERA1010_WINDOWS = "Opera/9.80 (Windows NT 5.1; U; en) Presto/2.2.15 Version/10.10";

    private static final String OPERA1050_WINDOWS = "Opera/9.80 (Windows NT 5.1; U; en) Presto/2.5.22 Version/10.50";

    private static final String CHROME3_MAC = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-US) AppleWebKit/532.0 (KHTML, like Gecko) Chrome/3.0.198 Safari/532.0";

    private static final String CHROME4_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/532.5 (KHTML, like Gecko) Chrome/4.0.249.89 Safari/532.5";

    private static final String SAFARI3_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 5.1; cs-CZ) AppleWebKit/525.28.3 (KHTML, like Gecko) Version/3.2.3 Safari/525.29";

    private static final String SAFARI4_MAC = "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_8; en-us) AppleWebKit/531.22.7 (KHTML, like Gecko) Version/4.0.5 Safari/531.22.7";

    private static final String IPHONE_IOS_5_1 = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B179 Safari/7534.48.3";

    private static final String IPHONE_IOS_4_0 = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";

    private static final String IPAD_IOS_4_3_1 = "Mozilla/5.0 (iPad; U; CPU OS 4_3_1 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8G4 Safari/6533.18.5";

    private static final String IPHONE_IOS9_1_FIREFOX = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) FxiOS/8.3b5826 Mobile/13B143 Safari/601.1.46";

    private static final String IPHONE_IOS9_1_SAFARI = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1";

    private static final String IPHONE_IOS9_1_CHROME = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1 (KHTML, like Gecko) CriOS/63.0.3239.73 Mobile/13B143 Safari/601.1.46";

    // application on the home screen, without Safari in user agent
    private static final String IPHONE_IOS_6_1_HOMESCREEN_SIMULATOR = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_1 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Mobile/10B141";

    private static final String ANDROID_HTC_2_1 = "Mozilla/5.0 (Linux; U; Android 2.1-update1; en-us; ADR6300 Build/ERE27) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17";

    private static final String ANDROID_GOOGLE_NEXUS_2_2 = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

    private static final String ANDROID_MOTOROLA_3_0 = "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13";

    private static final String ANDROID_GALAXY_NEXUS_4_0_4_CHROME = "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19";

    private static final String EDGE_WINDOWS_10 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.10240";

    private static final String PHANTOMJS_211_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1";

    private static final String CHROME_57_ON_IOS_10_3_1 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) CriOS/57.0.2987.137 Mobile/14E304 Safari/602.1";

    private static final String CHROME_40_ON_CHROMEOS = "Mozilla/5.0 (X11; CrOS x86_64 6457.31.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36";

    @Test
    public void testSafari3() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.SAFARI3_WINDOWS);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 2);
        assertBrowserVersion(bd, "3.2.3");
        assertEngineVersion(bd, 525.0F);
        assertWindows(bd);
    }

    @Test
    public void testSafari4() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.SAFARI4_MAC);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "4.0.5");
        assertEngineVersion(bd, 531.0F);
        assertMacOSX(bd);
    }

    @Test
    public void testIPhoneIOS6Homescreen() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IPHONE_IOS_6_1_HOMESCREEN_SIMULATOR);
        assertWebKit(bd);
        // not identified as Safari, no browser version available
        // assertSafari(bd);
        // assertBrowserMajorVersion(bd, 6);
        // assertBrowserMinorVersion(bd, 1);
        assertEngineVersion(bd, 536.0F);
        assertIOS(bd, 6, 1);
        assertIPhone(bd);
    }

    @Test
    public void testIPhoneIOS5() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IPHONE_IOS_5_1);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 5);
        assertBrowserMinorVersion(bd, 1);
        assertBrowserVersion(bd, "5.1");
        assertEngineVersion(bd, 534.0F);
        assertIOS(bd, 5, 1);
        assertIPhone(bd);
    }

    @Test
    public void testIPhoneIOS4() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IPHONE_IOS_4_0);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "4.0.5");
        assertEngineVersion(bd, 532.0F);
        assertIOS(bd, 4, 0);
        assertIPhone(bd);
    }

    @Test
    public void testIPadIOS4() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IPAD_IOS_4_3_1);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 5);
        assertBrowserMinorVersion(bd, 0);
        assertEngineVersion(bd, 533.0F);
        assertIOS(bd, 4, 3);
        assertIPad(bd);
    }

    @Test
    public void testAndroid21() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.ANDROID_HTC_2_1);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "4.0");
        assertEngineVersion(bd, 530.0F);
        assertAndroid(bd, 2, 1);
    }

    @Test
    public void testAndroid22() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.ANDROID_GOOGLE_NEXUS_2_2);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "4.0");
        assertEngineVersion(bd, 533.0F);
        assertAndroid(bd, 2, 2);
    }

    @Test
    public void testAndroid30() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.ANDROID_MOTOROLA_3_0);
        assertWebKit(bd);
        assertSafari(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "4.0");
        assertEngineVersion(bd, 534.0F);
        assertAndroid(bd, 3, 0);
    }

    @Test
    public void testAndroid40Chrome() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.ANDROID_GALAXY_NEXUS_4_0_4_CHROME);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 18);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "18.0.1025.133");
        assertEngineVersion(bd, 535.0F);
        assertAndroid(bd, 4, 0);
    }

    @Test
    public void testChrome3() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.CHROME3_MAC);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "3.0.198");
        assertEngineVersion(bd, 532.0F);
        assertMacOSX(bd);
    }

    @Test
    public void testChrome4() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.CHROME4_WINDOWS);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "4.0.249.89");
        assertEngineVersion(bd, 532.0F);
        assertWindows(bd);
    }

    @Test
    public void testChromeChromeOS() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.CHROME_40_ON_CHROMEOS);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 40);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "40.0.2214.38");
        assertEngineVersion(bd, 537.0F);
        assertChromeOS(bd);
    }

    @Test
    public void testChromeIOS() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.CHROME_57_ON_IOS_10_3_1);
        assertWebKit(bd);
        assertChrome(bd);
        assertBrowserMajorVersion(bd, 57);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "57.0.2987.137");
        assertEngineVersion(bd, 602.0F);
        assertIOS(bd, 10, 3);
    }

    @Test
    public void testFirefox3() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX30_WINDOWS);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "3.0.6");
        assertEngineVersion(bd, 1.9F);
        assertWindows(bd);
        bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX30_LINUX);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "3.0.12");
        assertEngineVersion(bd, 1.9F);
        assertLinux(bd);
    }

    @Test
    public void testFirefox33Android() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX33_ANDROID);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 33);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "33.0");
        assertAndroid(bd, (-1), (-1));
    }

    @Test
    public void testFirefox57Android() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX57_ANDROID);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 57);
        assertBrowserMinorVersion(bd, 0);
        assertAndroid(bd, 8, 1);
    }

    @Test
    public void testFirefox35() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX35_WINDOWS);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 5);
        assertBrowserVersion(bd, "3.5.8");
        assertEngineVersion(bd, 1.9F);
        assertWindows(bd);
    }

    @Test
    public void testFirefox36() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX36_WINDOWS);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 6);
        assertBrowserVersion(bd, "3.6");
        assertEngineVersion(bd, 1.9F);
        assertWindows(bd);
    }

    @Test
    public void testFirefox30b5() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX_30B5_MAC);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "3.0b5");
        assertEngineVersion(bd, 1.9F);
        assertMacOSX(bd);
    }

    @Test
    public void testFirefox40b11() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX_40B11_WIN);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "4.0b11");
        assertEngineVersion(bd, 2.0F);
        assertWindows(bd);
    }

    @Test
    public void testFirefox40b7() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX_40B7_WIN);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 4);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "4.0b7");
        assertEngineVersion(bd, 2.0F);
        assertWindows(bd);
    }

    @Test
    public void testKonquerorLinux() {
        // Just ensure detection does not crash
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.KONQUEROR_LINUX);
        assertLinux(bd);
    }

    @Test
    public void testFirefox36b() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX36B_MAC);
        assertGecko(bd);
        assertFirefox(bd);
        assertBrowserMajorVersion(bd, 3);
        assertBrowserMinorVersion(bd, 6);
        assertBrowserVersion(bd, "3.6");
        assertEngineVersion(bd, 1.9F);
        assertMacOSX(bd);
    }

    @Test
    public void testOpera964() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.OPERA964_WINDOWS);
        assertPresto(bd);
        assertOpera(bd);
        assertBrowserMajorVersion(bd, 9);
        assertBrowserMinorVersion(bd, 64);
        assertBrowserVersion(bd, "9.64");
        assertWindows(bd);
    }

    @Test
    public void testOpera1010() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.OPERA1010_WINDOWS);
        assertPresto(bd);
        assertOpera(bd);
        assertBrowserMajorVersion(bd, 10);
        assertBrowserMinorVersion(bd, 10);
        assertBrowserVersion(bd, "10.10");
        assertWindows(bd);
    }

    @Test
    public void testOpera1050() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.OPERA1050_WINDOWS);
        assertPresto(bd);
        assertOpera(bd);
        assertBrowserMajorVersion(bd, 10);
        assertBrowserMinorVersion(bd, 50);
        assertBrowserVersion(bd, "10.50");
        assertWindows(bd);
    }

    @Test
    public void testIE11() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IE11_WINDOWS_7);
        assertTrident(bd);
        assertEngineVersion(bd, 7);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 11);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "11.0");
        assertWindows(bd);
    }

    @Test
    public void testIE11Windows7CompatibilityViewIE7() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IE11_WINDOWS_7_COMPATIBILITY_VIEW_IE7);
        assertTrident(bd);
        assertEngineVersion(bd, 7);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 11);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "11.0");
        assertWindows(bd);
    }

    @Test
    public void testIE11Windows10CompatibilityViewIE7() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IE11_WINDOWS_10_COMPATIBILITY_VIEW_IE7);
        assertTrident(bd);
        assertEngineVersion(bd, 7);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 11);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "11.0");
        assertWindows(bd);
    }

    @Test
    public void testIE11InitialWindows10CompatibilityViewIE7() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IE11_INITIAL_WINDOWS_10_COMPATIBILITY_VIEW_IE7);
        assertTrident(bd);
        assertEngineVersion(bd, 7);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 11);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "11.0");
        assertWindows(bd);
    }

    @Test
    public void testIE11WindowsPhone81Update() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IE11_WINDOWS_PHONE_8_1_UPDATE);
        assertTrident(bd);
        assertEngineVersion(bd, 7);
        assertIE(bd);
        assertBrowserMajorVersion(bd, 11);
        assertBrowserMinorVersion(bd, 0);
        assertBrowserVersion(bd, "11.0");
        assertWindows(bd, true);
    }

    @Test
    public void testEdgeWindows10() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.EDGE_WINDOWS_10);
        assertEdge(bd);
        assertBrowserMajorVersion(bd, 12);
        assertBrowserMinorVersion(bd, 10240);
        assertBrowserVersion(bd, "12.10240");
        assertWindows(bd, false);
    }

    @Test
    public void testPhantomJs211() {
        VBrowserDetails bd = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.PHANTOMJS_211_MAC);
        assertPhantomJS(bd);
        assertWebKit(bd);
        assertBrowserMajorVersion(bd, 2);
        assertBrowserMinorVersion(bd, 1);
        assertBrowserVersion(bd, "2.1.1");
        assertMacOSX(bd);
    }

    @Test
    public void checkFFsupportedVersions() {
        VBrowserDetails details = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX_40B11_WIN);
        Assert.assertTrue(details.isTooOldToFunctionProperly());
        details = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.FIREFOX_SUPPORTED);
        Assert.assertFalse(details.isTooOldToFunctionProperly());
    }

    @Test
    public void testFirefoxIos() {
        VBrowserDetails details = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IPHONE_IOS9_1_FIREFOX);
        assertFirefox(details);
        assertBrowserMajorVersion(details, 8);
        assertBrowserMinorVersion(details, 3);
        assertBrowserVersion(details, "8.3b5826");
        assertWebKit(details);
        assertEngineVersion(details, 601);
    }

    @Test
    public void testSafariIos() {
        VBrowserDetails details = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IPHONE_IOS9_1_SAFARI);
        assertSafari(details);
        assertBrowserMajorVersion(details, 9);
        assertBrowserMinorVersion(details, 0);
        assertBrowserVersion(details, "9.0");
        assertWebKit(details);
        assertEngineVersion(details, 601);
    }

    @Test
    public void testChromeIos() {
        VBrowserDetails details = new VBrowserDetails(VBrowserDetailsUserAgentParserTest.IPHONE_IOS9_1_CHROME);
        assertChrome(details);
        assertBrowserMajorVersion(details, 63);
        assertBrowserMinorVersion(details, 0);
        assertBrowserVersion(details, "63.0.3239.73");
        assertWebKit(details);
        assertEngineVersion(details, 601);
    }
}

