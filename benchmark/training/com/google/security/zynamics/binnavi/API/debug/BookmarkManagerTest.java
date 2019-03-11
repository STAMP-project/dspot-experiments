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


import com.google.security.zynamics.binnavi.API.disassembly.Address;
import com.google.security.zynamics.binnavi.models.Bookmarks.memory.BookmarkManager;
import com.google.security.zynamics.binnavi.models.Bookmarks.memory.com.google.security.zynamics.binnavi.API.debug.BookmarkManager;
import com.google.security.zynamics.zylib.disassembly.CAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class BookmarkManagerTest {
    private final BookmarkManager m_nativeManager = new BookmarkManager();

    private final BookmarkManager m_apiManager = new com.google.security.zynamics.binnavi.API.debug.BookmarkManager(m_nativeManager);

    private final MockBookmarkManagerListener m_mockListener = new MockBookmarkManagerListener();

    @Test
    public void testAddBookmark() {
        m_apiManager.addListener(m_mockListener);
        try {
            m_apiManager.addBookmark(null, "Fark");
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            m_apiManager.addBookmark(new Address(291), null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        m_apiManager.addBookmark(new Address(291), "Fark");
        try {
            m_apiManager.addBookmark(new Address(291), "Fark");
            Assert.fail();
        } catch (final IllegalArgumentException exception) {
        }
        Assert.assertEquals(1, m_nativeManager.getNumberOfBookmarks());
        Assert.assertEquals(291, m_nativeManager.getBookmark(0).getAddress().toLong());
        Assert.assertEquals("Fark", m_nativeManager.getBookmark(0).getDescription());
        Assert.assertEquals(1, m_apiManager.getNumberOfBookmarks());
        Assert.assertEquals(291, m_apiManager.getBookmark(0).getAddress().toLong());
        Assert.assertEquals("Fark", m_apiManager.getBookmark(0).getDescription());
        Assert.assertEquals("addedBookmark/123;", m_mockListener.events);
        m_apiManager.removeListener(m_mockListener);
    }

    @Test
    public void testGetBookmark() {
        try {
            Assert.assertEquals(291, m_apiManager.getBookmark(null).getAddress().toLong());
        } catch (final NullPointerException exception) {
        }
        Assert.assertNull(m_apiManager.getBookmark(new Address(291)));
        m_apiManager.addBookmark(new Address(291), "Fark");
        Assert.assertEquals(1, m_apiManager.getNumberOfBookmarks());
        Assert.assertEquals(291, m_apiManager.getBookmark(new Address(291)).getAddress().toLong());
        Assert.assertEquals("Fark", m_apiManager.getBookmark(new Address(291)).getDescription());
    }

    @Test
    public void testPreinitialized() {
        final BookmarkManager nativeManager = new BookmarkManager();
        nativeManager.addBookmark(new com.google.security.zynamics.binnavi.models.Bookmarks.memory.CBookmark(new CAddress(0), "foo"));
        final com.google.security.zynamics.binnavi.API.debug.BookmarkManager apiManager = new com.google.security.zynamics.binnavi.API.debug.BookmarkManager(nativeManager);
        final Bookmark bm = apiManager.getBookmark(0);
        Assert.assertEquals(0, bm.getAddress().toLong());
        Assert.assertEquals("foo", bm.getDescription());
    }

    @Test
    public void testRemoveBookmark() {
        try {
            m_apiManager.removeBookmark(null);
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        try {
            m_apiManager.removeBookmark(new Address(291));
            Assert.fail();
        } catch (final NullPointerException exception) {
        }
        m_apiManager.addBookmark(new Address(291), "Fark");
        m_apiManager.addListener(m_mockListener);
        m_apiManager.removeBookmark(new Address(291));
        Assert.assertEquals(0, m_nativeManager.getNumberOfBookmarks());
        Assert.assertEquals(0, m_apiManager.getNumberOfBookmarks());
        Assert.assertEquals("removedBookmark/123;", m_mockListener.events);
        m_apiManager.removeListener(m_mockListener);
    }

    @Test
    public void testToString() {
        m_apiManager.addBookmark(new Address(4659), "Fark");
        m_apiManager.addBookmark(new Address(4660), "Fark");
        m_apiManager.addBookmark(new Address(4661), "Fark");
        Assert.assertEquals("Bookmark Manager (Managing 3 Bookmarks)", m_apiManager.toString());
    }
}

