package com.fishercoder;


import _966.Solution;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by varunu28 on 1/01/19.
 */
public class _966Test {
    private static Solution test;

    @Test
    public void test1() {
        Assert.assertEquals(Arrays.toString(new String[]{ "kite", "KiTe", "KiTe", "Hare", "hare", "", "", "KiTe", "", "KiTe" }), Arrays.toString(_966Test.test.spellchecker(new String[]{ "KiTe", "kite", "hare", "Hare" }, new String[]{ "kite", "Kite", "KiTe", "Hare", "HARE", "Hear", "hear", "keti", "keet", "keto" })));
    }
}

