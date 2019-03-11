package org.osmdroid.tileprovider.cachemanager;


import android.graphics.Rect;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CacheManagerTest {
    private final Random mRandom = new Random();

    /**
     * Make sure {@link org.osmdroid.tileprovider.cachemanager.CacheManager#getTilesCoverageIterable(BoundingBox, int, int)} returns the
     * same size and elements as the {@link org.osmdroid.tileprovider.cachemanager.CacheManager#getTilesCoverage(BoundingBox, int, int)}
     * for a big mapTileUpperBound
     */
    @Test
    public void testGetTilesIterableForBigMapTileUpperBound() {
        verifyGetTilesIterable(15, 15);
    }

    /**
     * Make sure {@link org.osmdroid.tileprovider.cachemanager.CacheManager#getTilesCoverageIterable(BoundingBox, int, int)} returns the
     * same size and elements as the {@link org.osmdroid.tileprovider.cachemanager.CacheManager#getTilesCoverage(BoundingBox, int, int)}
     * for a small mapTileUpperBound
     */
    @Test
    public void testGetTilesIterableForSmallMapTileUpperBound() {
        verifyGetTilesIterable(2, 2);
    }

    /**
     * Make sure {@link org.osmdroid.tileprovider.cachemanager.CacheManager#getTilesCoverageIterable(BoundingBox, int, int)} returns the
     * same size and elements as the {@link org.osmdroid.tileprovider.cachemanager.CacheManager#getTilesCoverage(BoundingBox, int, int)}
     * for the range of zoom levels.
     */
    @Test
    public void testGetTilesIterableForRangeOfZooms() {
        verifyGetTilesIterable(10, 11);
    }

    /**
     *
     *
     * @since 6.0.3
     */
    @Test
    public void testGetTilesRectSingleTile() {
        final TileSystem tileSystem = MapView.getTileSystem();
        final BoundingBox box = new BoundingBox();
        for (int zoom = 0; zoom <= (TileSystem.getMaximumZoomLevel()); zoom++) {
            final double longitude = tileSystem.getRandomLongitude(mRandom.nextDouble());
            final double latitude = tileSystem.getRandomLatitude(mRandom.nextDouble());
            box.set(latitude, longitude, latitude, longitude);// single point

            final Rect rect = CacheManager.getTilesRect(box, zoom);
            Assert.assertEquals(rect.left, rect.right);// single tile expected

            Assert.assertEquals(rect.top, rect.bottom);// single tile expected

        }
    }

    /**
     *
     *
     * @since 6.0.3
     */
    @Test
    public void testGetTilesRectWholeWorld() {
        final TileSystem tileSystem = MapView.getTileSystem();
        final BoundingBox box = // whole world
        new BoundingBox(tileSystem.getMaxLatitude(), tileSystem.getMaxLongitude(), tileSystem.getMinLatitude(), tileSystem.getMinLongitude());
        for (int zoom = 0; zoom <= (TileSystem.getMaximumZoomLevel()); zoom++) {
            final Rect rect = CacheManager.getTilesRect(box, zoom);
            Assert.assertEquals(0, rect.left);
            Assert.assertEquals(0, rect.top);
            final int maxSize = (-1) + (1 << zoom);
            Assert.assertEquals(maxSize, rect.bottom);
            Assert.assertEquals(maxSize, rect.right);
        }
    }
}

