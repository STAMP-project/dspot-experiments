package com.fishercoder;


import _917.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _917Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("dc-ba", _917Test.solution1.reverseOnlyLetters("ab-cd"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("j-Ih-gfE-dCba", _917Test.solution1.reverseOnlyLetters("a-bC-dEf-ghIj"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("Qedo1ct-eeLg=ntse-T!", _917Test.solution1.reverseOnlyLetters("Test1ng-Leet=code-Q!"));
    }
}

