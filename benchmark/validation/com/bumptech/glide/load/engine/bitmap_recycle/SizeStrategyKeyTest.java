package com.bumptech.glide.load.engine.bitmap_recycle;


import SizeStrategy.KeyPool;
import com.bumptech.glide.load.engine.bitmap_recycle.SizeStrategy.Key;
import com.google.common.testing.EqualsTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class SizeStrategyKeyTest {
    private KeyPool keyPool;

    @Test
    public void testEquality() {
        Key first = new Key(keyPool);
        first.init(100);
        Key second = new Key(keyPool);
        second.init(100);
        Key third = new Key(keyPool);
        third.init(50);
        new EqualsTester().addEqualityGroup(first, second).addEqualityGroup(third).testEquals();
    }

    @Test
    public void testReturnsSelfToPoolOnOffer() {
        Key key = new Key(keyPool);
        key.offer();
        Mockito.verify(keyPool).offer(ArgumentMatchers.eq(key));
    }

    @Test
    public void testInitSetsSize() {
        Key key = new Key(keyPool);
        key.init(100);
        Key other = new Key(keyPool);
        other.init(200);
        Assert.assertNotEquals(key, other);
        key.init(200);
        Assert.assertEquals(key, other);
    }
}

