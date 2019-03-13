/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// For android.webkit.MimeTypeMap
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class ParseFileStateTest {
    @Test
    public void testDefaults() {
        ParseFile.State state = new ParseFile.State.Builder().build();
        Assert.assertEquals("file", state.name());
        Assert.assertEquals(null, state.mimeType());
        Assert.assertNull(state.url());
    }

    @Test
    public void testProperties() {
        ParseFile.State state = build();
        Assert.assertEquals("test", state.name());
        Assert.assertEquals("application/test", state.mimeType());
        Assert.assertEquals("http://twitter.com/grantland", state.url());
    }

    @Test
    public void testCopy() {
        ParseFile.State state = build();
        ParseFile.State copy = build();
        Assert.assertEquals("test", copy.name());
        Assert.assertEquals("application/test", copy.mimeType());
        Assert.assertEquals("http://twitter.com/grantland", copy.url());
        Assert.assertNotSame(state, copy);
    }

    @Test
    public void testMimeType() {
        ParseFile.State state = build();
        Assert.assertEquals("test", state.mimeType());
    }

    @Test
    public void testMimeTypeNotSetFromExtension() {
        ParseFile.State state = build();
        Assert.assertEquals(null, state.mimeType());
    }
}

