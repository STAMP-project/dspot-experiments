/**
 * Copyright 2007 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zxing.client.result;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link URIParsedResult}.
 *
 * @author Sean Owen
 */
public final class URIParsedResultTestCase extends Assert {
    @Test
    public void testBookmarkDocomo() {
        URIParsedResultTestCase.doTest("MEBKM:URL:google.com;;", "http://google.com", null);
        URIParsedResultTestCase.doTest("MEBKM:URL:http://google.com;;", "http://google.com", null);
        URIParsedResultTestCase.doTest("MEBKM:URL:google.com;TITLE:Google;", "http://google.com", "Google");
    }

    @Test
    public void testURI() {
        URIParsedResultTestCase.doTest("google.com", "http://google.com", null);
        URIParsedResultTestCase.doTest("123.com", "http://123.com", null);
        URIParsedResultTestCase.doTest("http://google.com", "http://google.com", null);
        URIParsedResultTestCase.doTest("https://google.com", "https://google.com", null);
        URIParsedResultTestCase.doTest("google.com:443", "http://google.com:443", null);
        URIParsedResultTestCase.doTest("https://www.google.com/calendar/hosted/google.com/embed?mode=AGENDA&force_login=true&src=google.com_726f6f6d5f6265707075@resource.calendar.google.com", "https://www.google.com/calendar/hosted/google.com/embed?mode=AGENDA&force_login=true&src=google.com_726f6f6d5f6265707075@resource.calendar.google.com", null);
        URIParsedResultTestCase.doTest("otpauth://remoteaccess?devaddr=00%a1b2%c3d4&devname=foo&key=bar", "otpauth://remoteaccess?devaddr=00%a1b2%c3d4&devname=foo&key=bar", null);
        URIParsedResultTestCase.doTest("s3://amazon.com:8123", "s3://amazon.com:8123", null);
        URIParsedResultTestCase.doTest("HTTP://R.BEETAGG.COM/?12345", "HTTP://R.BEETAGG.COM/?12345", null);
    }

    @Test
    public void testNotURI() {
        URIParsedResultTestCase.doTestNotUri("google.c");
        URIParsedResultTestCase.doTestNotUri(".com");
        URIParsedResultTestCase.doTestNotUri(":80/");
        URIParsedResultTestCase.doTestNotUri("ABC,20.3,AB,AD");
        URIParsedResultTestCase.doTestNotUri("http://google.com?q=foo bar");
        URIParsedResultTestCase.doTestNotUri("12756.501");
        URIParsedResultTestCase.doTestNotUri("google.50");
        URIParsedResultTestCase.doTestNotUri("foo.bar.bing.baz.foo.bar.bing.baz");
    }

    @Test
    public void testURLTO() {
        URIParsedResultTestCase.doTest("urlto::bar.com", "http://bar.com", null);
        URIParsedResultTestCase.doTest("urlto::http://bar.com", "http://bar.com", null);
        URIParsedResultTestCase.doTest("urlto:foo:bar.com", "http://bar.com", "foo");
    }

    @Test
    public void testGarbage() {
        URIParsedResultTestCase.doTestNotUri("Da65cV1g^>%^f0bAbPn1CJB6lV7ZY8hs0Sm:DXU0cd]GyEeWBz8]bUHLB");
        URIParsedResultTestCase.doTestNotUri(("DEA\u0003\u0019M\u0006\u0000\b\u221a\u2022\u0000\u00ac\u00e1HO\u0000X$\u0001\u0000\u001fwfc\u0007!\u221a\u00e6\u00ac\u00ec\u00ac\u00f2" + (("\u0013\u0013\u00ac\u00e6Z{\u221a\u03c0\u221a\u00e9\u221a\u00f9\u221a\u00f6\u00ac\u00f3Z\u00ac\u00df\u00ac\u00ae+y_zb\u221a\u00b1k\u00117\u00ac\u220f\u000e\u00ac\u00dc\u221a\u00fa\u0000\u0000\u0000\u0000" + "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000") + "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u00ac\u00a3.ux")));
    }

    @Test
    public void testIsPossiblyMalicious() {
        URIParsedResultTestCase.doTestIsPossiblyMalicious("http://google.com", false);
        URIParsedResultTestCase.doTestIsPossiblyMalicious("http://google.com@evil.com", true);
        URIParsedResultTestCase.doTestIsPossiblyMalicious("http://google.com:@evil.com", true);
        URIParsedResultTestCase.doTestIsPossiblyMalicious("google.com:@evil.com", false);
        URIParsedResultTestCase.doTestIsPossiblyMalicious("https://google.com:443", false);
        URIParsedResultTestCase.doTestIsPossiblyMalicious("https://google.com:443/", false);
        URIParsedResultTestCase.doTestIsPossiblyMalicious("https://evil@google.com:443", true);
        URIParsedResultTestCase.doTestIsPossiblyMalicious("http://google.com/foo@bar", false);
        URIParsedResultTestCase.doTestIsPossiblyMalicious("http://google.com/@@", false);
    }

    @Test
    public void testMaliciousUnicode() {
        URIParsedResultTestCase.doTestIsPossiblyMalicious("https://google.com\u2215.evil.com/stuff", true);
        URIParsedResultTestCase.doTestIsPossiblyMalicious("\u202ehttps://dylankatz.com/moc.elgoog.www//:sptth", true);
    }

    @Test
    public void testExotic() {
        URIParsedResultTestCase.doTest("bitcoin:mySD89iqpmptrK3PhHFW9fa7BXiP7ANy3Y", "bitcoin:mySD89iqpmptrK3PhHFW9fa7BXiP7ANy3Y", null);
        URIParsedResultTestCase.doTest(("BTCTX:-TC4TO3$ZYZTC5NC83/SYOV+YGUGK:$BSF0P8/STNTKTKS.V84+JSA$LB+EHCG+8A725.2AZ-NAVX3VBV5K4MH7UL2.2M:" + (("F*M9HSL*$2P7T*FX.ZT80GWDRV0QZBPQ+O37WDCNZBRM3EQ0S9SZP+3BPYZG02U/LA*89C2U.V1TS.CT1VF3DIN*HN3W-O-" + "0ZAKOAB32/.8:J501GJJTTWOA+5/6$MIYBERPZ41NJ6-WSG/*Z48ZH*LSAOEM*IXP81L:$F*W08Z60CR*C*P.JEEVI1F02J07L6+") + "W4L1G$/IC*$16GK6A+:I1-:LJ:Z-P3NW6Z6ADFB-F2AKE$2DWN23GYCYEWX9S8L+LF$VXEKH7/R48E32PU+A:9H:8O5")), ("BTCTX:-TC4TO3$ZYZTC5NC83/SYOV+YGUGK:$BSF0P8/STNTKTKS.V84+JSA$LB+EHCG+8A725.2AZ-NAVX3VBV5K4MH7UL2.2M:" + (("F*M9HSL*$2P7T*FX.ZT80GWDRV0QZBPQ+O37WDCNZBRM3EQ0S9SZP+3BPYZG02U/LA*89C2U.V1TS.CT1VF3DIN*HN3W-O-" + "0ZAKOAB32/.8:J501GJJTTWOA+5/6$MIYBERPZ41NJ6-WSG/*Z48ZH*LSAOEM*IXP81L:$F*W08Z60CR*C*P.JEEVI1F02J07L6+") + "W4L1G$/IC*$16GK6A+:I1-:LJ:Z-P3NW6Z6ADFB-F2AKE$2DWN23GYCYEWX9S8L+LF$VXEKH7/R48E32PU+A:9H:8O5")), null);
        URIParsedResultTestCase.doTest("opc.tcp://test.samplehost.com:4841", "opc.tcp://test.samplehost.com:4841", null);
    }
}

