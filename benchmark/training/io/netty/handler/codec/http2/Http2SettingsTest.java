/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http2;


import Http2CodecUtil.SETTINGS_HEADER_TABLE_SIZE;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Http2Settings}.
 */
public class Http2SettingsTest {
    private Http2Settings settings;

    @Test
    public void standardSettingsShouldBeNotSet() {
        Assert.assertEquals(0, settings.size());
        Assert.assertNull(settings.headerTableSize());
        Assert.assertNull(settings.initialWindowSize());
        Assert.assertNull(settings.maxConcurrentStreams());
        Assert.assertNull(settings.pushEnabled());
        Assert.assertNull(settings.maxFrameSize());
        Assert.assertNull(settings.maxHeaderListSize());
    }

    @Test
    public void standardSettingsShouldBeSet() {
        settings.initialWindowSize(1);
        settings.maxConcurrentStreams(2);
        settings.pushEnabled(true);
        settings.headerTableSize(3);
        settings.maxFrameSize(Http2CodecUtil.MAX_FRAME_SIZE_UPPER_BOUND);
        settings.maxHeaderListSize(4);
        Assert.assertEquals(1, ((int) (settings.initialWindowSize())));
        Assert.assertEquals(2L, ((long) (settings.maxConcurrentStreams())));
        Assert.assertTrue(settings.pushEnabled());
        Assert.assertEquals(3L, ((long) (settings.headerTableSize())));
        Assert.assertEquals(Http2CodecUtil.MAX_FRAME_SIZE_UPPER_BOUND, ((int) (settings.maxFrameSize())));
        Assert.assertEquals(4L, ((long) (settings.maxHeaderListSize())));
    }

    @Test
    public void nonStandardSettingsShouldBeSet() {
        char key = 0;
        settings.put(key, ((Long) (123L)));
        Assert.assertEquals(123L, ((long) (settings.get(key))));
    }

    @Test
    public void settingsShouldSupportUnsignedShort() {
        char key = ((char) ((Short.MAX_VALUE) + 1));
        settings.put(key, ((Long) (123L)));
        Assert.assertEquals(123L, ((long) (settings.get(key))));
    }

    @Test
    public void headerListSizeUnsignedInt() {
        settings.maxHeaderListSize(Http2CodecUtil.MAX_UNSIGNED_INT);
        Assert.assertEquals(Http2CodecUtil.MAX_UNSIGNED_INT, ((long) (settings.maxHeaderListSize())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void headerListSizeBoundCheck() {
        settings.maxHeaderListSize(Long.MAX_VALUE);
    }

    @Test
    public void headerTableSizeUnsignedInt() {
        settings.put(SETTINGS_HEADER_TABLE_SIZE, ((Long) (Http2CodecUtil.MAX_UNSIGNED_INT)));
        Assert.assertEquals(Http2CodecUtil.MAX_UNSIGNED_INT, ((long) (settings.get(SETTINGS_HEADER_TABLE_SIZE))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void headerTableSizeBoundCheck() {
        settings.put(SETTINGS_HEADER_TABLE_SIZE, ((Long) (Long.MAX_VALUE)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void headerTableSizeBoundCheck2() {
        settings.put(SETTINGS_HEADER_TABLE_SIZE, Long.valueOf((-1L)));
    }
}

