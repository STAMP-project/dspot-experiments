/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.http;


import Http2Settings.DEFAULT_INITIAL_WINDOW_SIZE;
import Http2Settings.DEFAULT_MAX_CONCURRENT_STREAMS;
import Http2Settings.DEFAULT_MAX_FRAME_SIZE;
import Http2Settings.DEFAULT_MAX_HEADER_LIST_SIZE;
import io.vertx.core.http.impl.HttpUtils;
import io.vertx.test.core.TestUtils;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Thomas Segismont
 */
public class Http2SettingsTest {
    long[] min = new long[]{ 0, 0, 0, 0, 16384, 0 };

    long[] max = new long[]{ 4294967295L, 1, 4294967295L, Integer.MAX_VALUE, 16777215, Integer.MAX_VALUE };

    @Test
    public void testSettingsMin() {
        for (int i = 1; i <= 6; i++) {
            try {
                new Http2Settings().set(i, ((min[(i - 1)]) - 1));
                Assert.fail();
            } catch (IllegalArgumentException ignore) {
            }
        }
        Http2Settings settings = new Http2Settings();
        for (int i = 1; i <= 6; i++) {
            settings.set(i, min[(i - 1)]);
        }
        HttpUtils.fromVertxSettings(settings);
    }

    @Test
    public void testSettinsMax() {
        for (int i = 1; i <= 6; i++) {
            try {
                new Http2Settings().set(i, ((max[(i - 1)]) + 1));
                Assert.fail((("Was expecting setting " + (i - 1)) + " update to throw IllegalArgumentException"));
            } catch (IllegalArgumentException ignore) {
            }
        }
        Http2Settings settings = new Http2Settings();
        for (int i = 1; i <= 6; i++) {
            settings.set(i, max[(i - 1)]);
        }
        HttpUtils.fromVertxSettings(settings);
    }

    @Test
    public void toNettySettings() {
        Http2Settings settings = new Http2Settings();
        for (int i = 7; i <= 65535; i += 1) {
            // we need to clamp the random value to pass validation
            settings.set(65535, Math.min(4294967295L, TestUtils.randomPositiveLong()));
        }
        io.netty.handler.codec.http2.Http2Settings conv = HttpUtils.fromVertxSettings(settings);
        for (int i = 1; i <= 65535; i += 1) {
            Assert.assertEquals(settings.get(i), conv.get(((char) (i))));
        }
        settings = HttpUtils.toVertxSettings(conv);
        for (int i = 1; i <= 65535; i += 1) {
            Assert.assertEquals(settings.get(i), conv.get(((char) (i))));
        }
    }

    @Test
    public void testSettings() {
        Http2Settings settings = new Http2Settings();
        Assert.assertEquals(true, settings.isPushEnabled());
        Assert.assertEquals(DEFAULT_MAX_HEADER_LIST_SIZE, settings.getMaxHeaderListSize());
        Assert.assertEquals(DEFAULT_MAX_CONCURRENT_STREAMS, settings.getMaxConcurrentStreams());
        Assert.assertEquals(DEFAULT_INITIAL_WINDOW_SIZE, settings.getInitialWindowSize());
        Assert.assertEquals(DEFAULT_MAX_FRAME_SIZE, settings.getMaxFrameSize());
        Assert.assertEquals(null, settings.getExtraSettings());
        Http2Settings update = TestUtils.randomHttp2Settings();
        Assert.assertFalse(settings.equals(update));
        Assert.assertNotSame(settings.hashCode(), settings.hashCode());
        Assert.assertSame(settings, settings.setHeaderTableSize(update.getHeaderTableSize()));
        Assert.assertEquals(settings.getHeaderTableSize(), update.getHeaderTableSize());
        Assert.assertSame(settings, settings.setPushEnabled(update.isPushEnabled()));
        Assert.assertEquals(settings.isPushEnabled(), update.isPushEnabled());
        Assert.assertSame(settings, settings.setMaxHeaderListSize(update.getMaxHeaderListSize()));
        Assert.assertEquals(settings.getMaxHeaderListSize(), update.getMaxHeaderListSize());
        Assert.assertSame(settings, settings.setMaxConcurrentStreams(update.getMaxConcurrentStreams()));
        Assert.assertEquals(settings.getMaxConcurrentStreams(), update.getMaxConcurrentStreams());
        Assert.assertSame(settings, settings.setInitialWindowSize(update.getInitialWindowSize()));
        Assert.assertEquals(settings.getInitialWindowSize(), update.getInitialWindowSize());
        Assert.assertSame(settings, settings.setMaxFrameSize(update.getMaxFrameSize()));
        Assert.assertEquals(settings.getMaxFrameSize(), update.getMaxFrameSize());
        Assert.assertSame(settings, settings.setExtraSettings(update.getExtraSettings()));
        Map<Integer, Long> extraSettings = new java.util.HashMap(update.getExtraSettings());
        Assert.assertEquals(update.getExtraSettings(), extraSettings);
        extraSettings.clear();
        Assert.assertEquals(update.getExtraSettings(), settings.getExtraSettings());
        Assert.assertTrue(settings.equals(update));
        Assert.assertEquals(settings.hashCode(), settings.hashCode());
        settings = new Http2Settings(update);
        Assert.assertEquals(settings.getHeaderTableSize(), update.getHeaderTableSize());
        Assert.assertEquals(settings.isPushEnabled(), update.isPushEnabled());
        Assert.assertEquals(settings.getMaxHeaderListSize(), update.getMaxHeaderListSize());
        Assert.assertEquals(settings.getMaxConcurrentStreams(), update.getMaxConcurrentStreams());
        Assert.assertEquals(settings.getInitialWindowSize(), update.getInitialWindowSize());
        Assert.assertEquals(settings.getMaxFrameSize(), update.getMaxFrameSize());
        Assert.assertEquals(update.getExtraSettings(), settings.getExtraSettings());
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        Http2Settings s1 = new Http2Settings().setHeaderTableSize(1024);
        Http2Settings s2 = new Http2Settings().setHeaderTableSize(1024);
        Http2Settings s3 = new Http2Settings(s1.toJson());
        Http2Settings s4 = new Http2Settings().setHeaderTableSize(2048);
        Assert.assertEquals(s1, s1);
        Assert.assertEquals(s2, s2);
        Assert.assertEquals(s3, s3);
        Assert.assertEquals(s1, s2);
        Assert.assertEquals(s2, s1);
        Assert.assertEquals(s2, s3);
        Assert.assertEquals(s3, s2);
        Assert.assertEquals(s1, s3);
        Assert.assertEquals(s3, s1);
        Assert.assertEquals(s1.hashCode(), s2.hashCode());
        Assert.assertEquals(s2.hashCode(), s3.hashCode());
        Assert.assertFalse(s1.equals(null));
        Assert.assertFalse(s2.equals(null));
        Assert.assertFalse(s3.equals(null));
        Assert.assertNotEquals(s1, s4);
        Assert.assertNotEquals(s4, s1);
        Assert.assertNotEquals(s2, s4);
        Assert.assertNotEquals(s4, s2);
        Assert.assertNotEquals(s3, s4);
        Assert.assertNotEquals(s4, s3);
        Assert.assertNotEquals(s1.hashCode(), s4.hashCode());
        Assert.assertNotEquals(s2.hashCode(), s4.hashCode());
        Assert.assertNotEquals(s3.hashCode(), s4.hashCode());
    }
}

