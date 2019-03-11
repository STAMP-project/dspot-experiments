package de.danoeh.antennapod.core.feed;


import org.junit.Test;


public class FeedTest {
    private Feed original;

    private Feed changedFeed;

    @Test
    public void testCompareWithOther_feedImageDownloadUrlChanged() throws Exception {
        setNewFeedImageDownloadUrl();
        feedHasChanged();
    }

    @Test
    public void testCompareWithOther_sameFeedImage() throws Exception {
        changedFeed.setImageUrl(FeedMother.IMAGE_URL);
        feedHasNotChanged();
    }

    @Test
    public void testCompareWithOther_feedImageRemoved() throws Exception {
        feedImageRemoved();
        feedHasNotChanged();
    }

    @Test
    public void testUpdateFromOther_feedImageDownloadUrlChanged() throws Exception {
        setNewFeedImageDownloadUrl();
        original.updateFromOther(changedFeed);
        feedImageWasUpdated();
    }

    @Test
    public void testUpdateFromOther_feedImageRemoved() throws Exception {
        feedImageRemoved();
        original.updateFromOther(changedFeed);
        feedImageWasNotUpdated();
    }

    @Test
    public void testUpdateFromOther_feedImageAdded() throws Exception {
        feedHadNoImage();
        setNewFeedImageDownloadUrl();
        original.updateFromOther(changedFeed);
        feedImageWasUpdated();
    }
}

