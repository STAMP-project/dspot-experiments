/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.API.debug;


import com.google.security.zynamics.binnavi.models.Bookmarks.memory.CBookmark;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class BookmarkTest {
    private final CBookmark m_nativeBookmark = new CBookmark(new CAddress(291), "Hannes");

    private final Bookmark m_apiBookmark = new Bookmark(m_nativeBookmark);

    private final MockBookmarkListener m_mockListener = new MockBookmarkListener();

    @Test
    public void testConstructor() {
        Assert.assertEquals(291, m_apiBookmark.getAddress().toLong());
        Assert.assertEquals("Hannes", m_apiBookmark.getDescription());
    }

    @Test
    public void testNativeSetDescription() {
        m_apiBookmark.addListener(m_mockListener);
        Assert.assertEquals("", m_mockListener.events);
        m_nativeBookmark.setDescription("FooBert");
        Assert.assertEquals("FooBert", m_nativeBookmark.getDescription());
        Assert.assertEquals("FooBert", m_apiBookmark.getDescription());
        Assert.assertEquals("changedDescription/123/FooBert;", m_mockListener.events);
        m_apiBookmark.removeListener(m_mockListener);
    }

    @Test
    public void testSetDescription() {
        m_apiBookmark.addListener(m_mockListener);
        try {
            m_apiBookmark.setDescription(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        Assert.assertEquals("", m_mockListener.events);
        m_apiBookmark.setDescription("FooBert");
        Assert.assertEquals("FooBert", m_nativeBookmark.getDescription());
        Assert.assertEquals("FooBert", m_apiBookmark.getDescription());
        Assert.assertEquals("changedDescription/123/FooBert;", m_mockListener.events);
    }

    @Test
    public void testToString() {
        Assert.assertEquals("Bookmark 123/'Hannes'", m_apiBookmark.toString());
    }
}

