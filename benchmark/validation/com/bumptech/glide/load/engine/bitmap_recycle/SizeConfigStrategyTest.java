package com.bumptech.glide.load.engine.bitmap_recycle;


import Bitmap.Config;
import SizeConfigStrategy.KeyPool;
import com.google.common.testing.EqualsTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


@RunWith(JUnit4.class)
public class SizeConfigStrategyTest {
    @Mock
    private KeyPool pool;

    @Test
    public void testKeyEquals() {
        new EqualsTester().addEqualityGroup(new SizeConfigStrategy.Key(pool, 100, Config.ARGB_8888), new SizeConfigStrategy.Key(pool, 100, Config.ARGB_8888)).addEqualityGroup(new SizeConfigStrategy.Key(pool, 101, Config.ARGB_8888)).addEqualityGroup(new SizeConfigStrategy.Key(pool, 100, Config.RGB_565)).addEqualityGroup(/* config */
        new SizeConfigStrategy.Key(pool, 100, null)).testEquals();
    }
}

