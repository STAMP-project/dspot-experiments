package com.bumptech.glide.load.engine.bitmap_recycle;


import AttributeStrategy.KeyPool;
import Bitmap.Config.ARGB_4444;
import Bitmap.Config.RGB_565;
import com.bumptech.glide.load.engine.bitmap_recycle.AttributeStrategy.Key;
import com.google.common.testing.EqualsTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class AttributeStrategyKeyTest {
    private KeyPool keyPool;

    @Test
    public void testEquality() {
        Key first = new Key(keyPool);
        first.init(100, 100, ARGB_4444);
        Key second = new Key(keyPool);
        second.init(100, 100, ARGB_4444);
        Key third = new Key(keyPool);
        third.init(200, 100, ARGB_4444);
        Key fourth = new Key(keyPool);
        fourth.init(100, 200, ARGB_4444);
        Key fifth = new Key(keyPool);
        fifth.init(100, 100, RGB_565);
        new EqualsTester().addEqualityGroup(first, second).addEqualityGroup(third).addEqualityGroup(fourth).addEqualityGroup(fifth).testEquals();
    }

    @Test
    public void testReturnsSelfToPoolOnOffer() {
        Key key = new Key(keyPool);
        key.offer();
        Mockito.verify(keyPool).offer(ArgumentMatchers.eq(key));
    }

    @Test
    public void testInitSetsAttributes() {
        Key key = new Key(keyPool);
        key.init(100, 100, ARGB_4444);
        Key other = new Key(keyPool);
        other.init(200, 200, RGB_565);
        Assert.assertNotEquals(key, other);
        key.init(200, 200, RGB_565);
        Assert.assertEquals(key, other);
    }
}

