package com.fishercoder;


import _824.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _824Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("Imaa peaksmaaa oatGmaaaa atinLmaaaaa", _824Test.solution1.toGoatLatin("I speak Goat Latin"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("heTmaa uickqmaaa rownbmaaaa oxfmaaaaa umpedjmaaaaaa overmaaaaaaa hetmaaaaaaaa azylmaaaaaaaaa ogdmaaaaaaaaaa", _824Test.solution1.toGoatLatin("The quick brown fox jumped over the lazy dog"));
    }
}

