package net.glxn.qrgen.core.scheme;


import org.junit.Assert;
import org.junit.Test;


public class BookmarkTest {
    private static final String BOOKMARK = "MEBKM:URL:google.com;TITLE:Google;;";

    @Test
    public void testParse() {
        Bookmark bookmark = Bookmark.parse(BookmarkTest.BOOKMARK);
        Assert.assertEquals("google.com", bookmark.getUrl());
        Assert.assertEquals("Google", bookmark.getTitel());
    }

    @Test
    public void testToString() {
        Bookmark bookmark = new Bookmark();
        bookmark.setUrl("google.com");
        bookmark.setTitel("Google");
        Assert.assertEquals(BookmarkTest.BOOKMARK, bookmark.toString());
    }
}

