package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import SearchScroll.MAX_SCROLL_ID_LENGTH;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;


public class SearchScrollTest {
    @Test
    public void methodIsGetIfScrollIdIsShort() {
        String scrollId = Strings.padStart("scrollId", MAX_SCROLL_ID_LENGTH, 'x');
        SearchScroll searchScroll = new SearchScroll.Builder(scrollId, "1m").build();
        String uri = searchScroll.getURI(UNKNOWN);
        Assert.assertEquals("GET", searchScroll.getRestMethodName());
        Assert.assertNull(searchScroll.getData(new Gson()));
        Assert.assertTrue(((uri.length()) < 2000));
        Assert.assertTrue(uri.contains(scrollId));
    }

    @Test
    public void methodIsPostIfScrollIdIsLong() {
        String scrollId = Strings.padStart("scrollId", 2000, 'x');
        JsonObject expectedResults = new JsonObject();
        expectedResults.addProperty("scroll_id", scrollId);
        SearchScroll searchScroll = new SearchScroll.Builder(scrollId, "1m").build();
        String uri = searchScroll.getURI(UNKNOWN);
        Assert.assertEquals("POST", searchScroll.getRestMethodName());
        Assert.assertEquals(expectedResults.toString(), searchScroll.getData(new Gson()));
        Assert.assertTrue(((uri.length()) < 2000));
        Assert.assertFalse(uri.contains(scrollId));
    }

    @Test
    public void equalsReturnsTrueForSameScrollIds() {
        SearchScroll searchScroll1 = new SearchScroll.Builder("scroller1", "scroll").build();
        SearchScroll searchScroll1Duplicate = new SearchScroll.Builder("scroller1", "scroll").build();
        Assert.assertEquals(searchScroll1, searchScroll1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentScrollIds() {
        SearchScroll searchScroll1 = new SearchScroll.Builder("scroller1", "scroll").build();
        SearchScroll searchScroll2 = new SearchScroll.Builder("scroller2", "scroll").build();
        Assert.assertNotEquals(searchScroll1, searchScroll2);
    }

    @Test
    public void equalsReturnsFalseForDifferentScrollNames() {
        SearchScroll searchScroll1 = new SearchScroll.Builder("scroller", "scroll").build();
        SearchScroll searchScroll2 = new SearchScroll.Builder("scroller", "scroll2").build();
        Assert.assertNotEquals(searchScroll1, searchScroll2);
    }
}

