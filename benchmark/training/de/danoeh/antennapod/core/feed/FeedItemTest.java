package de.danoeh.antennapod.core.feed;


import org.junit.Test;


public class FeedItemTest {
    private FeedItem original;

    private FeedItem changedFeedItem;

    @Test
    public void testUpdateFromOther_feedItemImageDownloadUrlChanged() throws Exception {
        setNewFeedItemImageDownloadUrl();
        original.updateFromOther(changedFeedItem);
        assertFeedItemImageWasUpdated();
    }

    @Test
    public void testUpdateFromOther_feedItemImageRemoved() throws Exception {
        feedItemImageRemoved();
        original.updateFromOther(changedFeedItem);
        assertFeedItemImageWasNotUpdated();
    }

    @Test
    public void testUpdateFromOther_feedItemImageAdded() throws Exception {
        original.setImageUrl(null);
        setNewFeedItemImageDownloadUrl();
        original.updateFromOther(changedFeedItem);
        assertFeedItemImageWasUpdated();
    }
}

