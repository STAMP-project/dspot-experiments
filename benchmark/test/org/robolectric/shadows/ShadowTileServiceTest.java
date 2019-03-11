package org.robolectric.shadows;


import Build.VERSION_CODES;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Test for {@link org.robolectric.shadows.ShadowTileService}.
 */
@RunWith(AndroidJUnit4.class)
@Config(sdk = VERSION_CODES.N)
public final class ShadowTileServiceTest {
    private ShadowTileServiceTest.MyTileService tileService;

    @Test
    public void getTile() {
        Tile tile = getQsTile();
        assertThat(tile).isNotNull();
    }

    /**
     * A subclass of {@link TileService} for testing, To mimic the way {@link TileService} is used in
     * production.
     */
    static class MyTileService extends TileService {}
}

