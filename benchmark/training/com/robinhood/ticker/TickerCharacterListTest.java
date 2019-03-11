package com.robinhood.ticker;


import TickerCharacterList.CharacterIndices;
import TickerUtils.EMPTY_CHAR;
import org.junit.Assert;
import org.junit.Test;

import static TickerUtils.EMPTY_CHAR;


public class TickerCharacterListTest {
    @Test
    public void test_initialization() {
        final TickerCharacterList list = new TickerCharacterList("012");
        final char[] expected = new char[]{ EMPTY_CHAR, '0', '1', '2', '0', '1', '2' };
        Assert.assertArrayEquals(expected, list.getCharacterList());
    }

    @Test
    public void test_getCharacterIndices() {
        final TickerCharacterList list = new TickerCharacterList("012");
        final TickerCharacterList.CharacterIndices indices = list.getCharacterIndices('0', '1');
        Assert.assertEquals(1, indices.startIndex);
        Assert.assertEquals(2, indices.endIndex);
    }

    @Test
    public void test_getCharacterIndicesWraparound() {
        final TickerCharacterList list = new TickerCharacterList("012");
        final TickerCharacterList.CharacterIndices indices = list.getCharacterIndices('2', '0');
        Assert.assertEquals(3, indices.startIndex);
        Assert.assertEquals(4, indices.endIndex);
    }

    @Test
    public void test_getCharacterIndicesWraparound2() {
        final TickerCharacterList list = new TickerCharacterList("012");
        final TickerCharacterList.CharacterIndices indices = list.getCharacterIndices('0', '2');
        Assert.assertEquals(4, indices.startIndex);
        Assert.assertEquals(3, indices.endIndex);
    }

    @Test
    public void test_getCharacterIndicesEmptyNoWraparound() {
        final TickerCharacterList list = new TickerCharacterList("012");
        final TickerCharacterList.CharacterIndices indices = list.getCharacterIndices('2', EMPTY_CHAR);
        Assert.assertEquals(3, indices.startIndex);
        Assert.assertEquals(0, indices.endIndex);
    }
}

