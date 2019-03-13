/**
 * Copyright (C) 2012 Square, Inc.
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
package okhttp3.internal.http2;


import Settings.ENABLE_PUSH;
import Settings.HEADER_TABLE_SIZE;
import Settings.INITIAL_WINDOW_SIZE;
import Settings.MAX_CONCURRENT_STREAMS;
import Settings.MAX_FRAME_SIZE;
import Settings.MAX_HEADER_LIST_SIZE;
import org.junit.Assert;
import org.junit.Test;


public final class SettingsTest {
    @Test
    public void unsetField() {
        Settings settings = new Settings();
        Assert.assertEquals((-3), settings.getMaxConcurrentStreams((-3)));
    }

    @Test
    public void setFields() {
        Settings settings = new Settings();
        settings.set(HEADER_TABLE_SIZE, 8096);
        Assert.assertEquals(8096, settings.getHeaderTableSize());
        Assert.assertTrue(settings.getEnablePush(true));
        settings.set(ENABLE_PUSH, 1);
        Assert.assertTrue(settings.getEnablePush(false));
        settings.clear();
        Assert.assertEquals((-3), settings.getMaxConcurrentStreams((-3)));
        settings.set(Settings.MAX_CONCURRENT_STREAMS, 75);
        Assert.assertEquals(75, settings.getMaxConcurrentStreams((-3)));
        settings.clear();
        Assert.assertEquals(16384, settings.getMaxFrameSize(16384));
        settings.set(MAX_FRAME_SIZE, 16777215);
        Assert.assertEquals(16777215, settings.getMaxFrameSize(16384));
        Assert.assertEquals((-1), settings.getMaxHeaderListSize((-1)));
        settings.set(MAX_HEADER_LIST_SIZE, 16777215);
        Assert.assertEquals(16777215, settings.getMaxHeaderListSize((-1)));
        Assert.assertEquals(Settings.DEFAULT_INITIAL_WINDOW_SIZE, settings.getInitialWindowSize());
        settings.set(INITIAL_WINDOW_SIZE, 108);
        Assert.assertEquals(108, settings.getInitialWindowSize());
    }

    @Test
    public void merge() {
        Settings a = new Settings();
        a.set(HEADER_TABLE_SIZE, 10000);
        a.set(MAX_HEADER_LIST_SIZE, 20000);
        a.set(INITIAL_WINDOW_SIZE, 30000);
        Settings b = new Settings();
        b.set(MAX_HEADER_LIST_SIZE, 40000);
        b.set(INITIAL_WINDOW_SIZE, 50000);
        b.set(MAX_CONCURRENT_STREAMS, 60000);
        a.merge(b);
        Assert.assertEquals(10000, a.getHeaderTableSize());
        Assert.assertEquals(40000, a.getMaxHeaderListSize((-1)));
        Assert.assertEquals(50000, a.getInitialWindowSize());
        Assert.assertEquals(60000, a.getMaxConcurrentStreams((-1)));
    }
}

