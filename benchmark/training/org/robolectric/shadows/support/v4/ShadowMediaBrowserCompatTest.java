package org.robolectric.shadows.support.v4;


import MediaBrowserCompat.ItemCallback;
import MediaBrowserCompat.SearchCallback;
import MediaBrowserCompat.SubscriptionCallback;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;


/**
 * Tests for {@link org.robolectric.shadows.support.v4.ShadowMediaBrowserCompat}.
 */
@RunWith(RobolectricTestRunner.class)
public class ShadowMediaBrowserCompatTest {
    private Context context = RuntimeEnvironment.application;

    private MediaBrowserCompat mediaBrowser;

    private ShadowMediaBrowserCompat shadow;

    private MediaItem root;

    private MediaItem child;

    private final ShadowMediaBrowserCompatTest.MediaItemCallBack mediaItemCallBack = new ShadowMediaBrowserCompatTest.MediaItemCallBack();

    private final ShadowMediaBrowserCompatTest.MediaSubscriptionCallback mediaSubscriptionCallback = new ShadowMediaBrowserCompatTest.MediaSubscriptionCallback();

    private final ShadowMediaBrowserCompatTest.MediaSearchCallback mediaSearchCallback = new ShadowMediaBrowserCompatTest.MediaSearchCallback();

    private static final String ROOT_ID = "root_id";

    private static final String CHILD_ID = "child_id";

    @Test
    public void mediaBrowserConnection_isConnected() {
        assertThat(mediaBrowser.isConnected()).isTrue();
    }

    @Test
    public void mediaBrowserConnection_isDisconnected() {
        mediaBrowser.disconnect();
        assertThat(mediaBrowser.isConnected()).isFalse();
    }

    @Test
    public void mediaBrowser_getRootId() {
        String mediaBrowserRootId = mediaBrowser.getRoot();
        assertThat(mediaBrowserRootId).isEqualTo(ShadowMediaBrowserCompatTest.ROOT_ID);
    }

    @Test
    public void mediaBrowser_getItem() {
        mediaBrowser.getItem(ShadowMediaBrowserCompatTest.ROOT_ID, mediaItemCallBack);
        assertThat(mediaItemCallBack.getMediaItem()).isEqualTo(root);
        mediaItemCallBack.mediaItem = null;
        mediaBrowser.getItem("fake_id", mediaItemCallBack);
        assertThat(mediaItemCallBack.getMediaItem()).isNull();
    }

    @Test
    public void mediaBrowser_subscribe() {
        mediaBrowser.subscribe(ShadowMediaBrowserCompatTest.ROOT_ID, mediaSubscriptionCallback);
        assertThat(mediaSubscriptionCallback.getResults()).isEqualTo(Collections.singletonList(child));
        mediaBrowser.subscribe(ShadowMediaBrowserCompatTest.CHILD_ID, mediaSubscriptionCallback);
        assertThat(mediaSubscriptionCallback.getResults()).isEmpty();
        mediaBrowser.subscribe("fake_id", mediaSubscriptionCallback);
        assertThat(mediaSubscriptionCallback.getResults()).isEmpty();
    }

    @Test
    public void mediaBrowser_search() {
        mediaBrowser.search("root", null, mediaSearchCallback);
        assertThat(mediaSearchCallback.getResults()).isEqualTo(Collections.singletonList(root));
        mediaBrowser.search("title", null, mediaSearchCallback);
        final List<MediaItem> expectedResults = Arrays.asList(root, child);
        assertThat(mediaSearchCallback.getResults()).isEqualTo(expectedResults);
        mediaBrowser.search("none", null, mediaSearchCallback);
        assertThat(mediaSearchCallback.getResults()).isEmpty();
    }

    private static class MediaSearchCallback extends MediaBrowserCompat.SearchCallback {
        List<MediaItem> results;

        @Override
        public void onSearchResult(@NonNull
        String query, Bundle bundle, @NonNull
        List<MediaItem> list) {
            super.onSearchResult(query, bundle, list);
            results = list;
        }

        public List<MediaItem> getResults() {
            return results;
        }
    }

    private static class MediaSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {
        List<MediaItem> results;

        @Override
        public void onChildrenLoaded(@NonNull
        String parentId, @NonNull
        List<MediaItem> list) {
            super.onChildrenLoaded(parentId, list);
            results = list;
        }

        public List<MediaItem> getResults() {
            return results;
        }
    }

    private static class MediaItemCallBack extends MediaBrowserCompat.ItemCallback {
        MediaItem mediaItem;

        @Override
        public void onItemLoaded(MediaItem mediaItem) {
            super.onItemLoaded(mediaItem);
            this.mediaItem = mediaItem;
        }

        MediaItem getMediaItem() {
            return mediaItem;
        }
    }
}

