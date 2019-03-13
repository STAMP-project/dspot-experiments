/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.plugin.proxy;


import AnnotationKey.PROXY_HTTP_HEADER;
import ProxyHttpHeader.TYPE_APACHE;
import ProxyHttpHeader.TYPE_APP;
import ProxyHttpHeader.TYPE_NGINX;
import com.navercorp.pinpoint.common.util.LongIntIntByteByteStringValue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class ProxyHttpHeaderParserTest {
    @Test
    public void parseApacheHttpd() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        final long currentTimeMillis = System.currentTimeMillis();
        String value = (("t=" + currentTimeMillis) + "999") + " D=12345 i=99 b=1";
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_APACHE, value);
        Assert.assertTrue(proxyHttpHeader.isValid());
        Assert.assertEquals(currentTimeMillis, proxyHttpHeader.getReceivedTimeMillis());
        Assert.assertEquals(12345, proxyHttpHeader.getDurationTimeMicroseconds());
        Assert.assertEquals(99, proxyHttpHeader.getIdlePercent());
        Assert.assertEquals(1, proxyHttpHeader.getBusyPercent());
        Assert.assertEquals(PROXY_HTTP_HEADER, proxyHttpHeader.getAnnotationKey());
        LongIntIntByteByteStringValue tvalue = ((LongIntIntByteByteStringValue) (proxyHttpHeader.getAnnotationValue()));
        Assert.assertEquals(currentTimeMillis, tvalue.getLongValue());
        Assert.assertEquals(12345, tvalue.getIntValue2());
        Assert.assertEquals(99, tvalue.getByteValue1());
        Assert.assertEquals(1, tvalue.getByteValue2());
        System.out.println("");
    }

    @Test
    public void parseApacheHttpdOnlyReceivedTime() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        final long currentTimeMillis = System.currentTimeMillis();
        String value = ("t=" + currentTimeMillis) + "999";
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_APACHE, value);
        Assert.assertTrue(proxyHttpHeader.isValid());
        Assert.assertEquals(currentTimeMillis, proxyHttpHeader.getReceivedTimeMillis());
        Assert.assertEquals((-1), proxyHttpHeader.getDurationTimeMicroseconds());
        Assert.assertEquals((-1), proxyHttpHeader.getIdlePercent());
        Assert.assertEquals((-1), proxyHttpHeader.getBusyPercent());
        Assert.assertEquals(PROXY_HTTP_HEADER, proxyHttpHeader.getAnnotationKey());
        LongIntIntByteByteStringValue tvalue = ((LongIntIntByteByteStringValue) (proxyHttpHeader.getAnnotationValue()));
        Assert.assertEquals(currentTimeMillis, tvalue.getLongValue());
        Assert.assertEquals((-1), tvalue.getIntValue2());
        Assert.assertEquals((-1), tvalue.getByteValue1());
        Assert.assertEquals((-1), tvalue.getByteValue2());
    }

    @Test
    public void parseApacheHttpdOnlyDurationTime() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        final long currentTimeMillis = System.currentTimeMillis();
        String value = " D=12345";
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_APACHE, value);
        Assert.assertFalse(proxyHttpHeader.isValid());
    }

    @Test
    public void parseApacheHttpdOnlyIdle() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        String value = "i=99";
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_APACHE, value);
        Assert.assertFalse(proxyHttpHeader.isValid());
    }

    @Test
    public void parseApacheHttpdOnlyBusy() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        String value = "b=1";
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_APACHE, value);
        Assert.assertFalse(proxyHttpHeader.isValid());
    }

    @Test
    public void parseApacheHttpdTooShotReceivedTime() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        String value = "t=99" + " D=12345 i=99 b=1";
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_APACHE, value);
        Assert.assertFalse(proxyHttpHeader.isValid());
    }

    @Test
    public void parseNginx() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        String value = "t=1504248328.423 D=0.123";
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_NGINX, value);
        Assert.assertTrue(proxyHttpHeader.isValid());
        Assert.assertEquals(1504248328423L, proxyHttpHeader.getReceivedTimeMillis());
        Assert.assertEquals(123000L, proxyHttpHeader.getDurationTimeMicroseconds());
        Assert.assertEquals((-1), proxyHttpHeader.getIdlePercent());
        Assert.assertEquals((-1), proxyHttpHeader.getBusyPercent());
        Assert.assertEquals(PROXY_HTTP_HEADER, proxyHttpHeader.getAnnotationKey());
        LongIntIntByteByteStringValue tvalue = ((LongIntIntByteByteStringValue) (proxyHttpHeader.getAnnotationValue()));
        Assert.assertEquals(1504248328423L, tvalue.getLongValue());
        Assert.assertEquals(123000L, tvalue.getIntValue2());
        Assert.assertEquals((-1), tvalue.getByteValue1());
        Assert.assertEquals((-1), tvalue.getByteValue2());
    }

    @Test
    public void parseNginxMsec() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        String value = "t=1504248328.423";
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_NGINX, value);
        Assert.assertTrue(proxyHttpHeader.isValid());
        Assert.assertEquals(1504248328423L, proxyHttpHeader.getReceivedTimeMillis());
        Assert.assertEquals((-1), proxyHttpHeader.getDurationTimeMicroseconds());
        Assert.assertEquals((-1), proxyHttpHeader.getIdlePercent());
        Assert.assertEquals((-1), proxyHttpHeader.getBusyPercent());
        Assert.assertEquals(PROXY_HTTP_HEADER, proxyHttpHeader.getAnnotationKey());
        LongIntIntByteByteStringValue tvalue = ((LongIntIntByteByteStringValue) (proxyHttpHeader.getAnnotationValue()));
        Assert.assertEquals(1504248328423L, tvalue.getLongValue());
        Assert.assertEquals((-1), tvalue.getIntValue2());
        Assert.assertEquals((-1), tvalue.getByteValue1());
        Assert.assertEquals((-1), tvalue.getByteValue2());
    }

    @Test
    public void parseApp() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        final long currentTimeMillis = System.currentTimeMillis();
        String value = "t=" + currentTimeMillis;
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_APP, value);
        Assert.assertTrue(proxyHttpHeader.isValid());
        Assert.assertEquals(currentTimeMillis, proxyHttpHeader.getReceivedTimeMillis());
        Assert.assertEquals((-1), proxyHttpHeader.getDurationTimeMicroseconds());
        Assert.assertEquals((-1), proxyHttpHeader.getIdlePercent());
        Assert.assertEquals((-1), proxyHttpHeader.getBusyPercent());
        Assert.assertEquals(PROXY_HTTP_HEADER, proxyHttpHeader.getAnnotationKey());
        LongIntIntByteByteStringValue tvalue = ((LongIntIntByteByteStringValue) (proxyHttpHeader.getAnnotationValue()));
        Assert.assertEquals(currentTimeMillis, tvalue.getLongValue());
        Assert.assertEquals((-1), tvalue.getIntValue2());
        Assert.assertEquals((-1), tvalue.getByteValue1());
        Assert.assertEquals((-1), tvalue.getByteValue2());
    }

    @Test
    public void parseTimestampInvalidValue() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        final long currentTimeMillis = System.currentTimeMillis();
        String value = ("t=" + (String.valueOf(Long.MAX_VALUE))) + (String.valueOf(Long.MAX_VALUE));
        ProxyHttpHeader proxyHttpHeader = parser.parse(TYPE_APP, value);
        Assert.assertFalse(proxyHttpHeader.isValid());
    }

    @Test
    public void parseUnknown() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        // missing t=
        Assert.assertFalse(parser.parse(TYPE_APP, "x=99999").isValid());
        Assert.assertFalse(parser.parse(TYPE_APP, "D=0.00").isValid());
        Assert.assertFalse(parser.parse(TYPE_APP, "i=100").isValid());
        Assert.assertFalse(parser.parse(TYPE_APP, "b=100").isValid());
        Assert.assertFalse(parser.parse(TYPE_APP, "app=foo-bar").isValid());
        Assert.assertFalse(parser.parse(TYPE_APACHE, "D=100 i=1 b=99").isValid());
        Assert.assertFalse(parser.parse(TYPE_NGINX, "D=0.00").isValid());
        // invalid t=
        Assert.assertFalse(parser.parse(TYPE_APP, "t=A10101010").isValid());
        Assert.assertFalse(parser.parse(TYPE_APP, "t=0").isValid());
        Assert.assertFalse(parser.parse(TYPE_APACHE, "t=0").isValid());
        Assert.assertFalse(parser.parse(TYPE_APACHE, "t=10").isValid());
        Assert.assertFalse(parser.parse(TYPE_APACHE, "t=100").isValid());
        Assert.assertFalse(parser.parse(TYPE_NGINX, "t=0").isValid());
        Assert.assertFalse(parser.parse(TYPE_NGINX, "t=10").isValid());
        Assert.assertFalse(parser.parse(TYPE_NGINX, "t=10.0").isValid());
        Assert.assertFalse(parser.parse(TYPE_NGINX, "t=10.00").isValid());
    }

    @Test
    public void toReceivedTimeMillis() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        Assert.assertEquals(1504230492763L, parser.getNginxUnit().toReceivedTimeMillis("1504230492.763"));
        Assert.assertEquals(1504244246860L, parser.getApacheUnit().toReceivedTimeMillis("1504244246860824"));
        Assert.assertEquals(1504230492763L, parser.getAppUnit().toReceivedTimeMillis("1504230492763"));
        // invalid
        Assert.assertEquals(0L, parser.getNginxUnit().toReceivedTimeMillis("1504230492.76"));
        Assert.assertEquals(0L, parser.getNginxUnit().toReceivedTimeMillis("1504230492.7"));
        Assert.assertEquals(0L, parser.getNginxUnit().toReceivedTimeMillis("1504230492."));
        Assert.assertEquals(0L, parser.getApacheUnit().toReceivedTimeMillis("150"));
        Assert.assertEquals(0L, parser.getApacheUnit().toReceivedTimeMillis("15"));
        Assert.assertEquals(0L, parser.getApacheUnit().toReceivedTimeMillis("1"));
        Assert.assertEquals(0L, parser.getApacheUnit().toReceivedTimeMillis(""));
        Assert.assertEquals(0L, parser.getApacheUnit().toReceivedTimeMillis(null));
        Assert.assertEquals(0L, parser.getAppUnit().toReceivedTimeMillis("A"));
        Assert.assertEquals(0L, parser.getAppUnit().toReceivedTimeMillis("150B"));
    }

    @Test
    public void toDurationTimeMicros() throws Exception {
        ProxyHttpHeaderParser parser = new ProxyHttpHeaderParser();
        Assert.assertEquals(1001000, parser.getNginxUnit().toDurationTimeMicros("1.001"));
        Assert.assertEquals(1000, parser.getNginxUnit().toDurationTimeMicros("0.001"));
        Assert.assertEquals(123, parser.getApacheUnit().toDurationTimeMicros("123"));
        // invalid
        Assert.assertEquals(0, parser.getNginxUnit().toDurationTimeMicros("1.01"));
        Assert.assertEquals(0, parser.getNginxUnit().toDurationTimeMicros("1.1"));
        Assert.assertEquals(0, parser.getNginxUnit().toDurationTimeMicros("1."));
        Assert.assertEquals(0, parser.getNginxUnit().toDurationTimeMicros(".0"));
        Assert.assertEquals(0, parser.getNginxUnit().toDurationTimeMicros(".01"));
        Assert.assertEquals(0, parser.getApacheUnit().toDurationTimeMicros("a"));
        Assert.assertEquals(0, parser.getApacheUnit().toDurationTimeMicros(""));
        Assert.assertEquals(0, parser.getApacheUnit().toDurationTimeMicros(null));
    }
}

