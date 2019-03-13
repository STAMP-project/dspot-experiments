/**
 * Copyright 2009, Mahmood Ali.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 *
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following disclaimer
 *      in the documentation and/or other materials provided with the
 *      distribution.
 *    * Neither the name of Mahmood Ali. nor the names of its
 *      contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.notnoop.apns;


import com.notnoop.apns.internal.Utilities;
import java.util.Arrays;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class PayloadBuilderTest {
    @Test
    public void testEmpty() {
        final PayloadBuilder builder = new PayloadBuilder();
        final String expected = "{\"aps\":{}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void testOneAps() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("test");
        final String expected = "{\"aps\":{\"alert\":\"test\"}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void testTwoAps() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("test");
        builder.badge(9);
        final String expected = "{\"aps\":{\"alert\":\"test\",\"badge\":9}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void testSafariAps() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("test");
        builder.alertTitle("Test Title");
        builder.actionKey("View");
        builder.urlArgs("arg1", "arg2", "arg3");
        final String expected = "{\"aps\":{\"alert\":{\"body\":\"test\",\"title\":\"Test Title\",\"action-loc-key\":\"View\"},\"url-args\":[\"arg1\",\"arg2\",\"arg3\"]}}";
        assertEqualsJson(expected, builder.build());
    }

    @Test
    public void testTwoApsMultipleBuilds() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("test");
        builder.badge(9);
        final String expected = "{\"aps\":{\"alert\":\"test\",\"badge\":9}}";
        assertEqualsJson(expected, builder.build());
        assertEqualsJson(expected, builder.build());
    }

    @Test
    public void testIncludeBadge() {
        final String badge0 = APNS.newPayload().badge(0).toString();
        final String badgeNo = APNS.newPayload().clearBadge().toString();
        final String expected = "{\"aps\":{\"badge\":0}}";
        assertEqualsJson(expected, badge0);
        assertEqualsJson(expected, badgeNo);
    }

    @Test
    public void localizedTitleKeyAndArguments() {
        final PayloadBuilder builder = new PayloadBuilder().localizedTitleKey("GAME_PLAY_REQUEST_FORMAT").localizedTitleArguments("Jenna", "Frank");
        builder.sound("chime");
        final String expected = "{\"aps\":{\"sound\":\"chime\",\"alert\":{\"title-loc-key\":\"GAME_PLAY_REQUEST_FORMAT\",\"title-loc-args\":[\"Jenna\",\"Frank\"]}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void localizedOneWithArray() {
        final PayloadBuilder builder = new PayloadBuilder().localizedKey("GAME_PLAY_REQUEST_FORMAT").localizedArguments("Jenna", "Frank");
        builder.sound("chime");
        final String expected = "{\"aps\":{\"sound\":\"chime\",\"alert\":{\"loc-key\":\"GAME_PLAY_REQUEST_FORMAT\",\"loc-args\":[\"Jenna\",\"Frank\"]}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void localizedOneWithVarargs() {
        final PayloadBuilder builder = new PayloadBuilder().localizedKey("GAME_PLAY_REQUEST_FORMAT").localizedArguments("Jenna", "Frank");
        builder.sound("chime");
        final String expected = "{\"aps\":{\"sound\":\"chime\",\"alert\":{\"loc-key\":\"GAME_PLAY_REQUEST_FORMAT\",\"loc-args\":[\"Jenna\",\"Frank\"]}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void localizedTwo() {
        final PayloadBuilder builder = new PayloadBuilder().sound("chime").localizedKey("GAME_PLAY_REQUEST_FORMAT").localizedArguments("Jenna", "Frank");
        final String expected = "{\"aps\":{\"sound\":\"chime\",\"alert\":{\"loc-key\":\"GAME_PLAY_REQUEST_FORMAT\",\"loc-args\":[\"Jenna\",\"Frank\"]}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void customFieldSimple() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("test");
        builder.customField("ache1", "what");
        builder.customField("ache2", 2);
        final String expected = "{\"ache1\":\"what\",\"ache2\":2,\"aps\":{\"alert\":\"test\"}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void customFieldArray() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("test");
        builder.customField("ache1", Arrays.asList("a1", "a2"));
        builder.customField("ache2", new int[]{ 1, 2 });
        final String expected = "{\"ache1\":[\"a1\",\"a2\"],\"ache2\":[1,2],\"aps\":{\"alert\":\"test\"}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void customBody() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("what").actionKey("Cancel");
        final String expected = "{\"aps\":{\"alert\":{\"action-loc-key\":\"Cancel\",\"body\":\"what\"}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void multipleBuildCallsWithCustomBody() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("what").actionKey("Cancel");
        final String expected = "{\"aps\":{\"alert\":{\"action-loc-key\":\"Cancel\",\"body\":\"what\"}}}";
        assertEqualsJson(expected, builder.build());
        assertEqualsJson(expected, builder.build());
    }

    @Test
    public void customBodyReverseOrder() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.actionKey("Cancel").alertBody("what");
        final String expected = "{\"aps\":{\"alert\":{\"action-loc-key\":\"Cancel\",\"body\":\"what\"}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void alertNoView() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.actionKey(null).alertBody("what");
        final String expected = "{\"aps\":{\"alert\":{\"action-loc-key\":null,\"body\":\"what\"}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void alertNoViewSimpler() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.noActionButton().alertBody("what");
        final String expected = "{\"aps\":{\"alert\":{\"action-loc-key\":null,\"body\":\"what\"}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void alertWithImageOnly() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.launchImage("/test");
        final String expected = "{\"aps\":{\"alert\":{\"launch-image\":\"/test\"}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void alertWithImageAndText() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.launchImage("/test").alertBody("hello");
        final String expected = "{\"aps\":{\"alert\":{\"launch-image\":\"/test\",\"body\":\"hello\"}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void emptyApsWithFields() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.customField("achme2", new int[]{ 5, 8 });
        final String expected = "{\"achme2\":[5,8],\"aps\":{}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void abitComplicated() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.customField("achme", "foo");
        builder.sound("chime");
        builder.localizedKey("GAME_PLAY_REQUEST_FORMAT").localizedArguments("Jenna", "Frank");
        final String expected = "{\"achme\":\"foo\",\"aps\":{\"sound\":\"chime\",\"alert\":{\"loc-key\":\"GAME_PLAY_REQUEST_FORMAT\",\"loc-args\":[\"Jenna\",\"Frank\"]}}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void multipleBuildAbitComplicated() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.customField("achme", "foo");
        builder.sound("chime");
        builder.localizedKey("GAME_PLAY_REQUEST_FORMAT").localizedArguments("Jenna", "Frank");
        final String expected = "{\"achme\":\"foo\",\"aps\":{\"sound\":\"chime\",\"alert\":{\"loc-key\":\"GAME_PLAY_REQUEST_FORMAT\",\"loc-args\":[\"Jenna\",\"Frank\"]}}}";
        assertEqualsJson(expected, builder.build());
        assertEqualsJson(expected, builder.build());
    }

    @Test
    public void copyReturnsNewInstance() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.sound("chime");
        final PayloadBuilder copy = builder.copy();
        copy.badge(5);
        Assert.assertNotSame(builder, copy);
        final String expected = "{\"aps\":{\"sound\":\"chime\"}}";
        assertEqualsJson(expected, builder.build());
        final String copyExpected = "{\"aps\":{\"sound\":\"chime\",\"badge\":5}}";
        assertEqualsJson(copyExpected, copy.build());
    }

    @Test
    public void simpleEnglishLength() {
        final PayloadBuilder builder = new PayloadBuilder().alertBody("test");
        final String expected = "{\"aps\":{\"alert\":\"test\"}}";
        assertEqualsJson(expected, builder.build());
        final int actualLength = Utilities.toUTF8Bytes(expected).length;
        Assert.assertEquals(actualLength, builder.length());
        Assert.assertFalse(builder.isTooLong());
    }

    @Test
    public void abitComplicatedEnglishLength() {
        final byte[] dtBytes = new byte[32];
        new Random().nextBytes(dtBytes);
        final String deviceToken = Utilities.encodeHex(dtBytes);
        final PayloadBuilder builder = new PayloadBuilder().alertBody("test");
        final SimpleApnsNotification fromString = new SimpleApnsNotification(deviceToken, builder.build());
        final SimpleApnsNotification fromBytes = new SimpleApnsNotification(dtBytes, Utilities.toUTF8Bytes(builder.build()));
        final String expected = "{\"aps\":{\"alert\":\"test\"}}";
        final int actualPacketLength = (((1 + 2) + (dtBytes.length)) + 2) + (Utilities.toUTF8Bytes(expected).length);
        Assert.assertEquals(actualPacketLength, fromString.length());
        Assert.assertEquals(actualPacketLength, fromBytes.length());
        Assert.assertEquals(expected.length(), fromString.getPayload().length);
        Assert.assertArrayEquals(fromString.marshall(), fromBytes.marshall());
        Assert.assertFalse(builder.isTooLong());
    }

    @Test
    public void detectingLongMessages() {
        final String basic = "{\"aps\":{\"alert\":\"\"}}";
        final int wrapperOverhead = basic.length();
        final int cutoffForAlert = 2048 - wrapperOverhead;
        final PayloadBuilder wayShort = payloadOf(1);
        Assert.assertFalse(wayShort.isTooLong());
        Assert.assertTrue(((wayShort.length()) == (wrapperOverhead + 1)));
        final PayloadBuilder bitShort = payloadOf((cutoffForAlert - 1));
        Assert.assertFalse(bitShort.isTooLong());
        Assert.assertTrue(((bitShort.length()) == ((wrapperOverhead + cutoffForAlert) - 1)));
        final PayloadBuilder border = payloadOf(cutoffForAlert);
        Assert.assertFalse(border.isTooLong());
        Assert.assertTrue(((border.length()) == (wrapperOverhead + cutoffForAlert)));
        Assert.assertTrue(((border.length()) == 2048));
        final PayloadBuilder abitLong = payloadOf((cutoffForAlert + 1));
        Assert.assertTrue(abitLong.isTooLong());
        Assert.assertTrue(((abitLong.length()) == ((wrapperOverhead + cutoffForAlert) + 1)));
        final PayloadBuilder tooLong = payloadOf((cutoffForAlert + 1000));
        Assert.assertTrue(tooLong.isTooLong());
        Assert.assertTrue(((tooLong.length()) == ((wrapperOverhead + cutoffForAlert) + 1000)));
    }

    @Test
    public void shrinkLongMessages() {
        final String basic = "{\"aps\":{\"alert\":\"\"}}";
        final int wrapperOverhead = basic.length();
        final int cutoffForAlert = 2048 - wrapperOverhead;
        final int max_length = 2048;
        final PayloadBuilder wayShort = payloadOf(1);
        wayShort.shrinkBody();// NOOP

        Assert.assertFalse(wayShort.isTooLong());
        Assert.assertTrue(((wayShort.length()) == (wrapperOverhead + 1)));
        final PayloadBuilder bitShort = payloadOf((cutoffForAlert - 1));
        bitShort.shrinkBody();// NOOP

        Assert.assertFalse(bitShort.isTooLong());
        Assert.assertTrue(((bitShort.length()) == ((wrapperOverhead + cutoffForAlert) - 1)));
        final PayloadBuilder border = payloadOf(cutoffForAlert);
        Assert.assertFalse(border.isTooLong());// NOOP

        Assert.assertTrue(((border.length()) == max_length));
        final PayloadBuilder abitLong = payloadOf((cutoffForAlert + 1));
        abitLong.shrinkBody();
        Assert.assertFalse(abitLong.isTooLong());
        Assert.assertTrue(((abitLong.length()) == max_length));
        final PayloadBuilder tooLong = payloadOf((cutoffForAlert + 1000));
        tooLong.shrinkBody();
        Assert.assertFalse(tooLong.isTooLong());
        Assert.assertTrue(((tooLong.length()) == max_length));
    }

    @Test
    public void shrinkLongMessagesWithOtherthigns() {
        final String basic = "{\"aps\":{\"alert\":\"\"}}";
        final int wrapperOverhead = basic.length();
        final int cutoffForAlert = 2048 - wrapperOverhead;
        final int max_length = 2048;
        final PayloadBuilder wayShort = payloadOf(1).sound("default");
        Assert.assertFalse(wayShort.isTooLong());
        Assert.assertTrue(((wayShort.length()) <= max_length));
        final PayloadBuilder bitShort = payloadOf((cutoffForAlert - 1)).sound("default");
        bitShort.shrinkBody();// NOOP

        Assert.assertFalse(bitShort.isTooLong());
        Assert.assertTrue(((bitShort.length()) <= max_length));
        final PayloadBuilder border = payloadOf(cutoffForAlert).sound("default");
        border.shrinkBody();
        Assert.assertFalse(border.isTooLong());// NOOP

        Assert.assertTrue(((border.length()) == max_length));
        final PayloadBuilder abitLong = payloadOf((cutoffForAlert + 1)).sound("default");
        abitLong.shrinkBody();
        Assert.assertFalse(abitLong.isTooLong());
        Assert.assertTrue(((abitLong.length()) == max_length));
        final PayloadBuilder tooLong = payloadOf((cutoffForAlert + 1000)).sound("default");
        tooLong.shrinkBody();
        Assert.assertFalse(tooLong.isTooLong());
        Assert.assertTrue(((tooLong.length()) == max_length));
    }

    @Test
    public void removeAlertIfSooLong() {
        final PayloadBuilder tooLong = APNS.newPayload().customField("test", strOfLen(2048)).alertBody("what");
        tooLong.shrinkBody();
        final String payload = tooLong.build();
        MatcherAssert.assertThat(payload, IsNot.not(StringContains.containsString("alert")));
    }

    @Test
    public void supportsMDM() {
        final String mdm = APNS.newPayload().mdm("213").toString();
        final String expected = "{\"mdm\":\"213\"}";
        assertEqualsJson(expected, mdm);
    }

    @Test
    public void supportsNewsstand() {
        final String news = APNS.newPayload().forNewsstand().toString();
        final String expected = "{\"aps\":{\"content-available\":1}}";
        assertEqualsJson(expected, news);
    }

    @Test
    public void tooLongWithCustomFields() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("12345678");
        builder.customField("ache1", "what");
        builder.customField("ache2", 2);
        final String s1 = builder.toString();
        MatcherAssert.assertThat(s1, StringContains.containsString("12345678"));
        final String s2 = builder.toString();
        MatcherAssert.assertThat(s2, StringContains.containsString("12345678"));
        assertEqualsJson(s1, s2);
    }

    @Test
    public void trimWorksWithLongFields() {
        final PayloadBuilder builder = new PayloadBuilder();
        final String toolong = "1234567890123456789012345678901234567890" + (((("1234567890123456789012345678901234567890" + "1234567890123456789012345678901234567890") + "1234567890123456789012345678901234567890") + "1234567890123456789012345678901234567890") + "1234567890123456789012345678901234567890");
        builder.alertBody(toolong);
        builder.actionKey("OK");
        builder.shrinkBody();
        final String s2 = builder.toString();
        MatcherAssert.assertThat(s2, StringContains.containsString("12345678"));
    }

    @Test
    public void utf8Encoding() {
        final String str = "esem?ny";
        final PayloadBuilder builder = new PayloadBuilder();
        final String s1 = builder.alertBody(str).toString();
        MatcherAssert.assertThat(s1, StringContains.containsString(str));
    }

    @Test
    public void utf8EncodingEscaped() {
        final String str = "esem\u00e9ny";
        final PayloadBuilder builder = new PayloadBuilder();
        final String s1 = builder.alertBody(str).toString();
        MatcherAssert.assertThat(s1, StringContains.containsString(str));
    }

    @Test
    public void silentPingMessage() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.instantDeliveryOrSilentNotification();
        final String expected = "{\"aps\":{\"content-available\":1}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void silentPingMessageWithCustomKey() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.instantDeliveryOrSilentNotification();
        builder.customField("ache1", "what");
        final String expected = "{\"aps\":{\"content-available\":1},\"ache1\":\"what\"}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void instantMessageWithAlert() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.alertBody("test");
        builder.instantDeliveryOrSilentNotification();
        final String expected = "{\"aps\":{\"alert\":\"test\",\"content-available\":1}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }
}

