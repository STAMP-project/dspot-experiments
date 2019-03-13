package com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser;


import ITag.AUDIO_128K_AAC;
import ITag.AVC;
import ITag.VIDEO_1080P_AVC;
import ITag.VIDEO_1080P_WEBM;
import ITag.VIDEO_720P_AVC;
import ITag.WEBM;
import android.net.Uri;
import com.liskovsoft.sharedutils.TestHelpers;
import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.smartyoutubetv.BuildConfig;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.main.YouTubeSubParser;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.main.YouTubeSubParser.Subtitle;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.misc.ITag;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.misc.SimpleYouTubeMediaItem;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.mpd.MPDBuilder;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.mpd.SimpleMPDBuilder;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.tmp.CipherUtils;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.tmp.SimpleYouTubeInfoParser;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.tmp.YouTubeInfoParser;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Fixing unable to find manifest errors.
 * You have to set working directory to <code>$MODULE_DIR$</code>. <a href="http://robolectric.org/getting-started/">More info</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SimpleYouTubeInfoParserTest {
    private InputStream mYouTubeVideoInfo;

    private InputStream mYouTubeVideoInfoCiphered;

    private static final String sVideo = "https://r1---sn-4gxb5u-qo3s.googlevideo" + (((((("" + ".com/videoplayback?itag=137&clen=238313161&key=yt6&ipbits=0&initcwndbps=2827500&keepalive=yes&dur=605.433&ei=ktyIWc3fOdjrddjziZAP") + "&pcm2cms=yes&gir=yes&mt=1502141501&lmt=1502028605932130&sparams=clen%2Cdur%2Cei%2Cgir%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag") + "%2Ckeepalive%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Crequiressl%2Csource%2Cexpire&mv=m&signature") + "=DA2563BC19D0D81235FD450E4AF43FFA6CE84716.8F2CD08915FAD1A54CE0FB3CE83ABBCA7B022BFE&ms=au&id=o-AFEGaouCLpUXwcdQz") + "-S8ywonKslAFmkDIXB3kbbPi18i&expire=1502163187&mime=video%2Fmp4&ip=46.98.75.93&requiressl=yes&pl=16&mn=sn-4gxb5u-qo3s&mm=31&source") + "=youtube");

    private static final String sAudio = "https://r1---sn-4gxb5u-qo3s.googlevideo" + (((((".com/videoplayback?itag=140&clen=9617577&key=yt6&ipbits=0&initcwndbps=2827500&keepalive=yes&dur=605.506&ei=ktyIWc3fOdjrddjziZAP" + "&pcm2cms=yes&gir=yes&mt=1502141501&lmt=1502028596840916&sparams=clen%2Cdur%2Cei%2Cgir%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag") + "%2Ckeepalive%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Crequiressl%2Csource%2Cexpire&mv=m&signature") + "=801EE6B5D7D5EE28C0FFA35A5D1F1916EB3C226C.72D7E1A145D1D8837F6BFF6F964E41815DB4C44C&ms=au&id=o-AFEGaouCLpUXwcdQz") + "-S8ywonKslAFmkDIXB3kbbPi18i&expire=1502163187&mime=audio%2Fmp4&ip=46.98.75.93&requiressl=yes&pl=16&mn=sn-4gxb5u-qo3s&mm=31&source") + "=youtube");

    private static final String sVideoDeciphered = "https://r3---sn-4gxb5u-qo3s.googlevideo" + (((((((((("" + "") + ".com/videoplayback?sparams=clen%2Cdur%2Cei%2Cgir%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Ckeepalive%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv") + "%2Cpcm2cms%2Cpl%2Crequiressl%2Csource%2Cexpire&gir=yes&mm=31&mn=sn-4gxb5u-qo3s&key=yt6&clen=49431326&ei=SqWIWZXJC4SYNJrOqLgF&ms=au") + "&mt") + "=1502127302&pl=16&mv=m&requiressl=yes&lmt=1500318989274570&itag=137&keepalive=yes&ip=46.98.75.93&dur=277.320&pcm2cms=yes") + "&source") + "=youtube&id=o-AD0tcX8q3azwHnG4ymA9SxyeTUWpSh6f9gQa1zSD2vmU&mime=video%2Fmp4&expire=1502149034&ipbits=0&initcwndbps=2915000") + "") + "&signature") + "=8C85C198F11DA24D5964D412FA4487AA09E27A9C.893CF7AD1F333DF80C481AC9186D768667511E71");

    @Test
    public void tryToExtractSomeUrls() throws Exception {
        YouTubeInfoParser parser = new SimpleYouTubeInfoParser(mYouTubeVideoInfo);
        Uri url = parser.getUrlByTag(VIDEO_1080P_AVC);
        Assert.assertTrue(url.equals(Uri.parse(SimpleYouTubeInfoParserTest.sVideo)));
        Uri url2 = parser.getUrlByTag(AUDIO_128K_AAC);
        Assert.assertTrue(url2.equals(Uri.parse(SimpleYouTubeInfoParserTest.sAudio)));
    }

    @Test
    public void tryToExtractCipheredUrl() {
        YouTubeInfoParser parser = new SimpleYouTubeInfoParser(mYouTubeVideoInfoCiphered);
        Uri url = parser.getUrlByTag(VIDEO_1080P_AVC);
        Assert.assertTrue(url.equals(Uri.parse(SimpleYouTubeInfoParserTest.sVideoDeciphered)));
    }

    @Test
    public void decipherTest() {
        String originSig = "98C85C188F11DA24D5964D412FA4487AE09127A9C.893CF7AD1FA33DF80C481AC9186D768667511E7E7A73";
        String newSig = "8C85C198F11DA24D5964D412FA4487AA09E27A9C.893CF7AD1F333DF80C481AC9186D768667511E71";
        Assert.assertEquals(newSig, CipherUtils.decipherSignature(originSig));
    }

    @Test
    public void tryToExtractJSDecipherCode() {
        InputStream is = TestHelpers.openResource("tv-player.js");
        String result = "var EQ={F2:function(a,b){a.splice(0,b)},\n" + (((("Xy:function(a,b){var c=a[0];a[0]=a[b%a.length];a[b]=c},\n" + "LN:function") + "(a){a.reverse()}};\n") + "function decipherSignature(a){a=a.split(\"\");EQ.LN(a,23);EQ.F2(a,2);EQ.Xy(a,1);EQ.F2(a,3);EQ.Xy(a,11);EQ.Xy(a,9);EQ.F2") + "(a,2);return a.join(\"\")}");
        String jsCode = com.liskovsoft.smartyoutubetv.flavors.exoplayer.youtubeinfoparser.misc.CipherUtils.extractDecipherCode(is);
        Assert.assertEquals(result, jsCode);
    }

    @Test
    public void testTypeMatcher() {
        Assert.assertTrue(ITag.belongsToType(AVC, VIDEO_1080P_AVC));
        Assert.assertTrue(ITag.belongsToType(WEBM, VIDEO_1080P_WEBM));
        Assert.assertFalse(ITag.belongsToType(WEBM, VIDEO_720P_AVC));
    }

    @Test
    public void testHelpers() {
        String sampleString = "<xml>\nHello\nWorld\n</xml>";
        byte[] utf8s = sampleString.getBytes(Charset.forName("UTF8"));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(utf8s);
        Assert.assertEquals(sampleString, Helpers.toString(byteArrayInputStream));
    }

    @Test
    public void mpdBuilderTest2() {
        InputStream oneItem = TestHelpers.openResource("mpd_with_one_item");
        SimpleYouTubeMediaItem fakeItem = prepareFakeVideoItem();
        SimpleMPDBuilder fakeBuilder = new SimpleMPDBuilder();
        fakeBuilder.append(fakeItem);
        Assert.assertEquals(Helpers.toString(oneItem), Helpers.toString(fakeBuilder.build()));
    }

    @Test
    public void getAllSubsTest() {
        String content = TestHelpers.readResource("get_video_info_subs");
        YouTubeSubParser parser = new YouTubeSubParser(content);
        List<Subtitle> allSubs = parser.extractAllSubs();
        String formatKey = "fmt=vtt";
        String expected = ("https://www.youtube.com/api/timedtext?caps=&key=yttt1&expire=1515741851&v=WS7f5xpGYn8&hl=en_US&signature" + (("=1774F7B2CF8A652145BBED85C33EB92DD8186388.27F90A8C8C2B38844AC89AF3E62F96DDDF3471A4&xorp=True&sparams=caps%2Cv%2Cxorp%2Cexpire&lang" + "=en&name=en") + "&")) + formatKey;
        Assert.assertEquals(expected, allSubs.get(0).getBaseUrl());
    }

    @Test
    public void addSubsToMpdTest() {
        String content = TestHelpers.readResource("get_video_info_subs");
        YouTubeSubParser parser = new YouTubeSubParser(content);
        List<Subtitle> allSubs = parser.extractAllSubs();
        MPDBuilder builder = new SimpleMPDBuilder();
        builder.append(allSubs);
        builder.append(prepareFakeVideoItem());
        Assert.assertEquals(TestHelpers.readResource("mpd_with_one_sub2"), TestHelpers.readStream(builder.build()));
    }
}

