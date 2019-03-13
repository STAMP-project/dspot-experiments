package org.robolectric.shadows;


import Build.VERSION_CODES;
import android.service.quicksettings.Tile;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * test for {@link org.robolectric.shadows.ShadowTile}.
 */
@RunWith(AndroidJUnit4.class)
@Config(sdk = VERSION_CODES.N)
public final class ShadowTileTest {
    private Tile tile;

    private ShadowTile shadowTile;

    @Test
    public void updateTile() throws Exception {
        // this test passes if updateTile() throws no Exception.
        tile.updateTile();
        shadowTile.updateTile();
    }
}

